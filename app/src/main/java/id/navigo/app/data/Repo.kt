package id.navigo.app.data

data class LawHit(val law: Law, val score: Int, val pasal: List<String>)
data class ArticleHit(val law: Law, val article: Article, val score: Int) {
    val citation get() = "${law.number} Pasal ${article.number}"
}

class Repo(val db: AppDatabase) {

    suspend fun seedIfEmpty() {
        if (db.lawDao().count() == 0) {
            Seed.laws.forEach { (law, arts) ->
                val id = db.lawDao().insertLaw(law)
                db.lawDao().insertArticles(arts.map { it.copy(lawId = id) })
            }
        }
        if (db.templateDao().count() == 0) db.templateDao().insertAll(Seed.templates)
    }

    private val stop = setOf("yang", "dan", "di", "ke", "dari", "untuk", "apa", "itu", "tentang",
        "atau", "dengan", "saya", "bagaimana", "apakah", "jika", "kalau", "adalah", "ada")

    /** Normalisasi query: huruf kecil, buang tanda baca & stopword. */
    fun tokens(q: String): List<String> =
        q.lowercase().replace(Regex("[^a-z0-9 ]"), " ").split(" ")
            .filter { it.length >= 2 && it !in stop }.distinct()

    private fun count(h: String, t: String): Int {
        var c = 0; var i = h.indexOf(t)
        while (i >= 0) { c++; i = h.indexOf(t, i + t.length) }
        return c
    }

    /** Ranking: kecocokan judul x3, kecocokan pasal x2, ditambah frekuensi kata. */
    suspend fun searchLaws(q: String, category: String?): List<LawHit> {
        val tk = tokens(q)
        if (tk.isEmpty()) return emptyList()
        val arts = db.lawDao().allArticles().groupBy { it.lawId }
        return db.lawDao().laws()
            .filter { it.active && (category == null || category == "Semua" || it.category == category) }
            .mapNotNull { law ->
                val head = "${law.title} ${law.topic} ${law.number}".lowercase()
                val desc = law.description.lowercase()
                var score = 0
                val pasal = mutableListOf<String>()
                for (t in tk) { if (head.contains(t)) score += 3; score += count(desc, t) }
                for (a in arts[law.id].orEmpty()) {
                    val body = a.text.lowercase()
                    var s = 0
                    for (t in tk) {
                        if (body.contains(t)) s += 2 + count(body, t) - 1
                        if (a.number == t) s += 3
                    }
                    if (s > 0) { score += s; pasal += a.number }
                }
                if (score > 0) LawHit(law, score, pasal) else null
            }.sortedByDescending { it.score }
    }

    private val synonymMap = mapOf(
        "wanprestasi" to listOf("wanprestasi", "ingkar janji", "cidera janji", "pelanggaran kontrak"),
        "ingkar" to listOf("ingkar janji", "wanprestasi", "cidera janji"),
        "hki" to listOf("hki", "hak cipta", "merek", "paten", "kekayaan intelektual"),
        "cipta" to listOf("hak cipta", "pencipta", "ciptaan", "hki"),
        "merek" to listOf("merek", "brand", "hki", "lisensi"),
        "nda" to listOf("nda", "kerahasiaan", "rahasia dagang"),
        "mou" to listOf("mou", "nota kesepahaman", "perjanjian"),
        "pdp" to listOf("pdp", "data pribadi", "privasi", "pelindungan data"),
        "pajak" to listOf("pajak", "pph", "ppn", "perpajakan"),
        "sanksi" to listOf("sanksi", "denda", "pidana", "hukuman"),
        "pembajakan" to listOf("pembajakan", "pelanggaran hak cipta", "penggandaan")
    )

    private fun expandTokens(tokens: List<String>): List<String> {
        val result = tokens.toMutableList()
        for (t in tokens) {
            synonymMap[t]?.let { result.addAll(it) }
        }
        return result.distinct()
    }

    /** Retrieval pasal untuk RAG chatbot. */
    suspend fun searchArticles(q: String, limit: Int = 3): List<ArticleHit> {
        val tk = tokens(q)
        if (tk.isEmpty()) return emptyList()
        val expanded = expandTokens(tk)
        val laws = db.lawDao().laws().filter { it.active }.associateBy { it.id }
        val articleNumMatch = Regex("(?i)pasal\\s+(\\d+[a-z]?)").find(q)?.groupValues?.get(1)

        return db.lawDao().allArticles().mapNotNull { a ->
            val law = laws[a.lawId] ?: return@mapNotNull null
            val body = (a.text + " " + law.topic + " " + law.title).lowercase()
            var s = 0

            if (articleNumMatch != null && a.number.equals(articleNumMatch, ignoreCase = true)) {
                s += 10
            }

            for (t in expanded) {
                if (body.contains(t)) {
                    s += 1 + count(body, t)
                    if (law.title.lowercase().contains(t) || law.topic.lowercase().contains(t)) {
                        s += 2
                    }
                }
            }

            val minScore = if (tk.size == 1) 1 else 2
            if (s >= minScore) ArticleHit(law, a, s) else null
        }.sortedByDescending { it.score }.take(limit)
    }
}

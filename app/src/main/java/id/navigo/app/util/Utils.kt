package id.navigo.app.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object Net {
    fun online(c: Context): Boolean {
        val cm = c.getSystemService(ConnectivityManager::class.java) ?: return false
        val n = cm.activeNetwork ?: return false
        val cap = cm.getNetworkCapabilities(n) ?: return false
        return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

object Prefs {
    const val DAILY_LIMIT = 30
    private fun sp(c: Context) = c.getSharedPreferences("navigo", Context.MODE_PRIVATE)
    private fun key() = "q_" + SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
    fun hasConsent(c: Context) = sp(c).getBoolean("ai_consent", false)
    fun setConsent(c: Context, v: Boolean) = sp(c).edit().putBoolean("ai_consent", v).apply()
    fun quotaUsed(c: Context) = sp(c).getInt(key(), 0)
    /** Kuota harian pemanggilan AI per perangkat (pencegah penyalahgunaan). */
    fun consumeQuota(c: Context): Boolean {
        val n = quotaUsed(c)
        if (n >= DAILY_LIMIT) return false
        sp(c).edit().putInt(key(), n + 1).apply()
        return true
    }
}

/** Mengganti data pribadi dengan token sebelum dikirim ke AI, lalu memulihkannya lokal. */
class Anonymizer {
    private val map = linkedMapOf<String, String>()
    private var n = 0
    private val rules = listOf(
        "PIHAK" to Regex("(?i)(?<=nama\\s{0,3}:\\s{0,3})[^\\n,;]{2,60}"),
        "EMAIL" to Regex("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+"),
        "NPWP" to Regex("\\b\\d{2}\\.\\d{3}\\.\\d{3}\\.\\d-\\d{3}\\.\\d{3}\\b"),
        "NIK" to Regex("\\b\\d{16}\\b"),
        "TELP" to Regex("(\\+62|\\b0)8\\d{8,11}\\b"),
        "REKENING" to Regex("\\b\\d{10,15}\\b")
    )

    fun mask(text: String): String {
        var out = text
        for ((tag, re) in rules) out = re.replace(out) { m -> map.getOrPut(m.value) { "[${tag}_${++n}]" } }
        return out
    }

    fun restore(text: String): String {
        var out = text
        map.forEach { (real, token) -> out = out.replace(token, real) }
        return out
    }
}

object Guardrail {
    const val MAX_PROMPT = 4000
    const val MAX_DOC = 12000
    private val scope = listOf(
        "hukum", "uu", "undang", "pasal", "kontrak", "perjanjian", "mou", "hak cipta", "hki", "merek",
        "royalti", "lisensi", "paten", "izin", "nib", "umkm", "usaha", "pajak", "data pribadi", "wanprestasi",
        "ganti rugi", "denda", "nda", "kerahasiaan", "sengketa", "klien", "legal", "peraturan", "karya",
        "pidana", "perdata", "meterai", "materai", "bisnis", "pelanggan", "konsumen", "kuasa"
    )

    fun validateQuestion(q: String, hasContext: Boolean): String? {
        if (q.isBlank()) return "Pertanyaan tidak boleh kosong."
        if (q.length > MAX_PROMPT) return "Pertanyaan terlalu panjang (maks $MAX_PROMPT karakter)."
        val l = q.lowercase()
        if (!hasContext && scope.none { l.contains(it) })
            return "Maaf, NaviGo hanya menjawab pertanyaan seputar hukum bisnis (kontrak, hak cipta, merek, data pribadi, dll.)."
        return null
    }
}

object Dates {
    private val id = Locale("id", "ID")
    private val show = DateTimeFormatter.ofPattern("dd MMM yyyy", id)
    private val input = DateTimeFormatter.ofPattern("dd/MM/uuuu")
    fun show(day: Long): String = LocalDate.ofEpochDay(day).format(show)
    fun inputFmt(day: Long): String = LocalDate.ofEpochDay(day).format(input)
    fun parse(s: String): Long? = runCatching {
        LocalDate.parse(s.trim(), input.withResolverStyle(java.time.format.ResolverStyle.STRICT)).toEpochDay()
    }.getOrNull()
    fun dateTime(ms: Long): String = SimpleDateFormat("dd MMM yyyy", id).format(Date(ms))
}

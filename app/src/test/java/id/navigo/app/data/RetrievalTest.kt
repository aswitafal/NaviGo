package id.navigo.app.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class RetrievalTest {

    private val law1 = Law(
        id = 1, number = "UU No 28 Th 2014", title = "Hak Cipta",
        topic = "Hak Cipta dan Kekayaan Intelektual", description = "Undang-Undang Hak Cipta",
        category = "Perjanjian & Hak Cipta", active = true
    )
    private val law2 = Law(
        id = 2, number = "KUHPerdata", title = "Kitab Undang-Undang Hukum Perdata",
        topic = "Hukum Perikatan dan Kontrak", description = "Hukum Kontrak dan Perikatan",
        category = "Perjanjian & Hak Cipta", active = true
    )

    private val article1 = Article(
        id = 1, lawId = 1, number = "113",
        text = "Setiap Orang yang tanpa hak melakukan pelanggaran hak ekonomi pencipta pidana penjara paling lama 1 tahun."
    )
    private val article2 = Article(
        id = 2, lawId = 2, number = "1243",
        text = "Ganti rugi karena tidak dipenuhinya suatu perikatan (wanprestasi) mulai diwajibkan bila debitur lalai."
    )

    private fun createRepo(laws: List<Law>, articles: List<Article>): Repo {
        val fakeDao = object : LawDao {
            override suspend fun laws(): List<Law> = laws
            override fun lawsFlow(): Flow<List<Law>> = flowOf(laws)
            override suspend fun allArticles(): List<Article> = articles
            override suspend fun articlesOf(id: Long): List<Article> = articles.filter { it.lawId == id }
            override suspend fun count(): Int = laws.size
            override suspend fun insertLaw(l: Law): Long = 1L
            override suspend fun insertArticles(a: List<Article>) {}
            override suspend fun updateLaw(l: Law) {}
            override suspend fun deleteLaw(l: Law) {}
            override suspend fun deleteArticlesOf(id: Long) {}
            override suspend fun byRemote(r: String): Law? = null
        }

        val fakeDb = object : AppDatabase() {
            override fun lawDao(): LawDao = fakeDao
            override fun chatDao(): ChatDao = error("Not implemented")
            override fun docDao(): DocDao = error("Not implemented")
            override fun mouDao(): MouDao = error("Not implemented")
            override fun contractDao(): ContractDao = error("Not implemented")
            override fun templateDao(): TemplateDao = error("Not implemented")
            override fun clearAllTables() {}
            override fun createInvalidationTracker(): androidx.room.InvalidationTracker = error("Not implemented")
            override fun createOpenHelper(config: androidx.room.DatabaseConfiguration): androidx.sqlite.db.SupportSQLiteOpenHelper = error("Not implemented")
        }

        return Repo(fakeDb)
    }

    @Test
    fun testTokenization() {
        val repo = createRepo(emptyList(), emptyList())
        val tokens = repo.tokens("Bagaimana sanksi wanprestasi dan ganti rugi dalam perjanjian?")
        assertTrue(tokens.contains("sanksi"))
        assertTrue(tokens.contains("wanprestasi"))
        assertFalse(tokens.contains("dan"))
    }

    @Test
    fun testExactKeywordMatching() = runBlocking<Unit> {
        val repo = createRepo(listOf(law2), listOf(article2))
        val hits = repo.searchArticles("wanprestasi", limit = 3)
        assertEquals(1, hits.size)
        assertEquals("1243", hits[0].article.number)
    }

    @Test
    fun testSynonymMatching() = runBlocking<Unit> {
        val repo = createRepo(listOf(law2), listOf(article2))
        // Query "ingkar janji" should match article2 ("wanprestasi") via synonym expansion
        val hits = repo.searchArticles("ingkar janji", limit = 3)
        assertTrue("Pencarian sinonim ingkar janji harus mencocokkan wanprestasi", hits.isNotEmpty())
    }

    @Test
    fun testPhrasingVariations() = runBlocking<Unit> {
        val repo = createRepo(listOf(law1, law2), listOf(article1, article2))
        val hits = repo.searchArticles("Bagaimana akibat jika terjadi cidera janji atau wanprestasi?", limit = 3)
        assertTrue(hits.isNotEmpty())
        assertEquals("1243", hits[0].article.number)
    }

    @Test
    fun testNoMatchingArticles() = runBlocking<Unit> {
        val repo = createRepo(listOf(law1, law2), listOf(article1, article2))
        val hits = repo.searchArticles("astronomi dan luar angkasa", limit = 3)
        assertTrue(hits.isEmpty())
    }

    @Test
    fun testMultiArticleScoring() = runBlocking<Unit> {
        val repo = createRepo(listOf(law1, law2), listOf(article1, article2))
        val hits = repo.searchArticles("pasal 113 hak cipta", limit = 3)
        assertTrue(hits.isNotEmpty())
        assertEquals("113", hits[0].article.number)
    }
}

package id.navigo.app.data

import android.app.Application
import androidx.room.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

@Entity(tableName = "laws")
data class Law(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String, val number: String, val category: String,
    val topic: String, val description: String,
    val active: Boolean = true,
    val aiSummary: String? = null,
    val remoteId: String = ""
)

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lawId: Long, val number: String, val text: String
)

@Entity(tableName = "mou_analyses")
data class MouAnalysis(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String, val createdAt: Long, val source: String,
    val score: Int, val level: String, val summary: String,
    val risksJson: String, val limited: Boolean, val endDay: Long?
)

@Entity(tableName = "chat_sessions")
data class ChatSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String, val lastText: String, val msgCount: Int, val updatedAt: Long
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long, val role: String, val text: String,
    val citation: String? = null, val createdAt: Long
)

@Entity(tableName = "documents")
data class GeneratedDoc(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileName: String, val templateName: String, val path: String,
    val sizeKb: Int, val pages: Int, val createdAt: Long, val partyName: String
)

@Entity(tableName = "contracts")
data class Contract(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String, val party: String, val startDay: Long, val endDay: Long,
    val reminders: String, val notified: String = ""
) {
    fun daysLeft(): Int = (endDay - LocalDate.now().toEpochDay()).toInt()
    fun expired() = daysLeft() < 0
}

@Entity(tableName = "templates")
data class DocTemplate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, val description: String, val body: String,
    val fields: String, val version: Int = 1, val active: Boolean = true,
    val usedCount: Int = 0, val remoteId: String = ""
)

data class FormField(val key: String, val label: String, val type: String, val required: Boolean)

/** fields: "key|Label|TEXT/NUMBER|1;;key2|Label2|TEXT|0" */
fun DocTemplate.formFields(): List<FormField> =
    fields.split(";;").filter { it.isNotBlank() }.map {
        val p = it.split("|")
        FormField(p[0], p.getOrElse(1) { p[0] }, p.getOrElse(2) { "TEXT" }, p.getOrElse(3) { "1" } == "1")
    }

@Dao interface LawDao {
    @Query("SELECT * FROM laws ORDER BY id") suspend fun laws(): List<Law>
    @Query("SELECT * FROM laws ORDER BY id") fun lawsFlow(): Flow<List<Law>>
    @Query("SELECT * FROM articles") suspend fun allArticles(): List<Article>
    @Query("SELECT * FROM articles WHERE lawId=:id ORDER BY id") suspend fun articlesOf(id: Long): List<Article>
    @Query("SELECT COUNT(*) FROM laws") suspend fun count(): Int
    @Insert suspend fun insertLaw(l: Law): Long
    @Insert suspend fun insertArticles(a: List<Article>)
    @Update suspend fun updateLaw(l: Law)
    @Delete suspend fun deleteLaw(l: Law)
    @Query("DELETE FROM articles WHERE lawId=:id") suspend fun deleteArticlesOf(id: Long)
    @Query("SELECT * FROM laws WHERE remoteId=:r LIMIT 1") suspend fun byRemote(r: String): Law?
}

@Dao interface MouDao {
    @Query("SELECT * FROM mou_analyses ORDER BY createdAt DESC") fun all(): Flow<List<MouAnalysis>>
    @Insert suspend fun insert(a: MouAnalysis): Long
    @Delete suspend fun delete(a: MouAnalysis)
    @Query("SELECT COUNT(*) FROM mou_analyses WHERE createdAt>=:since") suspend fun countSince(since: Long): Int
}

@Dao interface ChatDao {
    @Query("SELECT * FROM chat_sessions ORDER BY updatedAt DESC") fun sessions(): Flow<List<ChatSession>>
    @Query("SELECT * FROM chat_messages WHERE sessionId=:sid ORDER BY id") fun messages(sid: Long): Flow<List<ChatMessage>>
    @Query("SELECT * FROM chat_messages WHERE sessionId=:sid ORDER BY id DESC LIMIT :n")
    suspend fun lastMessages(sid: Long, n: Int): List<ChatMessage>
    @Query("SELECT * FROM chat_sessions WHERE id=:id") suspend fun session(id: Long): ChatSession?
    @Insert suspend fun insertSession(s: ChatSession): Long
    @Update suspend fun updateSession(s: ChatSession)
    @Insert suspend fun insertMessage(m: ChatMessage): Long
    @Delete suspend fun deleteSession(s: ChatSession)
    @Query("DELETE FROM chat_messages WHERE sessionId=:sid") suspend fun deleteMessages(sid: Long)
}

@Dao interface DocDao {
    @Query("SELECT * FROM documents ORDER BY createdAt DESC") fun all(): Flow<List<GeneratedDoc>>
    @Insert suspend fun insert(d: GeneratedDoc): Long
    @Delete suspend fun delete(d: GeneratedDoc)
}

@Dao interface ContractDao {
    @Query("SELECT * FROM contracts ORDER BY endDay ASC") fun all(): Flow<List<Contract>>
    @Insert suspend fun insert(c: Contract): Long
    @Update suspend fun update(c: Contract)
    @Delete suspend fun delete(c: Contract)
}

@Dao interface TemplateDao {
    @Query("SELECT * FROM templates ORDER BY id") fun all(): Flow<List<DocTemplate>>
    @Query("SELECT * FROM templates WHERE id=:id") suspend fun get(id: Long): DocTemplate?
    @Query("SELECT COUNT(*) FROM templates") suspend fun count(): Int
    @Insert suspend fun insert(t: DocTemplate): Long
    @Insert suspend fun insertAll(t: List<DocTemplate>)
    @Update suspend fun update(t: DocTemplate)
    @Delete suspend fun delete(t: DocTemplate)
    @Query("SELECT * FROM templates WHERE remoteId=:r LIMIT 1") suspend fun byRemote(r: String): DocTemplate?
    @Query("SELECT * FROM templates") suspend fun list(): List<DocTemplate>
}

@Database(
    entities = [Law::class, Article::class, MouAnalysis::class, ChatSession::class,
        ChatMessage::class, GeneratedDoc::class, Contract::class, DocTemplate::class],
    version = 2, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lawDao(): LawDao
    abstract fun mouDao(): MouDao
    abstract fun chatDao(): ChatDao
    abstract fun docDao(): DocDao
    abstract fun contractDao(): ContractDao
    abstract fun templateDao(): TemplateDao
}

object ServiceLocator {
    lateinit var db: AppDatabase
    lateinit var repo: Repo
    fun init(app: Application) {
        System.loadLibrary("sqlcipher")
        val factory = SupportOpenHelperFactory(DbKey.passphrase(app))
        db = Room.databaseBuilder(app, AppDatabase::class.java, "navigo.db")
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration().build()
        repo = Repo(db)
        CoroutineScope(Dispatchers.IO).launch { repo.seedIfEmpty() }
    }
}

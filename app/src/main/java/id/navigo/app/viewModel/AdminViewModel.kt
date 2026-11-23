package id.navigo.app.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import id.navigo.app.data.Article
import id.navigo.app.data.CloudSync
import id.navigo.app.data.DocTemplate
import id.navigo.app.data.Law
import id.navigo.app.data.ServiceLocator
import id.navigo.app.util.Dates
import id.navigo.app.util.Prefs
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

data class AdminUser(
    val id: String, val name: String, val email: String, val role: String,
    val status: String, val joined: String, val business: String = "-"
)

class AdminViewModel(private val app: Application) : AndroidViewModel(app) {
    private val db = ServiceLocator.db
    private val fb = runCatching { FirebaseApp.getApps(app).isNotEmpty() }.getOrDefault(false)

    val laws = db.lawDao().lawsFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val templates = db.templateDao().all().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val users = MutableStateFlow<List<AdminUser>>(emptyList())
    val usersSample = MutableStateFlow(false)
    val stats = MutableStateFlow(Triple(0, 0, 0))   // pengguna, analisis hari ini, kuota %

    fun refreshStats() = viewModelScope.launch {
        val start = LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        val today = db.mouDao().countSince(start)
        stats.value = Triple(users.value.size, today, Prefs.quotaUsed(app) * 100 / Prefs.DAILY_LIMIT)
    }

    // ---- UU & pasal (lokal; sinkron Firestore belum diimplementasikan) ----
    fun addLaw(title: String, number: String, topic: String, desc: String, firstArticleNo: String, firstArticle: String) = viewModelScope.launch {
        val law = Law(title = title, number = number, category = "UU", topic = topic, description = desc)
        val id = db.lawDao().insertLaw(law)
        if (firstArticle.isNotBlank()) db.lawDao().insertArticles(listOf(Article(lawId = id, number = firstArticleNo.ifBlank { "1" }, text = firstArticle)))
        runCatching { CloudSync.pushLaw(app, law.copy(id = id)) }
    }
    fun renameLaw(l: Law, title: String) = viewModelScope.launch {
        val u = l.copy(title = title); db.lawDao().updateLaw(u); runCatching { CloudSync.pushLaw(app, u) }
    }
    fun deleteLaw(l: Law) = viewModelScope.launch {
        runCatching { CloudSync.deleteLaw(app, l) }
        db.lawDao().deleteArticlesOf(l.id); db.lawDao().deleteLaw(l)
    }
    fun toggleTemplate(t: DocTemplate) = viewModelScope.launch {
        val u = t.copy(active = !t.active); db.templateDao().update(u); runCatching { CloudSync.pushTemplate(app, u) }
    }
    fun saveTemplate(t: DocTemplate, name: String, body: String, active: Boolean) = viewModelScope.launch {
        val u = t.copy(name = name, body = body, active = active, version = t.version + 1)
        db.templateDao().update(u); runCatching { CloudSync.pushTemplate(app, u) }
    }
    /** Sekali jalan: unggah seluruh data seed lokal ke cloud. */
    fun pushAll() = viewModelScope.launch {
        db.lawDao().laws().forEach { runCatching { CloudSync.pushLaw(app, it) } }
        db.templateDao().all().first().forEach { runCatching { CloudSync.pushTemplate(app, it) } }
    }

    // ---- Akun pengguna (Firestore koleksi "users"; contoh lokal bila Firebase belum aktif) ----
    fun loadUsers() = viewModelScope.launch {
        if (fb) {
            val r = runCatching { FirebaseFirestore.getInstance().collection("users").get().await() }.getOrNull()
            if (r != null) {
                users.value = r.documents.map {
                    AdminUser(it.id, it.getString("name") ?: "-", it.getString("email") ?: "-", it.getString("role") ?: "user",
                        it.getString("status") ?: "aktif",
                        it.getLong("createdAt")?.let { ms -> Dates.dateTime(ms) } ?: "-", it.getString("business") ?: "-")
                }
                usersSample.value = false; refreshStats(); return@launch
            }
        }
        usersSample.value = true
        users.value = listOf(
            AdminUser("1", "Budi", "budi@email.com", "user", "aktif", "-"),
            AdminUser("2", "Siti", "siti@email.com", "user", "aktif", "-"),
            AdminUser("3", "Andi", "andi@email.com", "user", "diblokir", "-"),
            AdminUser("4", "Adi", "admin@navigo.id", "admin", "aktif", "-")
        )
        refreshStats()
    }

    fun updateUser(u: AdminUser, role: String = u.role, status: String = u.status) = viewModelScope.launch {
        if (fb && !usersSample.value) runCatching {
            FirebaseFirestore.getInstance().collection("users").document(u.id).update(mapOf("role" to role, "status" to status)).await()
        }
        users.update { l -> l.map { if (it.id == u.id) it.copy(role = role, status = status) else it } }
    }
}

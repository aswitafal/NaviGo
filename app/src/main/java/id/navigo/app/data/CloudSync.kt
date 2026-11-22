package id.navigo.app.data

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object CloudSync {
    private val fs get() = FirebaseFirestore.getInstance()
    private fun ready(ctx: Context) = FirebaseApp.getApps(ctx).isNotEmpty()
    private val db get() = ServiceLocator.db

    suspend fun pushLaw(ctx: Context, law: Law) {
        if (!ready(ctx)) return
        val col = fs.collection("laws")
        val doc = if (law.remoteId.isBlank()) col.document() else col.document(law.remoteId)
        val arts = db.lawDao().articlesOf(law.id).map { mapOf("number" to it.number, "text" to it.text) }
        doc.set(mapOf("title" to law.title, "number" to law.number, "category" to law.category,
            "topic" to law.topic, "description" to law.description, "active" to law.active,
            "articles" to arts, "updatedAt" to System.currentTimeMillis())).await()
        if (law.remoteId.isBlank()) db.lawDao().updateLaw(law.copy(remoteId = doc.id))
    }

    suspend fun deleteLaw(ctx: Context, law: Law) {
        if (ready(ctx) && law.remoteId.isNotBlank()) fs.collection("laws").document(law.remoteId).delete().await()
    }

    suspend fun pushTemplate(ctx: Context, t: DocTemplate) {
        if (!ready(ctx)) return
        val col = fs.collection("templates")
        val doc = if (t.remoteId.isBlank()) col.document() else col.document(t.remoteId)
        doc.set(mapOf("name" to t.name, "description" to t.description, "body" to t.body, "fields" to t.fields,
            "version" to t.version, "active" to t.active, "updatedAt" to System.currentTimeMillis())).await()
        if (t.remoteId.isBlank()) db.templateDao().update(t.copy(remoteId = doc.id))
    }

    /** Tarik data cloud ke Room. Dicocokkan lewat remoteId, atau nomor UU / nama template agar tidak duplikat. */
    suspend fun pull(ctx: Context) {
        if (!ready(ctx)) return
        val ld = db.lawDao()
        val remoteIds = mutableSetOf<String>()
        for (d in fs.collection("laws").get().await().documents) {
            remoteIds += d.id
            val number = d.getString("number").orEmpty()
            val local = ld.byRemote(d.id) ?: ld.laws().firstOrNull { it.number == number }
            val base = Law(
                id = local?.id ?: 0, title = d.getString("title").orEmpty(), number = number,
                category = d.getString("category") ?: "UU", topic = d.getString("topic").orEmpty(),
                description = d.getString("description").orEmpty(), active = d.getBoolean("active") ?: true,
                aiSummary = local?.aiSummary, remoteId = d.id
            )
            val id = if (local == null) ld.insertLaw(base) else { ld.updateLaw(base); local.id }
            @Suppress("UNCHECKED_CAST")
            val arts = (d.get("articles") as? List<Map<String, Any?>>).orEmpty()
            ld.deleteArticlesOf(id)
            ld.insertArticles(arts.map { Article(lawId = id, number = it["number"] as? String ?: "", text = it["text"] as? String ?: "") })
        }
        // hapus UU lokal yang sudah dihapus admin di cloud
        ld.laws().filter { it.remoteId.isNotBlank() && it.remoteId !in remoteIds }
            .forEach { ld.deleteArticlesOf(it.id); ld.deleteLaw(it) }

        val td = db.templateDao()
        for (d in fs.collection("templates").get().await().documents) {
            val name = d.getString("name").orEmpty()
            val local = td.byRemote(d.id) ?: td.list().firstOrNull { it.name == name }
            val t = DocTemplate(
                id = local?.id ?: 0, name = name, description = d.getString("description").orEmpty(),
                body = d.getString("body").orEmpty(), fields = d.getString("fields").orEmpty(),
                version = (d.getLong("version") ?: 1L).toInt(), active = d.getBoolean("active") ?: true,
                usedCount = local?.usedCount ?: 0, remoteId = d.id
            )
            if (local == null) td.insert(t) else td.update(t)
        }
    }
}
package id.navigo.app.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL
import java.nio.charset.StandardCharsets

data class PasalIdPassage(val number: String, val url: String)

data class PasalIdLaw(
    val title: String,
    val citation: String,
    val status: String?,
    val statusUncertain: Boolean,
    val url: String,
    val passages: List<PasalIdPassage>
)

object PasalIdApi {
    private const val BASE_URL = "https://pasal.id/api/v1/search"

    suspend fun search(context: Context, query: String): List<PasalIdLaw> = withContext(Dispatchers.IO) {
        val token = PasalTokenStore.get(context)
            ?: throw IllegalStateException("Token Pasal.id belum diatur.")
        val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.name())
        val connection = (URL("$BASE_URL?q=$encodedQuery&limit=5").openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 15_000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("X-API-Key", token)
        }

        try {
            val statusCode = connection.responseCode
            if (statusCode != HttpURLConnection.HTTP_OK) {
                when (statusCode) {
                    HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw IOException("Token Pasal.id ditolak (401). Periksa token API Anda.")
                    429 ->
                        throw IOException("Batas pencarian Pasal.id tercapai (429). Coba beberapa saat lagi.")
                    else ->
                        throw IOException("Pencarian Pasal.id gagal (HTTP $statusCode).")
                }
            }

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            parseResults(JSONObject(response))
        } finally {
            connection.disconnect()
        }
    }

    private fun parseResults(root: JSONObject): List<PasalIdLaw> {
        val results = root.optJSONArray("results") ?: return emptyList()
        return (0 until results.length()).mapNotNull { index ->
            val item = results.optJSONObject(index) ?: return@mapNotNull null
            val work = item.optJSONObject("work") ?: return@mapNotNull null
            val title = work.optString("title").takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val type = work.optString("type").takeIf { it.isNotBlank() }.orEmpty()
            val number = work.optString("number").takeIf { it.isNotBlank() }.orEmpty()
            val year = work.optString("year").takeIf { it.isNotBlank() }.orEmpty()
            val citation = listOf(type, "$number/$year").filter { it.isNotBlank() }.joinToString(" ")
            val bestPassage = item.optJSONObject("best_passage")
            val passages = buildList {
                bestPassage?.let { add(it) }
                val matching = item.optJSONArray("matching_pasals")
                if (matching != null) {
                    for (i in 0 until matching.length()) matching.optJSONObject(i)?.let(::add)
                }
            }.mapNotNull { passage ->
                val passageUrl = safePasalUrl(passage.optString("url")) ?: return@mapNotNull null
                val pasal = passage.optString("pasal").takeIf { it.isNotBlank() } ?: return@mapNotNull null
                PasalIdPassage(pasal, passageUrl)
            }.distinctBy { it.url }

            PasalIdLaw(
                title = title,
                citation = citation,
                status = work.optString("status").takeIf { it.isNotBlank() },
                statusUncertain = item.optBoolean("status_uncertain"),
                url = safePasalUrl(item.optString("url")).orEmpty(),
                passages = passages
            )
        }
    }

    private fun safePasalUrl(raw: String): String? =
        raw.takeIf { it.startsWith("https://pasal.id/") }
}

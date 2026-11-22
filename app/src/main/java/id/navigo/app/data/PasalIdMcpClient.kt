package id.navigo.app.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class PasalIdMcpSources(
    val context: String,
    val citations: List<String>
)

object PasalIdMcpClient {
    private const val ENDPOINT = "https://mcp.pasal.id/mcp"
    private const val SEARCH_CONTEXT_CHARS = 1_800
    private const val LAW_CONTEXT_CHARS = 900
    private const val LAW_TEXT_CHARS = 2_500
    private const val REQUEST_BUDGET_MS = 25_000L
    private val articlePattern = Regex("(?i)\\bpasal\\s+(\\d+[a-z]?(?:\\s*[-–]\\s*\\d+[a-z]?)?)")

    suspend fun retrieve(context: Context, query: String): PasalIdMcpSources? = withContext(Dispatchers.IO) {
        val token = PasalTokenStore.get(context)
            ?: throw IllegalStateException("Token Pasal.id belum diatur.")
        val session = McpSession(token, System.nanoTime() + REQUEST_BUDGET_MS * 1_000_000)
        val initialize = session.post(
            "initialize",
            JSONObject()
                .put("protocolVersion", "2025-03-26")
                .put("capabilities", JSONObject())
                .put("clientInfo", JSONObject().put("name", "NaviGo").put("version", "1.0"))
        )
        initialize?.optJSONObject("error")?.let { throw IOException(it.optString("message", "Inisialisasi Pasal.id gagal.")) }
        session.markInitialized()
        session.post("notifications/initialized", JSONObject(), expectResponse = false)

        val search = session.callTool("search_legal", JSONObject().put("query", query).put("limit", 3))
        val searchText = toolText(search)
        val searchData = toolData(search)
        val explicitSelector = articlePattern.find(query)?.groupValues?.getOrNull(1)?.let { "pasal $it" }
        var target = findTarget(searchData, explicitSelector)

        if (target == null && explicitSelector != null) {
            val resolved = session.callTool("resolve_law", JSONObject().put("reference", query))
            target = findLaw(resolved)?.copy(selector = explicitSelector)
        }

        val parts = mutableListOf<String>()
        if (searchText.isNotBlank()) parts += "HASIL PENCARIAN PASAL.ID:\n${searchText.take(SEARCH_CONTEXT_CHARS)}"
        val citations = findCitations(searchData).toMutableList()

        val selectedTarget = target
        val selector = selectedTarget?.selector
        if (selectedTarget != null && selector != null) {
            val lawContext = session.callTool(
                "get_law_context",
                JSONObject().put("law", selectedTarget.law).put("detail", "summary")
            )
            val read = session.callTool(
                "read_law",
                JSONObject()
                    .put("law", selectedTarget.law)
                    .put("selector", selector)
                    .put("max_chars", LAW_TEXT_CHARS)
                    .put("versi", if (hasCurrentConsolidation(lawContext)) "terkini" else "asli")
            )
            val lawContextText = toolText(lawContext).take(LAW_CONTEXT_CHARS)
            val lawText = toolText(read).take(LAW_TEXT_CHARS)
            if (lawContextText.isNotBlank()) parts += "STATUS PERATURAN PASAL.ID:\n$lawContextText"
            if (lawText.isNotBlank()) parts += "TEKS PERATURAN PASAL.ID:\n$lawText"
            citations += "${selector.replaceFirstChar { it.uppercase() }} ${selectedTarget.citation}"
        }

        if (parts.isEmpty()) return@withContext null
        PasalIdMcpSources(
            context = parts.joinToString("\n\n").take(SEARCH_CONTEXT_CHARS + LAW_CONTEXT_CHARS + LAW_TEXT_CHARS + 160),
            citations = citations.distinct()
        )
    }

    private data class LawTarget(val law: String, val citation: String, val selector: String?)

    private class McpSession(private val token: String, private val deadlineNanos: Long) {
        private var sessionId: String? = null
        private var requestId = 0
        private var initialized = false

        fun markInitialized() {
            initialized = true
        }

        fun callTool(name: String, arguments: JSONObject): JSONObject {
            val result = post(
                "tools/call",
                JSONObject().put("name", name).put("arguments", arguments)
            )?.optJSONObject("result") ?: throw IOException("Respons tool Pasal.id kosong.")
            if (result.optBoolean("isError")) {
                throw IOException(toolText(result).ifBlank { "Tool Pasal.id gagal dijalankan." })
            }
            return result
        }

        fun post(method: String, params: JSONObject, expectResponse: Boolean = true): JSONObject? {
            val payload = JSONObject()
                .put("jsonrpc", "2.0")
                .put("method", method)
                .put("params", params)
            if (expectResponse) payload.put("id", ++requestId)

            val connection = (URL(ENDPOINT).openConnection() as HttpURLConnection).apply {
                val remainingMs = ((deadlineNanos - System.nanoTime()) / 1_000_000).toInt()
                    .takeIf { it > 0 }
                    ?: throw IOException("Pencarian Pasal.id melewati batas waktu.")
                requestMethod = "POST"
                connectTimeout = remainingMs.coerceAtMost(8_000)
                readTimeout = remainingMs.coerceAtMost(12_000)
                instanceFollowRedirects = false
                doOutput = true
                setRequestProperty("Authorization", "Bearer $token")
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json, text/event-stream")
                sessionId?.let { setRequestProperty("Mcp-Session-Id", it) }
                if (initialized) {
                    setRequestProperty("Mcp-Protocol-Version", "2025-03-26")
                }
            }

            try {
                connection.outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(payload.toString()) }
                val status = connection.responseCode
                connection.getHeaderField("Mcp-Session-Id")?.let { sessionId = it }
                val stream = if (status in 200..299) connection.inputStream else connection.errorStream
                val body = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
                if (status !in 200..299) {
                    val message = when (status) {
                        HttpURLConnection.HTTP_UNAUTHORIZED -> "Token Pasal.id ditolak (401). Masukkan token akses yang valid."
                        429 -> "Batas permintaan Pasal.id tercapai (429)."
                        else -> "Permintaan Pasal.id gagal (HTTP $status)."
                    }
                    throw IOException(message)
                }
                if (!expectResponse || body.isBlank()) return null
                val response = parseResponse(body, connection.contentType.orEmpty())
                response.optJSONObject("error")?.let {
                    throw IOException(it.optString("message").ifBlank { "Respons MCP Pasal.id gagal." })
                }
                return response
            } finally {
                connection.disconnect()
            }
        }
    }

    private fun parseResponse(body: String, contentType: String): JSONObject {
        try {
            if (!contentType.contains("text/event-stream", ignoreCase = true)) return JSONObject(body)
            val data = body.lineSequence()
                .filter { it.startsWith("data:") }
                .map { it.removePrefix("data:").trim() }
                .lastOrNull { it.isNotBlank() }
                ?: throw IOException("Respons MCP Pasal.id tidak berisi data.")
            return JSONObject(data)
        } catch (e: org.json.JSONException) {
            throw IOException("Respons MCP Pasal.id tidak valid.", e)
        }
    }

    private fun toolText(result: JSONObject): String {
        result.optJSONObject("structuredContent")?.let { return it.toString() }
        val content = result.optJSONArray("content") ?: return result.toString()
        return (0 until content.length()).mapNotNull { index ->
            content.optJSONObject(index)?.optString("text")?.takeIf { it.isNotBlank() }
        }.joinToString("\n")
    }

    private fun toolData(result: JSONObject): JSONObject {
        result.optJSONObject("structuredContent")?.let { return it }
        val text = toolText(result).trim()
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start >= 0 && end > start) {
            runCatching { JSONObject(text.substring(start, end + 1)) }.getOrNull()?.let { return it }
        }
        return result
    }

    private fun findTarget(root: JSONObject, fallbackSelector: String?): LawTarget? =
        findTargetIn(root, fallbackSelector)

    private fun findTargetIn(value: Any?, fallbackSelector: String?): LawTarget? {
        when (value) {
            is JSONObject -> {
                val lawObject = value.optJSONObject("law")
                    ?: value.optJSONObject("work")
                    ?: value.optJSONObject("regulation")
                val lawValue = value.opt("law_id")
                    ?: lawObject?.opt("law_id")
                    ?: lawObject?.opt("id")
                if (lawValue != null && lawValue != JSONObject.NULL) {
                    val selector = findArticleSelector(value) ?: fallbackSelector
                    if (selector != null) {
                        val citation = lawObject?.optString("citation").orEmpty()
                            .ifBlank { value.optString("citation") }
                            .ifBlank { lawObject?.optString("title").orEmpty() }
                            .ifBlank { value.optString("title") }
                            .ifBlank { "Peraturan Pasal.id" }
                        return LawTarget(lawValue.toString(), citation, selector)
                    }
                }
                val keys = value.keys()
                while (keys.hasNext()) {
                    findTargetIn(value.opt(keys.next()), fallbackSelector)?.let { return it }
                }
            }
            is JSONArray -> {
                for (index in 0 until value.length()) {
                    findTargetIn(value.opt(index), fallbackSelector)?.let { return it }
                }
            }
        }
        return null
    }

    private fun findLaw(result: JSONObject): LawTarget? {
        val data = toolData(result)
        return findLawIn(data)
    }

    private fun findLawIn(value: Any?): LawTarget? {
        when (value) {
            is JSONObject -> {
                val lawObject = value.optJSONObject("law")
                    ?: value.optJSONObject("work")
                    ?: value.optJSONObject("regulation")
                val lawValue = value.opt("law_id")
                    ?: lawObject?.opt("law_id")
                    ?: lawObject?.opt("id")
                if (lawValue != null && lawValue != JSONObject.NULL) {
                    val citation = lawObject?.optString("citation").orEmpty()
                        .ifBlank { value.optString("citation") }
                        .ifBlank { lawObject?.optString("title").orEmpty() }
                        .ifBlank { value.optString("title") }
                        .ifBlank { "Peraturan Pasal.id" }
                    return LawTarget(lawValue.toString(), citation, null)
                }
                val keys = value.keys()
                while (keys.hasNext()) findLawIn(value.opt(keys.next()))?.let { return it }
            }
            is JSONArray -> {
                for (index in 0 until value.length()) findLawIn(value.opt(index))?.let { return it }
            }
        }
        return null
    }

    private fun findCitations(root: JSONObject): List<String> {
        val citations = mutableListOf<String>()
        fun visit(value: Any?) {
            when (value) {
                is JSONObject -> {
                    val lawObject = value.optJSONObject("law")
                        ?: value.optJSONObject("work")
                        ?: value.optJSONObject("regulation")
                    val lawValue = value.opt("law_id")
                        ?: lawObject?.opt("law_id")
                        ?: lawObject?.opt("id")
                    if (lawValue != null && lawValue != JSONObject.NULL) {
                        val citation = lawObject?.optString("citation").orEmpty()
                            .ifBlank { value.optString("citation") }
                            .ifBlank { lawObject?.optString("title").orEmpty() }
                            .ifBlank { value.optString("title") }
                        if (citation.isNotBlank()) {
                            val article = findArticleSelector(value)
                            citations += if (article == null) citation
                            else "${article.replaceFirstChar { it.uppercase() }} $citation"
                        }
                    }
                    val keys = value.keys()
                    while (keys.hasNext()) visit(value.opt(keys.next()))
                }
                is JSONArray -> {
                    for (index in 0 until value.length()) visit(value.opt(index))
                }
            }
        }
        visit(root)
        return citations.distinct()
    }

    private fun findArticleSelector(value: Any?): String? {
        when (value) {
            is JSONObject -> {
                val keys = value.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val item = value.opt(key)
                    if (key.equals("pasal", true) || key.equals("pasal_number", true) ||
                        key.equals("article", true) || key.equals("article_number", true)
                    ) {
                        val raw = item?.toString().orEmpty()
                        articlePattern.find(raw)?.groupValues?.getOrNull(1)?.let { return "pasal $it" }
                        raw.takeIf { it.matches(Regex("\\d+[a-z]?", RegexOption.IGNORE_CASE)) }
                            ?.let { return "pasal $it" }
                    }
                    findArticleSelector(item)?.let { return it }
                }
            }
            is JSONArray -> {
                for (index in 0 until value.length()) findArticleSelector(value.opt(index))?.let { return it }
            }
        }
        return null
    }

    private fun hasCurrentConsolidation(value: Any?): Boolean {
        when (value) {
            is JSONObject -> {
                val consolidation = value.optJSONObject("consolidation")
                if (consolidation?.optBoolean("available") == true) return true
                val keys = value.keys()
                while (keys.hasNext()) if (hasCurrentConsolidation(value.opt(keys.next()))) return true
            }
            is JSONArray -> {
                for (index in 0 until value.length()) if (hasCurrentConsolidation(value.opt(index))) return true
            }
        }
        return false
    }
}

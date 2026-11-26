package id.navigo.app.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

object AiClient {
    private const val MODEL = "gemini-2.0-flash"
    private const val REQUEST_TIMEOUT_MS = 60_000L
    private const val MAX_OUTPUT_TOKENS = 1024
    const val MAX_DOCUMENT_CHARS = 6_000
    const val MAX_DATABASE_CONTEXT_CHARS = 3_000
    const val MAX_HISTORY_CHARS = 1_200
    const val MAX_QUESTION_CHARS = 1_000
    private val cache = lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = MODEL,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                maxOutputTokens = MAX_OUTPUT_TOKENS
            }
        )
    }

    /** Error terakhir, untuk diagnosis. */
    @Volatile var lastError: String? = null

    fun troubleshootingHint(): String {
        val error = lastError.orEmpty().lowercase()
        val hint = when {
            error.contains("app check") || error.contains("appcheck") ||
                error.contains("attestation") || error.contains("403") ||
                error.contains("permission_denied") || error.contains("unauthenticated") ->
                "Periksa App Check: pada build debug, daftarkan debug token dari Logcat di Firebase Console > App Check > Apps."
            error.contains("not found") || error.contains("404") || error.contains("model") ->
                "Pastikan Firebase AI Logic aktif dan model $MODEL tersedia untuk project ini."
            error.contains("429") || error.contains("quota") || error.contains("resource_exhausted") ->
                "Batas permintaan AI tercapai. Periksa kuota Firebase AI Logic dan coba lagi nanti."
            else ->
                "Periksa koneksi internet, Firebase AI Logic, dan konfigurasi App Check di Firebase Console."
        }
        val detail = lastError?.takeIf { it.isNotBlank() }?.take(240)
        return if (detail == null) hint else "$hint\nDetail: $detail"
    }

    suspend fun json(prompt: String, timeoutMs: Long = REQUEST_TIMEOUT_MS): JSONObject? = withContext(Dispatchers.IO) {
        try {
            val raw = withTimeout(timeoutMs) { cache.value.generateContent(prompt).text }
            if (raw.isNullOrBlank()) {
                lastError = "$MODEL returned an empty response"
                Log.e("NaviGoAI", lastError!!)
                return@withContext null
            }
            val obj = runCatching {
                val clean = raw.trim()
                val start = clean.indexOf('{')
                val end = clean.lastIndexOf('}')
                if (start >= 0 && end > start) {
                    JSONObject(clean.substring(start, end + 1))
                } else {
                    JSONObject(clean.removePrefix("```json").removePrefix("```JSON").removePrefix("```").removeSuffix("```").trim())
                }
            }.getOrNull()
            if (obj == null) {
                lastError = "$MODEL returned invalid JSON"
                Log.e("NaviGoAI", lastError!!)
            } else {
                lastError = null
            }
            obj
        } catch (e: Exception) {
            if (e is CancellationException && e !is TimeoutCancellationException) throw e
            lastError = "${e.javaClass.simpleName}: ${e.message}"
            Log.e("NaviGoAI", "Firebase AI request failed for $MODEL", e)
            null
        }
    }
}
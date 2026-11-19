package id.navigo.app.ai

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

/**
 * Gemini (seri flash) lewat Firebase AI Logic SDK. Output dipaksa JSON.
 * Mengembalikan null bila Firebase belum dikonfigurasi, offline, timeout, atau JSON tidak valid.
 * Cek nama model terbaru di dokumentasi Firebase AI Logic bila model di bawah sudah usang.
 */
object AiClient {
    const val MODEL = "gemini-2.5-flash"

    private val model by lazy {
        runCatching {
            Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
                modelName = MODEL,
                generationConfig = generationConfig { responseMimeType = "application/json" }
            )
        }.getOrNull()
    }

    suspend fun json(prompt: String, timeoutMs: Long = 25_000): JSONObject? = withContext(Dispatchers.IO) {
        val m = model ?: return@withContext null
        val raw = runCatching { withTimeout(timeoutMs) { m.generateContent(prompt).text } }.getOrNull()
            ?: return@withContext null
        runCatching {
            JSONObject(raw.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim())
        }.getOrNull()
    }
}

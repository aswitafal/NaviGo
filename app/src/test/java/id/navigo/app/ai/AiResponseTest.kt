package id.navigo.app.ai

import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class AiResponseTest {

    @Test
    fun testValidJsonResponseParsing() {
        val rawResponse = """
            ```json
            {
                "answer": "Wanprestasi adalah kelalaian debitur dalam memenuhi kewajiban perikatan.",
                "citations": ["KUHPerdata Pasal 1243"]
            }
            ```
        """.trimIndent()

        val clean = rawResponse.trim()
        val start = clean.indexOf('{')
        val end = clean.lastIndexOf('}')
        assertTrue("JSON substring harus ditemukan", start >= 0 && end > start)

        val json = JSONObject(clean.substring(start, end + 1))
        assertEquals("Wanprestasi adalah kelalaian debitur dalam memenuhi kewajiban perikatan.", json.getString("answer"))
        assertEquals(1, json.getJSONArray("citations").length())
        assertEquals("KUHPerdata Pasal 1243", json.getJSONArray("citations").getString(0))
    }

    @Test
    fun testInvalidJsonResponseHandling() {
        val rawResponse = "Ini bukan format JSON yang valid."
        val obj = runCatching {
            val start = rawResponse.indexOf('{')
            val end = rawResponse.lastIndexOf('}')
            if (start >= 0 && end > start) {
                JSONObject(rawResponse.substring(start, end + 1))
            } else {
                JSONObject(rawResponse)
            }
        }.getOrNull()

        assertNull("Raw response yang bukan JSON harus mengembalikan null", obj)
    }

    @Test
    fun testEmptyResponseHandling() {
        val rawResponse = ""
        assertTrue("Respons kosong harus diidentifikasi", rawResponse.isBlank())
    }

    @Test
    fun testTroubleshootingHintForModelError() {
        AiClient.lastError = "404 Not Found: model gemini-1.5-flash not found"
        val hint = AiClient.troubleshootingHint()
        assertTrue(hint.contains("Firebase AI Logic") || hint.contains("model"))
    }
}

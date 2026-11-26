package id.navigo.app.viewModel

import id.navigo.app.util.Guardrail
import org.junit.Assert.*
import org.junit.Test

class ChatViewModelTest {

    @Test
    fun testGuardrailRejectionNotice() {
        val invalidQuestion = "Bagaimana cara membuat nasi goreng?"
        val notice = Guardrail.validateQuestion(invalidQuestion, hasContext = false)
        assertNotNull("Pertanyaan di luar scope harus menghasilkan notice", notice)
    }

    @Test
    fun testCitationFilteringLogic() {
        val allowedCitations = listOf("KUHPerdata Pasal 1243", "UU No 28 Th 2014 Pasal 113")
        val responseCitations = listOf("KUHPerdata Pasal 1243", "UU Khayalan Pasal 999")

        val usedCitations = responseCitations.mapNotNull { candidate ->
            allowedCitations.firstOrNull { it.equals(candidate, ignoreCase = true) || candidate.contains(it, ignoreCase = true) }
        }.distinct()

        assertEquals("Hanya sitasi sah yang boleh digunakan", 1, usedCitations.size)
        assertEquals("KUHPerdata Pasal 1243", usedCitations[0])
        assertFalse("Sitasi tidak dikenal tidak boleh dimasukkan", usedCitations.contains("UU Khayalan Pasal 999"))
    }

    @Test
    fun testFallbackCitationMessage() {
        val hits = listOf("KUHPerdata Pasal 1243")
        val cite = hits.joinToString(", ").ifBlank { null }
        assertEquals("KUHPerdata Pasal 1243", cite)
    }
}

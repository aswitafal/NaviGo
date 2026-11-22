package id.navigo.app.vm

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.navigo.app.ai.AiClient
import id.navigo.app.data.MouAnalysis
import id.navigo.app.data.ServiceLocator
import id.navigo.app.domain.AnalysisResult
import id.navigo.app.domain.Extractor
import id.navigo.app.domain.MouEngine
import id.navigo.app.domain.Risk
import id.navigo.app.util.Anonymizer
import id.navigo.app.util.Guardrail
import id.navigo.app.util.Net
import id.navigo.app.util.Prefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MouState(
    val title: String = "", val source: String = "", val text: String = "",
    val focus: Set<String> = emptySet(), val status: String = "Status: menunggu dokumen",
    val loading: Boolean = false, val error: String? = null, val result: AnalysisResult? = null
)

class MouViewModel(private val app: Application) : AndroidViewModel(app) {
    private val dao = ServiceLocator.db.mouDao()
    val state = MutableStateFlow(MouState())

    fun toggleFocus(f: String) = state.update { it.copy(focus = if (f in it.focus) it.focus - f else it.focus + f) }

    fun setText(t: String) = state.update {
        it.copy(text = t, title = t.lines().firstOrNull { l -> l.isNotBlank() }?.take(40) ?: "Teks dokumen",
            source = "Teks", error = null, status = "Status: teks siap (${t.length} karakter)")
    }

    fun loadUri(uri: Uri, isPdf: Boolean) = viewModelScope.launch {
        state.update { it.copy(loading = true, error = null, status = "Status: membaca dokumen…") }
        try {
            val (name, size) = Extractor.nameAndSize(app, uri)
            if (size > Extractor.MAX_BYTES) throw IllegalArgumentException("Ukuran file melebihi 10 MB.")
            val text = if (isPdf) Extractor.fromPdf(app, uri) else Extractor.fromImage(app, uri)
            finishExtract(name, if (isPdf) "PDF" else "Foto", text)
        } catch (e: Exception) {
            state.update { it.copy(loading = false, error = e.message ?: "Gagal membaca file (format/OCR).", status = "Status: gagal membaca") }
        }
    }

    fun loadBitmap(b: Bitmap) = viewModelScope.launch {
        state.update { it.copy(loading = true, error = null, status = "Status: membaca foto…") }
        try { finishExtract("Foto kamera", "Foto", Extractor.fromBitmap(b)) }
        catch (e: Exception) { state.update { it.copy(loading = false, error = "OCR gagal: ${e.message}") } }
    }

    private fun finishExtract(name: String, source: String, text: String) {
        state.update {
            if (text.trim().length < 100)
                it.copy(loading = false, text = "", error = "Teks terbaca kurang dari 100 karakter. Coba foto lebih jelas atau tempel teks manual.", status = "Status: teks tidak terbaca")
            else it.copy(loading = false, text = text, title = name.removeSuffix(".pdf"), source = source, status = "Status: ${text.length} karakter terbaca")
        }
    }

    /** Alur: validasi -> pre-screening lokal -> Gemini -> skor berbobot -> simpan. */
    fun analyze(onDone: () -> Unit) = viewModelScope.launch {
        val s = state.value
        if (s.text.trim().length < 100) { state.update { it.copy(error = "Pilih dokumen atau tempel teks (min. 100 karakter).") }; return@launch }
        if (!Net.online(app)) { state.update { it.copy(error = "Tidak ada koneksi internet. Analisis AI membutuhkan jaringan.") }; return@launch }
        if (!Prefs.consumeQuota(app)) { state.update { it.copy(error = "Kuota AI harian habis. Coba lagi besok.") }; return@launch }
        state.update { it.copy(loading = true, error = null, status = "Status: menganalisis…") }

        val text = s.text.take(AiClient.MAX_DOCUMENT_CHARS)
        val partial = s.text.length > text.length
        val anon = Anonymizer()
        val prompt = MouEngine.buildPrompt(anon.mask(text), s.focus)
        val json = AiClient.json(prompt)

        val limited = json == null || json!!.optString("summary").isBlank()
        val aiRisks = if (limited) emptyList() else MouEngine.parseRisks(json!!).map {
            it.copy(title = anon.restore(it.title), suggestion = anon.restore(it.suggestion))
        }
        val summary = when {
            limited -> "Mode terbatas: layanan AI tidak tersedia, hanya checklist klausul lokal yang ditampilkan.\n\n${AiClient.troubleshootingHint()}"
            partial -> "Catatan: dokumen melebihi ${AiClient.MAX_DOCUMENT_CHARS} karakter; AI hanya menganalisis bagian awal.\n\n${anon.restore(json!!.optString("summary"))}"
            else -> anon.restore(json!!.optString("summary"))
        }
        val risks = (aiRisks + MouEngine.missingAsRisks(text)).sortedBy { listOf("tinggi", "sedang", "rendah").indexOf(it.level.lowercase()) }
        val score = MouEngine.score(text, aiRisks)
        val level = MouEngine.classify(score)
        val end = MouEngine.extractEndDay(text)
        val now = System.currentTimeMillis()
        val id = dao.insert(MouAnalysis(title = s.title.ifBlank { "Dokumen" }, createdAt = now, source = s.source, score = score,
            level = level, summary = summary, risksJson = MouEngine.risksToJson(risks), limited = limited, endDay = end))
        val r = AnalysisResult(id, s.title, summary, risks, score, level, limited, end, now, s.source)
        state.update { it.copy(loading = false, result = r, status = "Status: selesai") }
        onDone()
    }

    fun openSaved(a: MouAnalysis) {
        state.update { it.copy(result = AnalysisResult(a.id, a.title, a.summary, MouEngine.risksFromJson(a.risksJson), a.score, a.level, a.limited, a.endDay, a.createdAt, a.source)) }
    }
}

package id.navigo.app.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.navigo.app.ai.AiClient
import id.navigo.app.data.MouAnalysis
import id.navigo.app.data.ServiceLocator
import id.navigo.app.domain.AnalysisResult
import id.navigo.app.domain.Extractor
import id.navigo.app.domain.MouEngine
import id.navigo.app.util.Anonymizer
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
        val clean = t.trim()
        it.copy(
            text = clean,
            title = clean.lines().firstOrNull { l -> l.isNotBlank() }?.trim()?.take(40) ?: "Teks dokumen",
            source = "Teks", error = null, result = null,
            status = if (clean.length < MouEngine.MIN_CHARS) "Status: teks terlalu pendek (${clean.length}/${MouEngine.MIN_CHARS} karakter)"
            else "Status: teks siap (${clean.length} karakter)"
        )
    }

    fun loadUri(uri: Uri, isPdf: Boolean, displayName: String? = null) = viewModelScope.launch {
        state.update { it.copy(loading = true, error = null, status = "Status: membaca dokumen…") }
        try {
            val mime = app.contentResolver.getType(uri).orEmpty()
            val formatOk = if (isPdf) mime == "application/pdf" else mime.startsWith("image/")
            if (mime.isNotEmpty() && !formatOk) {
                throw IllegalArgumentException(if (isPdf) "Format tidak didukung. Pilih file PDF." else "Format tidak didukung. Pilih file gambar (JPG/PNG).")
            }
            val (name, size) = Extractor.nameAndSize(app, uri)
            if (size > Extractor.MAX_BYTES) throw IllegalArgumentException("Ukuran file melebihi 10 MB.")
            val text = if (isPdf) Extractor.fromPdf(app, uri) else Extractor.fromImage(app, uri)
            finishExtract(displayName ?: name, if (isPdf) "PDF" else "Foto", text)
        } catch (e: Exception) {
            state.update { it.copy(loading = false, error = e.message ?: "Gagal membaca file (format/OCR).", status = "Status: gagal membaca") }
        }
    }

    private fun finishExtract(name: String, source: String, text: String) {
        val clean = text.trim()
        state.update {
            if (clean.length < MouEngine.MIN_CHARS)
                it.copy(loading = false, text = "", result = null,
                    error = "Teks terbaca kurang dari ${MouEngine.MIN_CHARS} karakter. Coba foto lebih jelas atau tempel teks manual.",
                    status = "Status: teks tidak terbaca")
            else it.copy(loading = false, text = clean, result = null, error = null,
                title = name.replace(Regex("(?i)\\.pdf$"), ""), source = source,
                status = "Status: ${clean.length} karakter terbaca")
        }
    }

    fun analyze(onDone: () -> Unit) = viewModelScope.launch {
        val s = state.value
        if (s.loading) return@launch
        if (s.text.trim().length < MouEngine.MIN_CHARS) {
            state.update { it.copy(error = "Pilih dokumen atau tempel teks (min. ${MouEngine.MIN_CHARS} karakter).") }; return@launch
        }
        if (!Net.online(app)) { state.update { it.copy(error = "Tidak ada koneksi internet. Analisis AI membutuhkan jaringan.") }; return@launch }
        if (!Prefs.consumeQuota(app)) { state.update { it.copy(error = "Kuota AI harian habis. Coba lagi besok.") }; return@launch }
        state.update { it.copy(loading = true, error = null, status = "Status: menganalisis…") }

        try {
            val full = s.text
            val aiText = full.take(AiClient.MAX_DOCUMENT_CHARS)
            val partial = full.length > aiText.length
            val anon = Anonymizer()
            val json = AiClient.json(MouEngine.buildPrompt(anon.mask(aiText), s.focus))
            val ai = json?.takeIf { it.optString("summary").isNotBlank() }
            val limited = ai == null

            val aiRisks = ai?.let { j ->
                MouEngine.parseRisks(j).map {
                    it.copy(title = anon.restore(it.title), suggestion = anon.restore(it.suggestion), basis = anon.restore(it.basis))
                }
            }.orEmpty()
            val summary = when {
                ai == null -> "Mode terbatas: layanan AI tidak tersedia, hanya checklist klausul lokal yang ditampilkan.\n\n${AiClient.troubleshootingHint()}"
                partial -> "Catatan: dokumen melebihi ${AiClient.MAX_DOCUMENT_CHARS} karakter; AI hanya menganalisis bagian awal.\n\n${anon.restore(ai?.optString("summary").orEmpty())}"
                else -> anon.restore(ai?.optString("summary").orEmpty())
            }

            val risks = (aiRisks + MouEngine.missingAsRisks(full)).sortedBy { MouEngine.levelOrder(it.level) }
            val score = MouEngine.score(full, aiRisks)
            val level = MouEngine.classify(score)
            val end = MouEngine.extractEndDay(full)
            val now = System.currentTimeMillis()
            val title = s.title.ifBlank { "Dokumen" }

            val id = dao.insert(MouAnalysis(title = title, createdAt = now, source = s.source, score = score,
                level = level, summary = summary, risksJson = MouEngine.risksToJson(risks), limited = limited, endDay = end))
            val r = AnalysisResult(id, title, summary, risks, score, level, limited, end, now, s.source)
            state.update { it.copy(loading = false, result = r, status = "Status: selesai") }
            onDone()
        } catch (e: Exception) {
            state.update { it.copy(loading = false, error = "Analisis gagal: ${e.message ?: "kesalahan tidak diketahui"}. Coba lagi.", status = "Status: gagal") }
        }
    }

    fun openSaved(a: MouAnalysis) {
        state.update {
            it.copy(result = AnalysisResult(a.id, a.title, a.summary, MouEngine.risksFromJson(a.risksJson),
                a.score, a.level, a.limited, a.endDay, a.createdAt, a.source))
        }
    }
}
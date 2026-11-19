package id.navigo.app.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.navigo.app.ai.AiClient
import id.navigo.app.data.DocTemplate
import id.navigo.app.data.GeneratedDoc
import id.navigo.app.data.ServiceLocator
import id.navigo.app.data.formFields
import id.navigo.app.domain.DocEngine
import id.navigo.app.domain.PdfWriter
import id.navigo.app.util.Anonymizer
import id.navigo.app.util.Net
import id.navigo.app.util.Prefs
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DocState(
    val template: DocTemplate? = null, val values: Map<String, String> = emptyMap(),
    val errors: Map<String, String> = emptyMap(), val useAi: Boolean = false,
    val busy: Boolean = false, val message: String? = null, val ready: GeneratedDoc? = null
)

class DocViewModel(private val app: Application) : AndroidViewModel(app) {
    private val tdao = ServiceLocator.db.templateDao()
    private val ddao = ServiceLocator.db.docDao()
    val templates = tdao.all().map { l -> l.filter { it.active } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val state = MutableStateFlow(DocState())

    fun select(t: DocTemplate) { state.value = DocState(template = t, values = mapOf("klausul_tambahan" to DocEngine.DEFAULT_EXTRA)) }
    fun setValue(k: String, v: String) = state.update { it.copy(values = it.values + (k to v), errors = it.errors - k) }
    fun setAi(b: Boolean) = state.update { it.copy(useAi = b) }

    /** Validasi form; bila saran AI aktif dan online, minta klausul tambahan (timeout 15 dtk), jika gagal pakai klausul standar. */
    fun prepare(onReady: () -> Unit) = viewModelScope.launch {
        val s = state.value; val t = s.template ?: return@launch
        val errs = mutableMapOf<String, String>()
        for (f in t.formFields()) {
            val v = s.values[f.key].orEmpty().trim()
            if (f.required && v.isEmpty()) errs[f.key] = "Wajib diisi"
            else if (f.type == "NUMBER" && v.isNotEmpty() && v.filter { it.isDigit() }.isEmpty()) errs[f.key] = "Isi dengan angka"
        }
        if (errs.isNotEmpty()) { state.update { it.copy(errors = errs) }; return@launch }
        var extra = DocEngine.DEFAULT_EXTRA
        var msg: String? = null
        if (s.useAi) {
            if (!Net.online(app) || !Prefs.consumeQuota(app)) msg = "Saran AI tidak tersedia, memakai klausul standar."
            else {
                state.update { it.copy(busy = true) }
                val anon = Anonymizer()
                val masked = s.values.mapValues { anon.mask(it.value) }
                val r = AiClient.json(DocEngine.suggestPrompt(t.name, masked), 15_000)?.optString("klausul")
                if (r.isNullOrBlank()) msg = "Saran AI gagal, memakai klausul standar." else extra = anon.restore(r)
            }
        }
        state.update { it.copy(busy = false, errors = emptyMap(), message = msg, values = it.values + ("klausul_tambahan" to extra)) }
        onReady()
    }

    fun previewText(): String {
        val s = state.value
        return DocEngine.fill(s.template?.body.orEmpty(), s.values)
    }

    fun generate(onDone: () -> Unit) = viewModelScope.launch {
        val s = state.value; val t = s.template ?: return@launch
        state.update { it.copy(busy = true, message = null) }
        try {
            val party = s.values["nama_pihak_1"].orEmpty().ifBlank { "Dokumen" }
            val safe = (t.name.take(20) + "_" + party.take(20)).replace(Regex("[^A-Za-z0-9]+"), "_").trim('_')
            val (file, pages) = PdfWriter.write(app, previewText(), "$safe.pdf")
            val doc = GeneratedDoc(fileName = file.name, templateName = t.name, path = file.absolutePath,
                sizeKb = (file.length() / 1024).toInt().coerceAtLeast(1), pages = pages,
                createdAt = System.currentTimeMillis(), partyName = s.values["nama_pihak_2"].orEmpty())
            val id = ddao.insert(doc)
            tdao.update(t.copy(usedCount = t.usedCount + 1))
            state.update { it.copy(busy = false, ready = doc.copy(id = id)) }
            onDone()
        } catch (e: Exception) {
            state.update { it.copy(busy = false, message = "Gagal membuat PDF: ${e.message}. Periksa ruang penyimpanan lalu coba lagi.") }
        }
    }

    fun openExisting(d: GeneratedDoc) = state.update { it.copy(ready = d) }
}

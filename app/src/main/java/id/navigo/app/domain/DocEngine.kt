package id.navigo.app.domain

import android.content.ContentValues
import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DocEngine {
    const val DEFAULT_EXTRA = "Hal-hal yang belum diatur dalam dokumen ini akan dimusyawarahkan para pihak secara kekeluargaan dan dituangkan dalam addendum tertulis."
    private val ph = Regex("\\{\\{(\\w+)\\}\\}")
    private val idLocale = Locale("id", "ID")

    private fun all(values: Map<String, String>): Map<String, String> {
        val now = Date()
        return mapOf(
            "tanggal" to SimpleDateFormat("d MMMM yyyy", idLocale).format(now),
            "nomor" to "NG/" + SimpleDateFormat("yyyyMMdd/HHmm", Locale.US).format(now)
        ) + values
    }

    private fun fmt(key: String, v: String): String =
        if (key == "nilai") v.filter { it.isDigit() }.toLongOrNull()?.let { NumberFormat.getInstance(idLocale).format(it) } ?: v else v

    fun fill(body: String, values: Map<String, String>): String {
        val a = all(values)
        return ph.replace(body) { m -> a[m.groupValues[1]]?.takeIf { it.isNotBlank() }?.let { fmt(m.groupValues[1], it) } ?: "………" }
    }

    /** Pratinjau: isian pengguna diberi warna biru. */
    fun annotated(body: String, values: Map<String, String>, blue: Color): AnnotatedString {
        val a = all(values)
        return buildAnnotatedString {
            var last = 0
            for (m in ph.findAll(body)) {
                append(body.substring(last, m.range.first))
                val v = a[m.groupValues[1]]?.takeIf { it.isNotBlank() }
                if (v != null) withStyle(SpanStyle(color = blue, fontWeight = FontWeight.SemiBold)) { append(fmt(m.groupValues[1], v)) }
                else withStyle(SpanStyle(color = Color.Gray)) { append("………") }
                last = m.range.last + 1
            }
            append(body.substring(last))
        }
    }

    fun suggestPrompt(templateName: String, values: Map<String, String>) = """
Kamu asisten hukum bisnis Indonesia. Buat 2 klausul tambahan singkat dan netral (maks 70 kata total, bahasa formal) untuk dokumen "$templateName".
Data: ${values.entries.joinToString("; ") { "${it.key}=${it.value}" }}
Contoh topik: sistem pembayaran termin, batas revisi, keterlambatan. Jangan membuat angka denda baru.
Balas HANYA JSON: {"klausul":"..."}""".trimIndent()
}

object PdfWriter {
    /** Render teks ke PDF A4 (Android PdfDocument), multi-halaman. Mengembalikan file dan jumlah halaman. */
    fun write(ctx: Context, text: String, fileName: String): Pair<File, Int> {
        val dir = File(ctx.filesDir, "documents").apply { mkdirs() }
        val file = File(dir, fileName)
        val pdf = PdfDocument()
        val pw = 595; val ph = 842; val m = 56
        val normal = TextPaint().apply { textSize = 11f; color = android.graphics.Color.BLACK; isAntiAlias = true }
        val bold = TextPaint(normal).apply { isFakeBoldText = true; textSize = 12f }
        var no = 1
        var page = pdf.startPage(PdfDocument.PageInfo.Builder(pw, ph, no).create())
        var y = m.toFloat()
        val width = pw - 2 * m
        text.split("\n").forEachIndexed { i, raw ->
            val p = raw.ifEmpty { " " }
            val head = i == 0 || p.startsWith("Pasal ") || p.startsWith("Catatan Tambahan")
            val layout = StaticLayout.Builder.obtain(p, 0, p.length, if (head) bold else normal, width)
                .setLineSpacing(0f, 1.15f)
                .setAlignment(if (i == 0) Layout.Alignment.ALIGN_CENTER else Layout.Alignment.ALIGN_NORMAL)
                .build()
            if (y + layout.height > ph - m) {
                pdf.finishPage(page); no++
                page = pdf.startPage(PdfDocument.PageInfo.Builder(pw, ph, no).create())
                y = m.toFloat()
            }
            val c = page.canvas
            c.save(); c.translate(m.toFloat(), y); layout.draw(c); c.restore()
            y += layout.height
        }
        pdf.finishPage(page)
        FileOutputStream(file).use { pdf.writeTo(it) }
        pdf.close()
        return file to no
    }

    fun saveToDownloads(ctx: Context, file: File): Boolean {
        val v = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, file.name)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/NaviGo")
        }
        val uri = ctx.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, v) ?: return false
        ctx.contentResolver.openOutputStream(uri)?.use { out -> file.inputStream().use { it.copyTo(out) } }
        return true
    }
}

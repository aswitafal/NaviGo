package id.navigo.app.domain

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/** Ekstraksi teks di perangkat: file asli tidak pernah diunggah. */
object Extractor {
    const val MAX_BYTES = 10L * 1024 * 1024

    fun nameAndSize(ctx: Context, uri: Uri): Pair<String, Long> {
        ctx.contentResolver.query(uri, null, null, null, null)?.use { c ->
            if (c.moveToFirst()) {
                val n = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val s = c.getColumnIndex(OpenableColumns.SIZE)
                return (if (n >= 0) c.getString(n) else "dokumen") to (if (s >= 0) c.getLong(s) else 0L)
            }
        }
        return "dokumen" to 0L
    }

    suspend fun fromPdf(ctx: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        ctx.contentResolver.openInputStream(uri)!!.use { s ->
            PDDocument.load(s).use { d -> PDFTextStripper().getText(d) }
        }
    }

    private val recognizer by lazy { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    suspend fun fromImage(ctx: Context, uri: Uri): String =
        recognizer.process(InputImage.fromFilePath(ctx, uri)).await().text

    suspend fun fromBitmap(bmp: Bitmap): String =
        recognizer.process(InputImage.fromBitmap(bmp, 0)).await().text
}

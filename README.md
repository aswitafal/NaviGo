# NaviGo (Android, Kotlin, Jetpack Compose)

Aplikasi asisten hukum UMKM/kreator sesuai proposal Kelompok 8. Desain mengikuti wireframe Figma (biru tua + putih, bottom nav 5 menu).

## Cara menjalankan
1. Buka folder ini di Android Studio (versi terbaru), tunggu Gradle sync. Jika wrapper error, pakai Gradle 8.9+.
2. (Opsional tapi disarankan) Firebase:
   - Buat project di Firebase Console, tambahkan app Android `id.navigo.app`.
   - Aktifkan Authentication (Email/Password), Firestore, dan **Firebase AI Logic** (Gemini Developer API).
   - Taruh `google-services.json` di folder `app/`.
   - Tanpa file itu aplikasi berjalan **mode demo**: login lokal, AI tidak aktif (analisis memakai checklist lokal, chatbot menampilkan pasal hasil pencarian).
3. Admin: di Firestore buat/ubah dokumen `users/{uid}` dengan `role: "admin"`. Mode demo: masuk dengan `admin@navigo.id`.
4. Model Gemini diatur di `ai/AiClient.kt` (`MODEL`). Ganti bila sudah usang.

## Struktur
- `data/` Room (UU, pasal, analisis, chat, dokumen, kontrak, template) + seed
- `domain/` MouEngine (checklist 8 klausul + skor berbobot), DocEngine/PdfWriter, Extractor (PDFBox + ML Kit OCR)
- `ai/` AiClient (Firebase AI Logic, output JSON)
- `util/` Anonymizer, Guardrail, kuota harian, Net, Dates
- `work/` ReminderWorker (WorkManager, H-30/14/7/1)
- `vm/` ViewModel per fitur, `ui/` layar Compose + NavGraph

## Belum diimplementasikan / perlu diperhatikan
- Login Google (tombol nonaktif), Firebase App Check, enkripsi riwayat, sinkron Firestore untuk UU/template admin.
- Isi pasal di `Seed.kt` berupa ringkasan; verifikasi ke JDIH sebelum dipakai.
- Aturan keamanan Firestore harus dibuat agar hanya admin yang bisa membaca/ubah `users` milik orang lain.
- Kode ini belum pernah dikompilasi di lingkungan penyusunnya; harap siap memperbaiki error kecil saat sync/build pertama.

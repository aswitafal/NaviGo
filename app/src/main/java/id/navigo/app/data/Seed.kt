package id.navigo.app.data

/**
 * Data awal UU/pasal dan template. Isi pasal berupa RINGKASAN (bukan kutipan resmi).
 * Admin wajib memverifikasi ke JDIH / peraturan.go.id dan memperbaruinya lewat menu Admin.
 */
object Seed {
    private fun a(n: String, t: String) = Article(lawId = 0, number = n, text = t)

    val laws: List<Pair<Law, List<Article>>> = listOf(
        Law(title = "UU No. 28 Tahun 2014 tentang Hak Cipta", number = "UU 28/2014", category = "UU", topic = "Hak Cipta",
            description = "Mengatur perlindungan hak cipta atas karya seperti lagu, musik, desain, foto, tulisan, dan perangkat lunak, termasuk hak moral, hak ekonomi, pengalihan, lisensi, royalti, dan sanksi pelanggaran.") to listOf(
            a("5", "Hak moral pencipta: berhak dicantumkan namanya pada ciptaan dan mempertahankan keutuhan karyanya. Hak moral melekat dan tidak dapat dialihkan selama pencipta masih hidup."),
            a("9", "Hak ekonomi pencipta mencakup penerbitan, penggandaan, penerjemahan, adaptasi, pendistribusian, pertunjukan, pengumuman, dan penyewaan ciptaan. Penggunaan komersial oleh pihak lain wajib seizin pencipta atau pemegang hak cipta."),
            a("16", "Hak cipta adalah benda bergerak tidak berwujud yang dapat beralih atau dialihkan, seluruhnya atau sebagian, melalui pewarisan, hibah, wakaf, wasiat, perjanjian tertulis, atau sebab lain yang dibenarkan peraturan."),
            a("40", "Ciptaan yang dilindungi mencakup buku, lagu atau musik, seni rupa, fotografi, film, arsitektur, program komputer, dan karya lain di bidang ilmu, seni, dan sastra."),
            a("80", "Pemegang hak cipta dapat memberi lisensi kepada pihak lain melalui perjanjian tertulis untuk melaksanakan hak ekonomi. Perjanjian lisensi tidak boleh merugikan perekonomian Indonesia."),
            a("87", "Penggunaan ciptaan secara komersial dalam pertunjukan, misalnya memutar lagu di tempat usaha, mewajibkan pembayaran imbalan (royalti) melalui lembaga manajemen kolektif."),
            a("113", "Ketentuan pidana: pelanggaran hak ekonomi untuk tujuan komersial dapat dikenai pidana penjara dan/atau denda sesuai tingkat pelanggarannya.")
        ),
        Law(title = "UU No. 20 Tahun 2016 tentang Merek dan Indikasi Geografis", number = "UU 20/2016", category = "UU", topic = "Merek",
            description = "Mengatur pendaftaran dan perlindungan merek dagang/jasa, jangka waktu perlindungan, penolakan pendaftaran, dan penyelesaian sengketa merek.") to listOf(
            a("3", "Hak atas merek diperoleh setelah merek terdaftar. Pendaftaran menjadi dasar perlindungan hukum atas merek usaha."),
            a("21", "Permohonan pendaftaran ditolak jika merek memiliki persamaan pada pokoknya atau keseluruhannya dengan merek terdaftar milik pihak lain untuk barang/jasa sejenis, atau diajukan dengan itikad tidak baik."),
            a("35", "Merek terdaftar mendapat perlindungan selama 10 tahun sejak tanggal penerimaan permohonan dan dapat diperpanjang.")
        ),
        Law(title = "KUH Perdata Pasal 1320, 1338, 1243 dan 1365 tentang Perjanjian", number = "KUHPerdata", category = "UU", topic = "Perjanjian",
            description = "Dasar hukum perjanjian di Indonesia: syarat sah perjanjian, kekuatan mengikat, wanprestasi, dan perbuatan melawan hukum.") to listOf(
            a("1320", "Syarat sah perjanjian ada empat: kesepakatan para pihak, kecakapan membuat perjanjian, suatu hal tertentu, dan sebab yang halal (tidak melanggar hukum)."),
            a("1338", "Perjanjian yang dibuat secara sah berlaku sebagai undang-undang bagi para pihak, tidak dapat dibatalkan sepihak, dan harus dilaksanakan dengan itikad baik."),
            a("1243", "Ganti rugi, biaya, dan bunga karena perjanjian tidak dipenuhi (wanprestasi) baru wajib dibayar setelah pihak yang berutang dinyatakan lalai."),
            a("1365", "Setiap perbuatan melawan hukum yang merugikan orang lain mewajibkan pelakunya mengganti kerugian tersebut.")
        ),
        Law(title = "UU No. 27 Tahun 2022 tentang Pelindungan Data Pribadi", number = "UU 27/2022", category = "UU", topic = "Data Pribadi",
            description = "Mengatur pemrosesan dan pelindungan data pribadi, kewajiban pengendali data, hak pemilik data, dan sanksi pelanggaran. Relevan bagi usaha yang menyimpan data pelanggan atau klien.") to listOf(
            a("20", "Pemrosesan data pribadi harus memiliki dasar yang sah, antara lain persetujuan yang sah dari pemilik data atau pemenuhan kewajiban perjanjian."),
            a("35", "Pengendali data pribadi wajib melindungi dan memastikan keamanan data pribadi yang diprosesnya, termasuk dari akses tanpa izin."),
            a("65", "Dilarang memperoleh atau mengumpulkan data pribadi yang bukan miliknya secara melawan hukum untuk menguntungkan diri sendiri atau orang lain.")
        ),
        Law(title = "UU No. 8 Tahun 1999 tentang Perlindungan Konsumen", number = "UU 8/1999", category = "UU", topic = "Konsumen",
            description = "Mengatur hak konsumen, kewajiban dan larangan bagi pelaku usaha, serta penyelesaian sengketa konsumen.") to listOf(
            a("4", "Hak konsumen antara lain atas keamanan, informasi yang benar dan jelas, serta kompensasi bila barang/jasa tidak sesuai perjanjian."),
            a("7", "Kewajiban pelaku usaha: beritikad baik, memberi informasi yang benar dan jujur, memperlakukan konsumen secara tidak diskriminatif, dan menjamin mutu barang/jasa."),
            a("8", "Pelaku usaha dilarang memproduksi atau memperdagangkan barang/jasa yang tidak sesuai standar, label, atau janji yang dinyatakan.")
        )
    )

    private fun f(vararg x: String) = x.joinToString(";;")
    private const val EXTRA = "{{klausul_tambahan}}"

    private val signature2 = """

PIHAK PERTAMA,




( {{nama_pihak_1}} )

PIHAK KEDUA,




( {{nama_pihak_2}} )"""

    private val closing = """

Pasal 4 — Klausul Tambahan
$EXTRA

Pasal 5 — Penyelesaian Sengketa
Perselisihan diselesaikan secara musyawarah. Bila tidak tercapai, para pihak menempuh mediasi atau melalui Pengadilan Negeri yang disepakati.

Pasal 6 — Dasar Hukum
Perjanjian ini dibuat berdasarkan kesepakatan para pihak sesuai Pasal 1320 dan Pasal 1338 KUH Perdata, dibuat rangkap dua di atas meterai cukup, dan masing-masing mempunyai kekuatan hukum yang sama."""

    val templates: List<DocTemplate> = listOf(
        DocTemplate(
            name = "Perjanjian Kerja Sama (PKS)", description = "Kolaborasi antar pihak, hak & kewajiban",
            fields = f("nama_pihak_1|Nama Pihak Pertama|TEXT|1", "nama_pihak_2|Nama Pihak Kedua|TEXT|1",
                "objek|Objek Kerja Sama|TEXT|1", "nilai|Nilai Kompensasi (Rp)|NUMBER|1", "durasi|Durasi (bulan)|NUMBER|1"),
            body = """SURAT PERJANJIAN KERJA SAMA
Nomor: {{nomor}}

Pada hari ini, {{tanggal}}, yang bertanda tangan di bawah ini:
1. Nama: {{nama_pihak_1}} (selanjutnya disebut PIHAK PERTAMA)
2. Nama: {{nama_pihak_2}} (selanjutnya disebut PIHAK KEDUA)

Pasal 1 — Objek Perjanjian
Para pihak sepakat bekerja sama dalam: {{objek}}.

Pasal 2 — Kompensasi dan Jangka Waktu
Nilai kerja sama sebesar Rp {{nilai}} dengan jangka waktu {{durasi}} bulan sejak perjanjian ini ditandatangani.

Pasal 3 — Hak Kekayaan Intelektual
Hak cipta atas hasil kerja tetap pada pihak yang menciptakan, kecuali dialihkan atau dilisensikan secara tertulis.$closing$signature2"""
        ),
        DocTemplate(
            name = "NDA / Perjanjian Kerahasiaan", description = "Lindungi informasi bisnis & karya",
            fields = f("nama_pihak_1|Nama Pihak Pertama|TEXT|1", "nama_pihak_2|Nama Pihak Kedua|TEXT|1",
                "jenis_info|Jenis Informasi Rahasia|TEXT|1", "durasi|Jangka Waktu Kerahasiaan (bulan)|NUMBER|1"),
            body = """PERJANJIAN KERAHASIAAN (NDA)
Nomor: {{nomor}}

Pada hari ini, {{tanggal}}, yang bertanda tangan di bawah ini:
1. Nama: {{nama_pihak_1}} (PIHAK PERTAMA)
2. Nama: {{nama_pihak_2}} (PIHAK KEDUA)

Pasal 1 — Informasi Rahasia
Informasi rahasia meliputi: {{jenis_info}}, serta informasi lain yang ditandai rahasia oleh PIHAK PERTAMA.

Pasal 2 — Kewajiban Menjaga Kerahasiaan
PIHAK KEDUA wajib menjaga kerahasiaan informasi tersebut, tidak mengungkapkannya kepada pihak ketiga, dan hanya menggunakannya untuk tujuan yang disepakati.

Pasal 3 — Jangka Waktu
Kewajiban kerahasiaan berlaku selama {{durasi}} bulan sejak perjanjian ini ditandatangani.$closing$signature2"""
        ),
        DocTemplate(
            name = "Pengalihan / Lisensi Hak Cipta", description = "Alihkan atau lisensikan karya dengan jelas",
            fields = f("nama_pihak_1|Nama Pihak Pertama (Pencipta)|TEXT|1", "nama_pihak_2|Nama Pihak Kedua|TEXT|1",
                "judul_karya|Judul Karya|TEXT|1", "bentuk|Bentuk (Pengalihan atau Lisensi non-eksklusif)|TEXT|1",
                "nilai|Nilai Kompensasi (Rp)|NUMBER|1", "durasi|Durasi (bulan)|NUMBER|1"),
            body = """SURAT PERJANJIAN PENGALIHAN / LISENSI HAK CIPTA
Nomor: {{nomor}}

Pada hari ini, {{tanggal}}, yang bertanda tangan di bawah ini:
1. Nama: {{nama_pihak_1}} (Pencipta, PIHAK PERTAMA)
2. Nama: {{nama_pihak_2}} (PIHAK KEDUA)

Pasal 1 — Objek Perjanjian
Judul karya: {{judul_karya}}. Bentuk: {{bentuk}}. Pengalihan hak cipta dilakukan secara tertulis sesuai Pasal 16 UU No. 28 Tahun 2014.

Pasal 2 — Kompensasi dan Durasi
Nilai: Rp {{nilai}}. Durasi: {{durasi}} bulan. Hak moral pencipta untuk dicantumkan namanya tetap melekat.

Pasal 3 — Batasan Penggunaan
PIHAK KEDUA hanya boleh menggunakan karya sesuai bentuk yang disepakati dan tidak mengalihkannya kepada pihak lain tanpa persetujuan tertulis PIHAK PERTAMA.$closing$signature2"""
        ),
        DocTemplate(
            name = "Surat Kuasa", description = "Kuasakan pengurusan HKI / kontrak",
            fields = f("nama_pihak_1|Nama Pemberi Kuasa|TEXT|1", "nama_pihak_2|Nama Penerima Kuasa|TEXT|1", "urusan|Urusan yang Dikuasakan|TEXT|1"),
            body = """SURAT KUASA
Nomor: {{nomor}}

Yang bertanda tangan di bawah ini, pada {{tanggal}}:
Nama: {{nama_pihak_1}} (PEMBERI KUASA)

Dengan ini memberi kuasa kepada:
Nama: {{nama_pihak_2}} (PENERIMA KUASA)

Untuk mengurus dan bertindak atas nama pemberi kuasa dalam hal: {{urusan}}.

Pasal 1 — Ruang Lingkup
Kuasa ini terbatas pada urusan tersebut di atas dan tidak mencakup tindakan lain tanpa persetujuan tertulis pemberi kuasa.

Pasal 2 — Catatan Tambahan
$EXTRA

Demikian surat kuasa ini dibuat di atas meterai cukup untuk dipergunakan sebagaimana mestinya.

PEMBERI KUASA,




( {{nama_pihak_1}} )

PENERIMA KUASA,




( {{nama_pihak_2}} )"""
        ),
        DocTemplate(
            name = "Surat Pernyataan Keaslian Karya", description = "Bukti keaslian & kepemilikan karya",
            fields = f("nama_pihak_1|Nama Pencipta|TEXT|1", "judul_karya|Judul Karya|TEXT|1", "jenis_karya|Jenis Karya (mis. logo, lagu, foto)|TEXT|1"),
            body = """SURAT PERNYATAAN KEASLIAN KARYA
Nomor: {{nomor}}

Saya yang bertanda tangan di bawah ini, pada {{tanggal}}:
Nama: {{nama_pihak_1}}

Dengan ini menyatakan bahwa karya berjudul "{{judul_karya}}" berjenis {{jenis_karya}} adalah hasil karya asli saya, bukan salinan atau jiplakan dari karya pihak lain.

Catatan Tambahan
$EXTRA

Apabila di kemudian hari pernyataan ini terbukti tidak benar, saya bersedia bertanggung jawab sesuai ketentuan hukum yang berlaku. Surat ini dibuat di atas meterai cukup.

Pembuat Pernyataan,




( {{nama_pihak_1}} )"""
        )
    )
}

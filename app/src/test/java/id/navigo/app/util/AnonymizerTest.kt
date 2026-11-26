package id.navigo.app.util

import org.junit.Assert.*
import org.junit.Test

class AnonymizerTest {

    @Test
    fun testNikMaskingAndRestore() {
        val anon = Anonymizer()
        val input = "NIK saya adalah 3171012345678901 untuk verifikasi."
        val masked = anon.mask(input)
        assertFalse("NIK harus dimasking", masked.contains("3171012345678901"))
        assertTrue("Masked text harus berisi token NIK", masked.contains("[NIK_"))

        val restored = anon.restore(masked)
        assertEquals("Hasil restore harus sama dengan input awal", input, restored)
    }

    @Test
    fun testEmailMaskingAndRestore() {
        val anon = Anonymizer()
        val input = "Kirim dokumen ke john.doe@example.com secepatnya."
        val masked = anon.mask(input)
        assertFalse("Email harus dimasking", masked.contains("john.doe@example.com"))
        assertTrue("Masked text harus berisi token EMAIL", masked.contains("[EMAIL_"))

        val restored = anon.restore(masked)
        assertEquals("Hasil restore harus sama dengan input awal", input, restored)
    }

    @Test
    fun testPhoneMaskingAndRestore() {
        val anon = Anonymizer()
        val input = "Nomor kontak: 081234567890 atau +62 812-3456-7890"
        val masked = anon.mask(input)
        assertFalse("Nomor telepon 081234567890 harus dimasking", masked.contains("081234567890"))
        assertTrue("Masked text harus berisi token TELP", masked.contains("[TELP_"))

        val restored = anon.restore(masked)
        assertEquals("Hasil restore harus sama dengan input awal", input, restored)
    }

    @Test
    fun testNpwpMaskingAndRestore() {
        val anon = Anonymizer()
        val input = "NPWP perusahaan: 01.234.567.8-901.000."
        val masked = anon.mask(input)
        assertFalse("NPWP harus dimasking", masked.contains("01.234.567.8-901.000"))
        assertTrue("Masked text harus berisi token NPWP", masked.contains("[NPWP_"))

        val restored = anon.restore(masked)
        assertEquals("Hasil restore harus sama dengan input awal", input, restored)
    }

    @Test
    fun testAccountMaskingAndRestore() {
        val anon = Anonymizer()
        val input = "Nomor rekening tujuan: 123456789012 bank mandiri."
        val masked = anon.mask(input)
        assertFalse("Nomor rekening harus dimasking", masked.contains("123456789012"))
        assertTrue("Masked text harus berisi token REKENING", masked.contains("[REKENING_"))

        val restored = anon.restore(masked)
        assertEquals("Hasil restore harus sama dengan input awal", input, restored)
    }

    @Test
    fun testNonStandardFormatMasking() {
        val anon = Anonymizer()
        val input = "Format NIK 3171-0123-4567-8901 dan email test.user+label@domain.co.id"
        val masked = anon.mask(input)
        assertFalse("Format NIK bertanda hubung harus dimasking", masked.contains("3171-0123-4567-8901"))
        assertFalse("Email dengan tanda plus harus dimasking", masked.contains("test.user+label@domain.co.id"))

        val restored = anon.restore(masked)
        assertEquals(input, restored)
    }
}

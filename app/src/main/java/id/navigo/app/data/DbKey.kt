package id.navigo.app.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object DbKey {
    private const val ALIAS = "navigo_db_key"

    private fun masterKey(): SecretKey {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (ks.getKey(ALIAS, null) as? SecretKey)?.let { return it }
        val g = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        g.init(
            KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256).build()
        )
        return g.generateKey()
    }

    /** Passphrase acak 32 byte, disimpan terenkripsi oleh kunci Keystore. */
    fun passphrase(ctx: Context): ByteArray {
        val sp = ctx.getSharedPreferences("navigo_sec", Context.MODE_PRIVATE)
        val stored = sp.getString("k", null)
        val c = Cipher.getInstance("AES/GCM/NoPadding")
        if (stored != null) {
            val raw = Base64.decode(stored, Base64.NO_WRAP)
            c.init(Cipher.DECRYPT_MODE, masterKey(), GCMParameterSpec(128, raw.copyOfRange(0, 12)))
            return c.doFinal(raw, 12, raw.size - 12)
        }
        val pass = ByteArray(32).also { SecureRandom().nextBytes(it) }
        c.init(Cipher.ENCRYPT_MODE, masterKey())
        sp.edit().putString("k", Base64.encodeToString(c.iv + c.doFinal(pass), Base64.NO_WRAP)).apply()
        return pass
    }
}
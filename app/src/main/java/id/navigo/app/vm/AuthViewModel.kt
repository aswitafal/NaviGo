package id.navigo.app.vm

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthState(
    val loggedIn: Boolean = false, val email: String = "", val name: String = "",
    val isAdmin: Boolean = false, val loading: Boolean = false,
    val error: String? = null, val info: String? = null, val checked: Boolean = false
)

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    /** true bila google-services.json terpasang dan Firebase aktif. */
    private val fb = runCatching { FirebaseApp.getApps(app).isNotEmpty() }.getOrDefault(false)
    val demoMode get() = !fb
    val state = MutableStateFlow(AuthState())

    init {
        viewModelScope.launch {
            val u = if (fb) FirebaseAuth.getInstance().currentUser else null
            if (u != null) {
                val (admin, blocked) = profile(u.uid, u.email.orEmpty())
                if (blocked) { FirebaseAuth.getInstance().signOut(); state.value = AuthState(checked = true) }
                else state.value = AuthState(true, u.email.orEmpty(), nameOf(u.email.orEmpty()), admin, checked = true)
            } else state.update { it.copy(checked = true) }
        }
    }

    private fun nameOf(email: String) = email.substringBefore("@").replaceFirstChar { it.uppercase() }

    /** Peran admin hanya dari Firestore (users/{uid}.role == "admin"). Mode demo: admin@navigo.id. */
    private suspend fun profile(uid: String?, email: String): Pair<Boolean, Boolean> {
        if (fb && uid != null) {
            val d = runCatching { FirebaseFirestore.getInstance().collection("users").document(uid).get().await() }.getOrNull()
            if (d != null && d.exists()) return (d.getString("role") == "admin") to (d.getString("status") == "diblokir")
            return false to false
        }
        return email.equals("admin@navigo.id", true) to false
    }

    fun submit(register: Boolean, email: String, pass: String, confirm: String) {
        val e = email.trim()
        val err = when {
            !Patterns.EMAIL_ADDRESS.matcher(e).matches() -> "Format email tidak valid"
            pass.length < 6 -> "Kata sandi minimal 6 karakter"
            register && pass != confirm -> "Konfirmasi kata sandi tidak sama"
            else -> null
        }
        if (err != null) { state.update { it.copy(error = err) }; return }
        viewModelScope.launch {
            state.update { it.copy(loading = true, error = null, info = null) }
            try {
                var uid: String? = null
                if (fb) {
                    val a = FirebaseAuth.getInstance()
                    val r = if (register) a.createUserWithEmailAndPassword(e, pass).await() else a.signInWithEmailAndPassword(e, pass).await()
                    uid = r.user?.uid
                    if (register && uid != null) runCatching {
                        FirebaseFirestore.getInstance().collection("users").document(uid).set(
                            mapOf("email" to e, "name" to nameOf(e), "role" to "user", "status" to "aktif",
                                "createdAt" to System.currentTimeMillis())
                        ).await()
                    }
                }
                val (admin, blocked) = profile(uid, e)
                if (blocked) {
                    if (fb) FirebaseAuth.getInstance().signOut()
                    throw IllegalStateException("Akun Anda diblokir oleh admin.")
                }
                state.update { it.copy(loggedIn = true, email = e, name = nameOf(e), isAdmin = admin, loading = false) }
            } catch (ex: Exception) {
                state.update { it.copy(loading = false, error = ex.message ?: "Gagal masuk") }
            }
        }
    }

    fun resetPassword(email: String) {
        if (!fb || !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            state.update { it.copy(error = "Isi email yang valid dulu (fitur ini butuh Firebase aktif).") }; return
        }
        viewModelScope.launch {
            runCatching { FirebaseAuth.getInstance().sendPasswordResetEmail(email.trim()).await() }
                .onSuccess { state.update { s -> s.copy(info = "Tautan reset kata sandi dikirim ke email.", error = null) } }
                .onFailure { ex -> state.update { s -> s.copy(error = ex.message) } }
        }
    }

    fun logout() {
        if (fb) FirebaseAuth.getInstance().signOut()
        state.value = AuthState(checked = true)
    }
}

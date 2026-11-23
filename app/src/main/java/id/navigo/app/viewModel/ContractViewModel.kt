package id.navigo.app.viewModel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.navigo.app.data.Contract
import id.navigo.app.data.ServiceLocator
import id.navigo.app.util.Dates
import id.navigo.app.work.ReminderWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class Prefill(val title: String = "", val party: String = "", val endDay: Long? = null)

class ContractViewModel(private val app: Application) : AndroidViewModel(app) {
    private val dao = ServiceLocator.db.contractDao()
    val contracts = dao.all().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    var prefill by mutableStateOf(Prefill())

    /** Mengembalikan pesan error validasi, atau null bila berhasil. */
    fun save(title: String, party: String, startStr: String, endStr: String, offsets: Set<Int>, onDone: (expired: Boolean) -> Unit): String? {
        if (title.isBlank()) return "Judul kontrak wajib diisi"
        val s = Dates.parse(startStr) ?: return "Format tanggal mulai harus dd/MM/yyyy"
        val e = Dates.parse(endStr) ?: return "Format tanggal berakhir harus dd/MM/yyyy"
        if (e < s) return "Tanggal berakhir harus setelah tanggal mulai"
        viewModelScope.launch {
            dao.insert(Contract(title = title.trim(), party = party.trim(), startDay = s, endDay = e,
                reminders = offsets.sortedDescending().joinToString(",")))
            ReminderWorker.runNow(app)
            prefill = Prefill()
            onDone(e < LocalDate.now().toEpochDay())
        }
        return null
    }

    /** Perpanjang kontrak: tanggal berakhir diundur, jadwal pengingat dihitung ulang. */
    fun extend(c: Contract, months: Long = 12) = viewModelScope.launch {
        val newEnd = LocalDate.ofEpochDay(c.endDay).plusMonths(months).toEpochDay()
        dao.update(c.copy(endDay = newEnd, notified = ""))
    }

    fun delete(c: Contract) = viewModelScope.launch { dao.delete(c) }
}

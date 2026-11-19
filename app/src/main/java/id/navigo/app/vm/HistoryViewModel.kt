package id.navigo.app.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.navigo.app.data.GeneratedDoc
import id.navigo.app.data.MouAnalysis
import id.navigo.app.data.ServiceLocator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class HistoryViewModel(app: Application) : AndroidViewModel(app) {
    private val db = ServiceLocator.db
    val analyses = db.mouDao().all().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val docs = db.docDao().all().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun deleteAnalysis(a: MouAnalysis) = viewModelScope.launch { db.mouDao().delete(a) }
    fun deleteDoc(d: GeneratedDoc) = viewModelScope.launch {
        runCatching { File(d.path).delete() }
        db.docDao().delete(d)
    }
}

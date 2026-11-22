package id.navigo.app

import android.app.Application
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import id.navigo.app.data.ServiceLocator
import id.navigo.app.work.ReminderWorker
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
class NaviGoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        if (FirebaseApp.getApps(this).isNotEmpty()) {
            val factory = if (BuildConfig.DEBUG) DebugAppCheckProviderFactory.getInstance()
            else PlayIntegrityAppCheckProviderFactory.getInstance()
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(factory)
        }
        ServiceLocator.init(this)
        PDFBoxResourceLoader.init(this)
        ReminderWorker.createChannel(this)
        ReminderWorker.schedule(this)
    }
}

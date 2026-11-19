package id.navigo.app

import android.app.Application
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import id.navigo.app.data.ServiceLocator
import id.navigo.app.work.ReminderWorker

class NaviGoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
        PDFBoxResourceLoader.init(this)
        ReminderWorker.createChannel(this)
        ReminderWorker.schedule(this)
    }
}

package id.navigo.app.work

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import id.navigo.app.MainActivity
import id.navigo.app.data.Contract
import id.navigo.app.data.ServiceLocator
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.concurrent.TimeUnit

class ReminderWorker(private val ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val dao = ServiceLocator.db.contractDao()
        for (c in dao.all().first()) {
            val left = c.daysLeft()
            if (left < 0) continue
            val offsets = c.reminders.split(",").mapNotNull { it.toIntOrNull() }
            val done = c.notified.split(",").mapNotNull { it.toIntOrNull() }.toMutableSet()
            val due = offsets.filter { left <= it && it !in done }
            if (due.isNotEmpty()) {
                notify(c, left)
                done.addAll(due)
                dao.update(c.copy(notified = done.joinToString(",")))
            }
        }
        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private fun notify(c: Contract, left: Int) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
        val open = PendingIntent.getActivity(ctx, c.id.toInt(), Intent(ctx, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val text = if (left == 0) "Kontrak berakhir hari ini" else "Kontrak berakhir dalam $left hari"
        val n = NotificationCompat.Builder(ctx, CHANNEL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(c.title).setContentText(text)
            .setContentIntent(open).setAutoCancel(true)
            .addAction(0, "Buka / perpanjang", open)
            .build()
        runCatching { NotificationManagerCompat.from(ctx).notify(c.id.toInt(), n) }
    }

    companion object {
        const val CHANNEL = "reminder"

        fun createChannel(ctx: Context) {
            val nm = ctx.getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(NotificationChannel(CHANNEL, "Pengingat Kontrak", NotificationManager.IMPORTANCE_DEFAULT))
        }

        /** Pemeriksaan harian oleh WorkManager. */
        fun schedule(ctx: Context) {
            WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
                "navigo_reminder", ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS).build()
            )
        }

        fun runNow(ctx: Context) {
            WorkManager.getInstance(ctx).enqueue(OneTimeWorkRequestBuilder<ReminderWorker>().build())
        }
    }
}

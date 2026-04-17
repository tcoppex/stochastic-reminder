package cc.polysfaer.stochapop.controller.broadcast

import android.content.Context
import android.content.Intent
import cc.polysfaer.stochapop.MainApplication

class BootReceiver : AsyncBroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val app = context.applicationContext as MainApplication
            val remindersRepository = app.container.remindersRepository
            val schedulerRepository = app.container.schedulerRepository

            goAsync {
                val reminders = remindersRepository.getAllReminders()
                reminders.filter { it.enabled }.forEach {
                    schedulerRepository.scheduleReminderAlarms(it)
                }
            }
        }
    }
}
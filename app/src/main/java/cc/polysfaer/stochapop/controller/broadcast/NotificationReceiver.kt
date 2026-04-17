package cc.polysfaer.stochapop.controller.broadcast

import android.content.Context
import android.content.Intent
import cc.polysfaer.stochapop.MainApplication
import cc.polysfaer.stochapop.controller.sendNotification

class NotificationReceiver : AsyncBroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as MainApplication
        val remindersRepository = app.container.remindersRepository
        val schedulerRepository = app.container.schedulerRepository

        // NOTE: As a reminder has way less than 1Mb, we could sent all relevant parameters
        //       as extra instead of query the db via async.
        val reminderId = intent.getIntExtra(INTENT_REMINDER_ID, -1)
        val notificationId = intent.getIntExtra(INTENT_NOTIFICATION_ID, 0)

        // We need a coroutine to access the Room Database.
        // If the work were more intensive we would need another scheme as the broadcast could finish beforehand.
        goAsync {
            val reminder = remindersRepository.getReminder(reminderId)
            if (reminder != null) {
                // We do not really need to check as the alarm would be cancelled otherwise, but still.
                if (reminder.enabled) {
                    sendNotification(context, reminder)
                }
                schedulerRepository.scheduleReminderNextSingleAlarm(reminder, notificationId)
            }
        }
    }
}

package cc.polysfaer.stochapop.data

import cc.polysfaer.stochapop.ui.screens.reminder.ReminderDetails
import cc.polysfaer.stochapop.ui.screens.reminder.toReminder
import java.time.DayOfWeek
import java.time.LocalTime

object DataSource {
    val reminderList = listOf(
        ReminderDetails(id=1, title="The Basics", message="- Single tap to edit,\n- Double tap to enable/disable,\n- Left swipe to delete.",
            enabled = false, useRandomRange = false, hasSound = true, hasVibration = false, selectedDays = setOf(DayOfWeek.MONDAY)),
        ReminderDetails(id=2, title="Random Ranges", message="Use a time range to spread notifications randomly.\n\nWhen a time range overlap two days, the starting day is used as the triggering day for the whole range.",
            enabled = false, hasSound = false, hasVibration = true,  notificationCount = 5, selectedDays = setOf(DayOfWeek.MONDAY , DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            startTime = LocalTime.of(23, 30), endTime = LocalTime.of(0,30)),
        ReminderDetails(id=3, title="Cool tips", message="Muted notifications use less battery \uD83C\uDF3F \uD83E\uDDA5",
            enabled = false, hasSound = false, hasVibration = false,  notificationCount = 12, selectedDays = setOf(DayOfWeek.SATURDAY)),
        ReminderDetails(id=4, title="Thank you!", message="◝(ᵔᗜᵔ)◜ ♡ ", enabled = true, useRandomRange = false,
            hasSound = true, hasVibration = true,  startTime = LocalTime.now().plusMinutes(1)),
    ).map { it.toReminder() }
}

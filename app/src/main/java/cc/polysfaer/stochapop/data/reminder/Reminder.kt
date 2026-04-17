package cc.polysfaer.stochapop.data.reminder

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.DayOfWeek
import java.time.LocalTime

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,                                        // Switch to Long?
    val title: String,                                      // size = ReminderEditUiState.MAX_TITLE_LENGTH
    val message: String,                                    // size = ReminderEditUiState.MAX_MESSAGE_LENGTH
    val enabled: Boolean,
    val useRandomRange: Boolean,
    val hasSound: Boolean,
    val hasVibration: Boolean,
    val notificationCount: Int,
    val startTime: LocalTime,                               // stored as Int (minutes of the day)
    val endTime: LocalTime,                                 // stored as Int (minutes of the day)
    val selectedDays: Set<DayOfWeek>,                       // stored as Int (mask of days)
)

class ReminderTimeConverter {
    @TypeConverter
    fun fromMinutesOfDay(value: Int?): LocalTime? {
        return value?.let {
            LocalTime.of(it / 60, it % 60)
        }
    }

    @TypeConverter
    fun toMinutesOfDay(time: LocalTime?): Int? {
        return time?.let {
            (it.hour * 60) + it.minute
        }
    }
}

class ReminderDayConverter {
    @TypeConverter
    fun fromSelectedDayBitmask(mask: Int?): Set<DayOfWeek>? {
        return mask?.let {
            DayOfWeek.entries.filterIndexed  { index, _ ->
                mask and (1 shl index) != 0
            }.toSet()
        }
    }

    @TypeConverter
    fun toSelectedDayBitmask(selectedDays: Set<DayOfWeek>?): Int? {
        return selectedDays?.let {
            selectedDays.fold(0, {curr, day ->
                curr or (1 shl (DayOfWeek.entries.indexOf(day)))
            })
        }
    }
}

/*
object LocalTimeSerializer : KSerializer<LocalTime> {
    private val converter = ReminderTimeConverter()
    override val descriptor = PrimitiveSerialDescriptor("LocalTime", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: LocalTime) {
        val minutes = converter.toMinutesOfDay(value) ?: 0
        encoder.encodeInt(minutes)
    }

    override fun deserialize(decoder: Decoder): LocalTime {
        val minutes = decoder.decodeInt()
        return converter.fromMinutesOfDay(minutes) ?: LocalTime.MIDNIGHT
    }
}

object DayOfWeekSetSerializer : KSerializer<Set<DayOfWeek>> {
    private val converter = ReminderDayConverter()
    override val descriptor = PrimitiveSerialDescriptor("DayOfWeekSet", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Set<DayOfWeek>) {
        val mask = converter.toSelectedDayBitmask(value) ?: 0
        encoder.encodeInt(mask)
    }

    override fun deserialize(decoder: Decoder): Set<DayOfWeek> {
        val mask = decoder.decodeInt()
        return converter.fromSelectedDayBitmask(mask) ?: emptySet()
    }
}
*/
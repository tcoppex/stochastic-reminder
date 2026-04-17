package cc.polysfaer.stochapop

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cc.polysfaer.stochapop.ui.StochaPopApp
import cc.polysfaer.stochapop.ui.theme.StochaPopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        AppNotificationChannels.initialize(this)
        setContent {
            StochaPopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    StochaPopApp()
                }
            }
        }
    }
}

object AppNotificationChannels {
    fun getChannelId(
        isUrgent: Boolean,
        isPublic: Boolean,
        hasSound: Boolean,
        hasVibration: Boolean,
    ): String {
        val importanceStr = if (isUrgent) "URGENT" else "DEFAULT"
        val visibilityStr = if (isPublic) "PUBLIC" else "PRIVATE"
        val soundStr      = if (hasSound) "SOUND" else "NONE"
        val vibrationStr  = if (hasVibration) "VIBRATION" else "NONE"
        val id = "${importanceStr}_${visibilityStr}_${soundStr}_${vibrationStr}"
        return id
    }

    fun getDefaultChannelId(hasSound: Boolean = true, hasVibration: Boolean = false): String = getChannelId(
        isUrgent = true, isPublic = true, hasSound, hasVibration
    )

    fun initialize(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)

        val importances = listOf(NotificationManager.IMPORTANCE_HIGH, NotificationManager.IMPORTANCE_DEFAULT)
        val visibilities = listOf(Notification.VISIBILITY_PUBLIC, Notification.VISIBILITY_PRIVATE)
        val modes = listOf("SV", "S", "V", "0")

        val channels = mutableListOf<NotificationChannel>()

        importances.forEach { importance ->
            val isUrgent = (importance == NotificationManager.IMPORTANCE_HIGH)
            visibilities.forEach { visibility ->
                val isPublic = (visibility == Notification.VISIBILITY_PUBLIC)
                modes.forEach { mode ->
                    val hasSound = mode.first() == 'S'
                    val hasVibration = mode.last() == 'V'
                    val id = getChannelId(isUrgent, isPublic, hasSound, hasVibration)
                    val channel = NotificationChannel(id, "Channel $id", importance).apply {
                        lockscreenVisibility = visibility
                        enableVibration(hasVibration)
                        if (!hasSound) setSound(null, null)
                    }
                    channels.add(channel)
                }
            }
        }
        manager.createNotificationChannels(channels)
    }
}
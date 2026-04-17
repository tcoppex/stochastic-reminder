package cc.polysfaer.stochapop

import android.app.Application
import cc.polysfaer.stochapop.data.AppContainer
import cc.polysfaer.stochapop.data.AppDataContainer

class MainApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
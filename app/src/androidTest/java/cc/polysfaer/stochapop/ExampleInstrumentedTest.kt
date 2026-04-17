package cc.polysfaer.stochapop

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import cc.polysfaer.stochapop.data.AppDatabase
import cc.polysfaer.stochapop.data.DataSource
import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("cc.polysfaer.stochapop", appContext.packageName)
    }
}

//@RunWith(AndroidJUnit4::class)
//class DatabaseGenerator {
//    @Test
//    fun generateAsset() {
//        val context = InstrumentationRegistry.getInstrumentation().targetContext
//        val db = Room.databaseBuilder(context, AppDatabase::class.java, "welcome.db").build()
//
//        runBlocking {
//            val dao = db.reminderDao()
//            DataSource.reminderList.forEach { dao.insert(it) }
//        }
//
//        db.close()
//    }
//}
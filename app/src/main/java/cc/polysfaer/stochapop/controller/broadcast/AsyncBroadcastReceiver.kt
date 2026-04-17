package cc.polysfaer.stochapop.controller.broadcast

import android.content.BroadcastReceiver
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class AsyncBroadcastReceiver : BroadcastReceiver() {
    /* Executes a coroutine within the BroadcastReceiver's lifecycle, up to the system's timeout limit (~10s). */
    protected fun goAsync(
        context: CoroutineContext = Dispatchers.IO,
        block: suspend CoroutineScope.() -> Unit
    ) {
        val pendingResult = goAsync()

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch(context) {
            try {
                block()
            } catch (e: Exception) {
                Log.e(this@AsyncBroadcastReceiver.javaClass.simpleName, "Async execution failed", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
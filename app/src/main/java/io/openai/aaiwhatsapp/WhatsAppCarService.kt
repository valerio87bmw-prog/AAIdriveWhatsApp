package io.openai.aaiwhatsapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import io.bimmergestalt.idriveconnectkit.android.IDriveConnectionReceiver
import io.bimmergestalt.idriveconnectkit.android.IDriveConnectionStatus
import io.bimmergestalt.idriveconnectkit.android.SecurityAccess

class WhatsAppCarService : Service() {

    companion object {
        private const val TAG = "AAIWhatsAppCar"
    }

    override fun onCreate() {
        super.onCreate()

        SecurityAccess
            .getInstance(applicationContext)
            .connect()

        Log.i(TAG, "WhatsApp car service created")
    }

    override fun onBind(intent: Intent?): IBinder? {
        if (intent != null) {
            IDriveConnectionReceiver()
                .onReceive(applicationContext, intent)

            Log.i(TAG, "AAIdrive connection received through bind")
        }

        logAvailableMessages()
        return null
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (intent != null) {
            IDriveConnectionReceiver()
                .onReceive(applicationContext, intent)

            Log.i(TAG, "AAIdrive connection received through start")
        }

        logAvailableMessages()

        return START_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        IDriveConnectionStatus.reset()

        Log.i(TAG, "AAIdrive connection reset")

        return super.onUnbind(intent)
    }

    private fun logAvailableMessages() {
        val messages =
            WhatsAppMessageStore.getMessages()

        Log.i(
            TAG,
            "Messages available to car: ${messages.size}"
        )

        messages.firstOrNull()?.let {
            Log.i(
                TAG,
                "Latest message: ${it.sender}: ${it.text}"
            )
        }
    }

    override fun onDestroy() {
        IDriveConnectionStatus.reset()

        Log.i(TAG, "WhatsApp car service destroyed")

        super.onDestroy()
    }
}

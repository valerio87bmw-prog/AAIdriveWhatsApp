package io.openai.aaiwhatsapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class CarAppService : Service() {

    companion object {
        private const val TAG = "AAIWhatsAppCarApp"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "CarAppService created")
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        Log.i(TAG, "CarAppService started")

        val messages = WhatsAppMessageStore.getMessages()

        Log.i(
            TAG,
            "Messages available: ${messages.size}"
        )

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.i(TAG, "CarAppService bind requested")
        return null
    }

    override fun onDestroy() {
        Log.i(TAG, "CarAppService destroyed")
        super.onDestroy()
    }
}

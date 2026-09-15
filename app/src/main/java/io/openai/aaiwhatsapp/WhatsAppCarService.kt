package io.openai.aaiwhatsapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class WhatsAppCarService : Service() {

    companion object {
        private const val TAG = "WhatsAppCarService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "AAIdrive WhatsApp car service started")
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        val messages = WhatsAppMessageStore.getMessages()

        Log.d(
            TAG,
            "Car service active. Messages available: ${messages.size}"
        )

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.i(TAG, "AAIdrive WhatsApp car service stopped")
        super.onDestroy()
    }
}

package io.openai.aaiwhatsapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class WhatsAppCarService : Service() {

    companion object {
        private const val TAG = "AAIWhatsAppCar"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "AAIdrive WhatsApp car service created")
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        val messages = WhatsAppMessageStore.getMessages()

        if (messages.isEmpty()) {
            Log.i(TAG, "Connected to AAIdrive - no WhatsApp messages yet")
        } else {
            val latest = messages.first()

            Log.i(
                TAG,
                "Latest WhatsApp message: ${latest.sender}: ${latest.text}"
            )
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.i(TAG, "AAIdrive requested WhatsApp car service")
        return null
    }

    override fun onDestroy() {
        Log.i(TAG, "AAIdrive WhatsApp car service destroyed")
        super.onDestroy()
    }
}

package io.openai.aaiwhatsapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * Entry point used by AAIdrive / Car Connection.
 *
 * This service deliberately stays small:
 * - it exposes the WhatsApp extension to Car Connection
 * - it keeps the Android process alive while the car is connected
 * - the actual iDrive UI will live in the CarApp implementation
 */
class WhatsAppCarService : Service() {

    companion object {
        private const val TAG = "AAIWhatsAppCar"

        const val ACTION_CAR_CONNECTED =
            "io.openai.aaiwhatsapp.CAR_CONNECTED"

        const val ACTION_CAR_DISCONNECTED =
            "io.openai.aaiwhatsapp.CAR_DISCONNECTED"
    }

    private var carConnected = false

    override fun onCreate() {
        super.onCreate()

        Log.i(
            TAG,
            "AAIdrive WhatsApp Car Connection service created"
        )
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        Log.i(
            TAG,
            "Car service started: ${intent?.action}"
        )

        when (intent?.action) {

            ACTION_CAR_CONNECTED -> {
                carConnected = true

                Log.i(
                    TAG,
                    "BMW / AAIdrive connection active"
                )

                logAvailableMessages()
            }

            ACTION_CAR_DISCONNECTED -> {
                carConnected = false

                Log.i(
                    TAG,
                    "BMW / AAIdrive disconnected"
                )
            }

            else -> {
                Log.i(
                    TAG,
                    "Car Connection requested WhatsApp extension"
                )

                logAvailableMessages()
            }
        }

        return START_STICKY
    }

    private fun logAvailableMessages() {

        val messages =
            WhatsAppMessageStore.getMessages()

        Log.i(
            TAG,
            "Messages available to car: ${messages.size}"
        )

        messages.firstOrNull()?.let { message ->

            Log.i(
                TAG,
                "Latest message: ${message.sender}: ${message.text}"
            )
        }
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? {

        Log.i(
            TAG,
            "AAIdrive requested WhatsApp Car Connection"
        )

        /*
         * The actual Car Connection binder will be attached
         * here when CarApp is initialized.
         */
        return null
    }

    override fun onDestroy() {

        carConnected = false

        Log.i(
            TAG,
            "AAIdrive WhatsApp Car Connection service destroyed"
        )

        super.onDestroy()
    }
}

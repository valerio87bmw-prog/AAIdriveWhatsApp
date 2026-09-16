package io.openai.aaiwhatsapp

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

data class WhatsAppMessage(
    val sender: String,
    val text: String,
    val timestamp: Long
)

object WhatsAppMessageStore {

    private const val PREFS = "whatsapp_messages"
    private const val MAX_MESSAGES = 30

    private val messages =
        mutableListOf<WhatsAppMessage>()

    private var initialized = false

    @Synchronized
    fun initialize(context: Context) {

        if (initialized) return

        val prefs =
            context.getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
            )

        messages.clear()

        for (i in 0 until MAX_MESSAGES) {

            val sender =
                prefs.getString("sender_$i", null)
                    ?: continue

            val text =
                prefs.getString("text_$i", null)
                    ?: continue

            val timestamp =
                prefs.getLong("timestamp_$i", 0L)

            messages.add(
                WhatsAppMessage(
                    sender,
                    text,
                    timestamp
                )
            )
        }

        initialized = true
    }

    @Synchronized
    fun add(
        context: Context,
        message: WhatsAppMessage
    ) {

        initialize(context)

        messages.removeAll {
            it.sender == message.sender &&
            it.text == message.text
        }

        messages.add(0, message)

        while (messages.size > MAX_MESSAGES)
            messages.removeAt(messages.lastIndex)

        save(context)
    }

    @Synchronized
    fun getMessages(): List<WhatsAppMessage> =
        messages.toList()

    @Synchronized
    fun clear(context: Context) {

        messages.clear()

        context
            .getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
            )
            .edit()
            .clear()
            .apply()
    }

    private fun save(context: Context) {

        val editor =
            context
                .getSharedPreferences(
                    PREFS,
                    Context.MODE_PRIVATE
                )
                .edit()
                .clear()

        messages.forEachIndexed { index, message ->

            editor.putString(
                "sender_$index",
                message.sender
            )

            editor.putString(
                "text_$index",
                message.text
            )

            editor.putLong(
                "timestamp_$index",
                message.timestamp
            )
        }

        editor.apply()
    }
}

class WhatsAppNotificationListener :
    NotificationListenerService() {

    companion object {

        private const val TAG =
            "AAIdriveWhatsApp"

        private val supportedPackages =
            setOf(
                "com.whatsapp",
                "com.whatsapp.w4b"
            )
    }

    override fun onCreate() {
        super.onCreate()

        WhatsAppMessageStore.initialize(
            applicationContext
        )
    }

    override fun onNotificationPosted(
        sbn: StatusBarNotification?
    ) {

        if (sbn == null) return

        if (sbn.packageName !in supportedPackages)
            return

        val notification =
            sbn.notification

        if (
            notification.flags and
            Notification.FLAG_GROUP_SUMMARY != 0
        ) return

        val extras =
            notification.extras

        val sender =
            extras
                .getCharSequence(
                    Notification.EXTRA_TITLE
                )
                ?.toString()
                ?.trim()
                .orEmpty()

        val text =
            extras
                .getCharSequence(
                    Notification.EXTRA_BIG_TEXT
                )
                ?.toString()
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: extras
                    .getCharSequence(
                        Notification.EXTRA_TEXT
                    )
                    ?.toString()
                    ?.trim()
                    .orEmpty()

        if (
            sender.isBlank() ||
            text.isBlank()
        ) return

        val message =
            WhatsAppMessage(
                sender = sender,
                text = text,
                timestamp = sbn.postTime
            )

        WhatsAppMessageStore.add(
            applicationContext,
            message
        )

        Log.d(
            TAG,
            "Message captured from $sender"
        )

        sendBroadcast(
            Intent(
                "io.openai.aaiwhatsapp.NEW_MESSAGE"
            ).apply {

                setPackage(packageName)

                putExtra(
                    "sender",
                    sender
                )

                putExtra(
                    "text",
                    text
                )

                putExtra(
                    "timestamp",
                    sbn.postTime
                )
            }
        )
    }
    

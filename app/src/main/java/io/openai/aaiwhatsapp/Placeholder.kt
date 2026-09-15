package io.openai.aaiwhatsapp

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.app.Notification
import android.content.Intent
import android.util.Log

data class WhatsAppMessage(
    val sender: String,
    val text: String,
    val timestamp: Long
)

object WhatsAppMessageStore {
    private val messages = mutableListOf<WhatsAppMessage>()

    @Synchronized
    fun add(message: WhatsAppMessage) {
        messages.removeAll {
            it.sender == message.sender &&
            it.text == message.text
        }

        messages.add(0, message)

        if (messages.size > 50) {
            messages.removeAt(messages.lastIndex)
        }
    }

    @Synchronized
    fun getMessages(): List<WhatsAppMessage> {
        return messages.toList()
    }

    @Synchronized
    fun clear() {
        messages.clear()
    }
}

class WhatsAppNotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "AAIdriveWhatsApp"

        private val supportedPackages = setOf(
            "com.whatsapp",
            "com.whatsapp.w4b"
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        if (sbn.packageName !in supportedPackages) return

        val extras = sbn.notification.extras

        val sender =
            extras.getCharSequence(Notification.EXTRA_TITLE)
                ?.toString()
                ?.trim()
                .orEmpty()

        val text =
            extras.getCharSequence(Notification.EXTRA_BIG_TEXT)
                ?.toString()
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
                ?: extras.getCharSequence(Notification.EXTRA_TEXT)
                    ?.toString()
                    ?.trim()
                    .orEmpty()

        if (sender.isBlank() || text.isBlank()) return

        val message = WhatsAppMessage(
            sender = sender,
            text = text,
            timestamp = sbn.postTime
        )

        WhatsAppMessageStore.add(message)

        Log.d(
            TAG,
            "WhatsApp notification received from $sender"
        )

        sendBroadcast(
            Intent("io.openai.aaiwhatsapp.NEW_MESSAGE").apply {
                setPackage(packageName)
                putExtra("sender", sender)
                putExtra("text", text)
                putExtra("timestamp", sbn.postTime)
            }
        )
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Non eliminiamo il messaggio dallo store:
        // vogliamo mantenerlo disponibile nell'interfaccia iDrive.
    }
}

package io.openai.aaiwhatsapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 64, 48, 48)
        }

        val title = TextView(this).apply {
            text = "AAIdrive WhatsApp"
            textSize = 28f
        }

        val description = TextView(this).apply {
            text =
                "\nWhatsApp bridge for BMW iDrive through AAIdrive.\n\n" +
                "Reads WhatsApp notifications and makes messages available " +
                "to the AAIdrive car extension."
            textSize = 17f
        }

        val permissionButton = Button(this).apply {
            text = "ENABLE NOTIFICATION ACCESS"

            setOnClickListener {
                try {
                    startActivity(
                        Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    )
                } catch (e: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "Open Android notification access settings manually.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        val statusButton = Button(this).apply {
            text = "SHOW CAPTURED MESSAGES"

            setOnClickListener {
                val messages = WhatsAppMessageStore.getMessages()

                if (messages.isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        "No WhatsApp messages captured yet.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val latest = messages.first()

                    Toast.makeText(
                        this@MainActivity,
                        "${latest.sender}: ${latest.text}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        layout.addView(title)
        layout.addView(description)
        layout.addView(permissionButton)
        layout.addView(statusButton)

        setContentView(layout)
    }
}

# AAIdrive WhatsApp v0.1

Experimental AAIdrive/iDrive WhatsApp notification bridge for the BMW X1 F48.

## What v0.1 does
- Captures WhatsApp and WhatsApp Business notification text locally on Android.
- Keeps up to 30 recent notification messages on-device.
- Registers a separate **WhatsApp** Connected App through AAIdrive.
- Displays the latest messages on iDrive using the same proven RHMI/AAIdrive path as the Weather prototype.
- Does **not** read WhatsApp databases, credentials, or cloud data.

## Safety / limitations
- Only data exposed by Android notifications is available. Muted chats or hidden notification content may not appear.
- Voice-note audio and full chat history are not available in v0.1.
- v0.1 is deliberately read-only on iDrive. Quick reply and voice reply are staged for the next build after this connection/UI is validated on the car.
- Avoid interacting with messages while driving.

## Build from phone with GitHub Actions
1. Put this project in a GitHub repository.
2. Put your existing `AAIdrive.apk` in `external/AAIdrive.apk` (or the same SmartThings Classic APK used for the Weather build). It is used only at build time to extract BMW-signed RHMI resources and is not included in this ZIP.
3. Run **Actions → Build APK → Run workflow**.
4. Download the `AAIdriveWhatsApp-debug` artifact and install `app-debug.apk`.
5. Open the app and grant **Notification access**.
6. Receive a WhatsApp test message, then open AAIdrive → Extensions and verify **AAIdrive WhatsApp** is detected.

## Privacy
All captured notification text stays in Android app-private SharedPreferences. No network permission is requested and nothing is uploaded by this add-on.

# OCEAN Android (Debug)

This is a new Android companion project for testing. It is not a direct compilation of the Electron desktop application.

## Current debug scope
- IRCTC website inside a restricted WebView
- Locally stored passenger profile
- User-triggered passenger-field autofill assistance
- Booking progress messages

## Important limitations
- The user must sign in and complete CAPTCHA/security challenges, payment authorization, and final booking confirmation.
- The app does not bypass CAPTCHA, anti-bot controls, queues, or website restrictions.
- Passenger fields are filled only after the user taps **Fill passenger form**. Always review them before continuing.
- The Electron/Puppeteer desktop modules, direct booking HTTP clients, stealth injection, payment automation, and desktop session storage have not been ported to Android.

## Build
The GitHub Actions workflow builds `app-debug.apk` and uploads it as a workflow artifact on pushes to `android-debug-build`.


## Signed release workflow

A separate workflow is available at **Actions → Android Production Release**. It requires repository Actions secrets before it can sign a release APK:

- `ANDROID_KEYSTORE_BASE64`: base64-encoded Android release keystore file
- `ANDROID_KEYSTORE_PASSWORD`: keystore password
- `ANDROID_KEY_ALIAS`: key alias inside the keystore
- `ANDROID_KEY_PASSWORD`: key password

Create and securely back up your own keystore before configuring these secrets. Never commit the keystore, passwords, or signing keys to the repository. Losing the keystore can prevent publishing updates that replace the same app identity.

The release workflow runs Android lint and unit tests, assembles the release APK, verifies its signature, and uploads the APK as a short-lived workflow artifact. A successful CI build does not replace device testing, security review, privacy review, or compliance checks required before production distribution.

## Current production-readiness status

This Android project is a companion app, not a full port of the Electron/Puppeteer desktop app. It currently focuses on passenger data management, saved journeys, backup/restore, and user-triggered autofill assistance in an IRCTC WebView. It does not implement or promise full automated ticket booking. CAPTCHA/security checks, payment approval, and final booking confirmation stay with the user.

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

# OCEAN Android production readiness

## Scope and current status

The Android application is a native Android companion, not a direct build of the Electron desktop app. The desktop uses Electron/Node.js, Puppeteer, a SQLite-backed profile store, multiple browser/session runners, proxy management, ticket history, updater/license windows, and payment-related modules. Those Node/Electron modules cannot be treated as implemented on Android simply because the APK builds.

### Implemented in Android prototype
- Native Android UI and restricted IRCTC WebView.
- Local passenger profile fields.
- Journey fields and user-triggered passenger-field autofill.
- User remains responsible for sign-in, security challenges, payment approval, and final booking confirmation.

### Not yet at feature parity
- Desktop SQLite schema and complete journey/profile/credential/payment CRUD.
- Multiple passenger rows and reusable passenger groups.
- Train search/results parity, saved journeys, booking history/ticket details/printing.
- Desktop session manager, multi-run/pair controls, proxy settings, device-specific flows.
- License/update/support/news integrations.
- End-to-end device tests, accessibility tests, network/security review, and production acceptance tests.

Do not advertise this build as feature-complete until the remaining items are implemented or explicitly removed from the Android product scope and accepted.

## Production signing

The Android Production Release GitHub Actions workflow builds a release APK only when repository Actions secrets are configured:

- ANDROID_KEYSTORE_BASE64: base64-encoded Android signing keystore (.jks).
- ANDROID_KEYSTORE_PASSWORD: keystore password.
- ANDROID_KEY_ALIAS: signing key alias.
- ANDROID_KEY_PASSWORD: key password.

Never commit the keystore, passwords, or signing material to the repository. Keep a secure offline backup of the keystore; losing it can prevent updates to the same app identity. Configure secrets in GitHub repository Settings > Secrets and variables > Actions, then run Actions > Android Production Release > Run workflow.

## Release gate

Before publishing to real users:
1. Complete the accepted Android feature list and review each desktop feature for parity or an explicit non-portable reason.
2. Run gradle lint test assembleRelease with the release signing secrets.
3. Verify APK signature and install/upgrade behavior on supported Android versions.
4. Test WebView navigation, lifecycle/process recreation, offline/network failures, data persistence, keyboard/small screens, and accessibility.
5. Review privacy/security and IRCTC terms. Do not bypass CAPTCHA, queues, anti-bot controls, or payment authorization.
6. Perform a manual end-to-end booking preparation test with the user in control of all security/payment/confirmation steps.

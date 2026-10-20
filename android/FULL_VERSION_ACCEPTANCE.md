# OCEAN Android Full-Version Implementation Plan

This checklist is the acceptance contract for the Android port. It is intentionally separate from the desktop Electron implementation; building an APK does not imply desktop feature parity.

## Product baseline
- [x] Native Android project and CI debug build.
- [x] Journey fields and single passenger profile prototype.
- [x] Official IRCTC WebView entry and user-triggered passenger autofill prototype.
- [ ] Multiple passengers per journey, add/edit/delete/reorder and validation.
- [ ] Reusable passenger profiles and passenger groups.
- [ ] Saved journey CRUD, station swap, date validation, class/quota selections.
- [ ] Train search using a documented/authorized data source, result screen, sorting and error states.
- [ ] Booking history and ticket details only from user-provided/exported or officially accessible data.
- [ ] Import/export and encrypted local data backup/restore.
- [ ] Settings, privacy controls, data deletion and diagnostics.
- [ ] Accessibility, tablet/phone responsive layouts and dark/light theme.
- [ ] Automated unit/UI tests and real-device acceptance tests.
- [ ] Signed release build, signature verification, install/upgrade test and release notes.

## Desktop feature inventory to assess
The desktop repository includes Electron windows and IPC, SQLite profile storage, journey/credential/payment records, train service and results, session management, Chrome/Brave/Electron runners, multi-run/pair status, proxy management, booking history, ticket printing, license/update/news/support surfaces, and payment-related UI modules. Each module must be mapped to Android support, an Android-native replacement, or an explicitly out-of-scope item before the app can be called full parity.

## Safety and compatibility
- Never embed desktop Node/Electron code or private credentials into the Android APK.
- Do not reproduce stealth/fingerprint spoofing, CAPTCHA solving/bypass, queue evasion, or automated payment authorization in the Android app.
- Sign-in, official security challenges, payment authorization, and final booking confirmation stay user-controlled.
- Do not claim live train/availability information unless it is obtained from an authorized source and timestamped.

## Release blockers
1. Complete the Android feature checklist or agree in writing which desktop features are not part of Android.
2. Add automated tests and pass lint/unit/UI checks.
3. Configure Android signing secrets in GitHub Actions. Keep the keystore private and backed up.
4. Verify signed APK on supported physical devices, including update/upgrade path.
5. Complete privacy/security review and manual end-to-end acceptance test.

# Eleven 0.1.0 validation

First preview based on Ten 0.19.0, commit `e31109dd202b88d1476c4ba35b35d49cca1a7f62`. The Android application ID is `com.nthexperience.desktop.eleven`; source namespaces retain the inherited package for continuity.

## Build and policy checks

- Java 17, Android SDK 36; app, instrumentation, fixture and install probe build successfully.
- Ten unit tests: 10 passed across accent contrast, Calculator and local AI.
- Pure-Java geometry policy: 157,840 taskbar bounds cases, reserved controls and pin column policies passed.
- Nota command policy passed: unique-app resolution, ambiguity, manual settings/search routes and writing/imported-text boundaries.
- Packaged manifest check passed: independent Eleven ID, provider authorities, Home intent, protected notification listener and instrumentation runner.

## Native validation

Final accelerated CI run: [42495665707](https://github.com/bdredenbach/Windows-Eleven-Style-Android-Launcher/actions/runs/42495665707), commit `54c3ee0402cc75079388b3e8e615338a76e6a10f`. Both jobs completed successfully on November 8, 2026. The app code is unchanged from `f677776c053d60f6ccec5488f15e65d6ebda3b1d`; later commits corrected native input timing and evidence collection.

| Check | Android 11 / API 30 | Android 16 / API 36 |
|---|---|---|
| Build, 10 unit tests, manifest and policy checks | Pass | Pass |
| Eleven shell, 86 assertions | Pass | Pass |
| Nota commands and Settings routing | Pass | Pass |
| Integrated Browser and real file/video callbacks | Pass | Pass |
| Snap, drag/cancel, maximize/restore, Shake and keyboard | Pass | Pass |

The earlier API 30 restore-button assertion ran while System UI and touch delivery were still changing the work area. The final harness waits after the top-edge drag and synchronizes the injected UP event. The button receives DOWN and UP, and the original floating-bounds, maximize and double-tap restore assertions all pass. No app behavior was bypassed with `performClick`. The collected API 30 AndroidRuntime error log contains no crash.


The Eleven suite contains 86 assertions covering identity, phone Task View availability, Start/search/state/pin reorder, separate panel destinations and repeat dismissal, slider presence, Settings navigation, built-in window padding, Nota and Explorer routes. It covers portrait/landscape and light/dark modes. It is not a substitute for all manual acceptance items.

The shared native suites exercise actual Nota app-command routing, browser navigation/JavaScript/storage/tabs/file chooser/downloads/video/rotation/recreation, and real title-bar input for snap previews, side-by-side focus, drag-away, cancellation, maximize/restore, portrait behavior, Shake and keyboard placement.

## Visual review

Reviewed API 30 screenshots for Start, Quick Settings, notifications/calendar and Settings in portrait/landscape and light/dark, plus Nota, Explorer and Snap previews. The final API 30 artifact contains 20 screenshots. Panels stay within the available work area; narrow or short panel content scrolls. AOSP ATD API 36 does not reliably return screenshots, so its evidence is behavioral instrumentation and UI assertions.

## Distributed package

The downloadable APK is the tested CI app signed with the retained preview key. Its 449 non-signing archive entries match the CI package from the final successful run `42495665707`; only signing metadata differs. The APK signature verifies with v2 and v3. See `RELEASE_SIGNING.md` for update continuity.

APK SHA-256: `545b54be86e8f46c17919a152d482329d8b9a294af472631930fe0066a641706`

The ZIP contains the APK and install notes. It does not contain the signing key. Eleven installs separately from the other Nth Desktop editions.

## Scope and limits

No physical Pixel test or Windows VM session has been performed here. The local software-only Android 11 emulator showed a System UI ANR; it is not release evidence. Accelerated CI results are the native evidence. Ten's older validation records remain as history and do not count as new Eleven results.

Android 8.0 is the minimum configured SDK; runtime checks currently target Android 11 and 16. Guided permission denial/revocation, large text, gesture-navigation cutouts, every accent across every built-in utility and every real file-operation conflict require wider device acceptance. Full Snap groups/Assist, Start folders, Explorer tabs and newer Start layouts remain outside 0.1.0.

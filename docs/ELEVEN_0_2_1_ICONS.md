# Eleven 0.2.1: pristine source icons

46 icons from [HaydenReeve/WindowsIcons](https://github.com/HaydenReeve/WindowsIcons), pinned to `9442e3ef014520d4337409a24be10b970a36d66b`, replace the primary shell drawings and inherited Ten artwork. The mapping covers Explorer, folders, file types, Recycle Bin, Settings, Calculator, Calendar, Photos, Task Manager, browser, devices and utilities. Task View selects the supplied light/dark variants; Widgets uses its supplied panel icon.

Each PNG preserves the RGBA pixels of the largest supplied ICO frame. The import applies no resizing, recoloring, sharpening, cropping or background flattening. Android bitmap filtering handles display scaling. The 46 images total 891,012 bytes. Source bitmaps take precedence over the older Canvas fallback, including in window captions and menus, and are never theme-tinted. Nth branding, Nth Nota and installed Android app icons remain independent.

[The icon manifest](ELEVEN_ICON_ASSETS.json) records original paths, source blob IDs, ICO and PNG hashes, all frame sizes and the selected frame. [Asset notes](../ASSETS.md), [ownership notices](../THIRD_PARTY_NOTICES.md) and [the verbatim upstream README](../third_party/windows-icons/README.upstream.md) retain provenance. The collection describes individual icon use, but the pinned repository has no standalone license; this project asserts neither public-domain status nor an additional permissive license.

## Completed validation

Application source: `3e80fdd90ae06098a36a5d89ac6e98e5c769cfec`. Final validation head: `7f8c7037f7d67b93140c587bc4d14f5487947923`, which changes only an IME test wait. [Actions run 42529164950](https://github.com/bdredenbach/Windows-Eleven-Style-Android-Launcher/actions/runs/42529164950) passed both Android 11/API 30 and Android 16/API 36.

- 24 unit tests and all main/instrumentation builds passed.
- All 46 exports passed source checksums, largest-frame selection and RGBA transparency checks.
- Both platforms passed all six native suites: ElevenSmoke, TenCommandsSmoke, ElevenNotaSmoke, ElevenNotaColdSmoke, BrowserSmoke and SnapSmoke.
- Each platform passed 216 shell assertions, including 99 added assertions for source availability, exact untinted rendering at 32/128 pixels across both themes, Task View variant changes and retained Nth branding. Each retained Nota suite passed 171 assertions.
- Native screenshot review confirmed the imported artwork renders cleanly. Neither run recorded Android Runtime errors or ANRs.

An earlier Android 11 run reached an existing Nota keyboard assertion during an IME resize. The test now waits for 320 ms of stable, fully visible geometry before its original visibility/bounds assertions. No assertion was removed and no production Nota code changed.

## Delivered package

Version `0.2.1` (code `4`), package `com.nthexperience.desktop.eleven`, minimum API 26 and target API 36. APK SHA-256: `c80e90384a09c17a267c87559f4df15944fd062bf7815c71725494d6cecf964e`.

The retained preview certificate is `b704c7a2da2d0d4425fa5fe61c122483389b07be327ad565ff198064af696265`, matching 0.2.0/0.1.1 for installation as an update. APK v2/v3 signature verification and 16 KiB ZIP alignment passed. All 498 non-signing APK entries match both independent final CI builds and the retained-key signed deliverable. The test-only follow-up has identical application code and assets.

No physical Pixel 10 or production cloud-writing call was tested. Platform-owned controls and third-party app icons remain Android-owned. The icon update does not claim a numerical Windows UI match percentage.

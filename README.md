# Nth Desktop Eleven

A Windows 11 inspired Android launcher by **An Nth Experience**. Current preview: **0.2.0**. Android 8.0+; targets Android 16.

Package: `com.nthexperience.desktop.eleven`. Installs alongside Nth Desktop Ten, Seven and Eight.

## First preview

- Centered taskbar composition with a scrolling app strip, original N11 identity and blue folded-ribbon wallpaper.
- Start search above uniform app pins, real recommendations, All apps, profile, Settings and Power. Pin/unpin and reorder through a pin's menu or Ctrl+Left/Right.
- Separate Quick Settings and notification/calendar destinations. Quick Settings has volume and brightness; clock/bell opens notifications and a month calendar.
- Rounded floating windows and flush maximized/snapped windows. Hold maximize for viable half-screen placement options.
- Compact Explorer command bar over the inherited real file operations, linked-folder access and recovery.
- Settings category navigation, rounded cards and updated Eleven Start instructions.
- Ten's resizable windows, virtual desktops, browser, notes, media and backups.
- Nth Nota category summaries, instructions and composer hints, separate real replies, and a compact New command control.
- Full ordinary Nota drafts, task/origin/reply state and private restoration placeholders scoped to each virtual desktop.

## Build and validation

Use Java 17, Android SDK 36 and `./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest`. Native and shared-engine checks run with `bash checks/run-eleven-checks.sh` on a connected test device. GitHub Actions covers Android 11 and 16. The Eleven suite checks identity, Start/search/reorder/state, the split panels, Settings and retained tool routes across orientations and light/dark modes.

See [0.2.0 recording reference and Android mappings](docs/ELEVEN_0_2_RECORDINGS.md), [0.1.1 Nota validation](docs/ELEVEN_0_1_1_NOTA.md) and [0.1.0 shell validation](docs/ELEVEN_0_1_VALIDATION.md) for actual completed checks and limits. Historical Ten test drivers and documents remain as lineage; their appearance-specific assertions are not the Eleven regression contract.

## Scope

This is the first shell preview. Start folders, Explorer tabs, a graphical multi-zone Snap picker/Assist/groups, newer Start variants and additional utilities are later stages. The maximize hold menu currently offers the engine's viable left/right halves, maximize and restore; it is not a full Windows Snap implementation.

Built-in Nth tools use launcher windows. Installed Android apps use Android tasks. No Windows VM or Windows executable support is bundled.

## Research and lineage

Based on [Ten 0.19.0](https://github.com/bdredenbach/Windows-Ten-Style-Android-Launcher/tree/e31109dd202b88d1476c4ba35b35d49cca1a7f62). See [Windows 11 research](docs/WINDOWS_11_RESEARCH.md), [implementation plan](docs/IMPLEMENTATION_PLAN.md), [acceptance checklist](docs/ACCEPTANCE_CHECKLIST.md), [references](research/SOURCES.md), [notices](THIRD_PARTY_NOTICES.md) and [asset provenance](ASSETS.md).

Independent Nth project; not affiliated with Microsoft. Windows is a Microsoft trademark.

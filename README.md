# Nth Desktop Eleven

A Windows 11 inspired Android launcher by **An Nth Experience**. Current preview: **0.2.0**. Android 8.0+; targets Android 16.

Package: `com.nthexperience.desktop.eleven`. Installs alongside Nth Desktop Ten, Seven and Eight.

## Recording-based update

- Left or centered taskbar, scrolling app strip, original N11 identity and blue folded-ribbon wallpaper.
- Unified Start with Pinned and All in one scroll surface, alphabetical List/Grid views, search, profile, configurable folder shortcuts and Power. Pin/unpin and reorder through a pin's menu or Ctrl+Left/Right.
- Separate Quick Settings and notification/calendar destinations. Quick Settings has scrolling action tiles, brightness above volume and a battery/settings footer; clock/bell opens notifications and a month calendar.
- Rounded floating windows and flush maximized/snapped windows. Hold or hover over maximize for viable halves, thirds, two-thirds and quadrant layouts.
- Six independent Explorer tabs with history/query/selection/scroll restoration; a compact command bar below the address row; real file operations, linked-folder access and recovery.
- Persistent Settings sidebar, category landing cards and breadcrumb details, updated icons/menus/scrollbars and themed Calculator.
- Resizable windows, Task View with a bottom desktop strip, Widgets/Discover rail, browser, notes, media and backups.
- Nth Nota category summaries, instructions and composer hints, separate real replies, and a compact New command control.
- Full ordinary Nota drafts, task/origin/reply state and private restoration placeholders scoped to each virtual desktop.

## Build and validation

Use Java 17, Android SDK 36 and `./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest`. Native and shared-engine checks run with `bash checks/run-eleven-checks.sh` on a connected test device. GitHub Actions covers Android 11 and 16. The Eleven suite checks identity, Start/search/reorder/state, the split panels, Settings and retained tool routes across orientations and light/dark modes.

See [0.2.0 recording reference and Android mappings](docs/ELEVEN_0_2_RECORDINGS.md), [0.1.1 Nota validation](docs/ELEVEN_0_1_1_NOTA.md) and [0.1.0 shell validation](docs/ELEVEN_0_1_VALIDATION.md) for actual completed checks and limits. Historical Ten test drivers and documents remain as lineage; their appearance-specific assertions are not the Eleven regression contract.

## Scope

The supplied Windows 11 laptop recordings are the design reference. Touch targets and layouts adapt to Android density and portrait/landscape space. Android owns privileged device controls, lock screens, accounts and updates. The app uses Nth artwork and does not claim a numerical pixel-match percentage. Pinned-app folders and automatic Snap Assist/groups remain outside the implemented window layouts.

Built-in Nth tools use launcher windows. Installed Android apps use Android tasks. No Windows VM or Windows executable support is bundled.

## Research and lineage

Based on [Ten 0.19.0](https://github.com/bdredenbach/Windows-Ten-Style-Android-Launcher/tree/e31109dd202b88d1476c4ba35b35d49cca1a7f62). See [Windows 11 research](docs/WINDOWS_11_RESEARCH.md), [implementation plan](docs/IMPLEMENTATION_PLAN.md), [acceptance checklist](docs/ACCEPTANCE_CHECKLIST.md), [references](research/SOURCES.md), [notices](THIRD_PARTY_NOTICES.md) and [asset provenance](ASSETS.md).

Independent Nth project; not affiliated with Microsoft. Windows is a Microsoft trademark.

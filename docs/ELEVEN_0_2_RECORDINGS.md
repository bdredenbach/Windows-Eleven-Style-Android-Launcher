# Eleven 0.2.0: laptop recording reference

The three supplied recordings are the primary visual reference for this revision. All decoded through their complete duration without errors: H.264, 1920 × 1080, 30 fps. Durations are 300.096 s, 300.033 s and 243.819 s. They show a newer unified Windows 11 Start layout, dark Settings, left-aligned taskbar, and modern Explorer. No Windows guest image or Termux/VNC environment is required to inspect these recordings.

Reference review sampled every six seconds, then inspected full-resolution frames for Start, Explorer, Quick Settings, Settings and snapping. The recordings remain private inputs; they are not copied into the public repository.

## Reference inventory and implementation

| Recording/time | Observed design | Eleven implementation |
| --- | --- | --- |
| 224513, 00:03–00:51 | Unified Start: fixed search/footer, pinned grid, All list/grid in a single scrolling body; alphabetical headings | One scroll surface; persisted List/Grid view, letter jump dialog, pin reordering and context actions. Pinned/Recent/All visibility and menu size in Settings. |
| 224513, 00:15–00:39 | Personalization > Start settings, independent layout controls, folder shortcuts beside Power | Start cards and switches; configurable folder shortcuts in a horizontally scrollable footer on phones. |
| 224513, 00:48 | Power popover above footer | Anchored popup with functional desktop/restart/device power destinations. Android shutdown/lock privileges are not fabricated. |
| 224513, 00:57–01:09 | Win+X and Search flyouts | Modern menu labels; Suggested/recent local destinations in Search, then real app/settings/file results as text is entered. |
| 224513, 01:15–01:33 | Privacy & security > Search | Search remains explicit about its actual Android/local sources; web search is a chosen action. |
| 224513, 01:45 | Task View: app previews above, desktop preview strip and New desktop below | Desktop thumbnails moved to bottom, including the current single desktop; switch/rename/close/move and private preview protection retained. |
| 224513, 01:57–02:33 | Explorer tabs, address/navigation row, compact command bar, left navigation | Six independent tabs, each with its own history, query, selection and scroll state; session restoration; Ctrl+T/W/Tab; command bar reordered below address; Home/Gallery/This PC navigation. Tab changes wait for file operations to finish. |
| 224513, 02:09–02:33 | Legacy Folder Options and view menus | Existing actual file view/sort/hidden/navigation/preview controls retained. Android document providers replace Windows drive semantics. |
| 224513, 02:45–03:33 | Calculator and Edge | Existing functional native calculator and Chromium-based Android WebView browser retained, including tabs, downloads and isolated private profiles when available. |
| 224513, 03:45–04:21 | Widgets/Discover/settings rail, cards, weather and markets | Right-side board with navigation rail; Widgets/Discover/Saved/settings destinations. Cached/live weather, calendar, utility/game cards and real news feeds; market links open current reports. No fabricated quotes, sports scores or headlines. |
| 224513, 04:33–04:39 | Three-column scrolling Quick Settings, brightness then volume, battery/settings footer | Separate tile faces/captions, nine scrolling quick actions, live brightness/media sliders, mute and sound output destination, battery footer. Restricted radios open Android controls. |
| 224513, 04:45–04:57 | Settings Home recommendations and personalization | Home device card, useful recommendations and personalization/Nota/backup cards. |
| 224514, 00:03–01:27 | System cards, Display, HDR/color management, Sound, Notifications, Focus | Persistent profile/category sidebar, category landing pages, breadcrumb details, rounded cards. Display/sound/notification controls use actual launcher or Android functionality. Hardware-owned HDR/color calibration routes stay in Android Display settings. |
| 224514, 01:33–02:33 | Power & battery, storage capacity and storage management | Actual device battery/charging/saver state; capacity bar populated from storage stats; power/timeout/usage/storage management destinations open Android. |
| 224514, 02:45–03:45 | Advanced, Recovery, AI/system components, optional features, About | Category destinations for workspaces, recovery, Nota assistance, actual installed system components/device features and truthful device specifications. |
| 224514, 03:51–04:57 | Bluetooth/devices; Personalization, Background, Colors, Themes, Lock screen | Corresponding card pages; Light/Dark/Custom mode choice, transparency, accents, wallpaper/theme/font controls; lock screen settings stay under Android. |
| 224515, 00:03–01:21 | Text input, fonts, device usage; Apps, Accounts, Time/language, Updates | Fonts and local profile remain native; device input/accounts/time/update settings link to the correct Android owner. No Windows/Microsoft service state is simulated. |
| 224515, 01:33–01:45 | Drag and maximize snapping surfaces | Graphical maximize-button picker for halves, two-thirds, thirds and quadrants; hover/hold to open; targets disabled when content would be too small. Existing drag-edge snapping and floating restore sizes preserved. |
| 224515, 01:51–02:03 | Rounded dark desktop context menus and submenus | Rounded menu surfaces, flat hover rows and transparent icon gutters; existing real desktop/icon/file actions preserved. |
| 224515, 02:09–02:15 | Recycle Bin tab, compact restore/empty/view commands, details columns | Compact command bar below address, tab-like header, real restore/empty/properties/sort/view actions and recovery data. |
| 224515, 02:27–04:03 | Installed apps, advanced app settings, maps/video/startup/gaming/privacy | Settings categories use the recorded hierarchy where meaningful. Installation, defaults, permissions and other operating-system-owned features open Android; existing Nth games/tools remain functional. |

## Android adaptation rules

- Keep Nth branding and original icon/background artwork. This revision does not redistribute the laptop's Microsoft wallpaper or logos, or claim literal pixel equivalence.
- Use density-independent sizing, Segoe-compatible bundled fonts and larger touch targets. Portrait keeps the Settings sidebar and content visible side by side; Explorer collapses its search/navigation chrome as space requires.
- Left taskbar alignment is the default; Center remains available in Personalization > Taskbar. Existing theme and desktop preferences are preserved on update. Fresh installations start with the dark blue theme.
- Android applications run as Android tasks. In-launcher windows support the implemented snap layouts; external Android apps are not embedded into those rectangles.
- Retain the Eight-derived Nth Nota guidance, independent desktop drafts, private passage restrictions and explicit cloud consent from 0.1.1.

## Validation

Local: 23 unit tests, main/debug APK build and all instrumentation compilation pass. Tests cover snap partitions/bounds/minimum sizes, Unicode app headings, backed-up choice validation, prior Nota state/action behavior and calculator/accent behavior.

Native regression: Android 11/API 30 and Android 16/API 36 runs exercise Start, search/state/reorder, independent shell panels, brightness/volume, Settings navigation, orientation/themes, Explorer tabs/session restoration, snap/restore geometry, Nth Nota drafts/privacy/IME, Browser and existing edge snapping. Final run identifiers and outcomes are recorded after completed execution and screenshot review.

The reference is a design target, not a numerical 99% certification. Platform-controlled screens and third-party apps necessarily retain their Android behavior.

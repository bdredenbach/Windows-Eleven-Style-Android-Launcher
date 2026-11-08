# Ten reuse and Eleven implementation plan

This is a plan, not a record of completed app changes. Source inspected: Ten 0.19.0 at `e31109dd202b88d1476c4ba35b35d49cca1a7f62`.

## Import checkpoint

1. Import the pinned Ten source into this repository, preserving THIRD_PARTY_NOTICES.md, ASSETS.md and useful lineage documents.
2. Establish `com.nthexperience.desktop.eleven` as the separate application ID, a distinct app label/icon, matching provider authorities, fixture/probe targets and test runners. Audit literal Ten package references before the first install.
3. Replace the inherited README/build labels with Eleven scope. Keep inherited Ten validation as historical evidence, not Eleven test results.
4. Build the unmodified engine after identity separation. Run unit and native regression on Android 11 and 16, then add a real current Pixel test. Validate installation alongside Ten and no data/provider collisions.

## Reuse map grounded in actual source files

All paths below are relative to Ten's `app/src/main/java/com/nthexperience/desktop/`.

| Existing source | Keep | Change for Eleven |
| --- | --- | --- |
| `MainActivity.java` | Tool routing, lifecycle, workspace coordination | Shell/taskbar composition, new panel routes and product strings |
| `StartMenu.java`, `TenStartData.java`, `StartSearch.java` | Search/launch resolution, pins and recent targets | Replace tile/group presentation with Eleven pins, folders and All apps |
| `DesktopWindow.java`, `AeroSnap.java`, `WindowArrangement.java` | Drag/resize, saved geometry and placement constraints | Floating/flush corner states, Snap picker, assist/group state |
| `TenDesktops.java`, `TenDesktopItems.java`, `TenTaskView.java` | Per-desktop state and window migration | Eleven overview layout and desktop controls |
| `TaskbarGrouping.java`, `TaskbarOrder.java`, `WindowPreviews.java` | Grouping, ordering and preview behavior | Centered strip, stable tray space, indicator geometry and touch previews |
| `VistaExplorer.java`, `DocumentOps.java`, `StorageAccess.java` | Real file operations, URI grants and recoverable access | Compact command bar, Home view, tab-specific navigation |
| `RecycleFolder.java`, `RecycleBin.java` | Existing delete/restore data | Eleven chrome and selection commands |
| `TenSettings.java`, `TenSettingsCatalog.java` | Shared searchable routes | Eleven category map, cards, breadcrumbs and category selection |
| `TenStyle.java`, `TenAppearance.java`, `AeroGlass.java` | Existing theme bindings and accent contrast work | Separate Eleven semantic materials and light/dark roles |
| `TenActionCenter.java`, `TenNotifications.java`, `SystemTray.java` | Optional real notifications, redaction and device control plumbing | Split Quick Settings from notification/calendar views |
| `TenNewsFlyout.java` | Opt-in feed loading, cache and connection feedback | Widgets-board layout and local cards |
| `TenNotaPanel.java`, `TenNotaActions.java`, `LocalAI.java` | App-command handling and writing backend boundaries | Eleven panel styling and contextual surfaces |
| `BrowserPanel.java`, `PhotoGallery.java`, `MediaPlayback.java` | Working browser/photo/media engines | Consistent Eleven surfaces, icons and controls |
| `TaskManagerPanel.java` | Available telemetry and Nth task management | Navigation and performance view styling; honest unavailable metrics |
| `StartupExperience.java`, `StorageSetup.java`, `UsageAccessExample.java` | Guided setup and recovery | New identity and Eleven visual treatment |

Confirm file existence against the pinned tree before implementation; the research validation checks the names in this table.

## Suggested build order

| Stage | Deliverable | Exit condition |
| --- | --- | --- |
| 0 | Independent package and baseline CI | Installs alongside Ten; engines build and existing relevant behavior passes |
| 1 | Start, taskbar, branding and shared materials | Search, launch, pin, taskbar overflow, keyboard and Back work in both orientations |
| 2 | Quick Settings, notifications, calendar, Widgets | Separate panel ownership; repeat-tap dismissal; permission denial/revocation works |
| 3 | Window chrome, Snap layouts, Task View | Bounds and restore geometry remain valid through rotate/recreate and desktop moves |
| 4 | Explorer, Recycle Bin, Settings | Real file operations survive access changes; every Settings search result opens a working target |
| 5 | Built-in apps and Nota polish | Existing content and commands survive restyling; all colors are readable |
| 6 | Optional newer Start and utilities | Version-specific layouts are deliberate, tested options rather than mixed defaults |

## Decisions for the first build

- Use the familiar Windows 11 pinned-app composition first; keep the current scrollable Start reference documented for a later option.
- Default to centered bottom taskbar, light neutral surfaces and blue accent. Preserve a dark option and contrast-safe accent roles from the beginning.
- Preserve the user's prior working desktop behaviors: icon size/spacing, guided setup recovery, saved window geometry, independent desktop contents and Nota's app actions.
- Keep Android navigation/cutouts outside the usable work area. Resize content; do not make phone hit targets tiny to match a PC screenshot.
- Treat screenshots as references. Carry original Nth branding forward and audit any inherited Windows artwork before distribution.
- Preserve Nth's independent app window model. Android tasks are external tasks; shell Snap applies to built-in windows.

## Validation policy

Run the relevant inherited pure-Java checks and Android suites after importing. Update appearance assertions to the chosen Eleven behavior; do not suppress failures wholesale. Add tests for new stateful behavior such as pin folders, tab histories, separate panel ownership and Snap groups. Use visual review for reversible cosmetic tuning.

Report local checks, CI builds, native emulator runs and physical-device observations separately. A green Ten README or old screenshot archive is not evidence that Eleven passes.

# Eleven 0.3.0 — recorded interaction gaps

This batch continues the three supplied laptop recordings against the [remaining-work audit](ELEVEN_REMAINING_RECORDING_GAPS.md). It implements the Settings/navigation foundation, Explorer controls, cascading menus, drag Snap and timed Focus. It does not complete all thirty findings or certify a numerical pixel match.

## Resulting behavior

| Area | Implemented behavior | Android adaptation |
| --- | --- | --- |
| Settings | Back/Forward, Alt keys and Android Back; bounded history; page/query/scroll and expanded-card restoration; deep ancestors and narrow overflow. | At the root, Back retains the existing window behavior. Both sidebar and content remain visible on phones. |
| Cards and landing pages | Expandable groups, child dividers, selectors and related links; actual device/network summary, wallpaper and six theme previews; revised System/Apps order. | Further older pages remain to migrate. Nth profile/device data replace laptop/account values. |
| Sound and Display | Actual output/input device names, working media volume, grouped device controls, reported density/refresh/HDR capability and nested Night light/color routes. | Privileged controls remain Android-owned destinations. Launcher-only brightness describes its scope. |
| Apps and recovery | Searchable/sortable installed-app inventory, app links/defaults, actual Nth Run aliases, Nth startup/workspace/media routes and Advanced/Recovery grouping. | No invented app sizes/publishers, executable support, global app-startup control or Windows recovery services. |
| Explorer commands | All/Word/Excel/PowerPoint/Images/Videos/PDF filters, visible active state and Select all/none/invert, Properties and Options in overflow. | Actual MIME/extension matching; directories stay navigable. Filters are independent per tab. |
| Explorer View/Show | Eight views; Compact, item checks, extensions and hidden items; distinct metadata Details and Preview panes. | Panes sit beside the list on wide windows and below on narrow windows. Headings follow the list width and checkbox gutter. |
| Folder Options | General/View/Search tabs, opening/click/new-window choices, privacy/Home controls, default view and search choices; staged Restore Defaults, Apply, OK and Cancel. | Search describes its current-folder/name scope. History clearing is a separately confirmed immediate action. |
| Explorer Home | Actual local recent files/folders and linked favorites, with privacy controls and centered empty/no-match states. | Device access/setup remain reachable through This PC and linked navigation. |
| Desktop menus | Saved Auto arrange, Align to grid and Show icons; compact New/Terminal/Show more organization; adjacent touch/hover/keyboard cascades. | Menu widths adapt to phones. Custom positions remain available after Auto arrange is switched off. |
| Snap and movement | Top caption-drag strip, target highlight, privacy-aware preview, release/cancel and mixed half-plus-stacked-quarter groups shared with maximize picker. Shake reads Android's batched movement history. | Layouts appear only when their minimum sizes fit. Native checks include separate and explicitly batched Shake gestures. |
| Focus | Adjustable 5–240 minute session, start/stop/countdown, elapsed-time/boot-aware expiry and optional temporary Nth app-feed suppression. An expired Stop cannot start a new session. | Live sessions stay out of backups; manual quiet state is preserved. Android owns system DND; unsupported badge/flashing controls are not advertised. |

Pristine icon assets and Nth branding are retained. The laptop recordings and extracted personal frames remain private and are not committed.

## Validation

Application and validation source: `493451097e71f7d71973fa68399a7c165d0c2836`.

All **31 unit tests** and main/instrumentation APK builds pass. The **157,840 shell geometry checks**, Nota command-boundary check, **46 pinned icon checks** and installed identity/manifest check pass.

Android 11/API 30 and Android 16/API 36 each passed all **seven native suites**, including **216 shell**, **110 recording-gap** and **171 Nota** assertions, retained command/cold-process/browser checks and separate plus explicitly batched Shake gestures. Evidence: [run 42537246432](https://github.com/bdredenbach/Windows-Eleven-Style-Android-Launcher/actions/runs/42537246432).

The first API 36 attempt timed out waiting for UI idle at Quick Settings capture. A fresh emulator rerun passed with the same source and assertions. API 30 passed the full corrected source.

The seven native suites are ElevenSmoke, ElevenGapSmoke, TenCommandsSmoke, ElevenNotaSmoke, ElevenNotaColdSmoke, BrowserSmoke and SnapSmoke. Coverage includes actual Settings recreation and meaningful scroll restoration, card state, Focus expiry/manual-state preservation, adjacent keyboard/touch menus, injected drag Snap/drop/cancel, provider-backed Explorer filtering, independent tab state and staged Folder Options with real label/check-box/search effects. Retained Nota privacy/drafts, browser downloads/uploads/video and window checks remain enabled.

Screenshot review covered portrait/landscape and both themes, including the corrected adjacent phone menus, three-tab options dialog and column alignment beside Details. Android 16's headless native screen capture returned black frames; app-view captures and Android 11 native captures supplied visual evidence.

An earlier Android 11 Shake check exposed slow synchronous injection producing a long press. Queuing the gesture exposed coalesced movements; the production detector now consumes historical samples, with a separate explicit batched-gesture restore check. No minimize/restore assertion was removed.

## Delivery status

The execution environment disconnected before the final retained-key APK could be produced and saved. Source and CI builds are preserved in GitHub. A final update APK using Eleven's existing preview signing key remains pending; CI debug packages should not be treated as a matching-key update over an installed preview.

## Remaining work

Recycle Bin multi-selection/metadata/View; notification sender controls; battery/taskbar percentage and power details; deeper Colors/Background/Themes/Start/Taskbar/lock-screen composition; Widgets; account/privacy/gaming details; modern browser toolbar/overflow, plus remaining small tool-title/menu icon aliases. See the audit progress table.

No physical Pixel 10, production cloud-writing request, external system DND authorization or numerical visual-match certification is claimed.

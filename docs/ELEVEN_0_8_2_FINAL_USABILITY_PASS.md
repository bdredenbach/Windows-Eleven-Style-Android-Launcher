# Eleven 0.8.2 — desktop clearance, menus and upgrade workflows

Expanded-taskbar desktop tiles now reserve their actual measured two-line label height and a usable minimum icon area, including larger resource font scales. The desktop keeps the normal taskbar grid anchor and saved position bytes; scrolling brings the lowest labels above the expanded taskbar. Expansion does not resize the desktop grid. The 0.8.1 upper running-app row and lower fixed controls are retained.

Desktop and Start shortcuts plus taskbar buttons consistently accept Menu or Shift+F10. Escape closes an entire menu cascade; Left returns from a submenu to its parent. Long labels receive measured menu row heights. Start app options now use the shared themed menu, including Open, pin/unpin, reorder and More options. Non-shortcut results retain their original pin eligibility.

## Recording audit status

The original audit documents preserve historical findings, rather than listing current unfinished features. This table points to the implementations retained in this release.

| Recording findings | Current implementation record |
| --- | --- |
| G01–G09, G11–G14, G18, G24–G26, G29 | [0.3 foundation](ELEVEN_0_3_RECORDING_GAPS.md), [0.4 themes and page depth](ELEVEN_0_4_THEMES_AND_AUDIT.md) |
| G10, G15–G17, G21–G23, G27–G28, G30 | [0.5 completion and purchaser workflows](ELEVEN_0_5_COMPLETION_AND_WALKTHROUGH.md) |
| Themes/Aero, Task Manager, secondary glyphs and counts | [0.4 themes](ELEVEN_0_4_THEMES_AND_AUDIT.md), [0.6 Task Manager](ELEVEN_0_6_TASK_MANAGER.md) |
| G31–G33: Start Category, weather/card menus, embedded markets | [0.7 recording review and markets](ELEVEN_0_7_MARKETS_AND_RECORDING_PASS.md) |
| Expanded taskbar and fixed lower controls | [0.8 expansion](ELEVEN_0_8_EXPANDED_TASKBAR.md), [0.8.1 row correction](ELEVEN_0_8_1_TASKBAR_ROWS.md) |

This pass revisited original recording frames around desktop cascades and Recycle Bin (224515 01:55, 01:58, 02:12), Start All views (224513 00:00, 00:03), and Widgets (224513 03:46), alongside the earlier full-duration sampled review. Private recordings/frames stay outside this repository. The review supports the targeted menu and text corrections; it does not certify pixel identity or exhaust every frame.

## Verification

Validation is in progress. The CI matrix runs Android 11/API30 and Android 16/API36, with 48 existing unit tests and 20 native suites (21 phases when the preceding-release artifact is available).

The new final-pass suite uses actual launcher views, injected lowest-folder taps and touch long presses, real keyboard menu paths, edge-positioned light/dark menus, cascade dismissal, and portrait/landscape layouts at normal and 1.6x in-process resource font scale. Resource font scaling is test configuration, not a physical device's accessibility-settings flow.

The new upgrade suite first installs the actual retained 0.8.1 build, seeds a note/theme/taskbar/desktop layout and backup, then installs the current build with `adb install -r`. It compares retained values and actual normal-text icon bounds, restores the previous-version backup, verifies undo, and reopens Notepad after a fresh Activity launch. Both APKs use the same disposable CI signing key for that emulator job. The delivery keeps the retained preview certificate. The historical baseline download is optional for future CI runs after artifact expiration; an unavailable baseline is explicitly reported as SKIP, never a verified upgrade.

Retained suites exercise fresh setup, Home/permission guidance, Settings catalogue traversal, built-in windows, Explorer/provider file operations, theme contrast, Task Manager, Widgets/markets, Nota, browser, Snap, feeds, photos and media. Physical Pixel 10 testing, physical mouse/keyboard hardware and production cloud-writing calls remain outside this automated pass. Android still owns privileged settings/accounts/lock screens. Market providers can restrict or delay symbols, and older WebViews retain the explicit update route.

Pinned-app folders and automatic Snap Assist/group selection remain separate roadmap items; neither was established as a missing action in these supplied recording segments.

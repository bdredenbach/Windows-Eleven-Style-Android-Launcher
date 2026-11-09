# Eleven 0.9.0: complete handoff and owner screenshot corrections

This update includes the complete fix commit `a4582b2fd34aa6e8c4fd797bb7c2e9975ef87ecc` on the matching 0.8.2 baseline. Brad approved updating `main` and delivering the signed APK on November 9, 2026.

## Screenshot corrections

| Report | Implementation |
| --- | --- |
| N¹¹ icon is misaligned/cropped in Android's installer | Rebalanced vector wordmark; separate mask-safe glyph for adaptive and monochrome foregrounds, with the same mark in the shell. The blue adaptive background fills Android's mask. |
| Upper taskbar apps ignore Left alignment | The app strip has its own bounds. Running buttons begin at the cluster's left edge; bottom pins begin after the fixed Start, Search, Task View and Nota buttons. Center remains available. |
| Missing taskbar magnifier | Portrait now keeps Search unless the user explicitly selects Hidden. Auto retains the wider field on large displays. Suggestions, recent searches, real local app/settings/linked-file results and explicit web/Nota actions remain available. |
| Show more options has the wrong commands | It replaces the modern menu with a square classic menu: View, Sort by, Refresh, Paste, Open in Terminal, New, Display settings and Personalize. View/Sort/New retain working submenus. Laptop-specific Git shell extensions are absent when no corresponding Android integration exists. |
| Start profile/power area differs from recording | Full-width footer with a smaller portrait/name, account options and an always-visible Power button beside a separate folder-shortcut scroller. Popovers open above the footer. Search uses its results surface without Start's footer. |
| Show desktop is too far right to press | A 24 dp focusable target stays inside the system-bar/cutout-safe taskbar. Tap minimizes actual Nth windows; tap again restores them. |
| Expanded taskbar covers folder labels | The grid reserves the maximum taskbar footprint in both modes. Automatic rows fit that area and do not jump when expansion changes. Saved position values remain unchanged; deliberately overflowing/free-positioned layouts can still scroll. |

The fixed taskbar controls remain alive, at the same bottom-row bounds, through height toggles. Both app rows retain their own ordering, previews and static/scrolling behavior. Bottom pins do not paint through the fixed controls while scrolling.

## Handoff integration

The full cache, partial cross-desktop restore/undo, restored-folder repair, optional ordered setup, approximate/precise location and Start uninstall changes are retained. Installations predating the ordered setup use the existing completed Welcome state to offer Location once, without repeating prior permission prompts. No new signing identity is generated.

Start's power popover adapts Windows' privileged commands to real Android destinations: Screen lock settings, Screen timeout, Android power settings and Restart Nth Desktop. It does not claim to shut down, lock or restart Android without OS privileges. The recorded account name and portrait remain the user's local profile choice.

## Validation

The complete Android 11/API30 and Android 16/API36 matrix passed at source `5d4dd4584569c5ab059954332bd936db8c59af73`: [run 42699523081](https://github.com/bdredenbach/Windows-Eleven-Style-Android-Launcher/actions/runs/42699523081). Each platform passed 106 unit tests and 22 native execution phases, including 147 owner-screenshot assertions, 575 final-usability assertions, 265 purchaser-walkthrough assertions, actual 0.8.2 upgrade/restore/undo and the native ordered setup with approximate-only location. Runtime captures contain no fatal exception. Final documentation updates do not change application or test code.

The new native `ElevenScreenshotSmoke` covers portrait/landscape, Left/Center, magnifier input/results, the upper-row origin, initial 5×9 label clearance, Show desktop touch/restore, full-width footer, Power with all shortcuts enabled, profile options and replacement classic-menu commands. It captures app-view evidence and the Android adaptive application icon. Existing geometry, theme, taskbar stability, real prior-APK upgrade, restore/undo and app suites remain in the workflow. Ordered setup, coarse-only location and uninstall/cancellation continue to have unit coverage.

The update regression uses the actual 0.8.2/code12 APK and the new 0.9.0/code13 APK. It checks retained preferences and saved coordinates; the one-time grid-height recalibration for taskbar clearance is an intentional visual change. Production delivery is externally aligned and signed with the retained Eleven preview key, then verified against its established certificate. Disposable CI signing is limited to emulator update checks.

The inherited storage/purchaser setup tests now follow the ordered flow rather than waiting for the superseded automatic Home/Explorer prompts. Native setup exercises the actual Android Home request and cancellation, file-step recreation, optional permission deferrals, the native approximate-only location grant and completed Welcome/no-repeat behavior. Existing manual Usage Access recovery, denied-file recovery and native resize checks remain.

Private reference recordings `224513.mp4` (Start 3–51s, Search 57–69s) and `224515.mp4` (desktop menu 111–123s) were revisited. Recordings and reference frames are excluded from public source and delivery. This is a native regression and visual comparison, with physical Pixel 10 confirmation remaining on the owner's device.

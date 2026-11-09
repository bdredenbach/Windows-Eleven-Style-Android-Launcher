# Eleven 0.4.0 — recording audit and appearance

## Reference pass

Rechecked all three private recordings with full-timeline ten-second contact sheets and additional full-resolution details, including the Colors page at `224514` 04:10 and Themes at 04:33. The earlier thirty-item audit remains the baseline. This is a frame-based review, not a numerical visual-match certification.

The six prominent theme previews on the recorded Personalization page are dark/light blue folds, the scenic Spotlight collection, Glow, Captured Motion and Sunrise. The larger Themes gallery also shows green bubbles and pale Flow. Eight corresponding choices now appear together. The recording does not expose the names or source of its personal/OEM bubble preset. Nth Spotlight rotates bundled Nth scenery rather than claiming access to Microsoft's Spotlight service.

All wallpaper additions are original Canvas artwork. Their composition and palettes are counterparts to the recordings, not Microsoft's original wallpaper files. Older Eleven Blue/Light/Midnight/Spectrum and the other Nth/classic Aero presets remain available: 14 modern presets plus nine classic presets, 23 total. Spectrum now actually uses a purple palette.

## Changes

- One **Transparency effects** switch in Colors, also available from Visual effects. Modern window captions, Settings backdrop, Explorer command/address rows, menus, taskbar, Start, Quick Settings and notifications use softened wallpaper tinting with a solid fallback. This approximates the recorded Windows 11 Mica/Acrylic effect. Classic Aero keeps its separate finish and tint/opacity controls.
- App menus use the app palette; Start, taskbar and shell panels use the independent Windows/shell palette. The Start-button menu follows the shell palette. Shared hover states, taskbar-preview captions and Widgets text were included in the pass.
- Background preview, Picture/Solid color/Slideshow selector, thumbnails, browse, fit and the existing staged multi-picture/interval/shuffle editor are available in Settings. Solid backgrounds survive theme export/import and desktop backup.
- Current-theme preview and working Background/Color/Sounds links precede the theme gallery. Saved/imported themes remain supported.
- Colors adds recent accents, a validated hexadecimal custom color with a literal preview, and Manual/Automatic accent choice. Automatic samples the actual wallpaper; foreground colors remain independently contrast corrected.
- Widgets has a real in-board Settings destination, correct page titles and selected rail state, Discover and hover preferences, card selection and the retained feed configuration. Light shell text/cards now use a light palette; weather and image overlays keep their own readable foregrounds.
- Appearance changes retain open native window instances and document selection. Private foreground content is not sampled for the material buffer.

## Audit status

| Findings | Current status after this round |
| --- | --- |
| G01–G09, G11–G14, G18, G24–G26, G29 | Retain the 0.3.0 implementation and its listed Android adaptations. |
| G19–G20 | Background/Themes/Colors composition and working local choices expanded in this round. |
| G23 | Settings/navigation/card choices and theme behavior expanded. Denser daily forecast and provider-backed sports/history/market data remain. Market cards still open report links. |
| G10 | Recycle Bin multi-selection, metadata and full View/Show remain. |
| G15–G17 | Notification sender filters, taskbar battery percentage and deeper power/storage composition remain. |
| G21–G22 | Additional Start recent/profile/new-app choices, grouped All-apps grid, Search history and Search-settings entry point remain. |
| G27–G28 | Further account/privacy/gaming composition remains. Android continues to own lock screens, keyboard themes and privileged settings. |
| G30 | Modern browser toolbar/overflow presentation remains; retain the current browser engine and privacy behavior. |

No new evidence from this pass establishes missing Start pin folders, automatic Snap Assist or saved Snap groups; those were not demonstrated in the supplied videos. The prior audit's Android-owned differences still apply. No personal recording frames are committed.

## Validation

Added an Eleven appearance regression covering every preset in portrait/landscape, menu contrast, all four app/shell mode combinations, the accent palette, transparency, note/selection retention, solid-background pixels, portable themes/backups, custom/automatic accents and Widgets settings. Existing shell, Explorer/Snap/Focus, Nota and browser suites remain enabled for Android 11 and Android 16. The final run passed on both API levels: [42548515367](https://github.com/bdredenbach/Windows-Eleven-Style-Android-Launcher/actions/runs/42548515367), application/validation source `10a2376c5529219e7e6718e38fbe957659b6eafe`.

All **34 unit tests**, main/instrumentation APK builds, **157,840 geometry checks**, Nota command boundaries, **46 pinned icon checks** and installed identity checks pass. Each API level passed all eight native suites, including **4,417 appearance assertions** over **46 preset/orientation combinations**, **216 shell**, **110 recording-gap** and **171 Nota** assertions. The appearance suite also checks slideshow draft restoration, actual Save/Cancel behavior, literal solid-background pixels, theme packing and backup validation.

Native screenshots on Android 11 and app-view captures on Android 16 were visually reviewed, including menu cascades, dark/light shell panels, portrait Colors and the recorded theme counterparts. Android 16 headless system captures remained black; its app-view captures and native behavior assertions are the evidence for that API level. New previews scale down in landscape; mode-specific selectors appear under Custom.

The delivered 0.4.0 APK (code 6) uses the retained preview certificate `b704c7a2da2d0d4425fa5fe61c122483389b07be327ad565ff198064af696265`. Signature schemes v2/v3 and 16 KiB ZIP alignment pass. All 537 non-signing ZIP entries match both independent CI builds. APK SHA-256: `e430f651f7e1c820b04aad75238f098ebe1c30242b9bab0f10cbc13b5268e6e7`.

No physical Pixel 10 or numerical visual-match percentage is claimed. The new artwork and Android material rendering are explicit approximations.

Microsoft references: [materials and solid fallbacks](https://learn.microsoft.com/en-us/windows/apps/develop/ui/materials), [personalize colors](https://support.microsoft.com/en-US/Windows/Experience/Personalization/personalize-your-colors-in-windows), [theme customization](https://support.microsoft.com/en-us/windows/experience/personalization/personalize-your-windows-experience-with-themes). The Windows 11 material names describe the visual reference; this app renders its own Android approximation.

# Eleven 0.5.0 — remaining controls and purchaser walkthrough

This records the 0.5.0 status of the 0.3.0 recording audit and the 0.4.0 appearance follow-up. See [0.6.0 Task Manager and polish](ELEVEN_0_6_TASK_MANAGER.md) for the subsequent round. Earlier audit documents preserve the state of those releases. The original recordings and extracted personal frames remain outside the repository.

## Implemented in this round

| Audit | Result |
| --- | --- |
| G10 | Recycle Bin has independent checkbox selection, restore/delete selected, select all/none/invert, six metadata columns, eight layouts and View/Show choices. New recovery manifests retain the original modification time. Older missing metadata is labeled unavailable. Image preview reads only the private recovery payload. Delete confirmations capture the reviewed selection. |
| G15 | Searchable notification senders, app icons, local feed switches and per-app Android notification settings. Filtering does not disable Android delivery. Clear visible notifications preserves filtered notifications. Revoked access fails closed. |
| G16 | Actual battery header, taskbar percentage preference, energy recommendations, power/timeout groups and Android-owned usage details. No invented history graph or acceleration control. |
| G17 | Actual storage capacity, nested categories, advanced links and enumerated Nth cache/recovery cleanup. Both cleanup categories start unchecked. Personal Downloads are outside its scope. The removal uses the reviewed snapshot and preserves cache files whose canonical path, length or modification time changed. |
| G21 | Recently added apps use actual package installation times. Recent-file and New-label choices, profile visibility and alphabetically grouped All-apps Grid are implemented. A New label clears after a successful app launch. |
| G22 | Search-settings shortcut, bounded completed-query history, clear/disable controls, source switches and included/excluded folder roots. Typing alone does not store a query. History and seen-app activity are not portable backup settings. |
| G23 | Daily ranges from actual forecast samples, optional NHL scores and Wikipedia On this day cards, independent provider choices and explicit loading/error/cached/update states. Existing news, hourly weather, card selection and board navigation remain. |
| G27 | Accounts includes the actual local profile image and Nth backup/security destinations. Choose profile picture now opens the image picker. Time/date/zone and Android account ownership retain the previous implementation. |
| G28 | Device interests change real Settings Home recommendations. Input/permission destinations, local activity clearing, grouped games/capture pages and the real Nth Steps Recorder are connected. Steps Recorder has its own window identity and taskbar restoration. Android owns keyboard themes, app permissions and device game performance. |
| G30 | Reload precedes the browser address field. Home/Favorites move into the overflow. The persistent bottom strip is removed. Overflow contains actual text-size controls and supported keyboard shortcuts; the existing browser engine and private profiles remain. This is text sizing, not whole-page zoom. |

The 23 theme presets, independent app/shell palettes and shared Transparency effects control from 0.4.0 remain. Existing G01–G09, G11–G14, G18–G20, G24–G26 and G29 implementations are retained and exercised again by the regression suite.

## Explicit differences and remaining content work

Market cards continue to open reports. Embedded index prices and market graphs remain unimplemented; a suitable redistribution-permitted provider is still needed. They are not replaced with sample or scraped prices. The NHL card uses official score data with a last-update label; it is not a guarantee of continuous live updates.

Windows account billing, Rewards, OneDrive, Windows Hello, PC power modes, OS reset, keyboard themes and privileged phone controls retain their documented Android/Nth adaptations. These are not represented as working Windows services.

## Regression and purchaser coverage

The workflow starts by clearing the disposable emulator's app data. The first-run suite follows the actual introduction and Android Home offer, defers file/usage access, revisits the guidance, grants/revokes actual Usage Access and verifies persisted resize bounds. A purchaser-style native walkthrough then explores every catalogued Settings page in portrait and landscape, 20 built-in destinations, minimize/restore, Notepad draft retention, Widgets, notifications, Task View and activity recreation. It includes injected taskbar touches and checks actual rendered controls; catalogue traversal is automated, not a claim that every possible user gesture was manually exercised.

Completion checks use real document-provider files and compare restored bytes, preserve an unselected recovery copy, exercise cancellation and selected permanent deletion, verify cleanup boundaries, completed-search privacy, real Android notification-listener filtering and browser text sizing. Public provider probes record CONNECTED or UNAVAILABLE separately from deterministic parser/state assertions. Broader suites retain all-preset theme contrast, shell/Explorer/Snap/Focus, Nota, browser, feed transport/retry, native shared-storage grants and photo decoding/navigation checks.

Every independent native suite is run before failures are reported, so one failed area does not hide the rest. Emulator screenshots and an area-visit ledger are collected. Android 16 headless system screenshots may be black; app-view captures and behavior assertions are used there, alongside Android 11 system screenshots.

Final matrix: [42555998124](https://github.com/bdredenbach/Windows-Eleven-Style-Android-Launcher/actions/runs/42555998124), application/validation source `646575d61533f36ae5ca5995f1006d74a4946df7`. **Both API30 and API36 jobs passed all 15 native suites and all 37 unit tests**, plus 157,840 geometry checks, Nota command boundaries, 46 pinned icon checks and identity validation. Each API passed 265 purchaser, 94 completion, 4,417 appearance, 216 shell, 109 recording-gap and 171 Nota assertions, alongside the browser, feed, storage, photo and media suites.

Both APIs connected to NHL (6 actual rows), Wikipedia (10 entries), BBC News and TechRadar (12 headlines each). Android 11 native screenshots and Android 16 app-view captures were reviewed. No captured FATAL EXCEPTION was found. Physical Pixel 10 and production cloud-writing calls were not exercised.

Delivered APK: code 7, `11769657` bytes, SHA-256 `c4b884d1cfe4fadb1eb6d5a83bf2c03a0c9bb90eb163be019419bc453dc1bc38`. The retained preview certificate is `b704c7a2da2d0d4425fa5fe61c122483389b07be327ad565ff198064af696265`; v2/v3 signing and 16 KiB ZIP alignment pass. All 537 non-signing entries match both independent CI builds and the delivered APK.

Minor cosmetic follow-up: singular/plural item counts and a few secondary menu glyph choices. These do not reopen the functional controls completed here.

## Provider references

- [MET Norway forecast data model](https://docs.api.met.no/doc/locationforecast/datamodel.html): forecast samples are grouped using the device's local time zone. Displayed ranges cover available samples, rather than claiming complete daily extrema.
- [Official NHL score response](https://api-web.nhle.com/v1/score/now) and [NHL scores](https://www.nhl.com/scores): team abbreviations, actual scores, schedule/final status and game-date destinations. No team logos or betting content are imported.
- [Wikimedia Wikifeeds](https://wikitech.wikimedia.org/wiki/Wikifeeds): On this day selected events with source links and CC BY-SA attribution. Provider loading is optional and can be disabled in Widgets Settings.

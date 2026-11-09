# Eleven 0.5.0 — remaining controls and purchaser walkthrough

This is the current status of the 0.3.0 recording audit and the 0.4.0 appearance follow-up. Earlier audit documents preserve the state of those releases. The original recordings and extracted personal frames remain outside the repository.

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

Final run results and delivered APK verification will be recorded after the matrix completes.

## Provider references

- [MET Norway forecast data model](https://docs.api.met.no/doc/locationforecast/datamodel.html): forecast samples are grouped using the device's local time zone. Displayed ranges cover available samples, rather than claiming complete daily extrema.
- [Official NHL score response](https://api-web.nhle.com/v1/score/now) and [NHL scores](https://www.nhl.com/scores): team abbreviations, actual scores, schedule/final status and game-date destinations. No team logos or betting content are imported.
- [Wikimedia Wikifeeds](https://wikitech.wikimedia.org/wiki/Wikifeeds): On this day selected events with source links and CC BY-SA attribution. Provider loading is optional and can be disabled in Widgets Settings.

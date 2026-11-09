# Eleven 0.7.0 — markets and another recording pass

This round embeds TradingView's official display-only Market Overview widget. It retains the provider's attribution and links, and never extracts quotes, calls undocumented quote APIs or bundles sample market values. Data can be delayed or unavailable for a particular symbol. The default clearly labeled Index ETFs view shows SPY, QQQ, DIA and IWM; Indices selects S&P 500, Nasdaq Composite, Dow Jones and Russell 2000 subject to the provider's display permissions. ETF prices are not index values.

The card has native symbol/range selectors, retry, report links and an off switch. Loading is optional in the card and Widgets Settings. Only the enabled choice, symbol set and range are portable settings. Quote values, chart history, provider web storage and cookies are excluded from desktop backups. Each provider WebView denies local-file/content access, mixed content, device permissions and non-HTTPS navigation. There is no native JavaScript bridge. Views are destroyed while hidden/backgrounded or when their board is closed; ordinary board rendering retains the displayed widget.

## Fresh recording review

Fresh frames were decoded directly from all three recordings at 15-second intervals across their full durations, followed by full-resolution frames of the changing Start view and Widgets board. The original recordings and review images remain private inputs outside the repository. This is a sampled visual/source review, not a numerical pixel-match certification.

| Finding | Recording evidence | Change |
| --- | --- | --- |
| G31 | 224513 00:00–00:03 | The All menu offers Category, Grid and List. Category had been missing. Add the category tile mosaic, real app actions and full category lists. Android-declared categories classify installed apps; unknown apps remain Other. Nth tools have explicit categories. Selection is saved and portable. |
| G32 | 224513 03:43–04:20 | The board leads with weather and uses per-card ellipsis menus. Move weather into the main Widgets lane, use equal lanes when wide enough, stack on narrow phones, and add Hide/Customize commands to weather, markets, calendar, scores and history cards. Hidden cards remain recoverable through Choose widgets. |
| G33 | 224513 03:45–03:51 | The market card includes actual values and percentage changes. Replace the report-only card with an official price/chart embed plus persistent report links. ETF and index modes are explicit; provider availability/delay labels stay visible. |

Existing 0.3–0.6 implementations are retained: Settings hierarchy/navigation, Explorer commands and file operations, Recycle Bin selection/recovery, Snap, Focus, 23 presets, Transparency effects, notification filters, storage/power controls, Start/search choices, Nota, browser, media and modern Task Manager.

The integration pass also found that minimizing the browser called the process-wide `WebView.pauseTimers()`, which could freeze other WebViews. Timer ownership now belongs to the Activity foreground/background lifecycle; per-window pause/resume remains local. A native regression checks an actual market-display timer continues after the browser window is minimized.

## Verification

Pending the complete Android 11/API30 and Android 16/API36 matrix. New unit checks cover fixed provider configuration, injection-resistant options, safe navigation, portable settings and honest app-category mapping. Native checks use a separately labeled local document to test display/lifecycle and record the official provider probe independently. A created iframe alone is not proof that all quotes are available; native screenshots must be reviewed.

## References

- [Official TradingView widgets](https://www.tradingview.com/widget/) and [integration documentation](https://www.tradingview.com/widget-docs/getting-started/).
- [Market Overview embed configuration](https://www.tradingview.com/widget-docs/widgets/watchlists/market-overview) and [data availability/delays](https://www.tradingview.com/widget-docs/faq/data/).
- [Attribution and terms](https://www.tradingview.com/policies/) and [widget privacy FAQ](https://www.tradingview.com/widget-docs/faq/general/). Attribution is retained as provided; the app offers direct display only.

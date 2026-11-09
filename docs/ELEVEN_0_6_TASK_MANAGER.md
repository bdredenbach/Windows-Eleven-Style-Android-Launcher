# Eleven 0.6.0 — Task Manager and polish

The inherited Task Manager still used Vista-style menu/tabs and green monitor graphs. This round replaces that layout with the modern Windows 11 arrangement: a collapsible side navigation, persistent process filter, page command bar, resource selectors, themed graphs and a Settings page. Portrait and narrow windows use a compact rail with a labeled navigation menu; landscape/wider windows can expand the rail.

## Supported pages and Android mappings

| Page | Working content and actions |
| --- | --- |
| Processes | Groups actual Nth windows, recently opened Android apps and Nth-owned processes. Search uses available names, package/publisher and PID data. Selection, sorting, Switch to and End task operate on the selected Nth window. External Android entries route to App info. Missing per-window CPU/memory/disk/network data is shown as unavailable rather than fabricated. |
| Performance | Resource selectors for Nth CPU, device RAM, primary storage capacity, device interface traffic and Nth Java heap. Larger measured graphs and numerical readings replace the old stacked green monitor. CPU is normalized across available logical processors; storage is capacity, not disk activity. |
| App history | Current-process CPU time, Nth UID traffic totals since device boot and optional foreground time today from Android Usage Access. Other apps' CPU/network history is not inferred. |
| Startup apps | Actual Nth introduction, chime and Welcome Center choices, with existing startup and Android app-settings routes. There is no fake control for another Android app's boot behavior. |
| Users | Actual local Nth profile, UID and available Nth resources. Nth profiles/virtual desktops are not presented as separate Android sign-ins. |
| Details | Nth-owned process names, actual PID/UID, measured PSS and Nth CPU. Own-launcher restart requires confirmation. Other processes are not terminated. |
| Services | Services reported for Nth's own UID, with explicit empty/unavailable states and notification-access routing. Android owns service lifetimes. |
| Settings | Default page, one/two/five-second update interval, animation choice, pause/resume and local System/Light/Dark app palette. Local theme choices leave the launcher's app and shell choices intact. |

Run new task now opens the actual Run dialog. Counter resets, unsupported readings and the first timing interval remain unavailable. Pause, minimization, backgrounding and closure stop sampling/animation. Reopening resumes measurement with a fresh timing baseline. Graph animation interpolates actual samples and obeys reduced motion.

Page/resource, filter, selected item, interval, pause and navigation choices use bounded window state. Old Applications/Processes/Networking states migrate to their modern destinations. Default page, speed, theme and the existing animation choice participate in validated desktop backup.

The polish pass also corrects singular recovery/file counts and replaces generic Favorites/Share menu icons with small original line symbols following the app palette.

## Validation

Six new unit tests cover CPU/rate arithmetic, missing-data sorting, legacy migration, bounded state round trips and invalid portable choices. The new native suite covers all pages, real Nth window actions and Run, live samples, pause/manual refresh, animation/visibility/background behavior, all 23 presets in both orientations, local palette isolation, backup, recreation, reopening, external App info routing and restart confirmation/callback scope. It is added to the complete Android 11/API30 and Android 16/API36 matrix.

Final matrix and APK verification will be recorded after completion. The market card still opens reports; embedded market values/graphs remain outside this update pending a suitable data provider. No private recording frames are included.

## Primary references

- [Microsoft's Windows 11 Task Manager redesign](https://blogs.windows.com/windows-insider/2022/02/16/announcing-windows-11-insider-preview-build-22557/): side navigation, page commands, Settings and dark appearance.
- [Microsoft's process filtering and local theme improvements](https://blogs.windows.com/windows-insider/2022/11/18/announcing-windows-11-insider-preview-build-25247/): name/PID/publisher filtering, Alt+F and Task Manager theme choices.
- [Microsoft Task Manager navigation guide](https://support.microsoft.com/en-us/accessibility/windows/use-a-screen-reader-to-navigate-windows-support-tools): Processes, Performance, App history, Startup apps, Users, Details, Services and Settings.
- [Android ActivityManager](https://developer.android.com/reference/android/app/ActivityManager): UID-scoped process memory and caller-owned service visibility.
- [Android TrafficStats](https://developer.android.com/reference/android/net/TrafficStats): actual interface/UID counters and unsupported values.

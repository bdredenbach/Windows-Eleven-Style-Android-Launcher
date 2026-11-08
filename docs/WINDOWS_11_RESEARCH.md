# Windows 11 design research

Checked November 8, 2026. Source IDs resolve in [SOURCES.md](../research/SOURCES.md).

## Evidence and limits

Primary Microsoft design/support pages, release notes, QEMU/PRoot documentation, the supplied Termux repository and its release metadata were reviewed. Five official UI images were visually inspected in the first reference set: Start, Quick Settings, Settings Home, Snap flyout and Mica. Additional Explorer, notification and Task Manager images are linked in the source index.

The available host is x86_64 Linux. No QEMU executable, `/dev/kvm`, Windows installation or connected Android emulator was found in the initial environment check. No VM was installed, Windows image downloaded, Android build run, benchmark measured or live Windows workflow tested. An embedded Microsoft Snap video could not be opened by the research service; no video-viewing claim is made.

## Emulation feasibility

Full Windows and an Android launcher are separate deliverables. QEMU system emulation can run an operating-system guest with software CPU translation; hardware acceleration depends on host support. PRoot provides a Linux process/filesystem environment and is not a replacement for a Windows kernel. Termux can host tools on Android, and VNC displays a guest's graphical output. [R01, R03, R04]

The supplied repository advertises ARM64, 4 GB RAM and 20 GB storage. Its October 2, 2025 release exposes three split archives totaling 4,844,381,292 compressed bytes. That verifies availability metadata, not the Windows build, runtime, integrity of downloaded bytes or phone performance. The extracted runner and disk image were not inspected. The 20 GB figure is the repository's claim, not a validated storage budget for a fresh Microsoft installation. [R01, R02]

For future hands-on research, a normal Windows PC or an accelerated VM with official Microsoft installation media is the preferred reference. Termux/QEMU is an optional experiment. The host architecture, guest architecture, firmware, drivers and accelerator must be checked before supplying a runnable command. Microsoft publishes official x64 and Arm64 installation media; the Arm64 download alone does not establish Android compatibility. [R03, R06]

VNC display and TCP port must not be confused: QEMU display `:1` maps to 5901 and `:2` maps to 5902. Read the actual runner configuration, use the matching viewer notation, and bind a local-only research server to loopback. [R05]

## Version policy

Windows 11 has changed since launch. The original centered pinned-app composition is documented in Microsoft's 2021 announcement. Current Start guidance includes Pinned, Recommended and All sections, with Category, Grid and List views. A September 2026 preview describes additional Start sizing/visibility controls, a Recent label and alternate taskbar positions. The generic taskbar support page still describes bottom-only positioning. These are version and rollout differences, not interchangeable reference states. [R07, R08, R09, R10]

**Proposal:** begin with the familiar pinned-app Start, bottom centered taskbar and clearly separated flyouts. Keep newer Start and taskbar variants as later explicit options. Do not mix old screenshots and newer controls without labeling the chosen design.

## Visual language

Microsoft distinguishes opaque, wallpaper-tinted Mica from translucent Acrylic used for transient surfaces. Geometry guidance specifies 8 effective-pixel outer corners, 4 for common controls and square corners for snapped/maximized windows. The type system uses Segoe UI Variable. Motion connects invocation, position and dismissal rather than applying an unrelated animation to every view. [R11-R15]

**Android proposals:** start with 8 dp floating surfaces and 4 dp controls; keep 48 dp touch targets and scalable text. These are adaptation choices, not measured equivalences between Windows effective pixels and Android dp. Use a neutral opaque fallback when blur is unavailable or disabled. Derive text and selection colors from semantic roles and verify every accent in light/dark modes. Use Android's available sans-serif until a redistributable typeface is chosen. Keep a single original Nth Eleven brand mark.

## Surface inventory and intended behavior

| Surface | Reference finding | Proposed Nth adaptation |
| --- | --- | --- |
| Start | Pinned grid, search, account/power area; current versions add a scrollable All section. [R07, R08] | Uniform app pins, real recents, folders and searchable installed apps. No inherited Ten tile-size controls in the Eleven layout. |
| Taskbar | Centered app composition, running markers, separate tray destinations. [R09] | Keep tray space reserved; scroll the app strip when narrow; long press shows app options. |
| Quick Settings | Network/sound/battery cluster opens controls. [R09] | Volume and brightness controls plus honest Android settings routes where a direct toggle is restricted. |
| Notifications/calendar | Clock or bell opens the notification/calendar panel. [R09] | Reuse notification access and redaction; open the month calendar independently of Quick Settings. |
| Widgets | A distinct information board rather than a return of Start live tiles. [R07] | Reuse opt-in news/weather; add local calendar and note cards. |
| Windows and Snap | Maximize-button layouts, top-edge layout invocation, assist and groups. [R16] | Hold maximize on touch; offer only layouts whose zones fit built-in Nth windows. |
| Task View | Window overview and multiple desktops. [R17] | Preserve existing desktop banks, window state and shortcut isolation. |
| Explorer | Home/Quick access, navigation pane, command bar, compact file menus. [R18] | Replace Ten's visible ribbon while keeping real folder grants and operations. |
| Recycle Bin | File-management behavior within the shell. | Keep Nth-managed deleted files, restore and empty actions; use Eleven command chrome. No claim to intercept deletion by other Android apps. |
| Settings | Persistent category navigation, search and grouped cards. [R19] | Reorganize categories; all visible rows have real launcher controls or Android destinations. |
| Control Panel | Still documented alongside Settings and other system tools. [R20] | Keep a clearly separate legacy utility window; share destinations rather than duplicate settings state. |
| Task Manager | System monitoring tool with a modern navigation treatment. [R20, R21] | Reuse real available telemetry; identify unavailable Android metrics instead of inventing live graphs. |

## Settings mapping

The left column follows Microsoft's category inventory. The right column is a proposed Android implementation, not a claim that Windows manages Android settings. [R19]

| Windows 11 category | Proposed working content |
| --- | --- |
| Home | Device card, recent launcher settings, theme shortcut and setup status |
| System | Display, sound, notifications, power, storage, multitasking and About |
| Bluetooth & devices | Connected-device routes and pointer/input preferences |
| Network & internet | Android network, VPN and connection destinations |
| Personalization | Background, colors, themes, Start, taskbar, icon size/spacing |
| Apps | Installed Android apps, defaults and Nth tools |
| Accounts | Local Nth profile plus Android account destination |
| Time & language | Android time, language, input and speech destinations |
| Gaming | Available Nth games and relevant device settings only |
| Accessibility | Text scale, contrast, reduced motion and Android accessibility |
| Privacy & security | Permissions, notification/usage access, Nota data choices and backup |
| Windows Update reference | Product-facing name: Updates; launcher version and Android update route |

## Explorer and interaction detail

Microsoft documents Home/Quick access, known-folder pinning and a streamlined menu whose primary operations include cut, copy, paste, rename, share and delete. More options exposes additional commands. Current documentation also describes tabs and Gallery. [R18, R22]

**Proposal:** navigation and query state belong to each tab, not to a shared global path. Back/Forward follow visit history; Up follows the parent hierarchy; breadcrumbs jump to a selected ancestor. Open linked folders through their actual URI grants. Cancellation, permission revocation and conflicting names must retain an actionable recovery path. For touch, expose labels for unfamiliar action icons. Treat the August 2026 Insider context-menu redesign as a later reference until its exact target version is chosen. [R23]

## Built-in applications and Nota

**Proposal:** first restyle the existing browser, Notepad/notes, Calculator, Photos, media player, Calendar, Task Manager and backup tools. Later add an Eleven-style Snipping workspace, a simple Paint-like drawing utility and a Clock with timers/stopwatch. Tabs and restoration for notes deserve a separate data-model pass. Microsoft documents Notepad tabs/session restoration and Snipping capture/recording; those references establish Windows behavior, not feature completion in Nth. [R24, R25]

Use Nth Nota as the assistant entry. Retain Ten's explicit local app commands, ambiguity choices, contextual writing and optional free AI fallback. Generated replies and imported page text must not become commands. Label external apps and Settings destinations clearly. Keep one discoverable Nota entry per browser toolbar area.

Android full-screen/app recording requires the platform MediaProjection flow and current foreground-service requirements. A preview of a built-in Nth window is a different feature and should not imply permission to capture arbitrary apps. [R26]

## Research conclusion

Nth Desktop Ten is a suitable engineering base. The largest visible changes are structural: Start, taskbar/tray composition, the split flyouts, Explorer command chrome and Settings cards. Preserve its engines and validate each visible replacement against the reference inventory. The next implementation should import the pinned baseline into Eleven, establish a separate application identity and CI, and then build one coherent shell. Hands-on Windows observation remains a separate evidence gap.

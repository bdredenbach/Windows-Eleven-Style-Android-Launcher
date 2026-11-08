# Primary references

Reviewed November 8, 2026. Links establish Windows or platform behavior; they do not establish that Nth implements it. Release-dependent or preview material is labeled. Official images remain reference material and are not application assets.

| ID | Source | Scope |
| --- | --- | --- |
| R01 | [xiv3r Termux Windows 11 README](https://github.com/xiv3r/termux-windows-11/blob/main/README.md) | Third-party requirements and installation outline; not executed |
| R02 | [Repository release](https://github.com/xiv3r/termux-windows-11/releases/tag/windows-11) | October 2, 2025 archive names, sizes and published digests; bytes not downloaded |
| R03 | [QEMU system introduction](https://www.qemu.org/docs/master/system/introduction.html) | System guests, TCG and accelerators |
| R04 | [PRoot documentation](https://proot-me.github.io/) | Linux userspace/filesystem behavior |
| R05 | [QEMU command reference](https://www.qemu.org/docs/master/system/qemu-manpage.html) and [VNC guidance](https://www.qemu.org/docs/master/system/vnc-security.html) | VNC display/port mapping and listener binding |
| R06 | [Microsoft Windows 11 Arm64 ISO](https://www.microsoft.com/en-us/software-download/windows11arm64) and [ISO documentation](https://learn.microsoft.com/en-us/windows/arm/iso) | Official media and host/driver considerations |
| R07 | [First Windows 11 preview](https://blogs.windows.com/windows-insider/2021/06/28/announcing-the-first-insider-preview-for-windows-11/) | Historical launch-era shell reference |
| R08 | [Customize Start](https://support.microsoft.com/en-us/windows/experience/personalization/customize-the-windows-start-menu) and [Start layout documentation](https://learn.microsoft.com/en-us/windows-hardware/customize/desktop/customize-the-windows-11-start-menu) | Current Start organization and view choices |
| R09 | [Customize the taskbar](https://support.microsoft.com/en-us/windows/experience/personalization/customize-the-taskbar-in-windows) | Taskbar, tray, Quick Settings and notification/calendar |
| R10 | [September 2026 preview KB5124006](https://support.microsoft.com/en-us/servicing/os/windows-11/2026/09/kb5124006-windows-11-26h1-update) | Preview/gradual-rollout Start and taskbar variants; conflicts with older generic guidance are documented |
| R11 | [Materials](https://learn.microsoft.com/en-us/windows/apps/design/signature-experiences/materials) | Mica, Acrylic and smoke |
| R12 | [Geometry](https://learn.microsoft.com/en-us/windows/apps/design/signature-experiences/geometry) | Corner states and control geometry |
| R13 | [Typography](https://learn.microsoft.com/en-us/windows/apps/design/signature-experiences/typography) | Segoe UI Variable and hierarchy |
| R14 | [Motion](https://learn.microsoft.com/en-us/windows/apps/design/signature-experiences/motion) | Connected motion and timing guidance |
| R15 | [Color](https://learn.microsoft.com/en-us/windows/apps/design/signature-experiences/color) | Semantic accent and light/dark contrast |
| R16 | [Snap windows](https://support.microsoft.com/en-us/windows/experience/snap-your-windows) | Layouts, assist, groups and input methods |
| R17 | [Multitasking](https://support.microsoft.com/en-us/windows/experience/how-to-multitask-in-windows) | Task View and desktops |
| R18 | [File Explorer](https://support.microsoft.com/en-us/windows/experience/fileexplorer/file-explorer-in-windows) | Home/Quick access, command bar and file menus |
| R19 | [Exploring Settings](https://support.microsoft.com/en-us/windows/experience/exploring-windows-settings) | Category and card navigation |
| R20 | [System configuration tools](https://support.microsoft.com/en-us/windows/experience/system-configuration-tools-in-windows) | Settings, Task Manager and Control Panel |
| R21 | [Preview build 22557](https://blogs.windows.com/windows-insider/2022/02/16/announcing-windows-11-insider-preview-build-22557/) | Historical Task Manager navigation redesign |
| R22 | [Explore File Explorer with a screen reader](https://support.microsoft.com/en-us/accessibility/windows/use-a-screen-reader-to-explore-and-navigate-file-explorer-in-windows) | Tabs, Home/Gallery and keyboard traversal |
| R23 | [Explorer/context-menu improvements](https://blogs.windows.com/windows-insider/2026/08/17/improving-file-explorer-context-menu-faster-simpler-and-more-customizable/) | August 2026 Insider reference, not universal baseline |
| R24 | [Notepad and Snipping updates](https://blogs.windows.com/windows-insider/2023/08/31/new-updates-for-snipping-tool-and-notepad-for-windows-insiders/) | Historical tabs/session and recording update |
| R25 | [Use Snipping Tool](https://support.microsoft.com/en-us/windows/apps/use-snipping-tool-to-capture-screenshots) | Current screenshot and recording workflow |
| R26 | [Android MediaProjection](https://developer.android.com/media/grow/media-projection) | Android capture consent and service requirements |
| R27 | [Termux app](https://github.com/termux/termux-app) | Current installation sources and plugin compatibility |
| R28 | [Nth Desktop Ten pinned tree](https://github.com/bdredenbach/Windows-Ten-Style-Android-Launcher/tree/e31109dd202b88d1476c4ba35b35d49cca1a7f62) | Read-only source audit, 0.19.0 |

## Official visual references

- [Start, current larger layout](https://support.microsoft.com/en-us/windows/media/windows-start-menu.png) - R08.
- [Quick Settings](https://support.microsoft.com/en-us/windows/media/quick-settings.png) - R09.
- [Settings Home](https://support.microsoft.com/en-us/windows/media/settings-home.png) - R19.
- [Snap flyout](https://support.microsoft.com/en-us/windows/media/snap-flyout.png) - R16.
- [Mica example](https://learn.microsoft.com/en-us/windows/apps/design/signature-experiences/images/materials_mica_hero_1880.png) - R11.
- [Explorer known folders](https://support.microsoft.com/en-us/windows/media/file-explorer-known-folders-png.png) - R18.
- [Notification center](https://support.microsoft.com/en-us/windows/media/notification-center-1.png) - R09.
- [Task Manager performance](https://support.microsoft.com/en-us/windows/media/task-manager-performance.png) - R20.

The embedded Snap video on R16 was unavailable to the research service. Live Windows interactions, animation timing measurements and phone emulation performance remain unverified.

# Nth Desktop Eleven

A Windows 11 inspired Android launcher project by **An Nth Experience**.

## Current checkpoint: research and build blueprint

The Windows 11 design investigation and a read-only audit of Nth Desktop Ten are complete for this checkpoint. This repository does **not yet contain an Android app or APK**. No Windows VM was booted during the research. The work uses primary documentation and official UI reference images; it does not claim hands-on Windows testing.

The proposed base is [Nth Desktop Ten](https://github.com/bdredenbach/Windows-Ten-Style-Android-Launcher) **0.19.0**, pinned to commit `e31109dd202b88d1476c4ba35b35d49cca1a7f62`. Ten has not been modified or imported into this repository.

- [Windows 11 design findings](docs/WINDOWS_11_RESEARCH.md)
- [Ten reuse and implementation plan](docs/IMPLEMENTATION_PLAN.md)
- [Interaction and validation checklist](docs/ACCEPTANCE_CHECKLIST.md)
- [Primary references](research/SOURCES.md)
- [Machine-readable source baseline](research/ten-baseline.json)

## Design direction

Rebuild the visible shell around centered Start/taskbar composition, a pinned-app Start grid, separate Quick Settings and notification/calendar panels, rounded floating windows, restrained materials, a compact Explorer command bar, card-based Settings, and Snap layouts. Reuse Ten's working file, window, setup, media and Nota engines.

The proposed initial visual target is the familiar Windows 11 pinned-app layout. Newer scrollable Start layouts and release-dependent taskbar options are documented separately so later work can offer them deliberately.

This is an Android launcher. Built-in Nth tools can live in resizable launcher windows; installed Android apps continue to use Android tasks. Windows executables are outside the launcher scope.

Independent Nth project; not affiliated with Microsoft. Windows is a Microsoft trademark. Official UI images linked in the research are design references, not licensed application assets. Preserve the base project's attribution and asset notices when importing its code.

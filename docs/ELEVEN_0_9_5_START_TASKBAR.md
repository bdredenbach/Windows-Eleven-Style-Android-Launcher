# Eleven 0.9.5 — Start cache and taskbar holds

Start retains its landing rows, app sorting, built-in tool records and profile footer across openings. It prepares columns using the available menu width while hidden, renders only the active landing/results surface, and avoids duplicate renders when clearing search. App scans, app names, pins, Start options, recent files/recents when enabled, seen-app badges, appearance and column changes invalidate the appropriate content. No installed-app catalog is persisted by this new view cache; the existing catalog scan/cache remains authoritative.

Expanded blank-row touch handling now delegates stationary touches to Android View long-press handling. Horizontal pans cancel pending holds. Independent row scrolling, app holds/jump lists and cross-row drag/reorder retain their existing handlers. Blank tray-container space also receives the taskbar menu.

The primary blank-taskbar menu offers Task Manager and Taskbar settings. Nth Desktop options is a submenu retaining expansion, scroll/static, pin management, widgets, Nota, window arrangements, desktop/window switching and saved workspaces.

Microsoft references:
- https://blogs.windows.com/windowsexperience/2022/10/18/making-the-everyday-easier-with-new-experiences-available-in-windows-11/ — Task Manager and Taskbar settings on the Windows 11 taskbar context menu.
- https://support.microsoft.com/en-us/windows/experience/personalization/customize-the-taskbar-in-windows — press/hold blank taskbar space to open Taskbar settings.

Validation added: six Start cache/invalidation unit cases, a real dispatched expanded-blank hold unit case, and native input tests for expanded/collapsed taskbar holds, both Windows menu actions, the expansion submenu, search return and ten warm Start openings. Native warm timing covers the show() call and excludes frame presentation and animation; it is not a physical Pixel latency measurement. The existing row-gesture suites continue exercising both-row pan/fling, drag/drop, keyboard/accessibility movement and backup persistence.

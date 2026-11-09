# Eleven 0.8.0 — expanded taskbar

Bring Eight 0.7.10's corrected taskbar layout to Eleven. Hold an empty taskbar area and choose **Expand taskbar**, or use **Settings > Personalization > Taskbar > Expand taskbar to two rows**. The equivalent checkbox is also available in **Advanced Desktop Settings > Taskbar and Start Menu**. The preference defaults off, persists, and participates in validated `.nthdesktop` backups.

Two rows use normal 48dp button heights. Start, Search, Task View, Nota, Widgets and the system tray retain their bottom-row placement; the extra task area extends upward. Left/Center alignment, labels, static fitting, scrolling, grouping, thumbnail previews, app switching and keyboard reordering remain available. Drag insertion and its marker use both pointer axes in expanded mode.

The two attached Ten/Eight recordings were reviewed at half-second intervals, including the expansion transitions. Their desktop icon jumping comes from recalculating cell height against the reduced work area. Eleven now anchors desktop grid geometry to the normal 52dp taskbar work area: expansion crops the visible scroll area while icon positions and sizes stay fixed. Saved coordinates are not rewritten. The second row uses an additional 48dp. New running tasks append to existing session slots instead of reshuffling Android buttons when Settings/Control Panel opens. Explicit manual order takes priority; editing pins resets the default pin order.

Eight reference commit: `8991b2e3d6baf7dfd41ed2dea25e05ce963d8c7e`. This adapts its task-item container, two-axis drag insertion and session ordering to Eleven's centered/left-aligned shell. Unrelated Eight desktop behaviors are excluded.

## Verification

Pending Android 11/API30 and Android 16/API36 checks. The added native suite watches every pre-draw frame across seven desktop scenarios: imported-style 5x9 portrait/5x6 landscape grids at 80% icons, both taskbar alignments, unsaved automatic placement, free placement and a scrolled sheet. It repeatedly changes expansion through the actual menu, Settings switch and Control Panel checkbox. It also checks unchanged saved layouts, button row stability, static/scrollable modes with labels, two-axis drag insertion, actual keyboard reordering and backup inclusion. The retained purchaser, theme, Task Manager, market, browser, storage, Nota, Snap, photo and media suites remain enabled.

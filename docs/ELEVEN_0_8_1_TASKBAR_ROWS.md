# Eleven 0.8.1 — running apps above fixed controls

Expanded mode now puts every open app button on the upper row and idle pinned shortcuts on the lower row. A pinned app uses its existing button: opening it moves that button up; minimizing leaves it up because the app remains open; closing returns its pinned shortcut to the lower row. Nth Nota's dedicated fixed button remains on the bottom row with Start, Search, Task View, Widgets, the clock and the tray.

The previous layout alternated buttons by child index. Expansion also rebuilt fixed controls, and centering depended on the narrower two-row content width. This fix uses each button's actual running state, resizes the existing taskbar in place, and calculates its horizontal anchor from the same preferred single-row width in both modes. Static mode fits each row independently; scrolling and grouping remain available. Keyboard and drag reordering operate within each button's assigned row.

The expansion setting and its menu, Settings and Advanced Desktop Settings entry points are unchanged. Desktop geometry stays anchored to the normal one-row work area, and saved positions are preserved.

## Verification

Pending Android 11/API30 and Android 16/API36 checks. The strengthened native suite checks real open/minimized/closed pinned apps, running Android buttons, mixed rows with labels/static fitting/scrolling, row-constrained drag and keyboard ordering, backup round trips, and the existing seven desktop stability cases. Eight additional scenarios watch every pre-draw frame and compare the actual View identities plus complete screen bounds of the fixed controls across expansion/collapse, both alignments, both orientations and both fitting modes. The taskbar preflight collects evidence and resets test data/screen size before the retained suites run.

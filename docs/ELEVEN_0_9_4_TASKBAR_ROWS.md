# Eleven 0.9.4 — independent taskbar rows

Expanded app rows scroll independently. Each row owns its touch pan, fling, wheel movement, bounds and drag autoscroll. Fixed controls keep their bottom placement. Collapsed mode keeps its original single scrolling strip.

Drag an app slowly to the other row to assign it there. Its saved row overrides the initial running-top/idle-bottom default and remains through launch, close/reopen, appearance rebuild, rotation and collapse/expand. Row choices participate in validated portable backups. Ctrl+Shift+Up/Down and accessibility actions offer the same row choices. Quick upward flicks still open jump lists; a stationary hold keeps the existing preview/menu behavior.

Native regression uses real input for swipes and cross-row drops, checks stationary geometry of the opposite row and fixed controls, exercises static fitting after manual placement, and verifies saved row choices. Existing taskbar stability, actual upgrade and full Android 11/16 regression remain included.

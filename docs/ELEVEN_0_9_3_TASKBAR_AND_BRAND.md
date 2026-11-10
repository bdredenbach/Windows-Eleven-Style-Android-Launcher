# Eleven 0.9.3 — full-width upper tasks and layered N¹¹ branding

The owner's 224714 recording shows running Android apps clipped halfway across the expanded taskbar. The app strip reserved the lower tray width on both rows. Expanded tasks now receive the whole safe taskbar width. Bottom idle pins have independent start/end bounds, and drawing is clipped around the fixed controls and tray even while scrolling. Static fitting uses each row's available width. Left/Center controls retain their positions during expansion.

The right-edge Show desktop strip is 8dp instead of 24dp, moving the clock, date and notifications 16dp right without reducing the clock's text area. Its native tap/restore behavior remains covered.

The original N¹¹ identity now uses a cyan-to-blue layered fold background. Shared vector resources update Android's adaptive/round icon, installer/toast identity, Start, startup/splash, profile fallback, Welcome, About and Run branding. The adaptive glyph and Android themed monochrome foreground remain mask safe. Supplied Microsoft application icons and user profile pictures retain their own identities.

Validation includes the existing complete Android 11/16 regression workflow and added actual eight-app upper-row rendering/touch checks for Left/Center and static/scrollable modes. Separate right-edge notification and lower pin reservation assertions prevent the recorded width regression.

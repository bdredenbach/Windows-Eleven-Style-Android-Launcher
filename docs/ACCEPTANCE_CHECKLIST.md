# Eleven acceptance checklist

Roadmap and acceptance targets. Checked entries have specific Eleven evidence in `ELEVEN_0_1_VALIDATION.md`; unchecked entries still need implementation or broader verification. The first preview does not claim the whole roadmap. Test portrait and landscape, light and dark, standard and large text, and both Android navigation styles where available.

## Identity and startup

- [ ] Installs alongside Ten, Seven and Eight with a separate name, icon, package and provider authorities.
- [ ] Guided Home setup remains available after cancel, denial and return from Android Settings.
- [ ] Optional file, usage and notification access are discoverable later in Settings.
- [ ] Restarting the launcher restores only its own saved state.

## Start and taskbar

- [ ] Start opens from its button and a second tap closes it; Back/Escape dismiss the top surface.
- [ ] Search finds real apps, Nth tools and Settings; unavailable file locations explain how to grant access.
- [ ] Pin/unpin, ordering and folders persist through recreation; app removal leaves no broken target.
- [ ] The selected Start layout has internally consistent labels and controls.
- [ ] Tray controls remain reachable with zero, one and many running tasks.
- [ ] Active/running indicators match actual Nth and supported external task state.
- [ ] Long press and mouse right click offer appropriate actions; keyboard focus is visible.
- [ ] Touch previews can open and close without a click reaching the desktop behind them.

## Panels

- [x] Quick Settings and notification/calendar use separate taskbar destinations.
- [x] Opening one incompatible shell panel dismisses the other; repeat taps do not stack copies.
- [ ] Volume/brightness work and restricted controls route to the correct Android page.
- [ ] Notification denial/revocation empties protected data and shows the correct setup route.
- [ ] Lock-screen content remains redacted according to the user's choice.
- [ ] Widgets show authentic local/cached data and clear loading/offline/empty states.

## Windows and desktops

- [ ] Floating windows round corners; maximized/snapped windows use the chosen flush geometry.
- [ ] Resize/drag obey work-area bounds, minimum sizes, cutouts and keyboard visibility.
- [ ] Hold maximize opens only viable Snap zones, without also maximizing on release.
- [ ] Snap Assist selects another built-in window; cancellation leaves a valid layout.
- [ ] Group restore handles closed, minimized and moved windows predictably.
- [ ] Rotation, keyboard resizing and activity recreation preserve usable restore bounds.
- [ ] Task View moves windows and closes desktops without content loss or singleton collisions.
- [ ] External Android apps remain clearly distinguished from built-in window previews.

## Explorer and Settings

- [ ] Back/Forward follow history; Up follows parent; breadcrumbs jump to the selected ancestor.
- [ ] Tabs, if implemented, have independent current path, selection and search/history.
- [ ] Cut/copy/paste/rename/delete/restore handle conflicts and permission revocation.
- [ ] Recycle Bin restore preserves real content; emptying targets only its managed contents.
- [ ] Narrow-window commands remain reachable and their disabled state explains unavailable actions.
- [ ] Each Settings category, search result and breadcrumb opens the intended real control.
- [ ] Settings and Control Panel share state and do not accidentally open an inherited Ten destination.
- [ ] Icon size, spacing, Home status and Usage Access remain discoverable.

## Nota, appearance and release

- [x] Clear app commands work; ambiguous names show actual app choices.
- [x] Page selections and model replies stay data and cannot execute desktop commands.
- [ ] Browser has the intended single Nota toolbar entry; closing it clears taskbar state.
- [ ] Every accent, including black/white, keeps labels, hints, selections and disabled text readable.
- [ ] Backgrounds, surface layers and title bars remain consistent in every built-in tool.
- [ ] Reduced motion and transparency fallback are usable.
- [ ] File pickers, browser selection, media and backups retain engine behavior.
- [x] CI evidence identifies commit, OS/API, orientation, checks run and failures.
- [x] Installable APK and optional ZIP are published only after the actual build is verified.

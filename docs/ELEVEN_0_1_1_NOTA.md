# Eleven 0.1.1 — Nth Nota category guidance

Ports the owner's Eight 0.7.8 handoff into Eleven 0.1.0. The reference is Eight source `2a293f253e3c8b7f24a09c1d13b6c61c78f39c40` and the handoff ZIP prepared November 8, 2026. Its checksums were verified. The port retains Eleven's package, signing identity, settings routes, Task View, actual virtual desktops, taskbar composition and browser title-bar integration.

## Behavior

All six stable categories have their own brief summary, main instructions and empty-composer hint. Guidance and replies are separate. Category changes retain the real response; Copy and Save use only that response, and remain disabled in guidance-only state. New command stays available in compact windows; the keyboard layout prioritizes the composer, New command and Send. In the shortest viewports, New command joins Send/Stop/Tools in the bottom row; the category and response controls return when more height is available.

Fresh explicit app commands still resolve locally on Send. Multiple matching activities require a labeled, icon-bearing choice. Same-name activities in the same package have distinct labels. Imported, history, private and generated text never becomes executable merely because it says “open …”. Clearing source context retains writing origin. Suggestions wait for Send. New chat/command cancel pending requests and clear only the current composer/reply, retaining ordinary saved history.

The local-first writing stack, explicit model preparation, app-local cloud consent, optional free fallback and confirmed source Apply flow remain. Private mode excludes automatic persistence; it does not promise offline-only writing. Explicit Copy/Save/Share remains an intentional user action.

## Desktop state and migration

`NotaDraftStore` stores one versioned state per desktop under `eleven_nota_drafts`. Each ordinary state contains up to 15,000 characters of composer and reply, category, writing-origin flag, reply style, source presentation, guidance visibility and selection. Hydration suppresses persistence until flags and text are restored together. Pending requests, executable cards and source-editor references are never restored.

Moving a Nota window carries its state and cache to the destination desktop. A destination with a live Nota window cannot accept another singleton; a closed destination cache is replaced by the moved window. Removing a desktop removes its obsolete cache. Legacy global drafts migrate only to the first desktop; a nonempty legacy draft without an origin flag becomes a writing passage. Legacy generic view-path snapshots cannot restore Nota text.

A private window saves only a private placeholder in session/Android saved state and marks its scoped entry private without replacing the preceding ordinary cache. Its passage, response and source title are excluded. Recreating or cold-opening that private window produces an empty private placeholder. Nota also disables generic parent view-state saving. Backup validation checks state version, desktop ownership, exact fields/types, text bounds, task range and writing origin. Workspace presets remove unsafe legacy generic Nota snapshots.

## Validation

[CI run 42505457935](https://github.com/bdredenbach/Windows-Eleven-Style-Android-Launcher/actions/runs/42505457935), tested source `2a2722b5206bfd488c0e3cf23b1fd6f529f999ba`, passed both emulator jobs. The application code is unchanged from `9359c556b0ee79df529a306f10ea19d0764f6944`; later commits synchronize native dialog actions. Eight's results are provenance, not Eleven validation.

| Check | Android 11 / API 30 | Android 16 / API 36 |
|---|---|---|
| Build, 20 unit tests, identity/runner checks | Pass | Pass |
| 157,840 taskbar bounds cases and Nota command policy | Pass | Pass |
| Eleven shell, 86 assertions | Pass | Pass |
| Retained Nota app commands and Settings routes | Pass | Pass |
| Nota port, 171 assertions | Pass | Pass |
| Fresh-process scoped state | Pass | Pass |
| Integrated Browser | Pass | Pass |
| Snap/window engine | Pass | Pass |

API 36 passed attempt 1. API 30 passed an unchanged attempt-2 rerun after attempt 1 timed out during an inherited shell-panel transition; that attempt recorded no crash or ANR. The final complete API 30 run passed all six native suites. No assertions were removed. Native History/import/Apply checks wait for completed dialog state before the next action.

Each platform produced 40 screenshots. Reviewed the six category guides and empty hints with an actual fixture reply retained, both app themes, portrait and both landscape rotations, the short keyboard layout and restored private placeholder. Settled native View rendering is used for Nota captures, so the keyboard capture shows the app's available viewport rather than the separate Android IME window. Both final AndroidRuntime error logs contain no crash.

The distributed APK is signed with the retained Eleven preview key and can update 0.1.0. All 449 non-signing archive entries match the passing API 30 and API 36 CI packages byte for byte. Signature Schemes v2/v3 and ZIP alignment are verified during packaging. The download ZIP contains only the app, install notes and public build verification.

APK SHA-256: `affc78bfe7911cff8a2a169744d10b33f60f4ee5f0d954d3c0872d59e9deac3b`.

Signer certificate SHA-256: `b704c7a2da2d0d4425fa5fe61c122483389b07be327ad565ff198064af696265`.

Native coverage includes category guides and actual reply actions, controlled writing results and cancellation, source Apply and reviewed imports, touch ownership, compact layouts, independent full-length drafts, desktop moves, Activity recreation, a separate cold process and private backup/session exclusion. The normal Eleven shell, Nota command routing, Browser and Snap suites remain in the regression run.

No physical Pixel 10/Android 17, live model preparation, live supported-device AICore generation or live cloud gateway request is claimed by this change. Controlled writing uses the real assistant/request/cancellation pipeline with a deterministic LocalAI backend fixture. Installed app launch tests use isolated fixture activities.

# Eleven 0.9.1: Usage Access return recovery

Ports the confirmed Ten 0.28.1 behavior from `0d6a89ec1d2213bddfa46e381b6d5cdaf4cae51a` into Eleven's existing setup controller. Eleven's package, signing identity, theme, Welcome Center, step ordinals, completed-state migration and folder-repair logic are retained.

Returning from Usage Access or App info clears the pending Android request, then rechecks the actual Usage Access grant. An ungranted return shows the same illustrated guide; only a real grant or the explicit Later choice moves on. Android activity result codes alone cannot complete this step. Early-result/focus guards and recreation persistence remain.

The guide uses Eleven's actual app name/icon, provides OK, Later and Open App info on supported Android versions, and explains the denial/restricted-settings recovery route. The body scrolls and the window fits system insets in portrait and landscape. Eleven's familiar Welcome Center stays behind the ordered guides. The manual Welcome Center/taskbar guide also returns after App info without automatically launching another Settings screen.

Validation includes state/lifecycle regression tests for denied and granted returns, repeated attempts, result-code independence, App info identity, recreation and explicit deferral. A separate full-system native run uses Android 11/default and Android 16/google_apis to exercise the actual Settings return and App info screens, layout, recreation and manual recovery. Its final grant check uses the real Android app-op in the disposable emulator and verifies the taskbar checkbox. It does not claim to reproduce the Pixel-specific denial popup. Existing full Eleven regression remains enabled separately.

Existing completed installations keep their setup progress; Welcome Center → Set up Usage Access is the manual retry route. Version 0.9.1/code14 uses Eleven's retained preview signing key.

#!/usr/bin/env bash
set -euo pipefail
launcher=com.nthexperience.desktop.eleven
collect() {
  mkdir -p screenshots
  timeout 15s adb logcat -d > screenshots/complete-device-log.txt || true
  for name in $(timeout 15s adb shell run-as "$launcher" ls files 2>/dev/null | tr -d '\r'); do
    case "$name" in eleven-usage-*.png) timeout 15s adb exec-out run-as "$launcher" cat "files/$name" > "screenshots/$name" 2>/dev/null || break;; esac
  done
}
trap collect EXIT
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb install -r fixture/build/outputs/apk/debug/fixture-debug.apk
adb shell pm clear "$launcher"
adb shell input keyevent KEYCODE_WAKEUP
adb shell wm dismiss-keyguard
timeout -k 3s 240s adb shell am instrument -w -e phase complete "$launcher.test/com.nthexperience.desktop.ElevenInitializationSmoke" | tee eleven-usage-initialization.txt
grep -q 'PASS:' eleven-usage-initialization.txt
! grep -Eq 'FAIL:|shortMsg=|Process crashed|INSTRUMENTATION_FAILED' eleven-usage-initialization.txt

#!/usr/bin/env bash
set -euo pipefail
launcher=com.nthexperience.desktop.eleven
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb install -r fixture/build/outputs/apk/debug/fixture-debug.apk
adb install -r installprobe/build/outputs/apk/debug/installprobe-debug.apk
mkdir -p screenshots
collect() {
 adb logcat -d -s AndroidRuntime:E > eleven-android-runtime.txt
 for file in $(adb shell run-as "$launcher" ls files | tr -d '\r' | sed -n '/^eleven-.*\.\(png\|tsv\|txt\)$/p'); do
  adb exec-out run-as "$launcher" cat "files/$file" > "screenshots/$file"
 done
}
trap collect EXIT
adb shell pm clear "$launcher"
timeout 240s adb shell am instrument -w -e suite storage -e phase setup "$launcher.test/com.nthexperience.desktop.StorageSmoke" | tee eleven-StorageSetup.txt
grep -q 'PASS' eleven-StorageSetup.txt
! grep -q 'FAIL:\|INSTRUMENTATION_FAILED\|Process crashed' eleven-StorageSetup.txt
adb shell am force-stop "$launcher"
timeout 240s adb shell am instrument -w "$launcher.test/com.nthexperience.desktop.ElevenMarketSmoke" | tee eleven-ElevenMarketSmoke.txt
grep -q 'PASS' eleven-ElevenMarketSmoke.txt
! grep -q 'FAIL:\|INSTRUMENTATION_FAILED\|Process crashed' eleven-ElevenMarketSmoke.txt

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
adb exec-out screencap -p > screenshots/eleven-at-exit.png
adb shell dumpsys activity top > eleven-activity-top.txt
adb shell dumpsys activity lastanr > eleven-last-anr.txt
adb shell dumpsys input_method > eleven-input-method.txt
for file in $(adb shell run-as "$launcher" ls files | tr -d '\r' | sed -n '/^\(eleven-\|aero-snap-\|photo-\|ten7-\|storage-\|home-first-run\|vista-usage-\|nth-media-\).*\.\(png\|tsv\|txt\)$/p'); do
 adb exec-out run-as "$launcher" cat "files/$file" > "screenshots/$file"
done
}
trap collect EXIT
adb shell pm clear "$launcher"
timeout 240s adb shell am instrument -w "$launcher.test/com.nthexperience.desktop.ElevenTaskbarSmoke" | tee eleven-ElevenTaskbarSmoke.txt
grep -q 'PASS' eleven-ElevenTaskbarSmoke.txt
! grep -q 'FAIL:\|INSTRUMENTATION_FAILED\|Process crashed' eleven-ElevenTaskbarSmoke.txt

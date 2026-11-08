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
for file in $(adb shell run-as "$launcher" ls files | tr -d '\r' | sed -n '/^eleven-.*\.png$/p'); do
 adb exec-out run-as "$launcher" cat "files/$file" > "screenshots/$file"
done
}
trap collect EXIT
for runner in ElevenSmoke TenCommandsSmoke BrowserSmoke SnapSmoke; do
 adb shell am force-stop "$launcher"
 adb shell am instrument -w "$launcher.test/com.nthexperience.desktop.$runner" | tee "eleven-$runner.txt"
 grep -q 'PASS' "eleven-$runner.txt"
 ! grep -q 'FAIL:\|INSTRUMENTATION_FAILED\|Process crashed' "eleven-$runner.txt"
done

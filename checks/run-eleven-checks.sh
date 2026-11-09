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
for file in $(adb shell run-as "$launcher" ls files | tr -d '\r' | sed -n '/^\(eleven-\|aero-snap-\|photo-\|ten7-\|storage-\|home-first-run\|vista-usage-\).*\.\(png\|tsv\)$/p'); do
 adb exec-out run-as "$launcher" cat "files/$file" > "screenshots/$file"
done
}
trap collect EXIT
adb shell pm clear "$launcher"
adb shell am force-stop "$launcher"
timeout 240s adb shell am instrument -w -e suite storage -e phase setup "$launcher.test/com.nthexperience.desktop.StorageSmoke" | tee eleven-StorageSetup.txt
grep -q 'PASS' eleven-StorageSetup.txt
! grep -q 'FAIL:\|INSTRUMENTATION_FAILED\|Process crashed' eleven-StorageSetup.txt
for runner in ElevenPurchaserSmoke ElevenCompletionSmoke ElevenThemeSmoke ElevenSmoke ElevenGapSmoke TenCommandsSmoke ElevenNotaSmoke ElevenNotaColdSmoke BrowserSmoke SnapSmoke TenFeedSmoke; do
 adb shell am force-stop "$launcher"
 timeout 240s adb shell am instrument -w "$launcher.test/com.nthexperience.desktop.$runner" | tee "eleven-$runner.txt"
 grep -q 'PASS' "eleven-$runner.txt"
 ! grep -q 'FAIL:\|INSTRUMENTATION_FAILED\|Process crashed' "eleven-$runner.txt"
done

adb shell appops set --uid "$launcher" MANAGE_EXTERNAL_STORAGE allow
adb shell am force-stop "$launcher"
timeout 240s adb shell am instrument -w -e suite storage -e phase device "$launcher.test/com.nthexperience.desktop.StorageSmoke" | tee eleven-StorageDevice.txt
grep -q 'PASS' eleven-StorageDevice.txt
! grep -q 'FAIL:\|INSTRUMENTATION_FAILED\|Process crashed' eleven-StorageDevice.txt
adb shell am force-stop "$launcher"
timeout 240s adb shell am instrument -w "$launcher.test/com.nthexperience.desktop.PhotoSmoke" | tee eleven-PhotoSmoke.txt
grep -q 'PASS' eleven-PhotoSmoke.txt
! grep -q 'FAIL:\|INSTRUMENTATION_FAILED\|Process crashed' eleven-PhotoSmoke.txt

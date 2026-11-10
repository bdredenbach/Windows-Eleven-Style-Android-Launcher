#!/usr/bin/env bash
set -euo pipefail
launcher=com.nthexperience.desktop.eleven
native_suite="${1:-full}"
if [ "$native_suite" = full ] && [ -f build/upgrade-seed/previous.apk ]; then adb install -r build/upgrade-seed/previous.apk; else adb install -r app/build/outputs/apk/debug/app-debug.apk; fi
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
failures=()
run_native() {
 local runner="$1" label="$2"
 shift 2
 adb shell am force-stop "$launcher"
 # Existing UI suites run with completed optional setup. Ordered setup itself is covered by InitializationFlowTest.
 adb shell "run-as $launcher sh -c 'mkdir -p shared_prefs; cat > shared_prefs/desktop_initialization_v1.xml'" <<'XML'
<?xml version='1.0' encoding='utf-8' standalone='yes' ?><map><int name="flow_version" value="2"/><int name="step" value="7"/></map>
XML
 if timeout 240s adb shell am instrument -w "$@" "$launcher.test/com.nthexperience.desktop.$runner" | tee "eleven-$label.txt"; then
  if grep -q 'PASS' "eleven-$label.txt" && ! grep -q 'FAIL:\|INSTRUMENTATION_FAILED\|Process crashed' "eleven-$label.txt"; then
   return
  fi
 fi
 failures+=("$label")
}
if [ "$native_suite" = TaskbarRows ]; then
 adb shell pm clear "$launcher"
 run_native ElevenTaskbarRowsSmoke ElevenTaskbarRowsSmoke
 if [ "${#failures[@]}" -ne 0 ]; then exit 1; fi
 exit 0
fi
if [ "$native_suite" = PhotoSmoke ]; then
 adb shell pm clear "$launcher"
 adb shell appops set --uid "$launcher" MANAGE_EXTERNAL_STORAGE allow
 run_native PhotoSmoke PhotoSmoke
 if [ "${#failures[@]}" -ne 0 ]; then exit 1; fi
 exit 0
fi
adb shell pm clear "$launcher"
if [ -f build/upgrade-seed/previous.apk ]; then
run_native ElevenUpgradeSmoke UpgradeSeed -e phase seed
adb shell am force-stop "$launcher"
adb install -r app/build/outputs/apk/debug/app-debug.apk
run_native ElevenUpgradeSmoke UpgradeCheck -e phase verify
collect
else
 echo "SKIP: preceding retained artifact unavailable; upgrade phases require that baseline." > eleven-upgrade-unavailable.txt
fi
adb shell pm clear "$launcher"
run_native ElevenScreenshotSmoke ElevenScreenshotSmoke
if [ "${#failures[@]}" -ne 0 ]; then exit 1; fi
collect
adb shell pm clear "$launcher"
run_native ElevenFinalPassSmoke ElevenFinalPassSmoke
if [ "${#failures[@]}" -ne 0 ]; then exit 1; fi
collect
adb shell pm clear "$launcher"
run_native ElevenTaskbarSmoke ElevenTaskbarSmoke
if [ "${#failures[@]}" -ne 0 ]; then exit 1; fi
adb shell pm clear "$launcher"
run_native ElevenTaskbarRowsSmoke ElevenTaskbarRowsSmoke
if [ "${#failures[@]}" -ne 0 ]; then exit 1; fi
collect
adb shell wm size reset
adb shell pm clear "$launcher"
run_native StorageSmoke StorageSetup -e suite storage -e phase setup
for runner in ElevenPurchaserSmoke ElevenCompletionSmoke ElevenTaskManagerSmoke ElevenMarketSmoke ElevenThemeSmoke ElevenSmoke ElevenGapSmoke TenCommandsSmoke ElevenNotaSmoke ElevenNotaColdSmoke BrowserSmoke SnapSmoke TenFeedSmoke; do
 run_native "$runner" "$runner"
done
adb shell appops set --uid "$launcher" MANAGE_EXTERNAL_STORAGE allow
run_native StorageSmoke StorageDevice -e suite storage -e phase device
run_native PhotoSmoke PhotoSmoke
run_native MediaSmoke MediaSmoke
if [ "${#failures[@]}" -ne 0 ]; then
 echo "Failed native suites: ${failures[*]}"
 exit 1
fi

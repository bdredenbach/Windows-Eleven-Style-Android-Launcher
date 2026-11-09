import os,subprocess
from pathlib import Path
sdk=Path(os.environ['ANDROID_HOME'])
aapt=sdk/'build-tools/35.0.0/aapt'
def dump(path):return subprocess.check_output([str(aapt),'dump','xmltree',path,'AndroidManifest.xml'],text=True)
main=dump('app/build/outputs/apk/debug/app-debug.apk')
tests=dump('app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk')
for needle in ['com.nthexperience.desktop.eleven','android.permission.BIND_NOTIFICATION_LISTENER_SERVICE','com.nthexperience.desktop.eleven.storage','com.nthexperience.desktop.eleven.theme-pictures','android.intent.category.HOME']:assert needle in main,needle
for runner in ['ElevenSmoke','ElevenNotaSmoke','ElevenNotaColdSmoke','ElevenCompletionSmoke','ElevenPurchaserSmoke']:
 assert 'com.nthexperience.desktop.'+runner in tests
assert 'com.nthexperience.desktop.ten.storage' not in main
print('PASS: independent Eleven package, providers, Home intent, protected notification listener and native runner')

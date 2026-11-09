"""Keep the matching public SDK/dependency jars for fast local Java validation."""
import os
from pathlib import Path
import shutil
import zipfile

out = Path('build/compile-kit')
(out / 'libs').mkdir(parents=True, exist_ok=True)
shutil.copy2(Path(os.environ['ANDROID_HOME']) / 'platforms/android-36/android.jar', out / 'android.jar')
cache = Path.home() / '.gradle/caches/modules-2/files-2.1'
for group in cache.iterdir():
    if not (group.name.startswith(('androidx.', 'org.jspecify', 'com.google.mlkit', 'com.google.android.gms', 'com.google.guava', 'com.google.ai', 'org.jetbrains.kotlin', 'org.json', 'junit', 'org.hamcrest'))):
        continue
    for artifact in group.rglob('*'):
        if artifact.suffix not in ('.jar', '.aar'):
            continue
        target = out / 'libs' / ('-'.join(artifact.relative_to(cache).parts[:-1]) + '-' + artifact.stem + '.jar')
        if artifact.suffix == '.aar':
            with zipfile.ZipFile(artifact) as archive:
                if 'classes.jar' in archive.namelist():
                    target.write_bytes(archive.read('classes.jar'))
        else:
            shutil.copy2(artifact, target)
print('Prepared Android 36 and AndroidX compile jars')

compiled = Path('app/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes')
if compiled.exists(): shutil.copytree(compiled, out / 'classes', dirs_exist_ok=True)
for resource in Path('app/build/intermediates').rglob('R.jar'):
    if 'androidTest' not in str(resource): shutil.copy2(resource, out / 'libs' / ('resource-' + '-'.join(resource.parts[-4:-1]) + '.jar'))

#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
: "${ANDROID_HOME:?Set ANDROID_HOME to your Android SDK directory}"
JAVAC="${JAVA_HOME:+$JAVA_HOME/bin/}javac"
KEYTOOL="${JAVA_HOME:+$JAVA_HOME/bin/}keytool"
BT="$ANDROID_HOME/build-tools/35.0.0"
ANDROID_JAR="$ANDROID_HOME/platforms/android-35/android.jar"
SRC=app/src/main
mkdir -p build/{compiled,generated,classes,dex}
"$BT/aapt2" compile --dir "$SRC/res" -o build/compiled/resources.zip
sed 's/<manifest /<manifest package="dev.chalknotes" /' "$SRC/AndroidManifest.xml" > build/AndroidManifest.xml
"$BT/aapt2" link -o build/base.apk -I "$ANDROID_JAR" --manifest build/AndroidManifest.xml --java build/generated --min-sdk-version 26 --target-sdk-version 35 --version-code 1 --version-name 1.0 build/compiled/resources.zip
find "$SRC/java" build/generated -name '*.java' > build/sources.txt
"$JAVAC" -encoding UTF-8 -source 8 -target 8 -bootclasspath "$ANDROID_JAR:$BT/core-lambda-stubs.jar" -d build/classes @build/sources.txt
find build/classes -name '*.class' > build/classes.txt
"$BT/d8" --lib "$ANDROID_JAR" --min-api 26 --output build/dex @build/classes.txt
cp build/base.apk build/unsigned.apk
(cd build/dex && zip -q -u ../unsigned.apk classes*.dex)
"$BT/zipalign" -f -p 4 build/unsigned.apk build/aligned.apk
# Development signing key. Keep the same key to install future updates.
if [ ! -f debug.keystore ]; then
 "$KEYTOOL" -genkeypair -keystore debug.keystore -storepass android -alias androiddebugkey -keypass android -dname 'CN=Chalk Notes Development' -keyalg RSA -keysize 2048 -validity 10000
fi
"$BT/apksigner" sign --ks debug.keystore --ks-pass pass:android --out build/Chalk-Notes.apk build/aligned.apk
"$BT/apksigner" verify --verbose build/Chalk-Notes.apk
"$BT/aapt2" dump badging build/Chalk-Notes.apk

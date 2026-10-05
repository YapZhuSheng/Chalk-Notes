# Chalk Notes for Android

A small, free native Android app inspired by handwritten home-screen notes. Original code and branding; no ads, subscription, account, analytics, or server. Android 8.0 or newer.

## Install

1. Download `Chalk-Notes.apk` on your Android phone and open it.
2. If Android asks, allow your browser or file manager to install this app.
3. Open **Chalk Notes**, draw a note, and tap **Put on my widget**.
4. Accept the launcher's widget prompt. Alternatively, long-press the home screen, select **Widgets → Chalk Notes**, and drag it onto the screen.
5. Resize the widget by long-pressing it. Tap the widget to return to the drawing board.

This APK is development-signed for direct installation. There is no Play Store publishing fee to install it this way. Keep the signing key to build compatible updates.

## Send a note to someone

Both people install the app. Tap **Share drawing** and choose your usual messaging app. The recipient saves the image and uses **Open image** in Chalk Notes, then **Put on my widget**. Apps that allow sharing received images can also share directly into Chalk Notes.

**Sharing is manual. This version does not pair phones or automatically update someone else's widget.** Messaging apps can use mobile data under your existing plan. The drawing app itself requires no network permission or hosting.

## Included

- Chalk-style drawing in six colours with adjustable width and eraser.
- Undo/redo for the last 15 changes during the current session.
- Persistent draft, kept separate from the published widget image.
- A resizable 2×2 home-screen widget; all copies show the same published note.
- PNG sharing and image import through Android's system file picker.
- Imports preserve aspect ratio and replace the draft after confirmation.
- App-private storage and narrowly scoped read-only sharing permission.

## Build

Open this folder in Android Studio (free), let it install SDK 35, and choose **Build → Build App Bundle(s) / APK(s) → Build APK(s)**. Standard Android Gradle project; no external application dependencies.

Or use the included build script with JDK 17+ and Android SDK platform 35 / build tools 35.0.0:

```sh
export ANDROID_HOME=/path/to/android-sdk
export JAVA_HOME=/path/to/jdk
./build-apk.sh
```

Output: `build/Chalk-Notes.apk`. The manual build also needs `zip` on PATH.

## Verification and limits

The supplied APK was compiled against Android SDK 35, its package metadata was checked, and its signature was verified with Android's apksigner. No physical device or emulator was available for runtime testing. Launcher-specific widget sizing and pinning should be checked on your phone. Touch drawing is not a substitute for a text entry accessibility mode. Uninstalling deletes the local draft and published note; use Share to keep copies.

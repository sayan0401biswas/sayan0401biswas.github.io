# Sayan Biswas for Android

This Android app opens the live portfolio in a native, full-screen WebView. It uses the SB launcher icon, supports Android 6.0 and newer, opens external links in the device's browser, and provides a retry screen when the portfolio cannot be reached.

## Build

The project uses Android Gradle Plugin 8.9.2, Gradle 8.11.1, and Java 17. A signed release APK is built and attached to a GitHub Release when an `android-v*` tag is pushed. The signing key is stored in GitHub Actions repository secrets and is not committed to this repository.

# Azisic

<img src="app/src/main/ic_launcher-playstore.png" width="96" height="96" alt="Azisic app icon">

Azisic is an Android music player based on Mochy Music, customized and maintained by **Abdul Agantal Omar**.

## Download

Open this repository’s **Releases** page, select **v1.1.0**, and download **Azisic-v1.1.0-preview.apk** from Assets. Open the APK on Android to install it.

If you installed the v1.0.0 Azisic preview, export your data from **Settings → Storage → Export user data** before uninstalling that preview. v1.1.0 uses a different signing key. Install v1.1.0, then import the backup in Azisic.

## What’s inside

The v1.2.0 source includes the new interface. The download instructions above are for the v1.1.0 preview.

- **Home** puts Liked Songs, History, Library, and Search in a two-column grid. Your existing shelves sit below it.
- **Search and Library** use artwork tiles and clearer category filters.
- **Pulse Deck** puts the cover front and center. **Night Session** pairs a rotating record with a compact track view. Switch layouts below the controls in Now Playing.
- The player menu includes **15-, 30-, and 60-minute sleep timer presets**.
- **Profile → Developer** lists Abdul Agantal Omar and links to the open-source notices.

## Preview status

The v1.1.0 APK installed and showed the welcome screen in an Android emulator. A music service timeout in that emulator prevented completion of the Home and playback checks.

## Build locally

Use JDK 21, Android SDK Platform 36, Build Tools 36.0.0, NDK 27.0.12077973, and CMake 3.22.1. Configure the SDK path in Android Studio or set `ANDROID_HOME`, then run the Gradle wrapper from the repository root.

```sh
./gradlew :app:assembleUniversalFossDebug
```

On Windows, use `gradlew.bat :app:assembleUniversalFossDebug`. The APK is written to `app/build/outputs/apk/universalFoss/debug/`.

### Build with Expo EAS

The repository also includes the Android preview build profile. For a build under a different Expo account, remove the existing `expo.extra.eas.projectId` from `app.json`, then link your own EAS project.

```sh
npx eas-cli@27.5.0 login
npx eas-cli@27.5.0 init
npx eas-cli@27.5.0 build --platform android --profile preview
```

## Report a problem

Open an issue with the Azisic version, Android version, device model, and steps to reproduce the problem.

## Source and license

Azisic was modified on November 7, 2026, from [Mochy Music](https://github.com/realidkroo/Mochy-Music) at [commit c81798b](https://github.com/realidkroo/Mochy-Music/commit/c81798ba4a342ac96a867536a78426b88a5e0587). It is distributed under [GPL-3.0](LICENSE). The original Mochy Music project credits Roo (realidkroo), key (realkeichi), and its contributors. Original notices apply. Azisic relies on third-party providers for online content.

### Upstream projects

- [ViviMusic](https://github.com/vivizzz007/vivi-music) by vivizzz007Z. The app Mochy Music forked from.
- [Metrolist](https://github.com/mostafaalagamy/Metrolist) by Mostafa Alagamy. The base framework.
- [SimpMusic](https://github.com/maxrave-dev/SimpMusic) by maxrave-dev. Lyrics integration.
- [ArchiveTune](https://github.com/rukamori/ArchiveTune) by Rukamori. Design and architecture inspiration.
- [RiMusic and InnerTube](https://github.com/fast4x/RiMusic) by fast4x. YouTube Music service wrapper.
- [LRCLIB](https://github.com/tranxuanthang/lrclib) by tranxuanthang. Synchronized lyrics.
- [Monochrome Music](https://github.com/monochrome-music/monochrome) by monochrome-music. FLAC streams.
- [AutoEq](https://autoeq.app) by Jaakko Pasanen. Headphone equalizer database.
- [LyricsPlus](https://github.com/ibratabian17/LyricsPlus) by Ibra Al Tabian and [Better Lyrics API](https://github.com/better-lyrics/api) by Boidushya. Additional lyrics providers.

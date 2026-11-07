# Azisic

<img src="app/src/main/ic_launcher-playstore.png" width="96" height="96" alt="Azisic app icon">

Azisic is an Android music player based on Mochy Music, customized and maintained by **Abdul Agantal Omar**.

## Android preview

Get the **Azisic v1.3.0** APK from the [Expo EAS build page](https://expo.dev/accounts/abdulomar120/projects/azisic/builds/6d1193e6-b73a-40da-b803-491c6f54a652). The [corresponding v1.3.0 source and build files](https://github.com/Aziz-coder0/Azisic/tree/a45358eac975c30b5a9a2256a06e225eb47ae43a) are available under [GPL-3.0](LICENSE).

If Android requires you to uninstall an older preview before installing this build, first back up your library and settings via **Settings → Storage → Export user data**. Import that backup after installing v1.3.0.

## What’s inside

- **Home** features the current song or a recent track, with one-tap playback and shortcuts to your library.
- **Search and Library** use artwork-led browsing. If Search cannot load genres, it offers Retry and a route to downloaded songs.
- **Playlist and album pages** bring the cover, title, and playback actions together in the default Flow layout.
- **Settings** groups Audio Lab, Appearance, and Storage, with the current player style and backup controls easier to find.
- **Player Styles** keeps the existing templates, swipe previews, and Edit and Set actions, including Poster, Pulse Deck, and Night Session.
- **Profile → Developer** lists Abdul Agantal Omar and links to the open-source notices.

## Preview status

The v1.3.0 Android build completed on Expo EAS. On-device playback testing is pending.

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

<div align="center">

# 🌙 MOONWAVE
### Premium Android Music Player

[![Android Build CI](https://github.com/rmdmahfuz727-sudo/MOONWAVE/actions/workflows/android-build.yml/badge.svg)](https://github.com/rmdmahfuz727-sudo/MOONWAVE/actions/workflows/android-build.yml)
[![Release](https://img.shields.io/badge/Release-v1.0.0-38BDF8.svg)](https://github.com/rmdmahfuz727-sudo/MOONWAVE/releases)
[![License: GPL-3.0](https://img.shields.io/badge/License-GPL--3.0-8B5CF6.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Android](https://img.shields.io/badge/Platform-Android%208.0%20to%2016-10B981.svg)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-EC4899.svg)](https://developer.android.com/jetpack/compose)

<p align="center">
  <b>A state-of-the-art, high-fidelity music player crafted for Android. Featuring hardware-accelerated Audio Lab DSP, synchronized LRCLIB lyrics, customizable turntable and cosmic layouts, and automated cloud builds.</b>
</p>

---

## 👨‍💻 Lead Developer & Maintainer

<p align="center">
  <a href="https://github.com/rmdmahfuz727-sudo">
    <img src="https://avatars.githubusercontent.com/u/289979958?v=4" width="130" height="130" style="border-radius: 50%; border: 3px solid #38BDF8;" alt="Mahfuz Rahman" />
  </a>
  <br/>
  <a href="https://github.com/rmdmahfuz727-sudo"><b>Mahfuz Rahman</b></a>
  <br/>
  <i>Android System Engineer & Open Source Maintainer</i>
  <br/>
  <a href="https://github.com/rmdmahfuz727-sudo">github.com/rmdmahfuz727-sudo</a>
</p>

</div>

---

## ✨ Signature Highlights

### 🎵 1. Advanced Playback Engine
* **Media3 ExoPlayer & MediaSessionService**: Gapless playback, crossfade, and background service lifecycle under partial wakelock.
* **Audio Focus & Headphone Safeguard**: Automatically pause when headphones disconnect or Bluetooth disconnects (`ACTION_AUDIO_BECOMING_NOISY`).
* **Android 13+ Notification Controls**: Native MediaStyle lockscreen and status bar controls with scrubbing seekbar and dynamic art.
* **Playback Speed**: Precision speed adjustments (`0.5x`, `0.75x`, `1.0x`, `1.25x`, `1.5x`, `2.0x`).
* **Sleep Timer**: Gentle countdown timer (`15m`, `30m`, `45m`, `60m`, or custom) that pauses playback automatically.

### 🎛️ 2. Hardware Audio Lab
* **5-Band / 10-Band Graphic Equalizer**: Real-time gain adjustment across center frequencies (60Hz, 230Hz, 910Hz, 3.6kHz, 14kHz) with hardware dB feedback.
* **Bass Boost DSP**: Dynamic low-end punch enhancement (0% to 100%).
* **3D Virtualizer**: Expansive surround sound spatialization.
* **Environmental Reverb**: Small Room, Medium Room, Large Room, Medium Hall, Large Hall, and Plate presets.

### 📜 3. Synchronized LRCLIB Lyrics
* **Official LRCLIB API**: Automated millisecond-accurate lyrics lookup with zero account requirement.
* **Karaoke Real-Time Scrolling**: Current active line highlighting with tap-to-seek playback integration.
* **Manual Search & Offset Calibration**: Built-in search dialog and manual time offset adjustment (+/- 500ms).
* **Local Offline Caching**: Synced lyrics are automatically cached locally in SQLite for instant offline availability.

### 🎨 4. Seven Unique Player Layouts
1. **Flagship**: Modern cosmic backdrop with dynamic waveform visualizer and large controls.
2. **Vinyl Turntable**: Realistic rotating vinyl disc with grooved vinyl animations synced to playback state.
3. **Full-Screen Artwork**: Immersive edge-to-edge album cover backdrop with floating glass controls.
4. **Minimal**: Clean typographic layout focused on distraction-free listening.
5. **Cosmic Gradient**: Fluid mesh gradient shift responsive to track mood.
6. **Lyrics-Focused**: Synchronized karaoke lyrics displayed right in the primary player screen.
7. **Docked Compact**: Persistent mini-player above navigation bar with progress track.

### 📂 5. Complete Local Music Library
* **Android MediaStore Scanning**: Automatic indexing of songs, albums, artists, and folder structures.
* **Sorting**: Sort by Title (A-Z / Z-A), Artist, Album, Date Added, and Duration.
* **Smart Playlists**: Automated *Favorites*, *Most Played*, and *Recently Added* playlists alongside custom user playlists.
* **Format Compatibility**: Direct playback of FLAC, WAV, ALAC, AAC, MP3, OPUS, and OGG formats.

---

## 🏗️ Architecture & Technology Stack

```
MOONWAVE/
├── app/
│   ├── src/main/java/com/moonwave/music/
│   │   ├── MoonwaveApp.kt                # Application initialization
│   │   ├── MainActivity.kt               # Navigation host & permissions
│   │   ├── core/
│   │   │   ├── model/                    # Domain entities (Song, Album, Playlist, Lyrics)
│   │   │   ├── playback/                 # MoonwaveMediaService & PlayerController
│   │   │   ├── audiofx/                  # AudioEffectsManager (EQ, Bass, Virtualizer)
│   │   │   ├── database/                 # SQLite storage & MusicScanner
│   │   │   ├── network/                  # LRCLIB API client & lyrics parser
│   │   │   └── util/                     # Time and string formatting helpers
│   │   └── ui/
│   │       ├── theme/                    # Brand palette, ThemeMode, typography
│   │       ├── components/               # MiniPlayer, SongListItem, AnimatedVinyl
│   │       ├── home/                     # Dashboard & quick hub
│   │       ├── library/                  # Songs, Albums, Artists, Folders tabs
│   │       ├── player/                   # 7 selectable player layouts
│   │       ├── lyrics/                   # Synced lyrics screen
│   │       ├── audiolab/                 # Equalizer & DSP controls
│   │       ├── playlists/                # Smart & custom playlist manager
│   │       ├── customization/            # Theme, accent, and layout switcher
│   │       ├── explore/                  # Codec and integration showcase
│   │       └── about/                    # Lead developer profile & credits
├── .github/
│   └── workflows/
│       ├── android-build.yml             # Automated CI & Debug APK compilation
│       └── release.yml                   # Signed Release APK & AAB publisher
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 📱 How to Build on an Android Phone (GitHub Actions)

As an Android-first developer, you do not need Android Studio or a desktop computer to build installable APKs for MOONWAVE!

1. Fork or push this repository to your GitHub account:
   ```bash
   git remote add origin https://github.com/rmdmahfuz727-sudo/MOONWAVE.git
   git push -u origin main
   ```
2. Navigate to your repository on GitHub via your phone's browser.
3. Open the **Actions** tab.
4. Select **MOONWAVE CI & Debug Build** and tap **Run workflow**.
5. Once the build completes (~2 minutes), download `MOONWAVE-debug.apk` directly to your phone and tap to install!
6. For tagged releases, simply create a tag (e.g. `v1.0.0`) to generate signed `MOONWAVE-release.apk` and `MOONWAVE-release.aab` automatically!

---

## ⚖️ Open-Source Attribution & License

MOONWAVE is free and open-source software distributed under the terms of the **GNU General Public License v3.0**.

### Attributions:
* **Mochy Music**: Reference architecture and provider patterns by [@realidkroo](https://github.com/realidkroo/Mochy-Music) (GNU GPL-3.0).
* **AndroidX Media3 ExoPlayer**: Audio playback engine by Google (Apache 2.0).
* **LRCLIB**: Open-source synchronized lyrics API by Tran Xuan Thang (MIT).
* **Coil**: Image loading library for Android Jetpack Compose (Apache 2.0).

---

<p align="center">
  Developed with ❤️ by <b><a href="https://github.com/rmdmahfuz727-sudo">Mahfuz Rahman</a></b>
</p>

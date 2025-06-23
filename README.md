# DesktopMode: macOS-Inspired Android Launcher

DesktopMode is a beautiful, modern Android launcher inspired by macOS. It features a dynamic, animated dock with real app icons, smooth wave-like hover animations, and a clean desktop background. Built with Jetpack Compose, it brings a desktop-like experience to your Android device.

## Features

- **macOS-Style Dock:**
  - Dynamic dock with real app icons (not just Material icons)
  - Smooth wave animation: icons grow and neighbors react when hovered (like macOS)
  - Dock expands/contracts smoothly when hovered
  - Icons overflow above the dock when enlarged, just like macOS
  - Recent apps section (shows recently used apps, if available)
  - Pinned apps section (customizable, with popular apps by default)
  - Dash separator between pinned and recent apps

- **Modern Compose UI:**
  - Beautiful vertical gradient background
  - Responsive, adaptive layout
  - All UI built with Jetpack Compose

- **Launcher Functionality:**
  - Set as your default launcher for a true desktop experience
  - Click any dock icon to launch the corresponding app

## Screenshots

*(Add screenshots here)*

## Getting Started

### Prerequisites
- Android Studio (Flamingo or newer recommended)
- Android device or emulator (API 26+ recommended)

### Installation
1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/DesktopMode.git
   cd DesktopMode
   ```
2. **Open in Android Studio:**
   - Open the project folder in Android Studio.
3. **Build & Run:**
   - Click Run ▶️ to build and install on your device/emulator.
4. **Set as Default Launcher:**
   - When prompted, set DesktopMode as your default launcher.

## Customization
- **Pinned Apps:**
  - Edit the `pinnedApps` list in `MainActivity.kt` to change which apps appear in the dock.
- **Recent Apps:**
  - The dock will show recent apps if available (requires permissions and may vary by device/Android version).
- **Dock Animation:**
  - The wave effect and dock expansion are fully customizable in the Compose code.

## Permissions
- `QUERY_ALL_PACKAGES` - To list and launch installed apps
- `GET_TASKS` (optional) - For recent apps (may not work on all devices)

## Known Issues
- Recent apps may not show on all Android versions due to system restrictions
- Some system apps may not display icons if not installed

## License
MIT License

---

**DesktopMode** brings a touch of macOS to your Android device. Enjoy a beautiful, animated, and modern desktop experience on mobile! 
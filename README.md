# Desktop Mode Launcher

A macOS-inspired Android launcher that provides a desktop-like experience with window management, top bar, and bottom dock.

## Features

### 🖥️ Desktop Mode Interface
- **Top Bar (Menu Bar)**: macOS-style top bar with system controls
- **Bottom Dock**: App launcher dock with quick access to frequently used apps
- **Auto-hide**: Both top bar and dock can hide when apps are in fullscreen mode

### 🪟 Window Management
- **Resizable Windows**: Apps can be opened in resizable windows
- **Window Controls**: Minimize, maximize, and close buttons for each window
- **Multi-window Support**: Run multiple apps simultaneously in separate windows
- **Window Positioning**: Drag and drop windows to different positions

### 🎯 Gesture Control
- **Home Gesture**: Swipe up from bottom to show/hide dock
- **Notification Gesture**: Swipe down from top to show/hide top bar
- **Back Gesture**: Swipe from left edge for custom back actions
- **Long Press**: Context menu with quick actions

### ⚙️ System Control
- **Brightness Control**: Adjust screen brightness
- **Volume Control**: Control media volume
- **WiFi Toggle**: Enable/disable WiFi
- **Bluetooth Toggle**: Enable/disable Bluetooth
- **Auto-rotate**: Toggle screen rotation

## Setup Instructions

### Prerequisites
- Android 8.0 (API 26) or higher
- Device with overlay permission support
- ADB access (for some features)

### Installation

1. **Build and Install**
   ```bash
   ./gradlew assembleDebug
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

2. **Grant Permissions**
   - Open the app and tap "Start Desktop Mode"
   - Grant overlay permission when prompted
   - For full system control, enable additional permissions via ADB:
   ```bash
   adb shell pm grant com.gpi.desktopmode android.permission.WRITE_SETTINGS
   adb shell pm grant com.gpi.desktopmode android.permission.WRITE_SECURE_SETTINGS
   adb shell pm grant com.gpi.desktopmode android.permission.MODIFY_PHONE_STATE
   ```

3. **Set as Default Launcher**
   - When prompted, select "Desktop Mode" as your default launcher
   - Or go to Settings > Apps > Default apps > Home app

### Usage

#### Starting Desktop Mode
1. Launch the app
2. Tap "Start Desktop Mode"
3. Grant overlay permission if prompted
4. The top bar and dock will appear

#### Using the Interface
- **Top Bar**: Contains system controls, clock, and hide/show buttons
- **Bottom Dock**: Quick access to apps, can be hidden/shown
- **App Grid**: Access all installed apps from the launcher screen

#### Window Management
- Apps launched from the dock or grid open in managed windows
- Use window controls to minimize, maximize, or close apps
- Drag window title bars to move windows
- Resize windows by dragging corners (when implemented)

#### Gestures
- **Swipe up from bottom**: Show/hide dock
- **Swipe down from top**: Show/hide top bar
- **Swipe from left edge**: Custom back action
- **Long press anywhere**: Context menu

## Architecture

### Services
- **DesktopOverlayService**: Manages top bar and dock overlays
- **WindowManagementService**: Handles app window management
- **GestureInterceptorService**: Intercepts system gestures

### ViewModels
- **LauncherViewModel**: Manages installed apps and launcher state
- **DesktopViewModel**: Manages desktop mode state and system settings

### Key Components
- **MainActivity**: Main launcher activity with app grid
- **TopBar**: macOS-style top bar with system controls
- **BottomDock**: App launcher dock
- **ManagedWindow**: Window wrapper for apps

## Permissions

### Required Permissions
- `SYSTEM_ALERT_WINDOW`: For overlay windows
- `QUERY_ALL_PACKAGES`: To list installed apps
- `FOREGROUND_SERVICE`: For persistent services

### Optional Permissions (for full functionality)
- `WRITE_SETTINGS`: System settings control
- `WRITE_SECURE_SETTINGS`: Secure settings control
- `MODIFY_PHONE_STATE`: Phone state control
- `CHANGE_WIFI_STATE`: WiFi control
- `BLUETOOTH_ADMIN`: Bluetooth control
- `MODIFY_AUDIO_SETTINGS`: Volume control

## Development

### Building
```bash
./gradlew assembleDebug
```

### Running Tests
```bash
./gradlew test
```

### Project Structure
```
app/src/main/java/com/gpi/desktopmode/
├── MainActivity.kt              # Main launcher activity
├── service/
│   ├── DesktopOverlayService.kt # Top bar and dock management
│   ├── WindowManagementService.kt # Window management
│   └── GestureInterceptorService.kt # Gesture handling
├── viewmodel/
│   ├── LauncherViewModel.kt     # Launcher state management
│   └── DesktopViewModel.kt      # Desktop mode state
└── ui/theme/                    # UI themes and styling
```

## Limitations

- Some system permissions require ADB or root access
- Window management is limited to overlay windows
- Gesture interception may conflict with system gestures
- Performance may vary on different devices

## Troubleshooting

### Overlay Permission Issues
- Go to Settings > Apps > Desktop Mode > Permissions
- Enable "Display over other apps"

### Services Not Starting
- Check if battery optimization is disabled for the app
- Ensure the app is not being killed by the system

### Gestures Not Working
- Ensure gesture interceptor service is running
- Check if system gestures are conflicting

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Inspired by macOS desktop experience
- Built with Jetpack Compose
- Uses Android Window Manager for overlays 
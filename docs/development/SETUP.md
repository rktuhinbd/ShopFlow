# Local Development Setup

Welcome to the ShopFlow engineering team. Follow these instructions to set up your local development environment.

## Prerequisites

- **Android Studio**: Ladybug (2024.2.1) or newer recommended.
- **JDK**: Java 17 is required for compiling the project.
- **Gradle**: The project uses Gradle wrapper. Do not use a local system Gradle installation.
- **OS**: The project is OS-agnostic but commands here show Windows PowerShell conventions, reflecting common setups. Adapt to bash/zsh as needed.

## Setup Instructions

1. **Clone the Repository**
   ```powershell
   git clone <repository_url> ShopFlow
   cd ShopFlow
   ```

2. **Configure JDK**
   Ensure your `JAVA_HOME` environment variable points to a JDK 17 installation:
   ```powershell
   $env:JAVA_HOME="C:\Program Files\Java\jdk-17"
   ```
   *Alternatively, configure the JDK inside Android Studio (Settings > Build, Execution, Deployment > Build Tools > Gradle).*

3. **Open in Android Studio**
   Open the `ShopFlow` root directory in Android Studio. Allow Gradle to sync fully.

## Building and Running

To build the debug APK from the command line:

```powershell
.\gradlew assembleDebug
```

To install the debug APK on a connected emulator or physical device:

```powershell
.\gradlew installDebug
```

Alternatively, use the green "Run" button in Android Studio to deploy to your selected device.

## Device Requirements

- **Minimum API**: Android API 24 (Android 7.0 Nougat)
- **Target API**: Android API 35
- **Testing Environment**: We recommend an emulator running API 33+ for UI consistency checks.

## Verifying the Build

Before committing any code, run the standard build and test verification:

```powershell
.\gradlew build
.\gradlew testDebugUnitTest
```

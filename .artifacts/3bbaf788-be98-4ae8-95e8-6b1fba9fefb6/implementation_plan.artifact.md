# Fix Hilt Sync Error (Android BaseExtension not found)

The project is using Android Gradle Plugin (AGP) 9.3.2, which enables the "New DSL" by default. This new DSL removes `BaseExtension` in favor of newer interfaces. Hilt 2.55 is currently incompatible with this change as it still searches for `BaseExtension`.

## Proposed Changes

### Build Configuration

#### [MODIFY] [gradle.properties](file:///C:/Users/yotech-79/AndroidStudioProjects/ShopFlow/gradle.properties)
- Add `android.newDsl=false` to opt-out of the new DSL and restore compatibility with Hilt.

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/yotech-79/AndroidStudioProjects/ShopFlow/app/build.gradle.kts)
- Revert the `compileSdk` syntax to the standard property-based assignment (`compileSdk = 37`) to match the restored legacy DSL.

## Verification Plan

### Automated Tests
- Run Gradle sync to verify that the "Android BaseExtension not found" error is resolved.
- Execute `./gradlew :app:assembleDebug` to ensure the project builds successfully.

### Manual Verification
- Verify that the Hilt plugin is correctly applied and the `android` block is recognized by the IDE.

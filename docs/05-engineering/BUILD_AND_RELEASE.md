# ShopFlow — Build and Release

**Version**: 1.0-DRAFT  
**Date**: 2026-08-27  
**Status**: DRAFT — PENDING HUMAN APPROVAL

---

## 1. Build Configuration

### Current State (Verified)
- Gradle Kotlin DSL
- Version Catalog (`gradle/libs.versions.toml`)
- Single module: `:app`
- compileSdk: 37 (release)
- targetSdk: 37
- minSdk: 24
- Java compatibility: 11
- Compose: enabled via `kotlin-compose` plugin

### R8 / Code Shrinking
Currently disabled in release build:
```kotlin
optimization {
    enable = false
}
```
**Plan**: Enable R8 optimization for release builds with proper ProGuard/R8 rules for Retrofit, Room, Hilt, and serialization.

## 2. 16 KB Page-Size Compatibility

### Background
Android 15+ (API 35+) devices may use 16 KB memory page sizes. APKs must be compatible.

### Verification Plan

1. **Native .so libraries**: Check all dependencies for native libraries
   - Retrofit/OkHttp: Pure JVM, no native code — SAFE
   - Room: SQLite — Android framework provides, not bundled — SAFE
   - Coil: Check for native decoders
   - Hilt: Pure annotation processing — SAFE

2. **APK analysis**:
   ```bash
   # After building release APK
   # Check ELF alignment of .so files
   zipalign -c -P 16 4 app-release.apk
   ```

3. **AAB verification**:
   - Build AAB for Play Store
   - Verify with bundletool

4. **Testing**:
   - Test on 16KB page-size emulator (API 35+ with 16KB config)

### Status
- UNKNOWN — NEEDS VERIFICATION after dependencies are added and release APK is built

## 3. Debug vs. Release

| Aspect | Debug | Release |
|--------|-------|---------|
| Signing | Debug keystore | TBD — REQUIRES DECISION |
| R8/Shrinking | Disabled | To be enabled |
| Logging | Enabled | Disabled |
| Network inspection | Allowed | Blocked |
| Compose compiler reports | Enabled | Disabled |

## 4. Signing

TBD — REQUIRES DECISION for release signing configuration.

## 5. Build Verification Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Lint check
./gradlew lint

# 16KB alignment check (after release build)
zipalign -c -P 16 4 app/build/outputs/apk/release/app-release.apk
```

---

**Document Status**: DRAFT — Awaiting human review and approval.

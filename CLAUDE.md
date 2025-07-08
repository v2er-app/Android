# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

### Build Commands
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK (requires GHUI_KEYSTORE_PASSWORD and GHUI_KEY_PASSWORD env vars)
./gradlew assembleRelease

# Build GitHub variant (for CI/CD)
./gradlew assembleGithub

# Clean and rebuild
./gradlew clean assembleDebug
```

### Testing Commands
```bash
# Run unit tests
./gradlew test

# Run specific variant tests
./gradlew testDebugUnitTest
./gradlew testReleaseUnitTest

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest
```

### Linting
```bash
# Run Android lint checks
./gradlew lint

# Note: Lint is configured with abortOnError false, so errors won't fail builds
```

### Installation
```bash
# Install debug build on connected device
./gradlew installDebug

# Install release build
./gradlew installRelease
```

## Architecture Overview

### Project Structure
The app follows MVP (Model-View-Presenter) architecture with dependency injection:

- **`app/src/main/java/me/ghui/v2er/`** - Main source code
  - **`module/`** - Feature modules organized by screen/feature
    - Each module has Contract interface defining View/Presenter interactions
    - Base classes in `module/base/` provide common functionality
  - **`injector/`** - Dagger 2 dependency injection setup
    - Components define injection targets
    - Modules provide dependencies
  - **`network/`** - Networking layer using Retrofit
    - `bean/` contains data models parsed from V2EX HTML
    - Uses Fruit library for HTML parsing
  - **`adapter/`** - RecyclerView adapters with base implementations
  - **`widget/`** - Custom UI components and behaviors

### Key Architectural Patterns

1. **MVP Pattern**: Each screen has:
   - Contract interface defining View and Presenter interfaces
   - Activity/Fragment implements View interface
   - Presenter handles business logic
   - BaseActivity provides common functionality

2. **Dependency Injection**: Dagger 2 provides:
   - AppComponent for app-wide dependencies
   - PerActivity scoped components for activities
   - Modules provide Retrofit services, presenters, etc.

3. **Network Layer**:
   - Retrofit + RxJava for API calls
   - HTML responses parsed using Fruit library
   - Data models in `network/bean/` map HTML structure

4. **Navigation**:
   - Navigator class handles activity transitions
   - Deep linking supported for V2EX URLs
   - Uses RxActivity to handle activity results

### Core Technologies
- **Language**: Java 8
- **DI**: Dagger 2
- **Networking**: Retrofit 2 + OkHttp 3 + RxJava 2
- **View Binding**: ButterKnife
- **Image Loading**: Glide 4
- **HTML Parsing**: Fruit library
- **Event Bus**: EventBus

### Important Implementation Details

1. **V2EX API**: The app parses HTML pages rather than using a JSON API
   - Fruit library extracts data from HTML using annotations
   - Models in `network/bean/` define HTML structure mapping

2. **Theme Support**: 
   - Day/Night mode support through Android theme system
   - Resources in `values-night/` for dark theme

3. **Build Variants**:
   - `debug`: Standard development build
   - `release`: Production build (requires keystore passwords)
   - `github`: CI/CD variant with embedded signing config

4. **Permissions**:
   - Internet access
   - Network state checking
   - External storage (for image saving)

5. **Deep Linking**: Supports V2EX URLs like:
   - `https://v2ex.com/t/*` - Topic pages
   - `https://v2ex.com/member/*` - User profiles
   - `https://v2ex.com/go/*` - Node pages

## CI/CD Configuration

### GitHub Actions
The project uses GitHub Actions for continuous integration:
- **Workflow**: `.github/workflows/android.yml`
- **Trigger**: Push and pull requests to main branch
- **JDK Version**: 11
- **Build Command**: `./gradlew build`

### Release Signing
Release builds require environment variables:
- `GHUI_KEYSTORE_PASSWORD`: Keystore password
- `GHUI_KEY_PASSWORD`: Key password
- GitHub variant has embedded signing config for CI/CD

## Project Configuration

### Version Management
Versions are centralized in `config.gradle`:
- compileSdkVersion: 33
- minSdkVersion: 27
- targetSdkVersion: 33
- buildToolsVersion: "33.0.0"

### Gradle Properties
Key settings in `gradle.properties`:
- AndroidX enabled
- Jetifier enabled for legacy library support
- JVM args: `-Xmx2048m -XX:MaxMetaspaceSize=512m`
- Parallel builds enabled
- Configuration cache enabled

### JDK Compatibility
For JDK 17+, add to `gradle.properties`:
```
org.gradle.jvmargs=--add-exports=java.base/sun.nio.ch=ALL-UNNAMED
```

## Third-Party Services

### Sentry Crash Reporting
- Configuration: `sentry.properties`
- DSN configured in app
- Automatic crash and error reporting

### Flurry Analytics
- Configuration: `app/flurry.config`
- Analytics tracking for user engagement

## Development Guidelines

- Follow existing MVP patterns when adding new features
- Use Dagger 2 for dependency injection
- Parse HTML responses using Fruit annotations
- Use RxJava for async operations
- Test on both day and night themes
- Ensure deep links work for new V2EX page types
- Run `./gradlew lint` before committing (errors won't fail build)

## App Distribution
- **Google Play**: https://play.google.com/store/apps/details?id=me.ghui.v2er
- **CoolApk**: https://www.coolapk.com/apk/me.ghui.v2er

## License & Contributing
- Licensed under GPL
- Contributors tracked via git history
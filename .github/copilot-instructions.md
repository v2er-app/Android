# V2er Android App - GitHub Copilot Instructions

**ALWAYS follow these instructions first and fallback to additional search and context gathering only if the information here is incomplete or found to be in error.**

## Working Effectively

The V2er Android app is a beautiful V2EX client built using MVP architecture with Dagger 2 dependency injection, RxJava, and Retrofit. The app parses HTML responses from V2EX rather than using a JSON API.

### Essential Setup Commands
Run these commands in sequence to set up the development environment:

```bash
# Verify Java 17 is installed (required)
java -version  # Should show Java 17

# Grant execute permissions to gradlew
chmod +x gradlew

# Clean and prepare the project
./gradlew clean --stacktrace
```

### Build Commands - **NEVER CANCEL BUILDS**

**CRITICAL: All build commands can take 5-15 minutes. NEVER CANCEL. Set timeouts to 60+ minutes minimum.**

```bash
# Build debug APK - NEVER CANCEL: Takes 5-10 minutes. Set timeout to 60+ minutes.
./gradlew assembleDebug --stacktrace

# Build release APK (requires signing configuration) - NEVER CANCEL: Takes 10-15 minutes. Set timeout to 60+ minutes.
./gradlew assembleRelease --stacktrace

# Clean and rebuild - NEVER CANCEL: Takes 8-12 minutes. Set timeout to 60+ minutes.
./gradlew clean assembleDebug --stacktrace

# Build App Bundle (AAB) for Play Store - NEVER CANCEL: Takes 10-15 minutes. Set timeout to 60+ minutes.
./gradlew bundleRelease --stacktrace
```

### Testing Commands - **NEVER CANCEL TESTS**

**CRITICAL: Test commands can take 5-20 minutes. NEVER CANCEL. Set timeouts to 30+ minutes minimum.**

```bash
# Run unit tests - NEVER CANCEL: Takes 3-8 minutes. Set timeout to 30+ minutes.
./gradlew test --stacktrace

# Run specific variant tests - NEVER CANCEL: Takes 2-5 minutes each. Set timeout to 30+ minutes.
./gradlew testDebugUnitTest --stacktrace
./gradlew testReleaseUnitTest --stacktrace

# Run instrumented tests (requires connected device/emulator) - NEVER CANCEL: Takes 10-20 minutes. Set timeout to 45+ minutes.
./gradlew connectedAndroidTest --stacktrace
```

### Linting and Code Quality

```bash
# Run Android lint checks - Takes 1-3 minutes. Lint is configured with abortOnError false, so errors won't fail builds.
./gradlew lint --stacktrace

# Note: Lint errors will not fail the build but should be reviewed and fixed when possible.
```

### Installation Commands

```bash
# Install debug build on connected device - Takes 1-2 minutes.
./gradlew installDebug --stacktrace

# Install release build on connected device - Takes 1-2 minutes.
./gradlew installRelease --stacktrace
```

## Network Connectivity Issues

**IMPORTANT**: If you encounter network connectivity issues with repositories (e.g., "dl.google.com: No address associated with hostname"), document this limitation in your work. The build will fail due to network restrictions in some environments. This is a known limitation and should be noted as "Build commands fail due to network connectivity restrictions in the current environment."

## Validation and Testing Scenarios

After making any changes, ALWAYS run through these validation steps:

### 1. Code Quality Validation
```bash
# Always run lint before committing - errors won't fail build but should be reviewed
./gradlew lint --stacktrace

# Check for compilation errors
./gradlew compileDebugJava --stacktrace
```

### 2. Unit Test Validation - **NEVER CANCEL: Set timeout to 30+ minutes**
```bash
# Run all unit tests
./gradlew test --stacktrace

# Check specific test classes if making focused changes
./gradlew test --tests="me.ghui.v2er.util.UriUtilsTest" --stacktrace
```

### 3. Manual Testing Scenarios
When making UI or functional changes, manually test these core scenarios:

**Core User Flows to Test:**
1. **App Launch**: Open the app and verify it loads without crashes
2. **Topic Browsing**: Navigate through topic lists and verify content loads
3. **Deep Linking**: Test V2EX URL handling:
   - `https://v2ex.com/t/*` - Topic pages
   - `https://v2ex.com/member/*` - User profiles
   - `https://v2ex.com/go/*` - Node pages
4. **Theme Switching**: Test both day and night themes
5. **Image Loading**: Verify images load correctly using Glide
6. **Network Handling**: Test with poor network conditions

**UI Testing Requirements:**
- Test on both day and night themes
- Verify RecyclerView scrolling performance
- Check custom UI components in `app/src/main/java/me/ghui/v2er/widget/`
- Ensure proper MVP pattern implementation

## Architecture and Navigation

### Project Structure
```
app/src/main/java/me/ghui/v2er/
├── module/           # Feature modules (MVP pattern)
│   ├── base/        # Base classes for activities/presenters
│   ├── login/       # Login feature
│   ├── topic/       # Topic viewing/creation
│   └── user/        # User profile features
├── injector/        # Dagger 2 dependency injection
│   ├── component/   # Dagger components
│   └── module/      # Dagger modules
├── network/         # Networking layer
│   ├── bean/        # Data models (HTML parsing)
│   └── *.java       # Retrofit services
├── adapter/         # RecyclerView adapters
├── widget/          # Custom UI components
└── util/           # Utility classes
```

### Key Files and Locations

**Configuration Files:**
- `config.gradle` - Version management and build configuration
- `gradle.properties` - Gradle settings including JVM args for Java 17+
- `app/build.gradle` - Main Android build configuration
- `sentry.properties` - Crash reporting configuration
- `app/flurry.config` - Analytics configuration

**Important Source Directories:**
- `app/src/main/java/me/ghui/v2er/module/` - All feature modules following MVP pattern
- `app/src/main/java/me/ghui/v2er/network/bean/` - Data models that parse V2EX HTML
- `app/src/main/java/me/ghui/v2er/widget/` - Custom UI components and behaviors
- `app/src/test/java/` - Unit tests (small test suite)
- `app/src/androidTest/java/` - Instrumented tests

**MVP Pattern Implementation:**
Each feature module contains:
- `Contract.java` - Interface defining View and Presenter contracts
- `*Activity.java` or `*Fragment.java` - View implementation
- `*Presenter.java` - Business logic implementation

### Development Guidelines

**Always follow these patterns when making changes:**

1. **MVP Architecture**: Use existing MVP patterns for new features
2. **Dependency Injection**: Use Dagger 2 for all dependencies
3. **HTML Parsing**: Use Fruit library annotations for V2EX data parsing
4. **Async Operations**: Use RxJava 2 for all async operations
5. **Image Loading**: Use Glide 4 for all image loading operations
6. **Navigation**: Use Navigator class for activity transitions

**Before committing changes:**
```bash
# Always run these validation steps
./gradlew lint --stacktrace
./gradlew test --stacktrace
# Manually test day and night themes
# Test relevant deep linking scenarios
```

## CI/CD Integration

The project uses GitHub Actions for CI/CD:

**Workflows:**
- `.github/workflows/ci.yml` - Runs tests, lint, and builds on PR/push
- `.github/workflows/release.yml` - Creates signed releases
- Uses JDK 17 and caches Gradle dependencies

**Expected CI Timings:**
- Unit tests: 5-10 minutes
- Lint checks: 2-5 minutes  
- Debug build: 8-15 minutes
- Release build: 10-20 minutes

## Signing Configuration

**For Debug Builds:**
Use default debug keystore - no additional configuration needed.

**For Release Builds:**
Requires environment variables (see `SIGNING.md` for details):
- `KEYSTORE_PASSWORD` - Keystore password
- `KEY_PASSWORD` - Key password  
- `KEY_ALIAS` - Key alias (defaults to "ghui")

**Security Note**: Never commit keystore files. The `.gitignore` excludes all `.jks` and `.keystore` files.

## Troubleshooting

### Common Issues and Solutions

1. **Build fails with "dl.google.com: No address associated with hostname"**
   - This indicates network connectivity restrictions
   - Document as a known limitation in your work
   - The CI/CD environment has proper connectivity

2. **Java compatibility issues**
   - Ensure Java 17 is being used
   - The `gradle.properties` file contains required JVM args for Java 17+

3. **Dagger compilation errors**
   - Clean and rebuild: `./gradlew clean assembleDebug`
   - Check that all `@Inject` and `@Provides` annotations are correct

4. **RxJava threading issues**
   - Ensure proper use of `.observeOn(AndroidSchedulers.mainThread())`
   - Use RxLifecycle for automatic subscription management

5. **HTML parsing failures**
   - Check Fruit annotations in `network/bean/` classes
   - V2EX may have changed their HTML structure

### Performance Considerations

- **Large RecyclerViews**: Use `SpeedyLinearLayoutManager` for improved scrolling
- **Image Loading**: Glide is configured with OkHttp integration for better performance
- **Memory**: Proguard and resource shrinking are enabled for release builds

## Key Technologies and Versions

- **Language**: Java 8 (source/target compatibility)
- **Build System**: Gradle 8.13 with Android Gradle Plugin
- **Minimum SDK**: API 27 (Android 8.1)
- **Target SDK**: API 36
- **Compile SDK**: API 36

**Core Dependencies:**
- **DI**: Dagger 2.57
- **Networking**: Retrofit 3.0.0 + OkHttp 5.1.0 + RxJava 2.2.21
- **View Binding**: ButterKnife 10.2.3
- **Image Loading**: Glide 4.16.0
- **HTML Parsing**: Fruit 1.0.4 (custom library)
- **Event Bus**: EventBus 3.3.1

## Distribution

- **Google Play Store**: https://play.google.com/store/apps/details?id=me.ghui.v2er
- **CoolApk**: https://www.coolapk.com/apk/me.ghui.v2er

The app is distributed under GPL license. See `LICENSE` file for details.
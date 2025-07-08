# Signing Configuration

This document explains how to set up signing for the V2er Android app.

## CI/CD Signing (GitHub Actions)

The release pipeline automatically handles signing using GitHub secrets:

1. **KEYSTORE_BASE64**: Base64-encoded keystore file
2. **KEYSTORE_PASSWORD**: Password for the keystore
3. **KEY_PASSWORD**: Password for the signing key
4. **KEY_ALIAS**: Alias of the signing key

The pipeline will:
1. Decode the keystore from the base64 secret
2. Place it in the correct location (`ghui.jks`)
3. Build signed APK/AAB files
4. Clean up the keystore file after building

## Local Development

For local signing, you have two options:

### Option 1: Use Debug Signing (Recommended)
Simply use the debug build variant, which uses Android's default debug keystore.

```bash
./gradlew assembleDebug
```

### Option 2: Set Up Release Signing
1. Obtain the keystore file from the project maintainer
2. Place it in the project root as `ghui.jks`
3. Set environment variables:
   ```bash
   export GHUI_KEYSTORE_PASSWORD="your-keystore-password"
   export GHUI_KEY_PASSWORD="your-key-password"
   ```
4. Build the release variant:
   ```bash
   ./gradlew assembleRelease
   ```

### Option 3: Use GitHub Variant
The GitHub variant uses a test keystore with known credentials:
- Keystore: `v2er.jks`
- Password: `v2er.app`
- Key alias: `v2er`
- Key password: `v2er.app`

**Note**: This should only be used for testing, not for production releases.

## Security Notes

- Never commit keystore files to the repository
- Keep keystore passwords secure and never share them publicly
- The `.gitignore` file is configured to exclude all `.jks` and `.keystore` files
- For production releases, always use the GitHub Actions release pipeline
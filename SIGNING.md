# Signing Configuration

This document explains how to set up signing for the V2er Android app.

## CI/CD Signing (GitHub Actions)

The release pipeline uses GitHub secrets as the single source of truth for signing:

1. **KEYSTORE_BASE64**: Base64-encoded keystore file (the only source for the keystore)
2. **KEYSTORE_PASSWORD**: Password for the keystore
3. **KEY_PASSWORD**: Password for the signing key
4. **KEY_ALIAS**: Alias of the signing key

The pipeline will:
1. Decode the keystore from the KEYSTORE_BASE64 secret
2. Place it temporarily as `app/keystore.jks`
3. Build signed APK/AAB files
4. Clean up the keystore file after building

## Local Development

For local development, use the debug build variant which uses Android's default debug keystore:

```bash
./gradlew assembleDebug
```

If you need to test release builds locally:
1. Obtain the base64-encoded keystore from the project maintainer
2. Decode it and place it as `app/keystore.jks`:
   ```bash
   echo "$KEYSTORE_BASE64" | base64 --decode > app/keystore.jks
   ```
3. Set environment variables:
   ```bash
   export GHUI_KEYSTORE_PASSWORD="your-keystore-password"
   export GHUI_KEY_PASSWORD="your-key-password"
   export GHUI_KEY_ALIAS="your-key-alias"  # Optional, defaults to "ghui"
   ```
4. Build the release variant:
   ```bash
   ./gradlew assembleRelease
   ```
5. **Important**: Remove the keystore file after building:
   ```bash
   rm app/keystore.jks
   ```

## Security Notes

- The keystore is ONLY stored as a base64-encoded GitHub secret
- Never commit keystore files to the repository
- Always clean up temporary keystore files after use
- The `.gitignore` file excludes all `.jks` and `.keystore` files
- For production releases, always use the GitHub Actions release pipeline
- The GitHub build variant now uses debug signing for simplicity
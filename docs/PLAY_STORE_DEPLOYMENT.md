# Google Play Store Deployment Guide

This guide explains how to set up automatic deployment to Google Play Store for the V2er Android app.

## Prerequisites

1. Google Play Developer Account
2. App already published on Play Store (at least once manually)
3. Google Play Console API access enabled
4. Service Account with appropriate permissions

## Setting Up Google Play Console

### 1. Enable Google Play Console API

1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Create a new project or select existing one
3. Enable "Google Play Android Developer API"
4. Go to APIs & Services > Google Play Android Developer API
5. Click "Enable"

### 2. Create Service Account

1. In Google Cloud Console, go to "IAM & Admin" > "Service Accounts"
2. Click "Create Service Account"
3. Name: `v2er-github-actions`
4. Description: "Service account for GitHub Actions deployments"
5. Click "Create and Continue"
6. Skip optional permissions (we'll set them in Play Console)
7. Click "Done"

### 3. Generate Service Account Key

1. Click on the created service account
2. Go to "Keys" tab
3. Click "Add Key" > "Create new key"
4. Choose JSON format
5. Download the key file (keep it secure!)

### 4. Grant Permissions in Play Console

1. Go to [Google Play Console](https://play.google.com/console)
2. Go to "Users and permissions"
3. Click "Invite new users"
4. Email: Use the service account email (format: `name@project-id.iam.gserviceaccount.com`)
5. Grant these permissions:
   - **App access**: Select "V2er" app
   - **Release management**: 
     - Manage production releases
     - Manage testing track releases
     - View app information
   - **Financial data**: View financial data (optional, for reports)

## GitHub Repository Setup

### Required Secrets

Add these secrets to your GitHub repository (Settings > Secrets and variables > Actions):

| Secret Name | Description | How to Get |
|------------|-------------|------------|
| `KEYSTORE_BASE64` | Base64 encoded keystore file | `base64 -i your-keystore.jks` (Mac) or `base64 your-keystore.jks` (Linux) |
| `KEYSTORE_PASSWORD` | Keystore password | Your keystore password |
| `KEY_ALIAS` | Key alias in keystore | Usually "ghui" or your chosen alias |
| `KEY_PASSWORD` | Key password | Your key password (often same as keystore password) |
| `PLAY_STORE_SERVICE_ACCOUNT_JSON` | Service account JSON content | Copy entire content of downloaded JSON file |

### Required Variables

Add these repository variables (Settings > Secrets and variables > Actions > Variables):

| Variable Name | Value | Description |
|--------------|-------|-------------|
| `ENABLE_SIGNING` | `true` | Enable APK/AAB signing |
| `ENABLE_PLAY_STORE_UPLOAD` | `true` | Enable automatic Play Store upload |

## Deployment Workflows

### 1. Automatic Deployment (Tag Push)

When you push a version tag, the app is automatically built and uploaded:

```bash
# Create and push a version tag
git tag v1.2.3
git push origin v1.2.3
```

This will:
- Build signed APK and AAB
- Create GitHub Release with artifacts
- Upload AAB to Play Store (Internal track, Draft status)

### 2. Manual Deployment (Workflow Dispatch)

Trigger deployment manually from GitHub Actions:

1. Go to Actions tab
2. Select "Release" workflow
3. Click "Run workflow"
4. Choose:
   - Version (e.g., v1.2.3)
   - Track (internal/alpha/beta/production)
   - Status (draft/completed)

### 3. Fastlane Deployment (Alternative)

Use Fastlane for more control:

```bash
# Local deployment
bundle install
bundle exec fastlane android deploy_internal

# Via GitHub Actions
# Go to Actions > Fastlane Deploy > Run workflow
```

## Release Tracks

### Track Strategy

1. **Internal** (`internal`)
   - For internal testing team
   - Quick iteration and testing
   - Status: Usually `draft`

2. **Alpha** (`alpha`)
   - Early testing with limited users
   - Major feature testing
   - Status: `completed`

3. **Beta** (`beta`)
   - Wider testing audience
   - Pre-release validation
   - Status: `completed`

4. **Production** (`production`)
   - Full release to all users
   - Staged rollout recommended
   - Status: `completed`

### Promotion Flow

```
Internal -> Alpha -> Beta -> Production
```

Use Fastlane lanes to promote:

```bash
# Promote from internal to alpha
bundle exec fastlane android promote_to_alpha

# Promote from alpha to beta
bundle exec fastlane android promote_to_beta

# Promote from beta to production (10% rollout)
bundle exec fastlane android promote_to_production

# Complete production rollout (100%)
bundle exec fastlane android complete_rollout
```

## Version Management

### Version Code and Name

Update in `app/build.gradle`:

```gradle
android {
    defaultConfig {
        versionCode 123  // Increment for each release
        versionName "1.2.3"  // User-visible version
    }
}
```

### Semantic Versioning

Follow semantic versioning (MAJOR.MINOR.PATCH):
- MAJOR: Breaking changes
- MINOR: New features
- PATCH: Bug fixes

## Release Notes

### Automated Release Notes

Place release notes in `whatsnew/` directory:

```
whatsnew/
├── whatsnew-en-US      # English
├── whatsnew-zh-CN      # Simplified Chinese
└── whatsnew-zh-TW      # Traditional Chinese
```

Maximum 500 characters per file.

### Metadata Management

For full metadata management with Fastlane:

```
fastlane/
└── metadata/
    └── android/
        ├── en-US/
        │   ├── title.txt
        │   ├── short_description.txt
        │   ├── full_description.txt
        │   └── changelogs/
        │       └── 123.txt  # Version code
        └── zh-CN/
            └── ...
```

## Troubleshooting

### Common Issues

1. **Authentication Failed**
   - Verify service account email in Play Console
   - Check JSON key is correctly copied to GitHub secret
   - Ensure permissions are granted in Play Console

2. **Version Code Already Exists**
   - Increment versionCode in build.gradle
   - Each upload must have unique version code

3. **Signing Issues**
   - Verify keystore base64 encoding
   - Check key alias matches
   - Ensure passwords are correct

4. **Upload Fails**
   - Check app package name matches
   - Verify AAB is correctly signed
   - Ensure track exists in Play Console

### Debug Commands

```bash
# Verify keystore
keytool -list -v -keystore your-keystore.jks

# Test service account locally
export GOOGLE_APPLICATION_CREDENTIALS=path/to/service-account.json
bundle exec fastlane supply init

# Check AAB signing
bundletool build-apks --bundle=app.aab --output=app.apks
unzip -l app.apks
```

## Security Best Practices

1. **Rotate Service Account Keys**
   - Regenerate keys periodically
   - Remove old keys from Google Cloud Console

2. **Limit Permissions**
   - Grant minimum required permissions
   - Use separate accounts for different environments

3. **Protect Secrets**
   - Never commit secrets to repository
   - Use GitHub encrypted secrets
   - Rotate passwords regularly

4. **Audit Access**
   - Review Play Console users regularly
   - Monitor service account usage
   - Check GitHub Actions logs

## CI/CD Best Practices

1. **Test Before Release**
   - Run tests in CI before deployment
   - Use internal track for validation
   - Gradual rollout for production

2. **Version Control**
   - Tag releases in git
   - Keep changelog updated
   - Document breaking changes

3. **Monitoring**
   - Monitor crash reports
   - Track user reviews
   - Check vitals in Play Console

## Support

For issues or questions:
- GitHub Issues: [v2er-app/V2er-Android](https://github.com/v2er-app/V2er-Android/issues)
- Play Console Help: [support.google.com/googleplay/android-developer](https://support.google.com/googleplay/android-developer)
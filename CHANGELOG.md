# Changelog

All notable changes to V2er Android app will be documented in this file.

## v2.3.16 (Build 246)
1. Feature: Native VShare page for improved sharing experience
2. Fix: VShare update check cache issue
3. Improvement: General stability and dependency updates

## v2.3.15 (Build 245)
1. Improvement: General stability and maintenance updates

## v2.3.14 (Build 244)
1. Feature: Add reply sorting by popularity
2. Feature: Add built-in browser option for external links
3. Feature: Add Imgur image upload integration
4. Feature: Show app version in Settings
5. Feature: Include past 3 versions in Google Play release notes
6. Fix: Reduce splash screen logo size to prevent clipping
7. Fix: Status bar color inconsistency in Settings page
8. Fix: Simplify version display to show only version name
9. Improvement: Update feedback channel to v2er.app/help
10. Improvement: Move feedback item to first position in settings

## v2.3.13 (Build 243)
1. Feature: Add Android Adaptive Icons support
2. Feature: Add analytics tracking to VShare WebView
3. Improvement: Update Telegram group URL to new invite link

## v2.3.12 (Build 242)
1. Feature: Update splash screen logo to match launcher icon
2. Feature: Implement fullscreen WebView for vshare with theme auto-adaptation
3. Feature: Add vshare version checking and notification badge
4. Feature: Improve Vshare WebView with intent URL support and status bar padding
5. Fix: Posting navigation - handle successful responses in error handler
6. Improvement: Use BaseActivity's loading indicator for Vshare WebView

## v2.3.11 (Build 241)
1. Feature: Update app icon to match iOS design
2. Fix: Auto-collapse title bar not working
3. Fix: Main interface scrolling and layout issues

## v2.3.10 (Build 240)
1. Feature: Improve AppBar scrolling behavior with dynamic toolbar visibility
2. Fix: AppBar title bar overlapping status bar issue
3. Fix: Improve font size and layout for better accessibility

---

## How to Update Changelog

When updating the version in `config.gradle`:

1. Add a new version section at the top of this file
2. List all changes since the last version:
   - Use "Feature:" for new features
   - Use "Fix:" for bug fixes
   - Use "Improvement:" for enhancements
   - Use "Breaking:" for breaking changes

Example format:
```
## v2.4.0 (Build 245)
1. Feature: Description of new feature
2. Fix: Description of bug fix
3. Improvement: Description of enhancement
```

The changelog will be automatically used in GitHub Release notes and Google Play.

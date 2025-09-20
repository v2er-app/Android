# AppBar Status Bar Overlap Fix Test

## Test Case: Verify AppBar Padding Uses Actual Status Bar Height

### Before Fix
- Hardcoded `paddingTop = isAppbarExpanted ? 0 : 36;`
- Fixed 36dp value doesn't account for different devices/Android versions
- Issue occurs on newer devices like iQOO 13 with OriginOS 5 Android 15

### After Fix
- Dynamic `paddingTop = isAppbarExpanted ? 0 : Utils.getStatusBarHeight();`
- Uses actual status bar height from system resources
- Should work correctly across all device types and Android versions

### Test Steps
1. Launch the app on a device with different status bar height than 36dp
2. Scroll down to collapse the AppBar
3. Verify that the SlidingTabLayout has proper top padding
4. Verify that the app title bar doesn't overlap with the system status bar

### Expected Results
- The SlidingTabLayout should have padding equal to the actual status bar height when AppBar is collapsed
- No visual overlap between app title bar and system status bar
- Proper spacing maintained across different devices and Android versions

### Technical Verification
- `Utils.getStatusBarHeight()` returns the correct status bar height in pixels
- The method already handles fallback to 24dp for devices without proper resource identifiers
- The fix maintains backward compatibility while solving the overlap issue
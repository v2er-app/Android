# UI Changes Mockup

## Before and After Comparison

### 1. Menu Bar Changes
```
Before:
[★] [分享] [逆序浏览] [⋮]

After:  
[★] [分享] [逆序浏览] [≡] [⋮]
                       ↑
                   New sort icon
```

### 2. Reply List - Time Sorting (Default)
```
1楼 ghui                    ♥ 5
    This is the main question...

2楼 user1                   ♥ 2  
    Here's my answer...

3楼 user2                   ♥ 8
    @user1 I agree with your solution
```

### 3. Reply List - Popularity Sorting  
```
3楼 user2                   ♥ 8
    @user1 I agree with your solution

1楼 ghui                    ♥ 5
    This is the main question...

2楼 user1                   ♥ 2
    Here's my answer...
```

### 4. Threaded Replies with Indentation
```
1楼 ghui                    ♥ 5
    Original question here...

2楼 alice                   ♥ 3
    First response...

    3楼 bob 💬               ♥ 1
        @alice Good point!
        [Subtle background, left margin]

        4楼 charlie 💬       ♥ 0  
            @bob Thanks for clarification
            [More indented, different background]

5楼 david                   ♥ 2
    New topic discussion...
```

## Visual Indicators

1. **Threading Indicator**: 💬 emoji after floor number
2. **Indentation**: Progressive left margin for nested replies
3. **Background**: Subtle gray background for threaded replies
4. **Sorting**: Real-time toggle without page reload

## User Flow

1. User opens topic page → sees default time-based sorting
2. User taps sort icon → switches to popularity sorting
3. Replies reorder immediately showing most-thanked first
4. Threading relationships shown with visual indentation
5. User preference saved for future visits

This provides both requested features:
- **按热门程度显示** (Display by popularity) ✅
- **楼中楼** (Threaded replies) ✅
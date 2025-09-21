package me.ghui.v2er.general;

/**
 * Reply sorting modes for topic replies
 * Created for issue #41 - adding reply sorting by popularity
 */
public enum ReplySortMode {
    BY_TIME(0, "按时间排序"),
    BY_POPULARITY(1, "按热门排序");

    private final int value;
    private final String description;

    ReplySortMode(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static ReplySortMode fromValue(int value) {
        for (ReplySortMode mode : values()) {
            if (mode.value == value) {
                return mode;
            }
        }
        return BY_TIME; // default
    }
}
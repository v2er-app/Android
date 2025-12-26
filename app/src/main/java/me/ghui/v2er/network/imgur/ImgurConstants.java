package me.ghui.v2er.network.imgur;

/**
 * Imgur service constants
 */
public final class ImgurConstants {

    private ImgurConstants() {}

    /**
     * Imgur API base URL
     */
    public static final String API_BASE_URL = "https://api.imgur.com/";

    /**
     * Default Imgur Client ID for anonymous uploads (same as iOS version)
     */
    public static final String DEFAULT_CLIENT_ID = "546c25a59c58ad7";

    /**
     * SharedPreferences key for custom Imgur Client ID
     */
    public static final String PREF_KEY_IMGUR_CLIENT_ID = "pref_key_imgur_client_id";

    /**
     * SharedPreferences key for upload history
     */
    public static final String PREF_KEY_UPLOAD_HISTORY = "pref_key_imgur_upload_history";

    /**
     * Maximum number of upload history records to keep
     */
    public static final int MAX_UPLOAD_HISTORY = 100;

    /**
     * Maximum file size in bytes (10MB - Imgur's limit)
     */
    public static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024;

    /**
     * Upload timeout in seconds
     */
    public static final int UPLOAD_TIMEOUT_SECONDS = 60;
}

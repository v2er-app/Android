package me.ghui.v2er.network.imgur;

import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import me.ghui.v2er.general.Pref;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.util.Check;

/**
 * Manager for Imgur upload history
 * Persists uploaded images to SharedPreferences using GSON serialization
 */
public class ImgurUploadHistory {

    private static ImgurUploadHistory sInstance;
    private List<UploadedImage> mUploadedImages;

    private ImgurUploadHistory() {
        loadFromStorage();
    }

    public static synchronized ImgurUploadHistory get() {
        if (sInstance == null) {
            sInstance = new ImgurUploadHistory();
        }
        return sInstance;
    }

    private void loadFromStorage() {
        String json = Pref.read(ImgurConstants.PREF_KEY_UPLOAD_HISTORY);
        if (Check.notEmpty(json)) {
            try {
                Type listType = new TypeToken<List<UploadedImage>>() {}.getType();
                mUploadedImages = APIService.gson().fromJson(json, listType);
            } catch (Exception e) {
                mUploadedImages = new ArrayList<>();
            }
        }
        if (mUploadedImages == null) {
            mUploadedImages = new ArrayList<>();
        }
    }

    private void saveToStorage() {
        String json = APIService.gson().toJson(mUploadedImages);
        Pref.save(ImgurConstants.PREF_KEY_UPLOAD_HISTORY, json);
    }

    /**
     * Add a new uploaded image to history
     *
     * @param imageData Image data from Imgur response
     */
    public void addImage(ImgurUploadResponse.ImgurImageData imageData) {
        UploadedImage uploadedImage = new UploadedImage();
        uploadedImage.id = imageData.getId();
        uploadedImage.link = imageData.getLink();
        uploadedImage.thumbnailLink = imageData.getThumbnailLink();
        uploadedImage.deleteHash = imageData.getDeleteHash();
        uploadedImage.width = imageData.getWidth();
        uploadedImage.height = imageData.getHeight();
        uploadedImage.size = imageData.getSize();
        uploadedImage.uploadTime = System.currentTimeMillis();

        // Remove duplicate if exists
        for (int i = mUploadedImages.size() - 1; i >= 0; i--) {
            if (uploadedImage.id.equals(mUploadedImages.get(i).id)) {
                mUploadedImages.remove(i);
            }
        }

        // Add to beginning of list
        mUploadedImages.add(0, uploadedImage);

        // Trim to max size
        while (mUploadedImages.size() > ImgurConstants.MAX_UPLOAD_HISTORY) {
            mUploadedImages.remove(mUploadedImages.size() - 1);
        }

        saveToStorage();
    }

    /**
     * Get all uploaded images
     *
     * @return List of uploaded images (newest first)
     */
    public List<UploadedImage> getUploadedImages() {
        return new ArrayList<>(mUploadedImages);
    }

    /**
     * Remove an image from history
     *
     * @param id Image ID to remove
     */
    public void removeImage(String id) {
        for (int i = 0; i < mUploadedImages.size(); i++) {
            if (mUploadedImages.get(i).id.equals(id)) {
                mUploadedImages.remove(i);
                saveToStorage();
                break;
            }
        }
    }

    /**
     * Clear all upload history
     */
    public void clearHistory() {
        mUploadedImages.clear();
        saveToStorage();
    }

    /**
     * Get the count of uploaded images
     */
    public int getCount() {
        return mUploadedImages.size();
    }

    /**
     * Data class for an uploaded image
     */
    public static class UploadedImage implements Serializable {
        public String id;
        public String link;
        public String thumbnailLink;
        public String deleteHash;
        public int width;
        public int height;
        public long size;
        public long uploadTime;

        /**
         * Get the direct image link
         */
        public String getLink() {
            return link;
        }

        /**
         * Get thumbnail link for display in list
         */
        public String getThumbnailLink() {
            return thumbnailLink != null ? thumbnailLink : link;
        }

        /**
         * Get formatted size string
         */
        public String getFormattedSize() {
            if (size < 1024) {
                return size + " B";
            } else if (size < 1024 * 1024) {
                return String.format("%.1f KB", size / 1024.0);
            } else {
                return String.format("%.2f MB", size / 1024.0 / 1024.0);
            }
        }
    }
}

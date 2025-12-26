package me.ghui.v2er.network.imgur;

import com.google.gson.annotations.SerializedName;

/**
 * Imgur API upload response model
 */
public class ImgurUploadResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("status")
    private int status;

    @SerializedName("data")
    private ImgurImageData data;

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public ImgurImageData getData() {
        return data;
    }

    /**
     * Imgur image data from upload response
     */
    public static class ImgurImageData {

        @SerializedName("id")
        private String id;

        @SerializedName("link")
        private String link;

        @SerializedName("deletehash")
        private String deleteHash;

        @SerializedName("width")
        private int width;

        @SerializedName("height")
        private int height;

        @SerializedName("size")
        private long size;

        @SerializedName("type")
        private String type;

        @SerializedName("datetime")
        private long datetime;

        public String getId() {
            return id;
        }

        public String getLink() {
            return link;
        }

        public String getDeleteHash() {
            return deleteHash;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public long getSize() {
            return size;
        }

        public String getType() {
            return type;
        }

        public long getDatetime() {
            return datetime;
        }

        /**
         * Generate thumbnail URL (Imgur format: insert 's' before extension for small thumbnail)
         * e.g., https://i.imgur.com/abc.jpg -> https://i.imgur.com/abcs.jpg
         */
        public String getThumbnailLink() {
            if (link == null) return null;
            int dotIndex = link.lastIndexOf('.');
            if (dotIndex > 0) {
                return link.substring(0, dotIndex) + "s" + link.substring(dotIndex);
            }
            return link;
        }
    }
}

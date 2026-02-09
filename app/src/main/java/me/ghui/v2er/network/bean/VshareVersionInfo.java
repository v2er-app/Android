package me.ghui.v2er.network.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Vshare page version information
 * Used to check if the vshare page content has been updated
 */
public class VshareVersionInfo extends BaseInfo {
    @SerializedName("version")
    private int version;

    @SerializedName("lastUpdated")
    private String lastUpdated;

    @SerializedName("items")
    private List<Item> items;

    public VshareVersionInfo() {
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<Item> getItems() {
        return items;
    }

    @Override
    public boolean isValid() {
        return version > 0;
    }

    public static class Item implements Serializable {
        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("description")
        private String description;

        @SerializedName("url")
        private String url;

        @SerializedName("icon")
        private String icon;

        @SerializedName("iconClass")
        private String iconClass;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getUrl() {
            return url;
        }

        public String getIcon() {
            return icon;
        }

        public String getIconClass() {
            return iconClass;
        }
    }
}

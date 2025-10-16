package me.ghui.v2er.network.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Vshare page version information
 * Used to check if the vshare page content has been updated
 */
public class VshareVersionInfo extends BaseInfo {
    @SerializedName("version")
    private int version;

    @SerializedName("lastUpdated")
    private String lastUpdated;

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

    @Override
    public boolean isValid() {
        return version > 0;
    }
}

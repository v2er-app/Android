package me.ghui.v2er.network;

import me.ghui.v2er.general.App;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.util.Check;

/**
 * Created by ghui on 25/03/2017.
 */

public interface Constants {
    String HTTPS_SCHEME = "https:";
    String HTTP_SCHEME = "http:";
    // Default values (also used as compile-time constants for annotations)
    String BASE_URL = HTTPS_SCHEME + "//www.v2ex.com";
    String HOST_NAME = "v2ex.com";
    String PACKAGE_NAME = "me.ghui.v2ex";
    String PREF_KEY_BASE_URL = "pref_key_base_url";

    static String getBaseUrl() {
        if (App.get() == null) return BASE_URL;
        String url = Pref.read(PREF_KEY_BASE_URL);
        if (Check.notEmpty(url)) {
            return url;
        }
        return BASE_URL;
    }

    static String getHostName() {
        String baseUrl = getBaseUrl();
        try {
            java.net.URI uri = new java.net.URI(baseUrl);
            String host = uri.getHost();
            String[] parts = host.split("\\.");
            if (parts.length > 2) {
                return host.substring(host.indexOf('.') + 1);
            }
            return host;
        } catch (Exception e) {
            return HOST_NAME;
        }
    }

    static boolean isCustomBaseUrl() {
        return !BASE_URL.equals(getBaseUrl());
    }
}

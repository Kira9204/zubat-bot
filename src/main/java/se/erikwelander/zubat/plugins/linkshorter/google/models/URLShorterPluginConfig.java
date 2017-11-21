package se.erikwelander.zubat.plugins.linkshorter.google.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class URLShorterPluginConfig {
    @Expose
    @SerializedName("Enabled")
    private boolean enabled;
    @Expose
    @SerializedName("ShortenAbove")
    private int shortenAbove;
    @Expose
    @SerializedName("AppName")
    private String appName;
    @Expose
    @SerializedName("AppKey")
    private String appKey;

    public URLShorterPluginConfig(boolean enabled, int shortenAbove, String appName, String appKey) {
        this.enabled = enabled;
        this.shortenAbove = shortenAbove;
        this.appName = appName;
        this.appKey = appKey;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getShortenAbove() {
        return shortenAbove;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppKey() {
        return appKey;
    }
}

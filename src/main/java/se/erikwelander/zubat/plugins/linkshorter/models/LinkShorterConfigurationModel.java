package se.erikwelander.zubat.plugins.linkshorter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LinkShorterConfigurationModel {
    @Expose
    @SerializedName("Enabled")
    private boolean enabled;
    @Expose
    @SerializedName("ShortenAbove")
    private int shortenAbove;
    @Expose
    @SerializedName("ToBase")
    private int toBase;
    @Expose
    @SerializedName("BaseURL")
    private String baseURL;

    public LinkShorterConfigurationModel(boolean enabled, int shortenAbove, int toBase, String baseURL) {
        this.enabled = enabled;
        this.shortenAbove = shortenAbove;
        this.toBase = toBase;
        this.baseURL = baseURL;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getShortenAbove() {
        return shortenAbove;
    }

    public int getToBase() {
        return toBase;
    }

    public String getBaseURL() {
        return baseURL;
    }
}
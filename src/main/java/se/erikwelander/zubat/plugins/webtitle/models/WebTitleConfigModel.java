package se.erikwelander.zubat.plugins.webtitle.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WebTitleConfigModel {
    @Expose
    @SerializedName("Enabled")
    private boolean enabled;
    @Expose
    @SerializedName("MaxLength")
    private int maxLength;
    @Expose
    @SerializedName("UserAgent")
    private String userAgent;
    @Expose
    @SerializedName("TimeOut")
    private int timeOut;
    @Expose
    @SerializedName("DisabledFor")
    private DisabledForModel[] disabledForModels;

    public WebTitleConfigModel(final boolean enabled,
                               final int maxLength,
                               final String userAgent,
                               final int timeOut,
                               final DisabledForModel[] disabledForModels) {
        this.enabled = enabled;
        this.timeOut = timeOut;
        this.disabledForModels = disabledForModels;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public WebTitleConfigModel setEnabled(final boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public WebTitleConfigModel setMaxLength(final int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public WebTitleConfigModel setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public WebTitleConfigModel setTimeOut(final int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public DisabledForModel[] getDisabledForModels() {
        return disabledForModels;
    }

    public WebTitleConfigModel setDisabledForModels(final DisabledForModel[] disabledForModels) {
        this.disabledForModels = disabledForModels;
        return this;
    }
}

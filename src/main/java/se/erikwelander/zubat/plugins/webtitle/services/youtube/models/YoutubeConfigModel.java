package se.erikwelander.zubat.plugins.webtitle.services.youtube.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class YoutubeConfigModel {

    @Expose
    @SerializedName("AppName")
    private String appName;
    @Expose
    @SerializedName("AppKey")
    private String appKey;

    public YoutubeConfigModel(String appName, String appKey) {
        this.appName = appName;
        this.appKey = appKey;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppKey() {
        return appKey;
    }
}

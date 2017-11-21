package se.erikwelander.zubat.plugins.webtitle.services.twitch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TwitchConfigModel {
    @Expose
    @SerializedName("ClientID")
    private String clientID;

    public TwitchConfigModel(String clientID) {
        this.clientID = clientID;
    }

    public String getClientID() {
        return clientID;
    }
}

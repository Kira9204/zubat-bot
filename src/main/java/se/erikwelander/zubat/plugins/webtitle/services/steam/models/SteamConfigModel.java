package se.erikwelander.zubat.plugins.webtitle.services.steam.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SteamConfigModel {
    @Expose
    @SerializedName("Country")
    private String country;

    public SteamConfigModel(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public SteamConfigModel setCountry(String country) {
        this.country = country;
        return this;
    }
}

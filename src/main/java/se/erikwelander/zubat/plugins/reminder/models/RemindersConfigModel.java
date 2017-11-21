package se.erikwelander.zubat.plugins.reminder.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RemindersConfigModel {

    @Expose
    @SerializedName("Enabled")
    private boolean enabled;
    @Expose
    @SerializedName("RefreshPoll")
    private int refreshPoll;

    public RemindersConfigModel(boolean enabled, int refreshPoll) {
        this.enabled = enabled;
        this.refreshPoll = refreshPoll;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getRefreshPoll() {
        return refreshPoll;
    }
}

package se.erikwelander.zubat.plugins.crosstalk.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CrossTalksModel {

    @Expose
    @SerializedName("Enabled")
    private boolean enabled;

    @Expose
    @SerializedName("CrossTalks")
    private CrossTalkModel[] crossTalk;

    public CrossTalksModel(final boolean enabled,
                           final CrossTalkModel[] crossTalk) {
        this.enabled = enabled;
        this.crossTalk = crossTalk;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public CrossTalksModel setEnabled(final boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public CrossTalkModel[] getCrossTalks() {
        return crossTalk;
    }

    public CrossTalksModel setCrossTalks(final CrossTalkModel[] crossTalk) {
        this.crossTalk = crossTalk;
        return this;
    }
}

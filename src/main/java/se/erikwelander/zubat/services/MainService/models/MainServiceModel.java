package se.erikwelander.zubat.services.MainService.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MainServiceModel {
    @Expose
    @SerializedName("Trigger")
    private String trigger;
    @Expose
    @SerializedName("EnabledProtocols")
    private String[] enabledProtocols;

    public MainServiceModel() {
    }

    public MainServiceModel(final String trigger,
                            final String[] enabledProtocols) {
        this.trigger = trigger;
        this.enabledProtocols = enabledProtocols;
    }

    public final String getTrigger() {
        return trigger;
    }

    public String[] getEnabledProtocols() {
        return this.enabledProtocols;
    }

    public MainServiceModel setEnabledProtocols(String[] enabledProtocols) {
        this.enabledProtocols = enabledProtocols;
        return this;
    }
}

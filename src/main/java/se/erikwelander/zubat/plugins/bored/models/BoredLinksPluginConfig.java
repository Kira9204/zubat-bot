package se.erikwelander.zubat.plugins.bored.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BoredLinksPluginConfig {
    @Expose
    @SerializedName("Enabled")
    private boolean enabled;
    @Expose
    @SerializedName("DisabledFor")
    private DisabledForModel[] disabledForModels;

    public BoredLinksPluginConfig(final boolean enabled,
                                  final DisabledForModel[] disabledForModels) {
        this.enabled = enabled;
        this.disabledForModels = disabledForModels;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public BoredLinksPluginConfig setEnabled(final boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public DisabledForModel[] getDisabledForModels() {
        return disabledForModels;
    }

    public BoredLinksPluginConfig setDisabledForModels(final DisabledForModel[] disabledForModels) {
        this.disabledForModels = disabledForModels;
        return this;
    }
}

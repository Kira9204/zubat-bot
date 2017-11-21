package se.erikwelander.zubat.plugins.webtitle.models;

import com.google.gson.annotations.Expose;

public class DisabledForModel {
    @Expose
    private String protocol;
    @Expose
    private String server;
    @Expose
    private String[] channels;

    public DisabledForModel(final String protocol,
                            final String server,
                            final String[] channels) {
        this.protocol = protocol;
        this.server = server;
        this.channels = channels;
    }

    public String getProtocol() {
        return protocol;
    }

    public DisabledForModel setProtocol(final String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getServer() {
        return server;
    }

    public DisabledForModel setServer(final String server) {
        this.server = server;
        return this;
    }

    public String[] getChannels() {
        return channels;
    }

    public DisabledForModel setChannels(final String[] channels) {
        this.channels = channels;
        return this;
    }
}

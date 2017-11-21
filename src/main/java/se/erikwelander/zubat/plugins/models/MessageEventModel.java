package se.erikwelander.zubat.plugins.models;

public class MessageEventModel {
    private String protocol;
    private String server;
    private String channel;
    private String user;
    private String hostMask;
    private String message;

    public MessageEventModel(final String protocol,
                             final String server,
                             final String channel,
                             final String user,
                             final String hostMask,
                             final String message) {
        this.protocol = protocol;
        this.server = server;
        this.channel = channel;
        this.user = user;
        this.hostMask = hostMask;
        this.message = message;
    }

    public String getProtocol() {
        return protocol;
    }

    public MessageEventModel setProtocol(final String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getServer() {
        return server;
    }

    public MessageEventModel setServer(final String server) {
        this.server = server;
        return this;
    }

    public String getChannel() {
        return channel;
    }

    public MessageEventModel setChannel(final String channel) {
        this.channel = channel;
        return this;
    }

    public String getUser() {
        return user;
    }

    public MessageEventModel setUser(final String user) {
        this.user = user;
        return this;
    }

    public String getHostMask() {
        return hostMask;
    }

    public MessageEventModel setHostMask(final String hostMask) {
        this.hostMask = hostMask;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public MessageEventModel setMessage(final String message) {
        this.message = message;
        return this;
    }
}

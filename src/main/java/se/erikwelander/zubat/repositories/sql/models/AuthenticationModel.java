package se.erikwelander.zubat.repositories.sql.models;

public class AuthenticationModel {

    private int id;
    private String protocol;
    private String server;
    private String channel;
    private String nick;
    private String hostMask;

    public AuthenticationModel(int id,
                               String protocol,
                               String server,
                               String channel,
                               String nick,
                               String hostMask) {
        this.id = id;
        this.protocol = protocol;
        this.server = server;
        this.channel = channel;
        this.nick = nick;
        this.hostMask = hostMask;
    }

    public AuthenticationModel(String protocol,
                               String server,
                               String channel,
                               String nick,
                               String hostMask) {
        this.id = -1;
        this.protocol = protocol;
        this.server = server;
        this.channel = channel;
        this.nick = nick;
        this.hostMask = hostMask;
    }

    public int getId() {
        return id;
    }

    public AuthenticationModel setId(int id) {
        this.id = id;
        return this;
    }

    public String getProtocol() {
        return protocol;
    }

    public AuthenticationModel setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getServer() {
        return server;
    }

    public AuthenticationModel setServer(String server) {
        this.server = server;
        return this;
    }

    public String getChannel() {
        return channel;
    }

    public AuthenticationModel setChannel(String channel) {
        this.channel = channel;
        return this;
    }

    public String getNick() {
        return nick;
    }

    public AuthenticationModel setNick(String nick) {
        this.nick = nick;
        return this;
    }

    public String getHostMask() {
        return hostMask;
    }

    public AuthenticationModel setHostMask(String hostMask) {
        this.hostMask = hostMask;
        return this;
    }
}

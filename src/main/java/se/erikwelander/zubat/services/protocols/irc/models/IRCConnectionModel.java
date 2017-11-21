package se.erikwelander.zubat.services.protocols.irc.models;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public class IRCConnectionModel {
    @Expose
    private String host = "localhost";
    @Expose
    private int port = 6667;
    @Expose
    private String[] channels;
    @Expose
    private String nickName = "Zubat";
    @Expose
    private String realName = "Zubat multi-purpose IRC bot";
    @Expose
    private int reconnectDelay = 120;
    @Expose
    private int reconnectAttempts = 2;
    @Expose
    private boolean autoReconnect = false;
    @Expose
    private boolean autoNickChange = true;

    public IRCConnectionModel() {
    }

    public IRCConnectionModel(final String host,
                              final int port,
                              final String[] channels,
                              final String nickName,
                              final String realName,
                              final int reconnectDelay,
                              final int reconnectAttempts,
                              final boolean autoReconnect,
                              final boolean autoNickChange) {
        this.host = host;
        this.port = port;
        this.channels = channels;
        this.nickName = nickName;
        this.realName = realName;
        this.reconnectDelay = reconnectDelay;
        this.reconnectAttempts = reconnectAttempts;
        this.autoReconnect = autoReconnect;
        this.autoNickChange = autoNickChange;
    }

    public String getHost() {
        return this.host;
    }

    public IRCConnectionModel setHost(final String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return this.port;
    }

    public IRCConnectionModel setPort(int port) {
        this.port = port;
        return this;
    }

    public String[] getChannels() {
        return this.channels;
    }

    public IRCConnectionModel setChannels(final String[] channels) {
        this.channels = channels;
        return this;
    }

    public String getNickName() {
        return this.nickName;
    }

    public IRCConnectionModel setNickName(final String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getRealName() {
        return this.realName;
    }

    public IRCConnectionModel setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public int getReconnectDelay() {
        return this.reconnectDelay;
    }

    public IRCConnectionModel setReconnectDelay(int reconnectDelay) {
        this.reconnectDelay = reconnectDelay;
        return this;
    }

    public int getReconnectAttempts() {
        return this.reconnectAttempts;
    }

    public IRCConnectionModel setReconnectAttempts(final int reconnectAttempts) {
        this.reconnectAttempts = reconnectAttempts;
        return this;
    }

    public boolean isAutoReconnect() {
        return this.autoReconnect;
    }

    public IRCConnectionModel setAutoReconnect(final boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
        return this;
    }

    public boolean isAutoNickChange() {
        return this.autoNickChange;
    }

    public IRCConnectionModel setAutoNickChange(final boolean autoNickChange) {
        this.autoNickChange = autoNickChange;
        return this;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

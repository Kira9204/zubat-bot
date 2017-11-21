package se.erikwelander.zubat.services.protocols.mumble.models;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public class MumbleConnectionModel {

    @Expose
    private String host;
    @Expose
    private int port;
    @Expose
    private String userName;
    @Expose
    private String password;
    @Expose
    private String channel;
    @Expose
    private String token;

    public MumbleConnectionModel() {
    }

    public MumbleConnectionModel(final String host,
                                 final int port,
                                 final String userName,
                                 final String password,
                                 final String channel,
                                 final String token) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.channel = channel;
        this.token = token;
    }

    public String getHost() {
        return this.host;
    }

    public MumbleConnectionModel setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return this.port;
    }

    public MumbleConnectionModel setPort(int port) {
        this.port = port;
        return this;
    }

    public String getUserName() {
        return this.userName;
    }

    public MumbleConnectionModel setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {
        return this.password;
    }

    public MumbleConnectionModel setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getChannel() {
        return this.channel;
    }

    public MumbleConnectionModel setChannel(String channel) {
        this.channel = channel;
        return this;
    }

    public String getToken() {
        return this.token;
    }

    public MumbleConnectionModel setToken(String token) {
        this.token = token;
        return this;
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

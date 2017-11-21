package se.erikwelander.zubat.services.protocols.gtalk.models;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public class GTalkConnectionModel {
    @Expose
    private String userName;
    @Expose
    private String password;
    @Expose
    private String nick;
    @Expose
    private String host;
    @Expose
    private String serviceName;
    @Expose
    private int port;

    public GTalkConnectionModel() {
    }

    public GTalkConnectionModel(final String userName,
                                final String password,
                                final String nick,
                                final String host,
                                final String serviceName,
                                final int port) {
        this.userName = userName;
        this.password = password;
        this.nick = nick;
        this.host = host;
        this.serviceName = serviceName;
        this.port = port;
    }

    public String getUserName() {
        return this.userName;
    }

    public GTalkConnectionModel setUserName(final String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {
        return this.password;
    }

    public GTalkConnectionModel setPassword(final String password) {
        this.password = password;
        return this;
    }

    public String getNick() {
        return this.nick;
    }

    public GTalkConnectionModel setNick(final String nick) {
        this.nick = nick;
        return this;
    }

    public String getHost() {
        return this.host;
    }

    public GTalkConnectionModel setHost(final String host) {
        this.host = host;
        return this;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public GTalkConnectionModel setServiceName(final String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public int getPort() {
        return this.port;
    }

    public GTalkConnectionModel setPort(final int port) {
        this.port = port;
        return this;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

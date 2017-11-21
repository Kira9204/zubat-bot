package se.erikwelander.zubat.repositories.sql.models;

import java.util.Date;

public class ReminderModel {

    private int id;
    private int type;
    private Date date;
    private String text;
    private String fromUser;
    private String toUser;
    private String protocol;
    private String server;
    private String channel;

    public ReminderModel(int id,
                         int type,
                         Date date,
                         String text,
                         String fromUser,
                         String toUser,
                         String protocol,
                         String server,
                         String channel) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.text = text;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.protocol = protocol;
        this.server = server;
        this.channel = channel;
    }

    public ReminderModel(int type,
                         Date date,
                         String text,
                         String fromUser,
                         String toUser,
                         String protocol,
                         String server,
                         String channel) {
        this.id = -1;
        this.type = type;
        this.date = date;
        this.text = text;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.protocol = protocol;
        this.server = server;
        this.channel = channel;
    }

    public int getId() {
        return id;
    }

    public ReminderModel setId(int id) {
        this.id = id;
        return this;
    }

    public int getType() {
        return type;
    }

    public ReminderModel setType(int type) {
        this.type = type;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public ReminderModel setDate(Date date) {
        this.date = date;
        return this;
    }

    public String getText() {
        return text;
    }

    public ReminderModel setText(String text) {
        this.text = text;
        return this;
    }

    public String getFromUser() {
        return fromUser;
    }

    public ReminderModel setFromUser(String fromUser) {
        this.fromUser = fromUser;
        return this;
    }

    public String getToUser() {
        return toUser;
    }

    public ReminderModel setToUser(String toUser) {
        this.toUser = toUser;
        return this;
    }

    public String getProtocol() {
        return protocol;
    }

    public ReminderModel setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getServer() {
        return server;
    }

    public ReminderModel setServer(String server) {
        this.server = server;
        return this;
    }

    public String getChannel() {
        return channel;
    }

    public ReminderModel setChannel(String channel) {
        this.channel = channel;
        return this;
    }
}

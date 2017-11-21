package se.erikwelander.zubat.repositories.sql.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SQLConnectorSettings {

    @Expose
    @SerializedName("Host")
    private String host;
    @Expose
    @SerializedName("Port")
    private String port;
    @Expose
    @SerializedName("Encoding")
    private String encoding;
    @Expose
    @SerializedName("TimeZone")
    private String timeZone;
    @Expose
    @SerializedName("Username")
    private String username;
    @Expose
    @SerializedName("Password")
    private String password;
    @Expose
    @SerializedName("Database")
    private String database;

    public SQLConnectorSettings(String host,
                                String port,
                                String encoding,
                                String timeZone,
                                String username,
                                String password,
                                String database) {
        this.host = host;
        this.port = port;
        this.encoding = encoding;
        this.timeZone = timeZone;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public final String getHost() {
        return host;
    }

    public final String getPort() {
        return port;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public final String getEncoding() {
        return encoding;
    }

    public final String getUsername() {
        return username;
    }

    public final String getPassword() {
        return password;
    }

    public final String getDatabase() {
        return database;
    }
}

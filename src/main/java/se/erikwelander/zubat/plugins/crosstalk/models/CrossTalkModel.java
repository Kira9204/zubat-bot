package se.erikwelander.zubat.plugins.crosstalk.models;

import com.google.gson.annotations.Expose;

public class CrossTalkModel {
    @Expose
    private String originProtocol;
    @Expose
    private String originServer;
    @Expose
    private String originChannel;

    @Expose
    private String destinationProtocol;
    @Expose
    private String destinationServer;
    @Expose
    private String destinationChannel;

    public CrossTalkModel(final String originProtocol,
                          final String originServer,
                          final String originChannel,
                          final String destinationProtocol,
                          final String destinationServer,
                          final String destinationChannel) {
        this.originProtocol = originProtocol;
        this.originServer = originServer;
        this.originChannel = originChannel;
        this.destinationProtocol = destinationProtocol;
        this.destinationServer = destinationServer;
        this.destinationChannel = destinationChannel;
    }

    public String getOriginProtocol() {
        return originProtocol;
    }

    public String getOriginServer() {
        return originServer;
    }

    public String getOriginChannel() {
        return originChannel;
    }

    public String getDestinationProtocol() {
        return destinationProtocol;
    }

    public String getDestinationServer() {
        return destinationServer;
    }

    public String getDestinationChannel() {
        return destinationChannel;
    }
}
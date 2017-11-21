package se.erikwelander.zubat.services.protocols.mumble.protocol;

import se.erikwelander.zubat.services.protocols.mumble.exceptions.MumbleBotException;
import se.erikwelander.zubat.services.protocols.mumble.models.MumbleConnectionModel;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection.Connection;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection.Session;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.PacketFactory;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.PacketType;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.SerializedPacket;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.handler.*;

import java.util.HashMap;
import java.util.Map;

public class MumbleBot implements Runnable {
    private MumbleConnectionModel connectionModel;
    private Map<PacketType, IPacketHandler> handlers = new HashMap<>();
    private Thread thread = null;

    public MumbleBot() {
    }

    public MumbleBot(MumbleConnectionModel connectionModel) {
        this.connectionModel = connectionModel;
        start();
    }

    private void registerHandlers() {
        if (!handlers.isEmpty()) {
            return;
        }
        handlers.put(PacketType.Version, new VersionHandler());
        handlers.put(PacketType.Ping, new PingHandler());
        handlers.put(PacketType.Reject, new RejectHandler());
        handlers.put(PacketType.ServerSync, new ServerSyncHandler());
        handlers.put(PacketType.ChannelState, new ChannelStateHandler());
        handlers.put(PacketType.UserRemove, new UserRemoveHandler(this.connectionModel));
        handlers.put(PacketType.UserState, new MumbleEventUserState(this.connectionModel));
        handlers.put(PacketType.TextMessage, new MumbleEventTextListener(this.connectionModel));
        handlers.put(PacketType.CryptSetup, new CryptSetupHandler());
        handlers.put(PacketType.PermissionQuery, new PermissionQueryHandler());
        handlers.put(PacketType.CodecVersion, new CodecVersionHandler());
        handlers.put(PacketType.ServerConfig, new ServerConfigHandler());
    }

    private void start() {
        if (null == this.thread) {
            this.thread = new Thread(this);
            this.thread.start();
        }

    }

    @Override
    public void run() {

        try (Connection connection = new Connection(connectionModel.getHost(), connectionModel.getPort())) {

            registerHandlers();
            connection.pushPacket(PacketFactory.newVersionPacket(1, 2, 13));
            connection.pushPacket(PacketFactory.newAuthenticatePacket(connectionModel.getUserName()));

            Session session = new Session(connection);

            while (connection.isConnected()) {
                while (connection.hasReceivedPackets()) {

                    SerializedPacket packet = connection.popPacket();

                    //connection.clearPackets(PacketType.TextMessage);    //Ugly hack, but it works.

                    if (handlers.containsKey(packet.getType())) {
                        handlers.get(packet.getType()).handle(session, packet);
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (Exception ex) {
                }
            }
        } catch (Exception ex) {
            throw new MumbleBotException("Could not establish a connection! Cause: " + ex.getMessage(), ex);
        }
    }
}

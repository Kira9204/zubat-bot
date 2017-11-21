/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot;

import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.command.CommandHandler;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection.Connection;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection.Session;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.PacketFactory;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.PacketType;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.SerializedPacket;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.handler.*;

import java.util.HashMap;
import java.util.Map;

// TODO: Use a logger instead of stdout to make sure logs are printed in correct order

/**
 * mumbot main class
 */
public class Mumbot {

    private static final Map<PacketType, IPacketHandler> handlers = new HashMap<>();

    public static void main(String[] args) {

        String hostname = "mumble.0x5e.se";
        int port = 64738;

        try (Connection connection = new Connection(hostname, port)) {

            System.out.println("Connection created, registering PacketHandlers...");
            registerHandlers();

            System.out.println("Starting handshakes with server...");
            connection.pushPacket(PacketFactory.newVersionPacket(1, 2, 13));
            connection.pushPacket(PacketFactory.newAuthenticatePacket("Zubat"));

            Session session = new Session(connection);

            while (connection.isConnected()) {
                while (connection.hasReceivedPackets()) {
                    SerializedPacket packet = connection.popPacket();

                    if (handlers.containsKey(packet.getType())) {
                        handlers.get(packet.getType()).handle(session, packet);
                    }
                }
            }

            System.out.println("shutting down...");
            session.setShouldExit(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void registerHandlers() {
        // TODO: Ignore-handler for messages that should be ignored (e.g. AuthenticateHandler extends IgnoreHandler)
        registerHandler(PacketType.Version, new VersionHandler());
        //registerHandler(PacketType.UDPTunnel, new UDPTunnelHandler());
        //registerHandler(PacketType.Authenticate, new AuthenticateHandler());
        registerHandler(PacketType.Ping, new PingHandler());
        registerHandler(PacketType.Reject, new RejectHandler());
        registerHandler(PacketType.ServerSync, new ServerSyncHandler());
        //registerHandler(PacketType.ChannelRemove, new ChannelRemoveHandler());
        registerHandler(PacketType.ChannelState, new ChannelStateHandler());
        registerHandler(PacketType.UserRemove, new UserRemoveHandler());
        registerHandler(PacketType.UserState, new UserStateHandler());
        //registerHandler(PacketType.BanList, new BanListHandler());
        registerHandler(PacketType.TextMessage, new TextMessageHandler(new CommandHandler()));
        //registerHandler(PacketType.PermissionDenied, new PermissionDeniedHandler());
        //registerHandler(PacketType.ACL, new ACLHandler());
        //registerHandler(PacketType.QueryUsers, new QueryUsersHandler());
        registerHandler(PacketType.CryptSetup, new CryptSetupHandler());
        //registerHandler(PacketType.ContextActionModify, new ContextActionModifyHandler());
        //registerHandler(PacketType.ContextAction, new ContextActionHandler());
        //registerHandler(PacketType.UserList, new UserListHandler());
        //registerHandler(PacketType.VoiceTarget, new VoiceTargetHandler());
        registerHandler(PacketType.PermissionQuery, new PermissionQueryHandler());
        registerHandler(PacketType.CodecVersion, new CodecVersionHandler());
        //registerHandler(PacketType.UserStats, new UserStatsHandler());
        //registerHandler(PacketType.RequestBlob, new RequestBlobHandler());
        registerHandler(PacketType.ServerConfig, new ServerConfigHandler());
        //registerHandler(PacketType.SuggestConfig, new SuggestConfigHandler());
    }

    private static void registerHandler(PacketType packetType, IPacketHandler handler) {
        handlers.put(packetType, handler);
    }
}

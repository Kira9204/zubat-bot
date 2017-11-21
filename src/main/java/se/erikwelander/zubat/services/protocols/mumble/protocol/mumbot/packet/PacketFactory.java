/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet;

import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.MumbleProto.Mumble;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server.Channel;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server.User;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.util.VersionHelper;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Helper for creating certain mandatory packets
 */
public class PacketFactory {
    public static Packet newVersionPacket(int major, int minor, int patch) {
        Mumble.Version message = Mumble.Version.newBuilder()
                .setOs(System.getProperty("os.name"))
                .setOsVersion(System.getProperty("os.version"))
                .setRelease("mumbot")
                .setVersion(VersionHelper.packVersionToInt32(major, minor, patch))
                .build();

        return new Packet(PacketType.Version, message);
    }

    public static Packet newAuthenticatePacket(String username) {
        Mumble.Authenticate message = Mumble.Authenticate.newBuilder()
                .setUsername(username)
                .build();

        return new Packet(PacketType.Authenticate, message);
    }

    public static Packet newPingPacket(long time) {
        Mumble.Ping message = Mumble.Ping.newBuilder()
                .setTimestamp(time)
                .build();

        return new Packet(PacketType.Ping, message);
    }

    public static Packet newTextMessagePacket(String msg, int sessionID, User... users) {
        Mumble.TextMessage message = Mumble.TextMessage.newBuilder()
                .setMessage(msg)
                .setActor(sessionID)
                .addAllSession(Arrays.stream(users).map(User::getSessionID).collect(Collectors.toList()))
                .build();

        return new Packet(PacketType.TextMessage, message);
    }

    public static Packet newTextMessagePacket(String msg, int sessionID, Channel... channels) {
        Mumble.TextMessage message = Mumble.TextMessage.newBuilder()
                .setMessage(msg)
                .setActor(sessionID)
                .addAllChannelId(Arrays.stream(channels).map(Channel::getID).collect(Collectors.toList()))
                .build();

        return new Packet(PacketType.TextMessage, message);
    }
}

/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.MumbleProto.Mumble;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection.Session;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.util.VersionHelper;

/**
 * Handler for Version messages. Basically just prints the version out and forgets about it.
 */
public class VersionHandler implements IProtobufPacketHandler<Mumble.Version> {
    @Override
    public Mumble.Version deserialize(byte[] data) throws InvalidProtocolBufferException {
        return Mumble.Version.parseFrom(data);
    }

    @Override
    public void handle(Session session, Mumble.Version message) {
        // Extract version numbers...
        int[] ver = VersionHelper.extractVersionFromInt32(message.getVersion());

        // ...and print them
        System.out.println(
                String.format("Server version: %d.%d.%d (%s: %s)",
                        ver[0],
                        ver[1],
                        ver[2],
                        message.getOs(),
                        message.getRelease()));
    }
}

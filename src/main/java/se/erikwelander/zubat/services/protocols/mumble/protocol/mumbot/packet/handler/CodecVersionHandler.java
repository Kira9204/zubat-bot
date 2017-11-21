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
 * Handler for CodecVersion messages.
 */
public class CodecVersionHandler implements IProtobufPacketHandler<Mumble.CodecVersion> {
    @Override
    public Mumble.CodecVersion deserialize(byte[] data) throws InvalidProtocolBufferException {
        return Mumble.CodecVersion.parseFrom(data);
    }

    @Override
    public void handle(Session session, Mumble.CodecVersion message) {
        // TODO: Save codec version somewhere

        System.out.println("Received codec version info:");

        // TODO: Figure out version format

        int[] ver = VersionHelper.extractVersionFromInt32(message.getAlpha());
        System.out.println("  CELT (Alpha): " + ver[0] + "." + ver[1] + "." + ver[2]);

        ver = VersionHelper.extractVersionFromInt32(message.getBeta());
        System.out.println("  CELT (Beta): " + ver[0] + "." + ver[1] + "." + ver[2]);

        System.out.println("  preferred: " + (message.getPreferAlpha() ? "Alpha" : "Beta"));

        if (message.hasOpus()) {
            System.out.println("  OPUS: " + message.getOpus());
        }
    }
}

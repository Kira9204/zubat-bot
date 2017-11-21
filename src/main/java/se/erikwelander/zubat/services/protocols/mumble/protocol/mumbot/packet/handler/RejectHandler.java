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

/**
 * Handler for Reject messages. Attempts to shut down the socket upon receiving.
 */
public class RejectHandler implements IProtobufPacketHandler<Mumble.Reject> {
    @Override
    public Mumble.Reject deserialize(byte[] data) throws InvalidProtocolBufferException {
        return null;
    }

    @Override
    public void handle(Session session, Mumble.Reject message) {
        System.out.println("Server rejected connection:");

        if (message.hasType()) {
            System.out.println("  RejectType: " + message.getType());
        }

        if (message.hasReason()) {
            System.out.println("  Reason: " + message.getReason());
        }

        session.setShouldExit(true);
    }
}

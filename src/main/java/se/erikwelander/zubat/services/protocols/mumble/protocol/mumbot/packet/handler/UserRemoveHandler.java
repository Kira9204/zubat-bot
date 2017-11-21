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
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server.UserRegistry;

/**
 * Handler for UserRemove messages. Attempts to shut down the socket if local session is removed.
 */
public class UserRemoveHandler implements IProtobufPacketHandler<Mumble.UserRemove> {
    @Override
    public Mumble.UserRemove deserialize(byte[] data) throws InvalidProtocolBufferException {
        return Mumble.UserRemove.parseFrom(data);
    }

    @Override
    public void handle(Session session, Mumble.UserRemove message) {
        int id = message.getSession();

        System.out.println("Removing user with sessionID: " + id + " (\"" + UserRegistry.get(id).getName() + "\")");

        if (id == session.getSessionID()) {
            System.out.println("wait a minute, that's us! :O");
            session.setShouldExit(true);
        }

        if (message.hasActor() && message.getActor() != id) {
            System.out.println("  issued by: " + UserRegistry.get(message.getActor()).getName());
        } else {
            System.out.println("  self-issued removal (leaving)");
        }

        if (message.hasReason()) {
            System.out.println("  reason: " + message.getReason());
        }

        if (message.getBan()) {
            System.out.println("  ban: " + message.getBan());
        }
    }
}

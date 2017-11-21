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
 * Handler for PermissionQuery messages.
 */
public class PermissionQueryHandler implements IProtobufPacketHandler<Mumble.PermissionQuery> {
    @Override
    public Mumble.PermissionQuery deserialize(byte[] data) throws InvalidProtocolBufferException {
        return Mumble.PermissionQuery.parseFrom(data);
    }

    @Override
    public void handle(Session session, Mumble.PermissionQuery message) {

        // TODO: Save permissions somewhere (create Permissions -class and store that in a User field?)

        System.out.println("Received PermissionQuery!");
    }
}

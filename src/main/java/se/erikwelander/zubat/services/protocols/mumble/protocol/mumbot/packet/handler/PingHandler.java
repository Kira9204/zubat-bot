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
 * Handler for Ping messages.
 */
public class PingHandler implements IProtobufPacketHandler<Mumble.Ping> {
    @Override
    public Mumble.Ping deserialize(byte[] data) throws InvalidProtocolBufferException {
        return Mumble.Ping.parseFrom(data);
    }

    @Override
    public void handle(Session session, Mumble.Ping message) {
        // XXX: Just ignore ping packets for now (printing them out is horribly flawed at the moment)
        //long time = System.currentTimeMillis();
        //System.out.println("PING: " + (time - message.getTimestamp()) + "ms");
    }
}

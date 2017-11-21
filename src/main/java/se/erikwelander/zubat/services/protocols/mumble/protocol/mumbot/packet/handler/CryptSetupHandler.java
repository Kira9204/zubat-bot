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
 * Handler for CryptSetup messages
 */
public class CryptSetupHandler implements IProtobufPacketHandler<Mumble.CryptSetup> {
    @Override
    public Mumble.CryptSetup deserialize(byte[] data) throws InvalidProtocolBufferException {
        return Mumble.CryptSetup.parseFrom(data);
    }

    @Override
    public void handle(Session session, Mumble.CryptSetup message) {
        System.out.println("Received crypt info from server:");

        if (message.hasKey()) {
            System.out.println("  key: " + message.getKey());
        }

        if (message.hasClientNonce()) {
            System.out.println("  cnonce: " + message.getClientNonce());
        }

        if (message.hasServerNonce()) {
            System.out.println("  snonce: " + message.getServerNonce());
        }

        // TODO: Setup cryptography

        // Cryptography is se-tup, set flag
        session.setCryptSetup(true);
    }
}

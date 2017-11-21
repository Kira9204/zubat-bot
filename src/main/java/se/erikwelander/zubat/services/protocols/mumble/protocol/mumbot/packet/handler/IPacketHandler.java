/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.handler;

import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection.Session;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.SerializedPacket;

import java.io.IOException;

/**
 * Packet handler for handling messages of type T
 */
public interface IPacketHandler<T> {

    /**
     * Deserialize message of type T from serialized packet.
     */
    T deserialize(byte[] data) throws IOException;

    /**
     * Deserializes and handles given packet
     */
    default void handle(Session session, SerializedPacket packet) {
        try {
            T message = deserialize(packet.getData());
            handle(session, message);
        } catch (IOException e) {
            System.err.println("Invalid packet data!");
        }
    }

    /**
     * Handles given message
     */
    void handle(Session session, T message);
}

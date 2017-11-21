/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet;

/**
 * Contains packet data in serialized form
 */
public class SerializedPacket {
    private final PacketType type;
    private final byte[] data;

    public SerializedPacket(PacketType type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    public PacketType getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }
}

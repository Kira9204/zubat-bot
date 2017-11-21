/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet;

import com.google.protobuf.Message;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Packets consist of prefix and payload
 */
public class Packet {
    private final PacketType type;
    private final Message payload;

    public Packet(PacketType type, Message payload) {
        this.payload = payload;
        this.type = type;
    }

    public void writeTo(OutputStream stream) throws IOException {
        byte[] payload = this.payload.toByteArray();
        byte[] length = ByteBuffer.allocate(4).putInt(payload.length).array();


        // Prefix
        stream.write(this.type.toBytes());
        stream.write(length);

        // Payload
        stream.write(payload);
    }

    public PacketType getType() {
        return this.type;
    }

    public Message getMessage() {
        return this.payload;
    }
}

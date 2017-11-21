/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

/**
 * Packet handler.
 */
public interface IProtobufPacketHandler<T extends Message> extends IPacketHandler<T> {
    @Override
    T deserialize(byte[] data) throws InvalidProtocolBufferException;
}

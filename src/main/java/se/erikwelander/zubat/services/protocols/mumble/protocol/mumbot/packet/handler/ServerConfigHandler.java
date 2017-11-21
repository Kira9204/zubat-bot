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
 * Handler for ServerConfig messages.
 */
public class ServerConfigHandler implements IProtobufPacketHandler<Mumble.ServerConfig> {
    @Override
    public Mumble.ServerConfig deserialize(byte[] data) throws InvalidProtocolBufferException {
        return Mumble.ServerConfig.parseFrom(data);
    }

    @Override
    public void handle(Session session, Mumble.ServerConfig message) {
        System.out.println("\nReceived server config:");

        // TODO: Save these somewhere. (...to Session?)

        if (message.hasMaxBandwidth()) {
            System.out.println("  max bandwidth: " + message.getMaxBandwidth());
        }

        if (message.hasAllowHtml()) {
            System.out.println("  html allowed: " + message.getAllowHtml());
        }

        if (message.hasMessageLength()) {
            System.out.println("  max message length: " + message.getMessageLength());
        }

        if (message.hasImageMessageLength()) {
            System.out.println("  max image message length: " + message.getImageMessageLength());
        }

        if (message.hasMaxUsers()) {
            System.out.println("  max users: " + message.getMaxUsers());
        }

        if (message.hasWelcomeText()) {
            session.printHello(message.getWelcomeText());
        }
    }
}

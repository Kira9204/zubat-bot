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
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server.Channel;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server.ChannelRegistry;

/**
 * Handler for ChannelState messages.
 */
public class ChannelStateHandler implements IProtobufPacketHandler<Mumble.ChannelState> {
    @Override
    public Mumble.ChannelState deserialize(byte[] data) throws InvalidProtocolBufferException {
        return Mumble.ChannelState.parseFrom(data);
    }

    @Override
    public void handle(Session session, Mumble.ChannelState message) {
        if (!message.hasChannelId()) {
            System.out.println("Received a ChannelState without channelID!");
            return;
        }

        int id = message.getChannelId();
        if (ChannelRegistry.containsChannel(id)) {
            handleUpdateChannel(id, message);
        } else {
            handleNewChannel(id, message);
        }
    }

    private void handleNewChannel(int id, Mumble.ChannelState message) {
        int parentID = message.hasParent() ? message.getParent() : -1; // No parent? Assume channel is root.
        String name = message.hasName() ? message.getName() : "NULL";
        String description =
                message.hasDescription()
                        ? message.getDescription()
                        : (message.hasDescriptionHash()
                        ? message.getDescriptionHash().toString()
                        : "No description provided.");

        boolean temporary = !message.hasTemporary() || message.getTemporary(); // Default to temporary channels
        int position = message.hasPosition() ? message.getPosition() : 0;
        int maxUsers = message.hasMaxUsers() ? message.getMaxUsers() : -1; // Value -1 means that server config is used

        System.out.println("Received ChannelState for a new channel! ID: " + id + " (\"" + name + "\")");
        Channel channel = new Channel(id, name, description, temporary, maxUsers, parentID, position);
        ChannelRegistry.register(channel);
    }

    private void handleUpdateChannel(int id, Mumble.ChannelState message) {
        Channel channel = ChannelRegistry.get(id);

        // Update info
        if (message.hasName()) channel.setName(message.getName());
        if (message.hasDescription()) channel.setDescription(message.getDescription());
        if (message.hasDescriptionHash()) channel.setDescription(message.getDescriptionHash().toString());
        if (message.hasParent()) channel.setParent(ChannelRegistry.get(message.getParent()));
        if (message.hasTemporary()) channel.setTemporary(message.getTemporary());
        if (message.hasPosition()) channel.setPosition(message.getPosition());
        if (message.hasMaxUsers()) channel.setMaxUsers(message.getMaxUsers());
        if (message.getLinksCount() > 0) channel.setLinks(message.getLinksList());

        // Add links
        if (message.getLinksAddCount() > 0) {
            message.getLinksAddList().stream()
                    .map(ChannelRegistry::get)
                    .forEach(channel::addLink);
        }

        // Remove links
        if (message.getLinksRemoveCount() > 0) {
            message.getLinksRemoveList().stream()
                    .map(ChannelRegistry::get)
                    .forEach(channel::removeLink);
        }

        System.out.println("Received updated ChannelState for an existing channel! ID: " + id + " (\"" + channel.getName() + "\")");
    }
}

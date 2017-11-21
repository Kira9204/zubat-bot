/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores channels.
 */
public class ChannelRegistry {
    private static final Map<Integer, Channel> channels = new HashMap<>();


    public static boolean containsChannel(int id) {
        return channels.containsKey(id);
    }

    public static void register(Channel channel) {
        channels.put(channel.getID(), channel);
    }

    public static Channel get(int id) {
        return channels.get(id);
    }
}

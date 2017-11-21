/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores user data
 */
public class UserRegistry {
    public static final Map<Integer, User> users = new HashMap<>();

    public static boolean containsUser(int sessionID) {
        return users.containsKey(sessionID);
    }

    public static void register(User user) {
        users.put(user.getSessionID(), user);
    }

    public static User get(int sessionID) {
        return users.get(sessionID);
    }

    public static void set(int sessionID, User user) {
        users.put(sessionID, user);
    }

    public static void remove(int sessionID) {
        users.remove(sessionID);
    }

    public static Collection<User> getAllUsers() {
        return users.values();
    }
}

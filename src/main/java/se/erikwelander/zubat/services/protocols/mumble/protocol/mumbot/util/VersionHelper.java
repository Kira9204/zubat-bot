/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.util;

/**
 * Helps bitpacking the mumble version number.
 */
public class VersionHelper {
    public static int packVersionToInt32(int major, int minor, int patch) {
        return ((major & 0xFFFF) << 16) + ((minor & 0xFF) << 8) + (patch & 0xFF);
    }

    public static int[] extractVersionFromInt32(int version) {
        return new int[]{
                version >> 16,
                (version & 0xFF00) >> 8,
                (version & 0xFF)
        };
    }
}

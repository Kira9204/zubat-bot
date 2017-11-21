/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server;

/**
 * Stores user data.
 */
public class User {
    private final int sessionID;
    private final String certHash;
    private int userID;
    private Channel channel;
    private boolean muted;
    private boolean deafened;
    private boolean mutedSelf;
    private boolean deafenedSelf;
    private boolean suppressed;

    private String name;
    private String comment;

    private boolean priority;
    private boolean recording;

    public User(int sessionID, int userID, String certHash, Channel channel, boolean muted, boolean deafened, boolean mutedSelf, boolean deafenedSelf, boolean suppressed, String name, String comment, boolean priority, boolean recording) {
        this.sessionID = sessionID;
        this.userID = userID;
        this.certHash = certHash;

        this.channel = channel;
        this.muted = muted;
        this.deafened = deafened;
        this.mutedSelf = mutedSelf;
        this.deafenedSelf = deafenedSelf;
        this.suppressed = suppressed;
        this.name = name;
        this.comment = comment;
        this.priority = priority;
        this.recording = recording;
    }

    public int getSessionID() {
        return sessionID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserId(int userID) {
        this.userID = userID;
    }

    public String getCertHash() {
        return certHash;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        if (channel == null) {
            System.out.println("Could not set channel: Channel was NULL!");
            return;
        }
        this.channel = channel;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isDeafened() {
        return deafened;
    }

    public void setDeafened(boolean deafened) {
        this.deafened = deafened;
    }

    public boolean isMutedSelf() {
        return mutedSelf;
    }

    public void setMutedSelf(boolean mutedSelf) {
        this.mutedSelf = mutedSelf;
    }

    public boolean isDeafenedSelf() {
        return deafenedSelf;
    }

    public void setDeafenedSelf(boolean deafenedSelf) {
        this.deafenedSelf = deafenedSelf;
    }

    public boolean isSuppressed() {
        return suppressed;
    }

    public void setSuppressed(boolean suppressed) {
        this.suppressed = suppressed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }
}

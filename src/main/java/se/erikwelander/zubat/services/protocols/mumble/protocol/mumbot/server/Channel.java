/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Stores channel data.
 */
public class Channel {
    private final int ID;
    private String name;
    private String description;
    private boolean temporary;
    private int maxUsers;

    private Channel parent;
    private Set<Channel> links;
    private int position;

    public Channel(int ID, String name, String description, boolean temporary, int maxUsers, int parentID, int position) {
        this(
                ID,
                name,
                description,
                temporary,
                maxUsers,
                parentID == -1 ? null : ChannelRegistry.get(parentID), // Set parent to null for root
                new HashSet<>(),
                position);
    }

    public Channel(int ID, String name, String description, boolean temporary, int maxUsers, Channel parent, Set<Channel> links, int position) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.temporary = temporary;
        this.maxUsers = maxUsers;
        this.parent = parent;
        this.links = links;
        this.position = position;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public Channel getParent() {
        return parent;
    }

    public void setParent(Channel parent) {
        this.parent = parent;
    }

    public Set<Channel> getLinks() {
        return links;
    }

    public void setLinks(List<Integer> ids) {
        this.links = ids.stream().map(ChannelRegistry::get).collect(Collectors.toSet());
    }

    public void addLink(Channel link) {
        this.links.add(link);
    }

    public void removeLink(Channel link) {
        this.links.remove(link);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Channel channel = (Channel) o;

        return getID() == channel.getID();

    }

    @Override
    public int hashCode() {
        return getID();
    }
}

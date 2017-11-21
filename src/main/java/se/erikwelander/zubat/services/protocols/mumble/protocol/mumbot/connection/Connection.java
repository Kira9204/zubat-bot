/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection;

import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.Packet;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.PacketType;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.SerializedPacket;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Handles connection to the server.
 */
public class Connection implements AutoCloseable {

    public static final int PING_INTERVAL = 15000; // Send keepalive every 15 seconds

    private final SocketReader reader;
    private final SocketWriter writer;
    private final Thread readerThread;
    private final Thread writerThread;

    private final LinkedList<Packet> sendQueue;
    private final LinkedList<SerializedPacket> receiveQueue;

    private final Socket socket;

    public Connection(String hostname, int port) throws IOException {
        this.socket = SocketHelper.createSocket(hostname, port);

        // Message I/O
        this.receiveQueue = new LinkedList<>();
        this.sendQueue = new LinkedList<>();
        this.reader = new SocketReader(this.socket, this.receiveQueue);
        this.writer = new SocketWriter(this.socket, this.sendQueue);

        this.readerThread = new Thread(this.reader, "Mumbot Socket Reader");
        this.writerThread = new Thread(this.writer, "Mumbot Socket Writer");

        this.readerThread.start();
        this.writerThread.start();
    }

    @Override
    public void close() throws IOException {
        this.reader.stop();
        this.writer.stop();
        synchronized (this.sendQueue) {
            this.sendQueue.notifyAll(); // Wake up the writer thread so that it dies properly.
        }
        this.socket.close();
    }

    public boolean isConnected() {
        return this.socket != null && this.socket.isConnected() && !this.socket.isClosed() && this.writerThread.isAlive() && this.readerThread.isAlive();
    }

    public boolean hasReceivedPackets() {
        return this.receiveQueue.size() > 0;
    }

    /**
     * Pops a packet from received messages queue
     */
    public SerializedPacket popPacket() {
        synchronized (this.receiveQueue) {
            if (this.hasReceivedPackets()) {
                return this.receiveQueue.pop();
            }

            return null;
        }
    }

    public void clearPackets(PacketType type) {
        for (int i = 0; i < this.sendQueue.size(); i++) {
            if (this.sendQueue.get(i).getType() == type) {
                this.sendQueue.remove(i);
                this.sendQueue.notifyAll();
            }
        }
    }

    /**
     * Pushes a packet to send queue
     */
    public void pushPacket(Packet packet) {
        synchronized (this.sendQueue) {
            this.sendQueue.push(packet);
            this.sendQueue.notifyAll();
        }
    }
}

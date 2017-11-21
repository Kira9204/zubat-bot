/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection;

import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.PacketType;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.SerializedPacket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 * Reads packets from socket's input stream and outputs them to Connection packet queue
 */
public class SocketReader extends SocketIOBase {

    private static final int PREFIX_LENGTH = 6;

    private final LinkedList<SerializedPacket> receiveQueue;
    private boolean hasPrefix;

    private PacketType packetType;
    private int packetLength;
    private ByteBuffer buffer;

    public SocketReader(Socket socket, LinkedList<SerializedPacket> receiveQueue) {
        super(socket);
        this.receiveQueue = receiveQueue;
        this.reset();
    }

    private void reset() {
        this.hasPrefix = false;
        this.buffer = ByteBuffer.allocate(PREFIX_LENGTH);
        this.packetLength = -1;
        this.packetType = null;
    }

    @Override
    protected synchronized boolean execute() {

        SerializedPacket packet;
        try {
            InputStream stream = this.getSocket().getInputStream();

            readPrefix(stream);

            if (this.hasPrefix) {
                packet = readPayload(stream);
            } else {
                return true;
            }
        } catch (IOException e) {
            System.out.println("Encountered an exception while reading packet:");
            e.printStackTrace();
            return true;
        }

        if (packet != null) {
            pushPacket(packet);
        }

        return false;
    }

    private synchronized void pushPacket(SerializedPacket packet) {
        synchronized (this.receiveQueue) {
            this.receiveQueue.add(packet);
            this.receiveQueue.notifyAll();
        }
    }

    private void readPrefix(InputStream stream) throws IOException {

        // Read PREFIX_LENGTH bytes from the stream
        for (int i = 0; i < PREFIX_LENGTH; i++) {
            int read = stream.read();

            // Read is -1 when stream is closed, which in turn means that connection has been closed.
            // That means: just get out of here!
            if (read == -1) {
                return;
            }

            this.buffer.put((byte) (read & 0xFF));
        }

        this.buffer.flip();

        this.packetType = PacketType.values()[this.buffer.getShort()];
        this.packetLength = this.buffer.getInt();

        //System.out.println("Packet prefix read! packet type:" + this.packetType + ", packet length: " + this.packetLength);

        this.buffer = ByteBuffer.allocate(this.packetLength);

        this.hasPrefix = true;
    }

    private SerializedPacket readPayload(InputStream stream) throws IOException {
        //System.out.println("Reading packet payload... expecting " + this.packetLength + " bytes of data...");

        // Write the whole packet from stream to the buffer
        for (int i = 0; i < this.packetLength; i++) {
            int read = stream.read();
            if (read == -1) {
                throw new IOException("EOS reached before expected! EOS byte index: " + i);
            }
            this.buffer.put((byte) (read & 0xFF));
        }

        // Flip buffer for reading
        this.buffer.flip();

        //System.out.println("Packet read! Packing data to SerializedPacket and sending it to handler...");

        // Data is stored in its serialized form.
        SerializedPacket packet = new SerializedPacket(packetType, this.buffer.array());

        // Reset for next packet.
        this.reset();

        return packet;
    }
}

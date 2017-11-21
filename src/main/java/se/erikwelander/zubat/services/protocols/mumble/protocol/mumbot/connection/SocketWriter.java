/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection;

import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.Packet;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.PacketType;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Takes packets from Connection packet queue and writes them to socket's output stream.
 */
public class SocketWriter extends SocketIOBase {

    private final LinkedList<Packet> writeQueue;

    public SocketWriter(Socket socket, LinkedList<Packet> writeQueue) {
        super(socket);

        this.writeQueue = writeQueue;
    }

    @Override
    protected boolean execute() {
        synchronized (this.writeQueue) {
            try {
                OutputStream stream = this.getSocket().getOutputStream();
                for (Packet packet : this.writeQueue) {
                    packet.writeTo(stream);
                }
                for (int i = 0; i < this.writeQueue.size(); i++) {
                    if (this.writeQueue.get(i).getType().equals(PacketType.TextMessage)) {
                        this.writeQueue.remove(i);
                    }

                }
            } catch (IOException e) {
                System.out.println("Encountered an exception while writing a packet:");
                e.printStackTrace();
                return true;
            }

            waitForQueueToFill();
        }

        return false;
    }

    private void waitForQueueToFill() {
        try {
            //System.out.println("Waiting for notify on lock in thread: " + Thread.currentThread().getName());
            this.writeQueue.wait();
        } catch (InterruptedException ignored) {
            //System.out.println("Continuing execution: " + Thread.currentThread().getName());
        }
    }
}

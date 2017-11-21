/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.MumbleProto.Mumble;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection.Connection;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection.Session;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.PacketFactory;

/**
 * Handler for ServerSync messages. Receiving ServerSync marks that we are done
 * initializing/synchronizing with server and can start running our tasks.
 */
public class ServerSyncHandler implements IProtobufPacketHandler<Mumble.ServerSync> {
    @Override
    public Mumble.ServerSync deserialize(byte[] data) throws InvalidProtocolBufferException {
        return Mumble.ServerSync.parseFrom(data);
    }

    @Override
    public void handle(Session session, Mumble.ServerSync message) {
        System.out.println("Server synchronization complete!");

        if (message.hasSession()) {
            System.out.println("  our sessionID: " + message.getSession());
            session.setSessionID(message.getSession());
        }

        if (message.hasMaxBandwidth()) {
            System.out.println("  max bandwidth: " + message.getMaxBandwidth());
        }

        if (message.hasWelcomeText()) {
            session.printHello(message.getWelcomeText());
        }

        // Set the session flag to indicate that initialization is done
        session.setHasReceivedServerSync(true);

        // TODO: Move this elsewhere, it's not packet handler's task to start new threads

        // Start ping/keepalive thread (socket read is blocking, so this has to be done in another thread)
        new Thread(() -> {
            while (!session.shouldExit()) {
                // Send ping packets every PING_INTERVAL seconds
                try {
                    session.getConnection().pushPacket(PacketFactory.newPingPacket(System.currentTimeMillis()));
                    // Sleep after sending the packet, so that shouldExit is read before next packet is sent.
                    Thread.sleep(Connection.PING_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Mumbot Network PING/keepalive").start();
    }
}

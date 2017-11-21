/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.MumbleProto.Mumble;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.command.CommandHandler;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection.Session;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.PacketFactory;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server.User;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server.UserRegistry;

/**
 * Handler for TextMessages
 */
public class TextMessageHandler implements IProtobufPacketHandler<Mumble.TextMessage> {

    private final CommandHandler commandHandler;

    public TextMessageHandler(CommandHandler commandHandler) {

        this.commandHandler = commandHandler;
    }

    @Override
    public Mumble.TextMessage deserialize(byte[] data) throws InvalidProtocolBufferException {
        return Mumble.TextMessage.parseFrom(data);
    }

    @Override
    public void handle(Session session, Mumble.TextMessage message) {
        System.out.println("received text message!");

        if (!message.hasActor()) {
            System.out.println("Sender is unknown!");
            return;
        }

        if (!message.hasMessage()) {
            System.out.println("No message!");
            return;
        }

        User sender = UserRegistry.get(message.getActor());
        String msg = message.getMessage();

        // TODO: CommandHandler
        // Is message a command?
        if (msg.equals("!hello")) {
            session.getConnection().pushPacket(PacketFactory.newTextMessagePacket("Haista vittu! :3", session.getSessionID(), sender));
        } else if (msg.equals("!hellochannel")) {
            session.getConnection().pushPacket(PacketFactory.newTextMessagePacket("Haistakaa vittu! juukyllä tarkotin teitä kaikkia, IHAN KAIKKIA! :3 <3", session.getSessionID(), sender.getChannel()));
        }
    }
}

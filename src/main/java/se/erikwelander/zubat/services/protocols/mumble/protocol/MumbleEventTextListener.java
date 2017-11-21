package se.erikwelander.zubat.services.protocols.mumble.protocol;

import com.google.protobuf.InvalidProtocolBufferException;
import se.erikwelander.zubat.services.protocols.mumble.MumbleEventHandler;
import se.erikwelander.zubat.services.protocols.mumble.models.MumbleConnectionModel;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.MumbleProto.Mumble;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection.Session;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.handler.IProtobufPacketHandler;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server.User;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server.UserRegistry;

public class MumbleEventTextListener implements IProtobufPacketHandler<Mumble.TextMessage> {

    private MumbleConnectionModel connectionModel;
    private MumbleEventHandler mumbleEventHandler;

    public MumbleEventTextListener(MumbleConnectionModel connectionModel) {
        this.connectionModel = connectionModel;
        this.mumbleEventHandler = new MumbleEventHandler(connectionModel);
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

        this.mumbleEventHandler.eventMessageRecieved(this.connectionModel, session, sender, msg);
    }
}

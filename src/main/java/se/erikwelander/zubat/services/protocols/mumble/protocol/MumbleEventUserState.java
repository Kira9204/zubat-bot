package se.erikwelander.zubat.services.protocols.mumble.protocol;

import com.google.protobuf.InvalidProtocolBufferException;
import se.erikwelander.zubat.services.protocols.mumble.MumbleEventHandler;
import se.erikwelander.zubat.services.protocols.mumble.models.MumbleConnectionModel;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.MumbleProto.Mumble;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection.Session;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.handler.IProtobufPacketHandler;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server.ChannelRegistry;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server.User;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server.UserRegistry;

public class MumbleEventUserState implements IProtobufPacketHandler<Mumble.UserState> {

    private MumbleConnectionModel connectionModel;
    private MumbleEventHandler mumbleEventHandler;

    public MumbleEventUserState(MumbleConnectionModel connectionModel) {
        this.connectionModel = connectionModel;
        this.mumbleEventHandler = new MumbleEventHandler(this.connectionModel);
    }

    @Override
    public Mumble.UserState deserialize(byte[] data) throws InvalidProtocolBufferException {
        return Mumble.UserState.parseFrom(data);
    }

    @Override
    public void handle(Session session, Mumble.UserState message) {
        if (!message.hasSession()) {
            return;
        }

        int id = message.getSession();
        if (UserRegistry.containsUser(id)) {
            handleUpdateUser(id, message);
        } else {
            handleNewUser(id, message);
        }
    }


    private void handleNewUser(int id, Mumble.UserState message) {

        int userID = message.hasUserId() ? message.getUserId() : -1;
        String name = message.hasName() ? message.getName() : "UNNAMED";
        String hash = message.hasHash() ? message.getHash() : "";
        String comment =
                message.hasComment()
                        ? message.getComment()
                        : message.hasCommentHash()
                        ? message.getCommentHash().toString() : "";
        int channelID = message.hasChannelId() ? message.getChannelId() : 0;

        boolean mute = message.hasMute() && message.getMute();
        boolean deaf = message.hasDeaf() && message.getDeaf();
        boolean muteSelf = message.hasSelfMute() && message.getSelfMute();
        boolean deafSelf = message.hasSelfDeaf() && message.getSelfDeaf();
        boolean suppress = message.hasSuppress() && message.getSuppress();
        boolean priority = message.hasPrioritySpeaker() && message.getPrioritySpeaker();
        boolean recording = message.hasRecording() && message.getRecording();

        User user = new User(id, userID, hash, ChannelRegistry.get(channelID), mute, deaf, muteSelf, deafSelf, suppress, name, comment, priority, recording);
        UserRegistry.register(user);

        this.mumbleEventHandler.eventUserJoined(this.connectionModel, user);
    }

    private void handleUpdateUser(int id, Mumble.UserState message) {
        User user = UserRegistry.get(id);

        // Update info
        if (message.hasUserId()) user.setUserId(message.getUserId());
        if (message.hasName()) user.setName(message.getName());
        if (message.hasChannelId()) user.setChannel(ChannelRegistry.get(message.getChannelId()));
        if (message.hasMute()) user.setMuted(message.getMute());
        if (message.hasDeaf()) user.setDeafened(message.getDeaf());
        if (message.hasSelfMute()) user.setMutedSelf(message.getSelfMute());
        if (message.hasSelfDeaf()) user.setDeafenedSelf(message.getSelfDeaf());
        if (message.hasSuppress()) user.setSuppressed(message.getSuppress());
        if (message.hasComment()) user.setComment(message.getComment());
        if (message.hasCommentHash()) user.setComment(message.getCommentHash().toString());
        if (message.hasPrioritySpeaker()) user.setPriority(message.getPrioritySpeaker());
        if (message.hasRecording()) user.setRecording(message.getRecording());

        UserRegistry.set(user.getSessionID(), user);

        this.mumbleEventHandler.eventUserUpdate(this.connectionModel, user);
    }
}
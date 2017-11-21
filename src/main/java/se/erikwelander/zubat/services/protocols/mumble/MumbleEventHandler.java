package se.erikwelander.zubat.services.protocols.mumble;

import se.erikwelander.zubat.globals.Globals;
import se.erikwelander.zubat.plugins.bored.BoredLinksPlugin;
import se.erikwelander.zubat.plugins.crosstalk.CrossTalk;
import se.erikwelander.zubat.plugins.crosstalk.interfaces.CrossTalkProtocolInterface;
import se.erikwelander.zubat.plugins.exceptions.PluginException;
import se.erikwelander.zubat.plugins.info.InfoPlugin;
import se.erikwelander.zubat.plugins.interfaces.PluginInterface;
import se.erikwelander.zubat.plugins.models.MessageEventModel;
import se.erikwelander.zubat.plugins.name.NamePlugin;
import se.erikwelander.zubat.plugins.webtitle.WebTitlePlugin;
import se.erikwelander.zubat.services.protocols.mumble.models.MumbleConnectionModel;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.MumbleProto.Mumble;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection.Session;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.packet.PacketFactory;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server.User;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server.UserRegistry;

import java.util.ArrayList;
import java.util.List;


public class MumbleEventHandler implements CrossTalkProtocolInterface {

    private MumbleConnectionModel connectionModel;
    private List<PluginInterface> plugins;

    public MumbleEventHandler(MumbleConnectionModel connectionModel) {

        this.connectionModel = connectionModel;

        plugins = new ArrayList<>();
        try {
            plugins.add(new BoredLinksPlugin());
        } catch (PluginException ex) {
            System.err.println("Failed to load plugin: BoredLinksPlugin. Cause: " + ex.getMessage());
        }
        try {
            plugins.add(new WebTitlePlugin());
        } catch (PluginException ex) {
            System.err.println("Failed to load plugin: webTitlePlugin. Cause: " + ex.getMessage());
        }
        plugins.add(new InfoPlugin());
        plugins.add(new NamePlugin());
    }

    public static void replyToUser(Session session, User user, String message) {
        session.getConnection().pushPacket(PacketFactory.newTextMessagePacket(message, session.getSessionID(), user));
    }

    public static void replyToChannel(Session session, User user, String message) {
        session.getConnection().pushPacket(PacketFactory.newTextMessagePacket(message, session.getSessionID(), user.getChannel()));
    }

    public static String getConnectedUsers() {

        if (null != UserRegistry.users && UserRegistry.users.size() > 0) {
            String users = "";
            for (User currentUser : UserRegistry.getAllUsers()) {
                users += currentUser.getName().toString() + ", ";
            }
            if (users.contains(",")) {
                users = users.substring(0, users.lastIndexOf(","));
            }
            return users;
        }
        return "";
    }

    public void eventUserJoined(MumbleConnectionModel model, User user) {
        MessageEventModel messageEventModel = new MessageEventModel(
                Globals.PROTOCOL_MUMBLE,
                model.getHost().toLowerCase().toString(),
                user.getChannel().getName().toLowerCase().toString(),
                user.getName().toString(),
                "" + user.getUserID(),
                "User joined: " + user.getName().toString()
        );
        triggerCrossTalk(messageEventModel, null, user);
    }

    public void eventUserLeft(MumbleConnectionModel model, Session session, Mumble.UserRemove message) {
        int id = message.getSession();
        User user = UserRegistry.get(id);

        MessageEventModel messageEventModel = new MessageEventModel(
                Globals.PROTOCOL_MUMBLE,
                model.getHost().toLowerCase().toString(),
                user.getChannel().getName().toLowerCase().toString(),
                user.getName().toString(),
                "" + user.getUserID(),
                "User left: " + user.getName().toString()
        );

        UserRegistry.remove(id);
        triggerCrossTalk(messageEventModel, null, user);
    }

    public void eventUserUpdate(MumbleConnectionModel model, User user) {
        MessageEventModel messageEventModel = new MessageEventModel(
                Globals.PROTOCOL_MUMBLE,
                model.getHost().toLowerCase().toString(),
                user.getChannel().getName().toLowerCase().toString(),
                user.getName().toString(),
                "" + user.getUserID(),
                "User moved: " + user.getName().toString()
        );
        triggerCrossTalk(messageEventModel, null, user);
    }

    public void eventMessageRecieved(MumbleConnectionModel connectionModel, Session session, User user, String message) {

        //Strip the message of HTML
        if (message.indexOf('<') != -1) {
            if (message.indexOf('>') != -1) {
                message = message.substring(9, message.indexOf("\">"));
            }
        }
        MessageEventModel messageEventModel = new MessageEventModel(
                Globals.PROTOCOL_MUMBLE,
                connectionModel.getHost().toLowerCase().toString(),
                user.getChannel().getName().toLowerCase().toString(),
                user.getName().toString(),
                "" + user.getUserID(),
                message
        );
        //m_session = session;
        try {
            triggerCrossTalk(messageEventModel, session, user);
        } catch (Exception ex) {

        }


        List<String> pluginSend = new ArrayList<>();
        for (PluginInterface plugin : plugins) {
            pluginSend.addAll(plugin.trigger(messageEventModel));
        }
        if (!pluginSend.isEmpty()) {
            for (String send : pluginSend) {
                replyToChannel(session, user, send);
            }
            return;
        }
    }

    @Override
    public void triggerCrossTalk(MessageEventModel messageEventModel, Object session, Object user) {
        Object[] object = new Object[2];
        object[0] = session;
        object[1] = user;
        CrossTalk.trigger(messageEventModel, object);
    }
}

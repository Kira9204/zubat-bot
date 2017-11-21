package se.erikwelander.zubat.plugins.crosstalk;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import se.erikwelander.zubat.globals.Globals;
import se.erikwelander.zubat.plugins.crosstalk.exceptions.CrossTalkException;
import se.erikwelander.zubat.plugins.crosstalk.models.CrossTalkModel;
import se.erikwelander.zubat.plugins.crosstalk.models.CrossTalksModel;
import se.erikwelander.zubat.plugins.models.MessageEventModel;
import se.erikwelander.zubat.services.protocols.irc.IRCEventListener;
import se.erikwelander.zubat.services.protocols.mumble.MumbleEventHandler;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection.Session;
import se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.server.User;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CrossTalk {

    private static CrossTalksModel crossTalkModel;
    private static Map<String, Object[]> map = new HashMap<>();

    public static void trigger(MessageEventModel messageEventModel, Object[] object) {

        if (!crossTalkModel.isEnabled()) {
            return;
        }

        String fromKey = messageEventModel.getProtocol() + messageEventModel.getServer()
                + messageEventModel.getChannel();
        map.put(fromKey, object);

        for (CrossTalkModel crossTalkModel : crossTalkModel.getCrossTalks()) {
            if (isValidFrom(crossTalkModel, messageEventModel)) {
                String toKey = crossTalkModel.getDestinationProtocol() +
                        crossTalkModel.getDestinationServer() +
                        crossTalkModel.getDestinationChannel();
                if (messageEventModel.getProtocol().equals(Globals.PROTOCOL_MUMBLE)) {
                    if (map.containsKey(toKey)) {
                        int botIndex = (int) map.get(toKey)[0];
                        IRCEventListener.sendMessageFromBotID(botIndex,
                                crossTalkModel.getDestinationChannel(),
                                messageEventModel.getUser() +
                                        "@" + messageEventModel.getProtocol() +
                                        ": " + messageEventModel.getMessage()
                        );
                    }
                } else if (messageEventModel.getProtocol().equals(Globals.PROTOCOL_IRC)) {
                    if (map.containsKey(toKey)) {
                        Object[] objects = map.get(toKey);
                        Session session = (Session) objects[0];
                        User user = (User) objects[1];

                        MumbleEventHandler.replyToChannel(session, user,
                                messageEventModel.getUser() +
                                        "@" + messageEventModel.getProtocol() +
                                        ": " + messageEventModel.getMessage()
                        );
                    }
                }
            }
        }
    }

    public static void loadCrossTalks() throws CrossTalkException {
        String filePath = Globals.PATH_CFG_PLUGIN + "CrossTalks.json";
        File file = new File(filePath);
        if (!file.isFile())
            throw new CrossTalkException("Could not load any configuration! The file " + filePath + " does not exist!");

        Gson gson = new Gson();
        String json = "";
        try {
            json = FileUtils.readFileToString(file.getAbsoluteFile(), "UTF8");
        } catch (IOException ex) {
            throw new CrossTalkException("Failed to read configuration file: " + file.getAbsolutePath() + "!", ex);
        }

        map.clear();
        CrossTalk.crossTalkModel = gson.fromJson(json, CrossTalksModel.class);
    }


    private static boolean isValidFrom(CrossTalkModel crossTalkModel, MessageEventModel messageEventModel) {
        return crossTalkModel.getOriginProtocol().equals(messageEventModel.getProtocol()) &&
                crossTalkModel.getOriginServer().equals(messageEventModel.getServer()) &&
                crossTalkModel.getOriginChannel().equals(messageEventModel.getChannel());
    }
}
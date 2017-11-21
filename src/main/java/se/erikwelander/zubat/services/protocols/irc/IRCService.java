package se.erikwelander.zubat.services.protocols.irc;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.pircbotx.Configuration;
import org.pircbotx.MultiBotManager;
import org.pircbotx.PircBotX;
import se.erikwelander.zubat.globals.Globals;
import se.erikwelander.zubat.services.protocols.irc.exceptions.IRCServiceException;
import se.erikwelander.zubat.services.protocols.irc.models.IRCConnectionModel;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class IRCService {

    private static MultiBotManager multiBotManager = new MultiBotManager();
    private static int botIdCounter = 0;

    public IRCService() throws IRCServiceException {
        IRCConnectionModel[] ircConnectionModels = loadConnectionModels();
        addBots(ircConnectionModels);
        startBots();
    }

    public static PircBotX getBot(final int botId) {
        return multiBotManager.getBotById(botId);
    }

    private void addBots(IRCConnectionModel[] connectionModels) {
        for (IRCConnectionModel model : connectionModels) {
            addBot(model);
        }
    }

    private void addBot(IRCConnectionModel connectionModel) {
        Configuration configuration = new Configuration.Builder()
                .setName(connectionModel.getNickName())
                .setRealName(connectionModel.getRealName())
                .setAutoNickChange(connectionModel.isAutoNickChange())
                .setAutoReconnectDelay(connectionModel.getReconnectDelay())
                .setAutoReconnectAttempts(connectionModel.getReconnectAttempts())
                .setAutoReconnect(connectionModel.isAutoReconnect())
                .addAutoJoinChannels(Arrays.asList(connectionModel.getChannels()))
                .addServer(connectionModel.getHost(), connectionModel.getPort())
                .addListener(new IRCEventListener(connectionModel, botIdCounter++))
                .buildConfiguration();
        multiBotManager.addBot(configuration);
    }

    private IRCConnectionModel[] loadConnectionModels() throws IRCServiceException {
        String currentPath = Globals.PATH_CFG_PROTOCOL + "irc/servers";

        File dir = new File(currentPath);
        if (!dir.isDirectory())
            throw new IRCServiceException("Could not load any configurations! The directory " + currentPath + " does not exist");

        File[] dirFiles = dir.listFiles();
        if (dirFiles.length == 0)
            throw new IRCServiceException("Could not load any configurations! The directory " + currentPath + " is empty!");

        IRCConnectionModel[] models = new IRCConnectionModel[dirFiles.length];
        Gson gson = new Gson();
        for (int i = 0; i < dirFiles.length; i++) {
            String json = null;
            try {
                json = FileUtils.readFileToString(dirFiles[i].getAbsoluteFile(), "UTF8");
            } catch (IOException e) {
                throw new IRCServiceException("Failed to read configuration file: " + dirFiles[i].getAbsolutePath() + "!");
            }
            models[i] = gson.fromJson(json, IRCConnectionModel.class);
        }
        return models;
    }

    public void startBots() {
        multiBotManager.start();
    }
}

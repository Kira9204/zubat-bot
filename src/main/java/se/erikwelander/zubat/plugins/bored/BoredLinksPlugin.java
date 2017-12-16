package se.erikwelander.zubat.plugins.bored;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import se.erikwelander.zubat.globals.Globals;
import se.erikwelander.zubat.libs.ReggexLib;
import se.erikwelander.zubat.plugins.bored.models.BoredLinksPluginConfig;
import se.erikwelander.zubat.plugins.bored.models.DisabledForModel;
import se.erikwelander.zubat.plugins.exceptions.PluginException;
import se.erikwelander.zubat.plugins.interfaces.PluginInterface;
import se.erikwelander.zubat.plugins.models.MessageEventModel;
import se.erikwelander.zubat.plugins.webtitle.WebTitlePlugin;
import se.erikwelander.zubat.repositories.sql.LinksRepository;
import se.erikwelander.zubat.repositories.sql.exceptions.LinksRepositoryException;
import se.erikwelander.zubat.repositories.sql.models.LinkModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static se.erikwelander.zubat.globals.Globals.TRIGGER;

public class BoredLinksPlugin implements PluginInterface {

    private static final String REGGEX_BORED_ONE = "^" + TRIGGER + "bored";
    private static final String REGGEX_BORED_MANY = "^" + TRIGGER + "bored ([0-9]+)";
    private static final String REGGEX_BORED_REMOVE_LAST = "^" + TRIGGER + "bored remove last";
    private static final String REGGEX_BORED_REMOVE_INDEX = "^" + TRIGGER + "bored remove ([0-9]+)";
    private static final String REGGEX_BORED_HELP = "^" + TRIGGER + "bored help";
    private static final String REGGEX_CONTAINS_URL = "http://|https://";
    private static final String REGGEX_IS_VALID_URL = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
    private static BoredLinksPluginConfig config;
    private WebTitlePlugin webTitlePlugin;
    LinksRepository repository;

    public BoredLinksPlugin() throws PluginException {
        this.config = loadModels();
        webTitlePlugin = new WebTitlePlugin();
        try {
            repository = new LinksRepository();
        } catch (LinksRepositoryException e) {
            throw new PluginException("Could not construct repository! Cause: "+e.getMessage(), e);
        }
    }


    private BoredLinksPluginConfig loadModels() throws PluginException {
        String filePath = Globals.PATH_CFG_PLUGIN + "BoredLinks.json";
        File file = new File(filePath);
        if (!file.isFile()) {
            throw new PluginException("Could not load any configuration! The file " + filePath + " does not exist!");
        }

        Gson gson = new Gson();
        String json = "";
        System.out.println("Loading file: "+file.getAbsoluteFile());
        try {
            json = FileUtils.readFileToString(file.getAbsoluteFile(), "UTF8");
        } catch (IOException ex) {
            throw new PluginException("Failed to read configuration file: " + file.getAbsolutePath() + "!", ex);
        }

        BoredLinksPluginConfig model = gson.fromJson(json, BoredLinksPluginConfig.class);
        return model;
    }

    @Override
    public boolean supportsAction(MessageEventModel messageEventModel) {
        String message = messageEventModel.getMessage().toLowerCase();
        if (ReggexLib.match(message, REGGEX_BORED_ONE) ||
                ReggexLib.match(message, REGGEX_BORED_MANY) ||
                ReggexLib.match(message, REGGEX_BORED_REMOVE_LAST) ||
                ReggexLib.match(message, REGGEX_BORED_REMOVE_INDEX) ||
                ReggexLib.match(message, REGGEX_BORED_HELP) ||
                ReggexLib.match(message, REGGEX_CONTAINS_URL)) {
            return true;
        }
        return false;
    }

    @Override
    public List<String> trigger(MessageEventModel messageEventModel) {

        List<String> toSend = new ArrayList<>();
        if (!supportsAction(messageEventModel)) {
            return toSend;
        }

        if (isDisabledFor(messageEventModel)) {
            //toSend.add("Disabled for this channel");
            return toSend;
        }

        String message = messageEventModel.getMessage();
        if (ReggexLib.match(message, REGGEX_CONTAINS_URL)) {
            String parts[] = message.split(" ");
            for (String part : parts) {
                if (ReggexLib.match(part, REGGEX_IS_VALID_URL)) {
                    LinkModel linkModel = new LinkModel(part);
                    try {
                        repository.addLink(linkModel);
                    } catch (LinksRepositoryException ex) {
                        //toSend.add(ex.getMessage());
                    }
                }
            }
        } else if (ReggexLib.match(message, REGGEX_BORED_REMOVE_LAST)) {
            try {
                repository.removeLast();
            } catch (LinksRepositoryException ex) {
                toSend.add("Exception: Failed to modify database");
                return toSend;
            }
        } else if (ReggexLib.match(message, REGGEX_BORED_REMOVE_INDEX)) {
            String mID = ReggexLib.find(message, REGGEX_BORED_REMOVE_INDEX, 1);

            if (mID.isEmpty()) {
                toSend.add("Failed to remove link: empty ID!");
                toSend.add("The correct format is: " + TRIGGER + "bored delete <id>");
                return toSend;
            }

            int linkID = 0;
            try {
                linkID = Integer.parseInt(mID);
            } catch (NumberFormatException ex) {
                toSend.add(mID + " Not a valid number!");
                return toSend;
            }

            try {
                repository.removeLink(linkID);

            } catch (LinksRepositoryException ex) {
                toSend.add("Exception: Failed to modify database");
                return toSend;
            }
        } else if (ReggexLib.match(message, REGGEX_BORED_ONE)) {
            int numberOfLinks = 1;
            if (ReggexLib.match(message, REGGEX_BORED_MANY)) {
                String regRes = ReggexLib.find(message, REGGEX_BORED_MANY, 1);
                if (!regRes.isEmpty()) {
                    numberOfLinks = Integer.parseInt(regRes);
                }
            }

            List<String> links = new ArrayList<>();
            List<LinkModel> models = new ArrayList<>();
            try {
                models = repository.getRandomLinks(numberOfLinks);
            } catch (Exception ex) {
                links.add(ex.getMessage());
            }

            StringBuilder builder = new StringBuilder();
            for (LinkModel model : models) {
                builder.append("[" + model.getId() + "] " + model.getUrl() + " ");
            }
            toSend.add(builder.toString());
            for (LinkModel model : models) {
                toSend.add(webTitlePlugin.getTitle(model.getUrl()));
            }
        } else if (ReggexLib.match(message, REGGEX_BORED_HELP)) {
            toSend.add("Usage: " + TRIGGER + "bored, " + TRIGGER + "bored <numlinks>, " + TRIGGER + "bored remove last, " + TRIGGER + "bored remove <id>");
        }
        return toSend;
    }


    private final boolean isDisabledFor(final MessageEventModel messageEventModel) {

        if (!this.config.isEnabled()) {
            return false;
        }

        for (DisabledForModel model : this.config.getDisabledForModels()) {
            if (model.getServer().equalsIgnoreCase(messageEventModel.getServer()) &&
                    model.getProtocol().equalsIgnoreCase(messageEventModel.getProtocol())) {

                for (String channel : model.getChannels()) {
                    if (channel.equalsIgnoreCase(messageEventModel.getChannel())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

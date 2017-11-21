package se.erikwelander.zubat.plugins.linkshorter;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import se.erikwelander.zubat.globals.Globals;
import se.erikwelander.zubat.libs.ReggexLib;
import se.erikwelander.zubat.plugins.exceptions.PluginException;
import se.erikwelander.zubat.plugins.interfaces.PluginInterface;
import se.erikwelander.zubat.plugins.linkshorter.models.LinkShorterConfigurationModel;
import se.erikwelander.zubat.plugins.models.MessageEventModel;
import se.erikwelander.zubat.repositories.sql.ShortLinksRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LinkShorterPlugin implements PluginInterface {

    private static final String TRIGGER = Globals.TRIGGER + "short (http://|https://)";
    private LinkShorterConfigurationModel config;
    private ShortLinksRepository repository;

    public LinkShorterPlugin() throws PluginException {
        this.config = loadModels();
        repository = new ShortLinksRepository();
    }

    private LinkShorterConfigurationModel loadModels() throws PluginException {
        String filePath = Globals.PATH_CFG_PLUGIN + "ShortLink.json";
        File file = new File(filePath);
        if (!file.isFile()) {
            throw new PluginException("Could not load any configuration! The file " + filePath + " does not exist!");
        }

        Gson gson = new Gson();
        String json = "";
        try {
            json = FileUtils.readFileToString(file.getAbsoluteFile(), "UTF8");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            throw new PluginException("Failed to read configuration file: " + file.getAbsolutePath() + "!", ex);
        }

        LinkShorterConfigurationModel model = gson.fromJson(json, LinkShorterConfigurationModel.class);
        return model;
    }

    public String shortenLink(String URL) throws PluginException {
        if (!ReggexLib.match(URL, Globals.REGGEX_IS_VALID_URL)) {
            throw new PluginException("Failed to shorten URL! Cause: Invalid URL!");
        }

        try {
            int linkID = repository.addLink(URL);
            return config.getBaseURL() + Integer.toString(linkID, config.getToBase());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            throw new PluginException("Failed to shorten URL! Cause: " + ex.getMessage());
        }
    }

    public int getShortenAbove() {
        return config.getShortenAbove();
    }

    @Override
    public boolean supportsAction(MessageEventModel messageEventModel) {
        if (!config.isEnabled() ||
                !ReggexLib.match(messageEventModel.getMessage(), TRIGGER)) {
            return false;
        }
        return true;
    }

    @Override
    public List<String> trigger(MessageEventModel messageEventModel) {
        List<String> toSend = new ArrayList<>();
        if (!supportsAction(messageEventModel)) {
            return toSend;
        }
        try {
            String shortURL = shortenLink(messageEventModel.getMessage().substring(7));
            toSend.add(shortURL);
            return toSend;
        } catch (PluginException ex) {
            return toSend;
        }
    }
}

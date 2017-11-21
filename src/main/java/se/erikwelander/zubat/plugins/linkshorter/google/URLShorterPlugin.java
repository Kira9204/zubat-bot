package se.erikwelander.zubat.plugins.linkshorter.google;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.model.Url;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import se.erikwelander.zubat.globals.Globals;
import se.erikwelander.zubat.libs.ReggexLib;
import se.erikwelander.zubat.plugins.exceptions.PluginException;
import se.erikwelander.zubat.plugins.interfaces.PluginInterface;
import se.erikwelander.zubat.plugins.linkshorter.google.models.URLShorterPluginConfig;
import se.erikwelander.zubat.plugins.models.MessageEventModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class URLShorterPlugin implements PluginInterface {

    private static final String TRIGGER = Globals.TRIGGER + "short (http://|https://)";
    private URLShorterPluginConfig config;

    public URLShorterPlugin() throws PluginException {
        this.config = loadModels();
    }

    private URLShorterPluginConfig loadModels() throws PluginException {
        String filePath = Globals.PATH_CFG_PLUGIN + "UrlShorter.google.old.json";
        File file = new File(filePath);
        if (!file.isFile()) {
            throw new PluginException("Could not load any configuration! The file " + filePath + " does not exist!");
        }

        Gson gson = new Gson();
        String json = "";
        try {
            json = FileUtils.readFileToString(file.getAbsoluteFile(), "UTF8");
        } catch (IOException ex) {
            throw new PluginException("Failed to read configuration file: " + file.getAbsolutePath() + "!", ex);
        }

        URLShorterPluginConfig model = gson.fromJson(json, URLShorterPluginConfig.class);
        return model;
    }

    public String shortenLongURL(String URL) throws PluginException {

        if (!ReggexLib.match(URL, Globals.REGGEX_IS_VALID_URL)) {
            throw new PluginException("Failed to shorten URL! Cause: Invalid URL!");
        }

        Urlshortener urlshortener = new Urlshortener.Builder
                (
                        new NetHttpTransport(), new JacksonFactory(),
                        request -> {
                        }
                ).setApplicationName(this.config.getAppName()).build();

        Url url = new Url();
        url.setLongUrl(URL);

        try {
            url = urlshortener.url().insert(url).setKey(this.config.getAppKey()).execute();
        } catch (Exception ex) {
            throw new PluginException("Failed to shorten URL! Cause: " + ex.getMessage());
        }

        return url.getId();
    }

    public int getShortenAbove() {
        return this.config.getShortenAbove();
    }


    @Override
    public boolean supportsAction(MessageEventModel messageEventModel) {
        if (!this.config.isEnabled() ||
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
            String shortURL = shortenLongURL(messageEventModel.getMessage().substring(7));
            toSend.add(shortURL);
            return toSend;
        } catch (PluginException ex) {
            return toSend;
        }
    }
}

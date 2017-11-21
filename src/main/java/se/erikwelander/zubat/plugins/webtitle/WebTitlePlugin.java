package se.erikwelander.zubat.plugins.webtitle;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import se.erikwelander.zubat.globals.Globals;
import se.erikwelander.zubat.libs.ReggexLib;
import se.erikwelander.zubat.plugins.exceptions.PluginException;
import se.erikwelander.zubat.plugins.interfaces.PluginInterface;
import se.erikwelander.zubat.plugins.linkshorter.LinkShorterPlugin;
import se.erikwelander.zubat.plugins.models.MessageEventModel;
import se.erikwelander.zubat.plugins.webtitle.models.DisabledForModel;
import se.erikwelander.zubat.plugins.webtitle.models.WebTitleConfigModel;
import se.erikwelander.zubat.plugins.webtitle.services.steam.SteamPlugin;
import se.erikwelander.zubat.plugins.webtitle.services.svtplay.SVTPlayPlugin;
import se.erikwelander.zubat.plugins.webtitle.services.twitch.TwitchPlugin;
import se.erikwelander.zubat.plugins.webtitle.services.vimeo.VimeoPlugin;
import se.erikwelander.zubat.plugins.webtitle.services.webprices.WebPricesPlugin;
import se.erikwelander.zubat.plugins.webtitle.services.youtube.YoutubePlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static se.erikwelander.zubat.globals.Globals.REGGEX_IS_VALID_URL;

public class WebTitlePlugin implements PluginInterface {

    public static final String REGGEX_CONTAINS_URL = "http://|https://";
    public static int maxLength;
    private WebTitleConfigModel webTitleConfigModel;
    private List<PluginInterface> childPlugins;
    private WebService webService;
    private LinkShorterPlugin linkShorterPlugin;

    public WebTitlePlugin() throws PluginException {
        try {
            webTitleConfigModel = loadModels();
            webService = new WebService(webTitleConfigModel.getUserAgent());
        } catch (Exception ex) {
            throw new PluginException(this.getClass().getName() + ": Failed to initialize plugin. Cause: " + ex.getMessage(), ex);
        }

        try {
            linkShorterPlugin = new LinkShorterPlugin();
        } catch (Exception ex) {
            throw new PluginException(this.getClass().getName() + ": Failed to initialize linkshorter component. Cause: " + ex.getMessage(), ex);
        }
        maxLength = webTitleConfigModel.getMaxLength();
        childPlugins = loadChildPlugins();
    }

    private WebTitleConfigModel loadModels() throws PluginException {
        String filePath = Globals.PATH_CFG_PLUGIN + "WebTitle.json";
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

        WebTitleConfigModel model = gson.fromJson(json, WebTitleConfigModel.class);
        return model;
    }

    private List<PluginInterface> loadChildPlugins() {
        List<PluginInterface> children = new ArrayList<>();
        try {
            children.add(new YoutubePlugin());
        } catch (PluginException ex) {
            System.err.println("Failed to load plugin: YoutubePlugin. Cause: " + ex.getMessage());
        }
        try {
            children.add(new SteamPlugin());
        } catch (PluginException ex) {
            System.err.println("Failed to load plugin: SteamPlugin. Cause: " + ex.getMessage());
        }
        try {
            children.add(new TwitchPlugin(webTitleConfigModel.getUserAgent()));
        } catch (PluginException ex) {
            System.err.println("Failed to load plugin: TwitchPlugin. Cause: " + ex.getMessage());
        }

        children.add(new VimeoPlugin(webTitleConfigModel.getUserAgent()));
        children.add(new SVTPlayPlugin(webTitleConfigModel.getUserAgent()));
        children.add(new WebPricesPlugin(webTitleConfigModel.getUserAgent()));

        return children;
    }

    @Override
    public boolean supportsAction(final MessageEventModel messageEventModel) {
        return ReggexLib.match(messageEventModel.getMessage(), REGGEX_CONTAINS_URL);
    }

    @Override
    public List<String> trigger(final MessageEventModel messageEventModel) {

        List<String> toSend = new ArrayList<>();
        if (isDisabledFor(messageEventModel)) {
            return toSend;
        }
        if (!supportsAction(messageEventModel)) {
            return toSend;
        }

        boolean childTriggered = false;

        for (PluginInterface plugin : childPlugins) {

            if (plugin.supportsAction(messageEventModel)) {
                List<String> toAdd = plugin.trigger(messageEventModel);

                if (ReggexLib.match(messageEventModel.getMessage(), REGGEX_IS_VALID_URL)
                        && messageEventModel.getMessage().length() > linkShorterPlugin.getShortenAbove()) {
                    try {
                        String shorten = linkShorterPlugin.shortenLink(ReggexLib.find(messageEventModel.getMessage(), REGGEX_IS_VALID_URL, 0));
                        String replaceString = "(" + shorten + ") " + toAdd.get(0);
                        toAdd.set(0, replaceString);
                    } catch (PluginException ex) {
                    }
                }
                toSend.addAll(toAdd);
                childTriggered = true;
                break;
            }
        }

        if (childTriggered) {
            return toSend;
        }

        String parts[] = messageEventModel.getMessage().split(" ");
        for (String part : parts) {
            if (ReggexLib.match(part, REGGEX_IS_VALID_URL)) {
                String title = "";
                try {
                    title = webService.getWebTitle(part);
                } catch (Exception ex) {
                    //toSend.add("Exception at " + this.getClass().getName() + ". Cause: Failed to download document: " + ex.getMessage());
                }

                if (title.length() > maxLength) {
                    title = title.substring(0, maxLength - 3);
                    title += "...";
                }
                if (!title.isEmpty()) {
                    if (part.length() > linkShorterPlugin.getShortenAbove()) {
                        try {
                            String shorten = linkShorterPlugin.shortenLink(ReggexLib.find(part, REGGEX_IS_VALID_URL, 0));
                            toSend.add("(" + shorten + ") Title: " + title);
                        } catch (PluginException ex) {
                        }
                    } else {
                        toSend.add("Title: " + title);
                    }
                }
            }
        }

        /*
        if(toSend.isEmpty()) {
            for (String part : parts) {
                if (ReggexLib.match(part, REGGEX_IS_VALID_URL)) {
                    try {
                        String shorten = linkShorterPlugin.shortenLink(part);
                        toSend.add("Short: "+shorten);
                    } catch (PluginException ex) {
                    }
                    break;
                }
            }
        }
        */
        return toSend;
    }

    private final boolean isDisabledFor(final MessageEventModel messageEventModel) {

        if (!webTitleConfigModel.isEnabled()) {
            return false;
        }

        for (DisabledForModel model : webTitleConfigModel.getDisabledForModels()) {
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

    public String getTitle(String url) {
        MessageEventModel messageEventModel = new MessageEventModel(
                Globals.PROTOCOL_IRC,
                "",
                "",
                "",
                "",
                url
        );
        try {
            List<String> titles = trigger(messageEventModel);
            if (titles.size() > 0) {
                return titles.get(0);
            }
            return "";
        } catch (Exception ex) {
            return "";
        }
    }
}

package se.erikwelander.zubat.plugins.webtitle.services.twitch;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import se.erikwelander.zubat.globals.Globals;
import se.erikwelander.zubat.plugins.exceptions.PluginException;
import se.erikwelander.zubat.plugins.interfaces.PluginInterface;
import se.erikwelander.zubat.plugins.models.MessageEventModel;
import se.erikwelander.zubat.plugins.webtitle.WebService;
import se.erikwelander.zubat.plugins.webtitle.exceptions.WebServiceException;
import se.erikwelander.zubat.plugins.webtitle.services.twitch.models.TwitchConfigModel;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwitchPlugin implements PluginInterface {

    private static TwitchConfigModel configModel;
    private static Pattern urlPattern;
    private WebService webService;

    public TwitchPlugin() throws PluginException {
        configModel = loadModels();
        urlPattern = createURLPattern();
        webService = new WebService();
    }

    public TwitchPlugin(String userAgent) throws PluginException {
        configModel = loadModels();
        urlPattern = createURLPattern();
        webService = new WebService(userAgent);
    }

    private final Pattern createURLPattern() {
        return Pattern.compile(
                "https://www.twitch.tv/(\\w+)$",
                Pattern.CASE_INSENSITIVE);
    }

    private TwitchConfigModel loadModels() throws PluginException {
        String filePath = Globals.PATH_CFG_PLUGIN_CHILD + "twitch.json";
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

        TwitchConfigModel model = gson.fromJson(json, TwitchConfigModel.class);

        return model;
    }

    private final String getChannelName(final String URL) {
        Matcher matcher = urlPattern.matcher(URL);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    public final TwitchStreamInfoModel getStreamInfoModel(final String URL) throws PluginException {
        final String channelName = getChannelName(URL);
        String jsonString = "";
        try {
            jsonString = webService.getWebString("https://api.twitch.tv/kraken/channels/" + channelName + "?client_id=" + configModel.getClientID());
        } catch (WebServiceException ex) {
            throw new PluginException(this.getClass().getName() + ": getStreamInfoModel(): Failed to download document", ex);
        }
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(jsonString).getAsJsonObject();

        TwitchStreamInfoModel model = new TwitchStreamInfoModel(jsonObject);

        return model;
    }


    private String formatVideoInfo(final TwitchStreamInfoModel tv) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Title: ");
        stringBuilder.append(tv.getStatus());
        stringBuilder.append(". Channel: ");
        stringBuilder.append(tv.getStreamer());
        stringBuilder.append(". Game: ");
        stringBuilder.append(tv.getGame());
        stringBuilder.append(". Views: ");
        stringBuilder.append(tv.getViews());
        stringBuilder.append(". Followers: ");
        stringBuilder.append(tv.getFollowers());

        return stringBuilder.toString();
    }


    @Override
    public boolean supportsAction(MessageEventModel messageEventModel) {
        String parts[] = messageEventModel.getMessage().split(" ");
        for (String part : parts) {
            if (!getChannelName(part).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> trigger(MessageEventModel messageEventModel) {
        String parts[] = messageEventModel.getMessage().split(" ");
        List<String> lines = new ArrayList<>();
        for (String part : parts) {
            if (!getChannelName(part).isEmpty()) {
                TwitchStreamInfoModel video;
                try {
                    video = getStreamInfoModel(part);
                } catch (PluginException ex) {
                    //lines.add(ex.getMessage());
                    continue;
                }

                lines.add(formatVideoInfo(video));
            }
        }
        return lines;
    }

    private class TwitchStreamInfoModel {
        private String status;
        private String game;
        private String streamer;
        private String views;
        private String followers;

        public TwitchStreamInfoModel(final JsonObject jsonObject) {
            this.status = jsonObject.get("status").getAsString();
            if (!jsonObject.get("game").isJsonNull()) {
                this.game = jsonObject.get("game").getAsString();
            } else {
                this.game = "Event";
            }
            this.streamer = jsonObject.get("display_name").getAsString();

            int tempViews = jsonObject.get("views").getAsInt();
            this.views = NumberFormat.getInstance(Locale.forLanguageTag("SE")).format(tempViews);

            int tempFollowers = jsonObject.get("followers").getAsInt();
            this.followers = NumberFormat.getInstance(Locale.forLanguageTag("SE")).format(tempFollowers);
        }

        public String getStatus() {
            return status;
        }

        public String getGame() {
            return game;
        }

        public String getStreamer() {
            return streamer;
        }

        public String getViews() {
            return views;
        }

        public String getFollowers() {
            return followers;
        }

    }
}

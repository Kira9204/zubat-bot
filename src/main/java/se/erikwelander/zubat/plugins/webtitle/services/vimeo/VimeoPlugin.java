package se.erikwelander.zubat.plugins.webtitle.services.vimeo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import se.erikwelander.zubat.plugins.exceptions.PluginException;
import se.erikwelander.zubat.plugins.interfaces.PluginInterface;
import se.erikwelander.zubat.plugins.models.MessageEventModel;
import se.erikwelander.zubat.plugins.webtitle.WebService;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VimeoPlugin implements PluginInterface {

    private static Pattern urlPattern;
    private WebService webService;

    public VimeoPlugin() {
        urlPattern = createURLPattern();
        webService = new WebService();
    }

    public VimeoPlugin(String userAgent) {
        urlPattern = createURLPattern();
        webService = new WebService(userAgent);
    }

    private final Pattern createURLPattern() {
        return Pattern.compile(
                "https://vimeo.com/(\\d+)",
                Pattern.CASE_INSENSITIVE);
    }

    private final String getVideoID(final String URL) {
        Matcher matcher = urlPattern.matcher(URL);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    private VimeoVideoInfoModel getVimeoInfoModel(String URL) throws PluginException {
        final String videoID = getVideoID(URL);
        String jsonString = "";
        try {
            jsonString = webService.getWebString("http://vimeo.com/api/v2/video/" + videoID + ".json");
            JsonParser jsonParser = new JsonParser();
            JsonArray jsonArray = jsonParser.parse(jsonString).getAsJsonArray();
            JsonElement jsonElement = jsonArray.get(0);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            VimeoVideoInfoModel model = new VimeoVideoInfoModel(jsonObject);
            return model;
        } catch (Exception ex) {
            throw new PluginException(this.getClass().getName() + ": getVimeoInfoModel: Failed to parse video information.", ex);
        }
    }

    private String formatVideoInfo(final VimeoVideoInfoModel vvi) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Title: ");
        stringBuilder.append(vvi.getTitle());
        stringBuilder.append(". Channel: ");
        stringBuilder.append(vvi.getChannel());
        stringBuilder.append(". Duration: ");
        stringBuilder.append(vvi.getDuration() + ".");
        if (vvi.isHasStats()) {
            stringBuilder.append(" Views: ");
            stringBuilder.append(vvi.getViews());
            stringBuilder.append(". Likes: ");
            stringBuilder.append(vvi.getLikes());
        }

        return stringBuilder.toString();
    }

    @Override
    public boolean supportsAction(MessageEventModel messageEventModel) {
        String parts[] = messageEventModel.getMessage().split(" ");
        for (String part : parts) {
            Matcher matcher = urlPattern.matcher(part);
            if (matcher.find()) {
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
            if (!getVideoID(part).isEmpty()) {
                VimeoVideoInfoModel video;
                try {
                    video = getVimeoInfoModel(part);
                } catch (PluginException ex) {
                    //lines.add(ex.getMessage());
                    continue;
                }

                lines.add(formatVideoInfo(video));
            }
        }
        return lines;
    }

    private class VimeoVideoInfoModel {
        private String title;
        private String channel;
        private String views;
        private String duration;
        private String likes;
        private boolean hasStats;

        public VimeoVideoInfoModel(final JsonObject jsonObject) {
            formatAndStore(jsonObject);
        }

        private void formatAndStore(final JsonObject jsonObject) {

            this.title = jsonObject.get("title").getAsString();
            this.channel = jsonObject.get("user_name").getAsString();
            long numPlays = 0;
            this.hasStats = false;
            if (jsonObject.has("stats_number_of_plays")) {
                numPlays = jsonObject.get("stats_number_of_plays").getAsLong();
                hasStats = true;
            }
            int duration = jsonObject.get("duration").getAsInt();

            int hours = (duration / 3600) % 24;
            int minutes = (duration / 60) % 60;
            int seconds = duration % 60;

            this.duration = "";
            if (hours > 0) {
                this.duration += "" + hours + "h ";
            }
            if (minutes > 0) {
                this.duration += "" + minutes + "m ";
            }
            if (seconds > 0) {
                this.duration += "" + seconds + "s";
            }

            this.views = NumberFormat.getInstance(Locale.forLanguageTag("SE")).format(numPlays);

            if (hasStats) {
                int likes = jsonObject.get("stats_number_of_likes").getAsInt();
                this.likes = NumberFormat.getInstance(Locale.forLanguageTag("SE")).format(likes);
            } else {
                this.likes = "0";
            }
        }

        public String getTitle() {
            return title;
        }

        public String getChannel() {
            return channel;
        }

        public String getViews() {
            return views;
        }

        public String getDuration() {
            return duration;
        }

        public String getLikes() {
            return likes;
        }

        public boolean isHasStats() {
            return hasStats;
        }
    }

}

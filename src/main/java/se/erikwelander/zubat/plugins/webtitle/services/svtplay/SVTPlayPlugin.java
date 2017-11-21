package se.erikwelander.zubat.plugins.webtitle.services.svtplay;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import se.erikwelander.zubat.plugins.exceptions.PluginException;
import se.erikwelander.zubat.plugins.interfaces.PluginInterface;
import se.erikwelander.zubat.plugins.models.MessageEventModel;
import se.erikwelander.zubat.plugins.webtitle.WebService;
import se.erikwelander.zubat.plugins.webtitle.exceptions.WebServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SVTPlayPlugin implements PluginInterface {

    private static Pattern urlPattern;
    private WebService webService;

    public SVTPlayPlugin() {
        urlPattern = createURLPattern();
        webService = new WebService();
    }

    public SVTPlayPlugin(String userAgent) {
        urlPattern = createURLPattern();
        webService = new WebService(userAgent);
    }

    private final Pattern createURLPattern() {
        return Pattern.compile(
                "https://www.svtplay.se/video/(\\d+)",
                Pattern.CASE_INSENSITIVE);
    }

    private final String getVideoID(String URL) {
        Document pageDocument = webService.getWebDocument(URL);
        Elements videoIDElements = pageDocument.select("video[data-video-id]");
        if (videoIDElements.size() > 0) {
            String videoID = videoIDElements.get(0).attr("data-video-id");
            if (videoID.length() > 0) {
                return videoID;
            }
        }
        return "";
    }

    public SVTPlayVideoInfoModel getSVTVideoInfoModel(final String URL) throws PluginException {
        final String videoID = getVideoID(URL);
        String jsonString = "";
        try {
            jsonString = webService.getWebString("http://www.svt.se/videoplayer-api/video/" + videoID);
        } catch (WebServiceException ex) {
            throw new PluginException(this.getClass().getName() + ": getSVTVideoInfoModel: Failed to retrieve video information.", ex);
        }

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(jsonString).getAsJsonObject();

        SVTPlayVideoInfoModel model = new SVTPlayVideoInfoModel(jsonObject);
        return model;
    }

    private String formatVideoInfo(SVTPlayVideoInfoModel svtv) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Show: ");
        stringBuilder.append(svtv.getShowTitle());
        stringBuilder.append(". Episode: ");
        stringBuilder.append(svtv.getEpisodeTitle());
        stringBuilder.append(". Duration: ");
        stringBuilder.append(svtv.getDuration() + ".");

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
            Matcher matcher = urlPattern.matcher(part);
            if (matcher.find()) {
                SVTPlayVideoInfoModel video;
                try {
                    video = getSVTVideoInfoModel(part);
                } catch (PluginException ex) {
                    //lines.add(ex.getMessage());
                    continue;
                }

                lines.add(formatVideoInfo(video));
            }
        }
        return lines;
    }


    private class SVTPlayVideoInfoModel {
        private String showTitle;
        private String episodeTitle;
        private String duration;

        public SVTPlayVideoInfoModel(JsonObject jsonObject) {
            formatAndStore(jsonObject);
        }

        private void formatAndStore(JsonObject jsonObject) {
            this.showTitle = jsonObject.get("programTitle").getAsString();
            this.episodeTitle = jsonObject.get("episodeTitle").getAsString();
            int showDuration = jsonObject.get("contentDuration").getAsInt();

            int hours = (showDuration / 3600) % 24;
            int minutes = (showDuration / 60) % 60;
            int seconds = showDuration % 60;

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
        }

        public String getShowTitle() {
            return showTitle;
        }

        public String getEpisodeTitle() {
            return episodeTitle;
        }

        public String getDuration() {
            return duration;
        }
    }
}

package se.erikwelander.zubat.plugins.webtitle.services.youtube;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import se.erikwelander.zubat.globals.Globals;
import se.erikwelander.zubat.plugins.exceptions.PluginException;
import se.erikwelander.zubat.plugins.interfaces.PluginInterface;
import se.erikwelander.zubat.plugins.models.MessageEventModel;
import se.erikwelander.zubat.plugins.webtitle.services.youtube.models.YoutubeConfigModel;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubePlugin implements PluginInterface {

    private static Pattern urlPattern;
    private YoutubeConfigModel configModel;

    public YoutubePlugin() throws PluginException {
        configModel = loadModels();
        urlPattern = createURLPattern();
    }

    private YoutubeConfigModel loadModels() throws PluginException {
        String filePath = Globals.PATH_CFG_PLUGIN_CHILD + "youtube.json";
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

        YoutubeConfigModel model = gson.fromJson(json, YoutubeConfigModel.class);
        return model;
    }


    private final Pattern createURLPattern() {
        return Pattern.compile(
                "(?:youtube(?:-nocookie)?\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})",
                Pattern.CASE_INSENSITIVE);
    }

    private String getVideoID(final String URL) {
        Matcher matcher = urlPattern.matcher(URL);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    private final YouTube buildNewYouTubeService() {
        return new YouTube.Builder
                (
                        new NetHttpTransport(), new JacksonFactory(),
                        request -> {
                        }
                ).setApplicationName(configModel.getAppName()).build();
    }


    private Video getYoutubeVideoInfoModel(String URL) throws PluginException {
        final String videoID = getVideoID(URL);
        YouTube youTube = buildNewYouTubeService();
        YouTube.Videos.List videoRequest = null;

        try {
            videoRequest = youTube.videos().list("snippet,statistics,contentDetails");
        } catch (IOException ex) {
            throw new PluginException(this.getClass().getName() + ": getYoutubeVideoInfoModel(): Failed to initialize videos list.", ex);
        }

        videoRequest.setId(videoID);
        videoRequest.setKey(configModel.getAppKey());

        VideoListResponse videoResponseList;
        try {
            videoResponseList = videoRequest.execute();
        } catch (IOException ex) {
            throw new PluginException(this.getClass().getName() + ": getYoutubeVideoInfoModel(): Failed to retrieve videos list.", ex);
        }
        Video video = videoResponseList.getItems().get(0);

        return video;
    }

    private String formatVideoInfo(final Video video) {

        YoutubeVideoFormatter ytf = new YoutubeVideoFormatter(video);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Title: ");
        stringBuilder.append(ytf.getTitle());
        stringBuilder.append(". Channel: ");
        stringBuilder.append(ytf.getChannel());
        stringBuilder.append(". Duration: ");
        stringBuilder.append(ytf.getDuration());
        stringBuilder.append(". Views: ");
        stringBuilder.append(ytf.getViewCount());
        if(!ytf.getLikeRatio().isEmpty()) {
            stringBuilder.append(". Rating: ");
            stringBuilder.append(ytf.getLikeRatio());
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
                Video video;
                try {
                    video = getYoutubeVideoInfoModel(part);
                } catch (PluginException ex) {
                    //lines.add(ex.getMessage());
                    continue;
                }

                lines.add(formatVideoInfo(video));
            }
        }
        return lines;
    }


    private class YoutubeVideoFormatter {

        private final String FORMAT_LANGUAGE = "SE";
        private String title;
        private String channel;
        private String viewCount;
        private String likes;
        private String likeRatio;
        private String duration;

        public YoutubeVideoFormatter(final Video video) {
            formatAndStore(video);
        }

        private void formatAndStore(final Video video) {
            title = video.getSnippet().getTitle();
            channel = video.getSnippet().getChannelTitle();
            viewCount = NumberFormat.getInstance(Locale.forLanguageTag(FORMAT_LANGUAGE)).format(video.getStatistics().getViewCount());

            if(null != video.getStatistics().getLikeCount() && null != video.getStatistics().getDislikeCount()) {
                long positive = Long.parseLong(video.getStatistics().getLikeCount().toString());
                long negative = Long.parseLong(video.getStatistics().getDislikeCount().toString());
                long total = positive + negative;
                float positivePercent = (float) (positive * 100) / total;
                likeRatio = "" + positivePercent;
                int decimalPoint = likeRatio.indexOf('.');
                if (decimalPoint > -1) {
                    likeRatio = likeRatio.substring(0, decimalPoint);
                    likeRatio += "%";
                }
                likes = NumberFormat.getInstance(Locale.forLanguageTag(FORMAT_LANGUAGE)).format(positive);
            } else {
                likeRatio = "";
            }

            duration = video.getContentDetails().getDuration();
            if (duration.equals("PT0S")) {
                duration = "LIVE";
            } else {
                duration = duration.substring(2);
                int index = duration.indexOf("H");
                if (index > -1) {
                    duration = duration.substring(0, index + 1) + " " + duration.substring(index + 1);
                }
                index = duration.indexOf("M");
                if (index > -1) {
                    duration = duration.substring(0, index + 1) + " " + duration.substring(index + 1);
                }
                duration = duration.toLowerCase();
            }
        }

        public String getTitle() {
            return title;
        }

        public String getChannel() {
            return channel;
        }

        public String getViewCount() {
            return viewCount;
        }

        public String getLikes() {
            return likes;
        }

        public String getLikeRatio() {
            return likeRatio;
        }

        public String getDuration() {
            return duration;
        }
    }
}

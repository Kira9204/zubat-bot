package se.erikwelander.zubat.plugins.webtitle.services.steam;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import se.erikwelander.zubat.globals.Globals;
import se.erikwelander.zubat.plugins.exceptions.PluginException;
import se.erikwelander.zubat.plugins.interfaces.PluginInterface;
import se.erikwelander.zubat.plugins.models.MessageEventModel;
import se.erikwelander.zubat.plugins.webtitle.WebService;
import se.erikwelander.zubat.plugins.webtitle.services.steam.models.SteamConfigModel;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SteamPlugin implements PluginInterface {

    private static Pattern urlPattern;
    private SteamConfigModel steamConfigModel;
    private WebService webService;

    public SteamPlugin() throws PluginException {
        steamConfigModel = loadModels();
        urlPattern = createURLPattern();
        webService = new WebService();
    }

    public SteamPlugin(String userAgent) throws PluginException {
        steamConfigModel = loadModels();
        urlPattern = createURLPattern();
        webService = new WebService(userAgent);
    }


    private SteamConfigModel loadModels() throws PluginException {
        String filePath = Globals.PATH_CFG_PLUGIN_CHILD + "steam.json";
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

        SteamConfigModel model = gson.fromJson(json, SteamConfigModel.class);
        return model;
    }

    private final Pattern createURLPattern() {
        return Pattern.compile(
                "http://store.steampowered.com/app/(\\d+)",
                Pattern.CASE_INSENSITIVE);
    }

    private String getAppID(final String URL) {
        Matcher matcher = urlPattern.matcher(URL);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    public final SteamGameInfoModel getSteamGameInfo(final String URL) throws PluginException {
        final String gameID = getAppID(URL);
        try {
            String jsonString = webService.getWebString("http://store.steampowered.com/api/appdetails?appids=" + gameID + "&cc=" + steamConfigModel.getCountry());

            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(jsonString);
            JsonObject rootObject = jsonElement.getAsJsonObject();
            JsonObject appObject = rootObject.getAsJsonObject("" + gameID);

            final SteamGameInfoModel steamGameInfoModel = new SteamGameInfoModel(appObject);
            return steamGameInfoModel;
        } catch (Exception ex) {
            throw new PluginException(this.getClass().getName() + ": getSteamGameInfo: Failed to parse game info.", ex);
        }
    }

    private String formatSteamInfo(final SteamGameInfoModel sgi) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Title: ");
        stringBuilder.append(sgi.getName());
        stringBuilder.append(". Price: ");
        stringBuilder.append(sgi.getPrice());
        if (sgi.getDiscount() > 0) {
            stringBuilder.append(" (" + sgi.getDiscount() + "% off!)");
        }
        stringBuilder.append(". Recommendations: ");
        stringBuilder.append(sgi.getRecommendations() + ".");

        return stringBuilder.toString();
    }

    @Override
    public boolean supportsAction(MessageEventModel messageEventModel) {
        String parts[] = messageEventModel.getMessage().split(" ");
        for (String part : parts) {
            if (!getAppID(part).isEmpty()) {
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
            if (!getAppID(part).isEmpty()) {
                SteamGameInfoModel model;
                try {
                    model = getSteamGameInfo(part);
                } catch (PluginException ex) {
                    //lines.add(ex.getMessage());
                    continue;
                }

                lines.add(formatSteamInfo(model));
            }
        }
        return lines;
    }


    private class SteamGameInfoModel {
        private String name;
        private String price;
        private String recommendations = "0";
        private int discount;

        public SteamGameInfoModel(final JsonObject jsonObject) {
            formatAndStoreInfo(jsonObject);
        }

        private void formatAndStoreInfo(final JsonObject jsonObject) {
            JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
            name = dataObject.get("name").getAsString();

            JsonObject priceObject = dataObject.get("price_overview").getAsJsonObject();
            price = priceObject.get("final").getAsString();
            discount = priceObject.get("discount_percent").getAsInt();
            String currency = priceObject.get("currency").getAsString();

            JsonObject recommendationsObject = dataObject.getAsJsonObject("recommendations");
            if (null != recommendationsObject) {
                int recommendations = recommendationsObject.get("total").getAsInt();
                this.recommendations = NumberFormat.getInstance(Locale.forLanguageTag("SE")).format(recommendations);
            }

            if (price.length() == 5) {
                String tmpPrice = price.substring(0, 3);
                tmpPrice += ",";
                tmpPrice += price.substring(3);
                price = tmpPrice;
            } else if (price.length() == 4) {
                String tmpPrice = price.substring(0, 2);
                tmpPrice += ",";
                tmpPrice += price.substring(2);
                price = tmpPrice;
            } else if (price.length() == 3) {
                String tmpPrice = price.substring(0, 1);
                tmpPrice += ",";
                tmpPrice += price.substring(1);
                price = tmpPrice;
            } else if (price.length() == 2) {
                price = "0," + price;
            }
            price += " " + currency;
        }

        public String getName() {
            return name;
        }

        public String getPrice() {
            return price;
        }

        public String getRecommendations() {
            return recommendations;
        }

        public int getDiscount() {
            return discount;
        }
    }
}

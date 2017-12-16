package se.erikwelander.zubat.plugins.webtitle.services.webprices;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import se.erikwelander.zubat.plugins.exceptions.PluginException;
import se.erikwelander.zubat.plugins.interfaces.PluginInterface;
import se.erikwelander.zubat.plugins.models.MessageEventModel;
import se.erikwelander.zubat.plugins.webtitle.WebService;
import se.erikwelander.zubat.plugins.webtitle.exceptions.WebServiceException;

import java.util.ArrayList;
import java.util.List;

public class WebPricesPlugin implements PluginInterface {

    private static final String[] supportedDomains = {
            "www.webhallen.com",
            "www.inet.se",
            "www.netonnet.se",
            "www.dustin.se",
            "www.dustinhome.se",
            "cdon.se",
            "www.kjell.com",
            "www.clasohlson.com",
            "www.sweclockers.com",
            "www.blocket.se",
            "m.blocket.se",
            "www.biltema.se",
            "www.tradera.com",
            "www.sfbok.se",
            "www.ikea.com",
            "www.prisjakt.nu",
            "www.mathem.se",
            "www.jula.se",
            "www.amazon.co.uk",
            "www.amazon.com",
            "shop.lego.com",
            "www.ebay.co.uk",
            "www.ebay.com"};
    private WebService webService;

    public WebPricesPlugin() {
        webService = new WebService();
    }

    public WebPricesPlugin(final String userAgent) {
        webService = new WebService(userAgent);
    }

    private String createWebPriceString(String domain, String URL) throws PluginException {
        Document pageDocument = null;
        try {
            pageDocument = webService.getWebDocument(URL);
        } catch (WebServiceException ex) {
            throw new PluginException(this.getClass().getName() + ": createWebPriceString: Failed to download document!", ex);
        }

        String title = "Title: " + webService.getWebTitle(pageDocument);
        String price = "";
        if (domain.equals("www.webhallen.com")) {
            Elements priceElements = pageDocument.select("#product_price");
            if (priceElements.size() > 0) {
                price = priceElements.get(0).text() + " kr";
            }
            if (price.length() > 0) {
                return title + ". Price: " + price;
            }
            return title;
        } else if (domain.equals("www.inet.se")) {
            Elements priceElements = pageDocument.select(".active-price");
            if (priceElements.size() > 0) {
                if (priceElements.size() >= 1) {
                    price = priceElements.get(0).text();
                }
            }
            if (price.length() > 0) {
                return title + ". Price: " + price;
            }
            return title;
        } else if (domain.equals("www.netonnet.se")) {
            Elements metaPriceElements = pageDocument.select("span[itemprop=price]");
            if (metaPriceElements.size() > 0) {
                price = metaPriceElements.get(0).text();
                if (price.length() > 0) {
                    return title + ". Price: " + price + " kr";
                }
            }
            return title;
        } else if (domain.equals("www.dustin.se") || domain.equals("www.dustinhome.se")) {
            Elements metaPriceElements = pageDocument.select("meta[itemprop=price]");
            if (metaPriceElements.size() > 0) {
                price = metaPriceElements.get(0).attr("content");
                if (price.length() > 0) {
                    return title + ". Price: " + price;
                }
            }
            return title;
        } else if (domain.equals("cdon.se")) {
            Elements metaPriceElements = pageDocument.select("meta[itemprop=price]");
            if (metaPriceElements.size() > 0) {
                price = metaPriceElements.get(0).attr("content");
                if (price.length() > 0) {
                    return title + ". Price: " + price + "kr";
                }
            }
            return title;
        } else if (domain.equals("www.clasohlson.com")) {
            Elements metaPriceElements = pageDocument.select("strong[class=price]");
            if (metaPriceElements.size() > 0) {
                price = metaPriceElements.get(0).text();
                price += "kr";
                return title + ". Price: " + price;
            }
            return title;
        } else if (domain.equals("www.kjell.com")) {
            Elements priceElements = pageDocument.select(".bestPrice");
            if (priceElements.size() > 0) {
                price = priceElements.get(0).text();
            }
            if (price.length() > 0) {
                return title + ". Price: " + price + "kr";
            }
            return title;
        } else if (domain.equals("www.sweclockers.com")) {
            Elements ddElements = pageDocument.select("dd");
            if (ddElements.size() >= 6) {
                price = ddElements.get(4).text();
                String location = ddElements.get(5).child(0).text();
                String published = ddElements.get(6).text();

                String message = title + ". Price: " + price + ". Location: " + location + ". Published: " + published;
                return message;
            } else {
                return title;
            }
        } else if (URL.contains("m.blocket.se")) {
            URL = URL.replace("m.blocket.se", "blocket.se");
            Document pageDocument2 = webService.getWebDocument(URL);
            String title2 = "Title: " + webService.getWebTitle(pageDocument2);

            Elements priceElements = pageDocument2.select("#vi_price");
            if (priceElements.size() > 0) {
                price = priceElements.get(0).text();
                price = price.substring(0, price.length() - 2);
                price += " kr";

                String area = "";
                Elements areaElements = pageDocument2.select(".area_label");
                if (areaElements.size() > 0) {
                    area = areaElements.get(0).text();
                    area = area.substring(1);
                    area = area.substring(0, area.length() - 1);
                }

                String published = "";
                Elements timeElements = pageDocument2.select("#seller-info time");
                if (timeElements.size() > 0) {
                    published = timeElements.get(0).text();
                }

                if (price.length() > 0) {
                    return title2 + ". Price: " + price + ". Area: " + area + ". Published: " + published;
                }
            }
            return title2;
        } else if (domain.equals("www.blocket.se")) {
            Elements priceElements = pageDocument.select("#vi_price");
            if (priceElements.size() > 0) {
                price = priceElements.get(0).text();
                price = price.substring(0, price.length() - 2);
                price += " kr";

                String area = "";
                Elements areaElements = pageDocument.select(".area_label");
                if (areaElements.size() > 0) {
                    area = areaElements.get(0).text();
                    area = area.substring(1);
                    area = area.substring(0, area.length() - 1);
                }

                String published = "";
                Elements timeElements = pageDocument.select("#seller-info time");
                if (timeElements.size() > 0) {
                    published = timeElements.get(0).text();
                }

                if (price.length() > 0) {
                    return title + ". Price: " + price + ". Area: " + area + ". Published: " + published;
                }
            }
            return title;
        } else if (domain.equals("www.biltema.se")) {
            Elements priceElements = pageDocument.select(".pricePart");
            if (priceElements.size() > 0) {
                price = priceElements.get(0).text();
            }
            if (price.length() > 0) {
                return title + ". Price: " + price;
            }
            return title;
        } else if (domain.equals("www.tradera.com")) {
            price = "";
            Elements metaPriceElements = pageDocument.select("span[data-leading-bid-sum]");
            if (metaPriceElements.size() > 0) {
                price = metaPriceElements.get(0).text();
            }
            if (metaPriceElements.size() == 0) {
                metaPriceElements = pageDocument.select("span[data-next-bid]");
                if (metaPriceElements.size() > 0) {
                    price = metaPriceElements.get(0).text();
                }
            }
            if (metaPriceElements.size() == 0) {
                metaPriceElements = pageDocument.select(".view-item-fixed-price");
                if (metaPriceElements.size() > 0) {
                    price = metaPriceElements.get(0).text();
                }
            }
            Elements metaTimeLeftElements = pageDocument.select("span[data-time-left]");
            if (metaTimeLeftElements.size() > 0) {
                String timeLeft = metaTimeLeftElements.get(0).text();
                if (price.length() > 0 && timeLeft.length() > 0) {
                    timeLeft = timeLeft.replace("tim", "hours");
                    timeLeft = timeLeft.replace("min", "minutes");
                    timeLeft = timeLeft.replace("dagar", "days");
                    return title + ". Highest bidding: " + price + " SEK. Auction ends in: " + timeLeft;
                } else if (price.length() > 0) {
                    return title + ". Price: " + price+" SEK";
                }
            } else if (metaPriceElements.size() > 0) {
                return title + ". Price: " + price+" SEK";
            }
            return title;
        } else if (domain.equals("www.sfbok.se")) {
            Elements priceElements = pageDocument.select(".field-item.even");
            if (priceElements.size() > 0) {
                price = priceElements.get(0).text();
            }
            if (price.length() > 0) {
                return title + ". Price: " + price;
            }
            return title;
        } else if (domain.equals("www.ikea.com")) {
            Elements priceElements = pageDocument.select("#price1");
            if (priceElements.size() > 0) {
                price = priceElements.get(0).text();
            }
            if (price.length() > 0) {
                return title + ". Price: " + price;
            }
            return title;
        } else if (domain.equals("www.prisjakt.nu")) {
            Elements priceElements = pageDocument.select(".pris");
            if (priceElements.size() > 0) {
                price = priceElements.get(0).text();
            }
            if (price.length() > 0) {
                price = price.substring(0, price.length() - 2);
                return title + ". Price from: " + price + " SEK";
            }
            return title;
        } else if (domain.equals("www.mathem.se")) {
            Elements priceElements = pageDocument.select("#spnPrice");
            if (priceElements.size() > 0) {
                price = price.replace(":", "");
                price = price.replace("-", "");
                price = priceElements.first().text() + "kr";
            }
            if (price.length() > 0) {
                return title + ". Price: " + price;
            }
            return title;
        } else if (domain.equals("www.jula.se")) {
            Elements metaPriceElements = pageDocument.select("strong[class=main-price]");
            if (metaPriceElements.size() > 0) {
                price = metaPriceElements.get(0).text();
                price = price.replace(",", "");
                price = price.replace("-", "");
                price += "kr";
                return title + ". Price: " + price;
            }
            return title;
        } else if (domain.equals("www.amazon.co.uk") || domain.equals("www.amazon.com")) {
            Elements priceElements = pageDocument.select("#priceblock_ourprice");
            if (priceElements.size() > 0) {
                price = priceElements.get(0).text();
            }
            if (price.length() > 0) {
                return title + ". Price: " + price;
            }
            return title;
        } else if (domain.equals("shop.lego.com")) {
            Elements priceElements = pageDocument.select(".product-price__list-price");
            if (priceElements.size() > 0) {
                price = priceElements.get(0).text();
            }
            if (price.length() > 0) {
                return title + ". Price: " + price;
            }
            return title;
        } else if (domain.equals("www.ebay.co.uk") || domain.equals("www.ebay.com")) {
            Elements metaPriceElements = pageDocument.select("span[itemprop=price]");
            Elements metaCurrencyElements = pageDocument.select("span[itemprop=priceCurrency]");
            if (metaPriceElements.size() > 0) {
                price = metaPriceElements.get(0).attr("content");
                int index = price.indexOf('.');
                if (index > -1) {
                    price = price.substring(0, index);
                }
            }

            String currency = "";
            if (metaCurrencyElements.size() > 0) {
                currency = metaCurrencyElements.get(0).attr("content");
            }

            if (price.length() > 0 && currency.length() > 0) {
                return title + ". Price: " + price + " " + currency;
            } else if (price.length() > 0) {
                return title + ". Price: " + price;
            }
        }
        return "";
    }

    @Override
    public boolean supportsAction(MessageEventModel messageEventModel) {
        String parts[] = messageEventModel.getMessage().split(" ");
        for (String part : parts) {
            String domain = webService.getDomain(part);
            for (String currentDomain : supportedDomains) {
                if (currentDomain.equals(domain)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> trigger(MessageEventModel messageEventModel) {
        String parts[] = messageEventModel.getMessage().split(" ");
        List<String> lines = new ArrayList<>();
        for (String part : parts) {
            String domain = webService.getDomain(part);
            for (String currentDomain : supportedDomains) {
                if (currentDomain.equals(domain)) {
                    try {
                        String data = createWebPriceString(domain, part);
                        lines.add(data);
                    } catch (Exception ex) {
                        lines.add(this.getClass().getName() + ": createWebPriceString: Failed to download document");
                    }
                }
            }
        }
        return lines;
    }
}

package se.erikwelander.zubat.plugins.webtitle;


import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import se.erikwelander.zubat.libs.ReggexLib;
import se.erikwelander.zubat.plugins.webtitle.exceptions.WebServiceException;

import java.net.URI;

import static se.erikwelander.zubat.globals.Globals.REGGEX_IS_VALID_URL;

public class WebService {
    private static final int TIMEOUT = 1000;
    private static String userAgent;

    public WebService() {
        this.userAgent = "";
    }

    public WebService(final String userAgent) {
        this.userAgent = userAgent;
    }

    public final boolean isValidURL(final String URL) {
        return ReggexLib.match(URL, REGGEX_IS_VALID_URL);
    }

    public String getDomain(final String URL) {
        try {
            URI uri = new URI(URL);
            String domain = uri.getHost();

            return domain;
        } catch (Exception ex) {
            return "";
        }
    }

    public Document getWebDocument(final String URL) throws WebServiceException {
        if (!isValidURL(URL)) {
            throw new WebServiceException(this.getClass().getName() + ": getWebDocument(): Invalid URL: " + URL);
        }
        try {
            Document document = Jsoup.connect(URL).userAgent(userAgent).timeout(TIMEOUT).get();
            document.outputSettings().charset(CharEncoding.UTF_8);
            return document;
        } catch (Exception ex) {
            throw new WebServiceException(this.getClass().getName() + ": getWebDocument(): " + ex.getMessage(), ex);
        }
    }

    public String getWebString(final String URL) throws WebServiceException {
        try {
            return Jsoup.connect(URL).userAgent(userAgent).timeout(TIMEOUT).ignoreContentType(true).execute().body();
        } catch (Exception ex) {
            throw new WebServiceException(this.getClass().getName() + ": getWebString(): " + ex.getMessage(), ex);
        }
    }

    public String getWebTitle(final Document documentIn) {
        return webTitle(documentIn);
    }

    public String getWebTitle(final String URL) throws WebServiceException {
        try {
            Document document = null;
            try {
                document = getWebDocument(URL);
            } catch (Exception ex) {
                throw new WebServiceException(this.getClass().getName() + ": getWebTitle(): " + ex.getMessage(), ex);
            }
            return webTitle(document);
        } catch (Exception ex) {
            throw new WebServiceException(this.getClass().getName() + ": getWebTitle(): " + ex.getMessage(), ex);
        }
    }

    private String webTitle(final Document documentIn) {
        Document document = documentIn;
        //String title = document.title();
        String title = document.getElementsByTag("title").get(0).html();
        if (title.isEmpty()) {
            return "";
        }

        title = title.replace("\n", "");
        title = title.replace("\r", "");
        title = StringEscapeUtils.unescapeHtml4(title);

        return title;
    }
}

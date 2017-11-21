package se.erikwelander.zubat.services.tools.RSSParser;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import se.erikwelander.zubat.services.tools.RSSParser.exceptions.RSSParserException;
import se.erikwelander.zubat.services.tools.RSSParser.models.RSSEntryModel;
import se.erikwelander.zubat.services.tools.RSSParser.models.RSSFeedModel;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class RSSParser {
    public RSSParser() {
    }

    public RSSFeedModel parseRSS(final String url) {

        URL feedUrl = null;
        try {
            feedUrl = new URL(url);
        } catch (MalformedURLException ex) {
            throw new RSSParserException("The URL " + url + " is not a valid URL!", ex);
        }

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = null;
        try {
            feed = input.build(new XmlReader(feedUrl));
        } catch (IOException ex) {
            throw new RSSParserException("An IO exception occurred! Cause: " + ex.getMessage(), ex);
        } catch (FeedException ex) {
            throw new RSSParserException("Failed to parse XML! Cause: " + ex.getMessage(), ex);
        }

        final List<SyndEntry> entries = (List<SyndEntry>) feed.getEntries();
        final int numEntries = entries.size();
        RSSEntryModel feedEntries[] = new RSSEntryModel[numEntries];

        for (int i = 0; i < numEntries; i++) {
            SyndEntry entry = entries.get(i);
            feedEntries[i] = new RSSEntryModel(entry.getTitle(), entry.getAuthor(), entry.getUri(), entry.getPublishedDate(), entry.getUpdatedDate());
        }
        return new RSSFeedModel(feed.getTitle(), url, feedEntries);
    }
}

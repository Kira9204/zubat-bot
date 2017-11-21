package se.erikwelander.zubat.services.tools.RSSParser.models;

public class RSSFeedModel {

    private String feedTitle;
    private String feedURL;
    private RSSEntryModel[] rssEntries;

    public RSSFeedModel(final String feedTitle, final String feedURL, final RSSEntryModel[] rssEntries) {
        this.feedTitle = feedTitle;
        this.feedURL = feedURL;
        this.rssEntries = rssEntries;
    }

    public String getFeedTitle() {
        return this.feedTitle;
    }

    public String getFeedURL() {
        return this.feedURL;
    }

    public RSSEntryModel[] getRssEntries() {
        return this.rssEntries;
    }
}

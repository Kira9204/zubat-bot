package se.erikwelander.zubat.services.tools.RSSParser.models;

import java.util.Date;

public class RSSEntryModel {
    private String title;
    private String authur;
    private String url;
    private Date publishedDate;
    private Date modifiedDate;
    //SimpleDateFormat dateFormat =
    //new SimpleDateFormat("EEEEE d MMMMM HH:mm", new Locale("sv", "SE"));

    public RSSEntryModel(final String title,
                         final String authur,
                         final String url,
                         final Date publishedDate,
                         final Date modifiedDate) {
        this.title = title;
        this.authur = authur;
        this.url = url;
        this.publishedDate = publishedDate;
        this.modifiedDate = modifiedDate;
    }

    public String getTitle() {
        return this.title;
    }

    public RSSEntryModel setTitle(final String title) {
        this.title = title;
        return this;
    }

    public String getAuthur() {
        return this.authur;
    }

    public RSSEntryModel setAuthur(final String authur) {
        this.authur = authur;
        return this;
    }

    public String getUrl() {
        return this.url;
    }

    public RSSEntryModel setUrl(final String url) {
        this.url = url;
        return this;
    }

    public Date getPublishedDate() {
        return this.publishedDate;
    }

    public RSSEntryModel setPublishedDate(final Date publishedDate) {
        this.publishedDate = publishedDate;
        return this;
    }

    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    public RSSEntryModel setModifiedDate(final Date modifiedDate) {
        this.modifiedDate = modifiedDate;
        return this;
    }
}

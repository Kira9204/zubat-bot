package se.erikwelander.zubat.repositories.sql.models;

public class LinkModel {

    private int id;
    private String url;

    public LinkModel(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public LinkModel(String url) {
        this.id = 0;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

}

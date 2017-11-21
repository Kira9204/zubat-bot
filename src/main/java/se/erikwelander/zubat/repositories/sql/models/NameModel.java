package se.erikwelander.zubat.repositories.sql.models;

public class NameModel {

    private int id;
    private String name;

    public NameModel(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

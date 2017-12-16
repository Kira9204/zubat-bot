package se.erikwelander.zubat.repositories.sql;

import se.erikwelander.zubat.repositories.sql.exceptions.LinksRepositoryException;
import se.erikwelander.zubat.repositories.sql.models.LinkModel;
import se.erikwelander.zubat.repositories.sql.sqlconnector.SQLConnector;
import se.erikwelander.zubat.repositories.sql.sqlconnector.exceptions.SQLConnectorException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LinksRepository {

    private SQLConnector sqlConnector;

    public LinksRepository() throws LinksRepositoryException {
        try {
            sqlConnector = new SQLConnector();
        } catch (SQLConnectorException ex) {
            throw new LinksRepositoryException(this.getClass().getName() + "Failed to establish database connection! Cause: " + ex.getMessage(), ex);
        }
    }

    public int addLink(LinkModel model) throws LinksRepositoryException {

        try {
            StringBuilder builder = new StringBuilder();
            builder.append("INSERT INTO links_bored (link_date, link_url) ");
            builder.append("VALUES (NOW(), ?)");

            PreparedStatement statement = sqlConnector.prepareStatement(builder.toString());
            statement.setString(1, model.getUrl());

            int newKey = sqlConnector.queryUpdate(statement);

            return newKey;
        } catch (SQLException | SQLConnectorException ex) {
            throw new LinksRepositoryException(this.getClass().getName() + "Failed to insert model! Cause: " + ex.getMessage(), ex);
        }
    }

    public List<LinkModel> getRandomLinks(final int numLinks) throws LinksRepositoryException {

        try {
            String sql = "SELECT link_id, link_url FROM links_bored ORDER BY RAND() LIMIT " + numLinks + ";";
            PreparedStatement statement = sqlConnector.prepareStatement(sql);
            ResultSet result = sqlConnector.queryResult(statement);

            List<LinkModel> models = new ArrayList<>();
            while (result.next()) {
                LinkModel model = new LinkModel(result.getInt("link_id"),
                        result.getString("link_url"));
                models.add(model);
            }

            return models;

        } catch (SQLException | SQLConnectorException ex) {
            throw new LinksRepositoryException(this.getClass().getName() + "Failed to retrieve model! Cause: " + ex.getMessage(), ex);
        }
    }

    public void removeLink(final int ID) throws LinksRepositoryException {

        try {
            String sql = "DELETE FROM links_bored WHERE link_id = ?;";
            PreparedStatement statement = sqlConnector.prepareStatement(sql);
            statement.setInt(1, ID);
            sqlConnector.queryUpdate(statement);
        } catch (SQLException | SQLConnectorException ex) {
            throw new LinksRepositoryException(this.getClass().getName() + "Failed to delete model! Cause: " + ex.getMessage(), ex);
        }
    }

    public void removeLast() throws LinksRepositoryException {

        try {
            String sql = "DELETE FROM links_bored ORDER BY link_id DESC LIMIT 1;";
            PreparedStatement statement = sqlConnector.prepareStatement(sql);
            sqlConnector.queryUpdate(statement);
        } catch (SQLConnectorException ex) {
            throw new LinksRepositoryException(this.getClass().getName() + "Failed to delete model! Cause: " + ex.getMessage(), ex);
        }
    }
}

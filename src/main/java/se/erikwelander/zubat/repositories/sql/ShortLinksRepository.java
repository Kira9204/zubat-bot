package se.erikwelander.zubat.repositories.sql;

import se.erikwelander.zubat.repositories.sql.exceptions.LinksRepositoryException;
import se.erikwelander.zubat.repositories.sql.sqlconnector.SQLConnector;
import se.erikwelander.zubat.repositories.sql.sqlconnector.exceptions.SQLConnectorException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ShortLinksRepository {

    private SQLConnector sqlConnector;

    public ShortLinksRepository() throws LinksRepositoryException {
        try {
            sqlConnector = new SQLConnector();
        } catch (SQLConnectorException ex) {
            throw new LinksRepositoryException(this.getClass().getName() + "Failed to establish database connection! Cause: " + ex.getMessage(), ex);
        }
    }

    public int addLink(final String link) throws LinksRepositoryException {

        try {
            StringBuilder builder = new StringBuilder();
            builder.append("INSERT INTO links_short (link) ");
            builder.append("VALUES (?)");

            PreparedStatement statement = sqlConnector.prepareStatement(builder.toString());
            statement.setString(1, link);

            int insertID = sqlConnector.queryUpdate(statement);
            return insertID;

        } catch (SQLException | SQLConnectorException ex) {
            throw new LinksRepositoryException(this.getClass().getName() + "Failed to insert model! Cause: " + ex.getMessage(), ex);
        }
    }
}

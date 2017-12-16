package se.erikwelander.zubat.repositories.sql;

import se.erikwelander.zubat.repositories.sql.exceptions.NamesRepositoryException;
import se.erikwelander.zubat.repositories.sql.models.NameModel;
import se.erikwelander.zubat.repositories.sql.sqlconnector.SQLConnector;
import se.erikwelander.zubat.repositories.sql.sqlconnector.exceptions.SQLConnectorException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NamesRepository {

    public static final int NAME_POKEMON = 1;
    public static final int NAME_SODA = 2;

    private SQLConnector sqlConnector;

    public NamesRepository() throws NamesRepositoryException {
        try {
            sqlConnector = new SQLConnector();
        } catch (SQLConnectorException ex) {
            throw new NamesRepositoryException(this.getClass().getName() + "Failed to establish database connection! Cause: " + ex.getMessage(), ex);
        }
    }

    public List<NameModel> getRandomNamesOfType(final int nameType, final int numNames) throws NamesRepositoryException {

        try {
            String tableName = "";
            if (nameType == NAME_POKEMON) {
                tableName = "names_pokemon";
            } else if (nameType == NAME_SODA) {
                tableName = "names_soda";
            } else {
                return null;
            }

            String sql = "SELECT * FROM " + tableName + " ORDER BY RAND() LIMIT " + numNames + ";";
            PreparedStatement statement = sqlConnector.prepareStatement(sql);
            ResultSet result = sqlConnector.queryResult(statement);

            List<NameModel> models = new ArrayList<>();
            while (result.next()) {
                NameModel model = new NameModel(result.getInt("name_id"), result.getString("name"));
                models.add(model);
            }

            return models;

        } catch (SQLException | SQLConnectorException ex) {
            throw new NamesRepositoryException(this.getClass().getName() + "Failed to retrieve model! Cause: " + ex.getMessage(), ex);
        }
    }
}

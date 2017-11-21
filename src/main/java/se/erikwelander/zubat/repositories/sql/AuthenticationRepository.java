package se.erikwelander.zubat.repositories.sql;

import se.erikwelander.zubat.plugins.models.MessageEventModel;
import se.erikwelander.zubat.repositories.sql.exceptions.AuthenticationRepositoryException;
import se.erikwelander.zubat.repositories.sql.models.AuthenticationModel;
import se.erikwelander.zubat.repositories.sql.sqlconnector.SQLConnector;
import se.erikwelander.zubat.repositories.sql.sqlconnector.exceptions.SQLConnectorException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationRepository {

    public AuthenticationRepository() {
    }

    public AuthenticationModel getWithMessageEvent(MessageEventModel messageEventModel) throws AuthenticationRepositoryException {

        SQLConnector sqlConnector;
        try {
            sqlConnector = new SQLConnector();
        } catch (SQLConnectorException ex) {
            throw new AuthenticationRepositoryException(this.getClass().getName() + "Failed to establish database connection! Cause: " + ex.getMessage(), ex);
        }

        try {
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT A.auth_id, ");
            builder.append("P.protocol_name AS auth_protocol, ");
            builder.append("S.server_hostname AS auth_server, ");
            builder.append("C.channel_name AS auth_channel, ");
            builder.append("N.nick_name AS auth_nick, ");
            builder.append("H.hostmask AS auth_hostmask ");
            builder.append("FROM zubat.auth AS A ");
            builder.append("JOIN protocols AS P ON P.protocol_id = A.protocol_id ");
            builder.append("JOIN servers AS S ON S.server_id = A.server_id ");
            builder.append("JOIN channels AS C ON C.channel_id = A.channel_id ");
            builder.append("JOIN nicks as N ON N.nick_id = A.nick_id ");
            builder.append("JOIN hostmasks AS H ON H.hostmask_id = A.hostmask_id ");
            builder.append("WHERE P.protocol_name = ? AND ");
            builder.append("S.server_hostname = ? AND ");
            builder.append("C.channel_name = ? AND ");
            builder.append("N.nick_name = ? AND ");
            builder.append("H.hostmask = ? LIMIT 1;");


            PreparedStatement statement = sqlConnector.prepareStatement(builder.toString());
            statement.setString(1, messageEventModel.getProtocol());
            statement.setString(2, messageEventModel.getServer());
            statement.setString(3, messageEventModel.getChannel());
            statement.setString(4, messageEventModel.getUser());
            statement.setString(5, messageEventModel.getHostMask());

            ResultSet result = sqlConnector.queryResult(statement);

            if (!result.next()) {
                return null;
            }


            AuthenticationModel model = new AuthenticationModel(result.getInt("auth_id"),
                    result.getString("auth_protocol"),
                    result.getString("auth_server"),
                    result.getString("auth_channel"),
                    result.getString("auth_nick"),
                    result.getString("auth_hostmask"));

            sqlConnector.disconnect();
            return model;

        } catch (SQLException | SQLConnectorException ex) {
            throw new AuthenticationRepositoryException(this.getClass().getName() + "Failed to retrieve model! Cause: " + ex.getMessage(), ex);
        }
    }
}

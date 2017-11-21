package se.erikwelander.zubat.repositories.sql.sqlconnector;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import se.erikwelander.zubat.globals.Globals;
import se.erikwelander.zubat.repositories.sql.models.SQLConnectorSettings;
import se.erikwelander.zubat.repositories.sql.sqlconnector.exceptions.SQLConnectorException;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLConnector {

    private static SQLConnectorSettings dbInfo;
    private Connection sqlConnection;
    private Statement sqlStatement;

    public SQLConnector() throws SQLConnectorException {
        if (null == dbInfo) {
            dbInfo = loadModels();
        }
        connect();
    }

    @Override
    protected void finalize() throws Throwable{
        try {
            disconnect();
        } catch (SQLConnectorException ex) {
            System.err.println(ex.getMessage());
        } finally {
            super.finalize();
        }
    }

    private SQLConnectorSettings loadModels() throws SQLConnectorException {
        String filePath = Globals.PATH_CFG_DATABASE + "db.json";
        File file = new File(filePath);
        if (!file.isFile()) {
            throw new SQLConnectorException("Could not load any configuration! The file " + filePath + " does not exist!");
        }

        Gson gson = new Gson();
        String json = "";
        try {
            json = FileUtils.readFileToString(file.getAbsoluteFile(), "UTF8");
        } catch (IOException ex) {
            throw new SQLConnectorException("Failed to read configuration file: " + file.getAbsolutePath() + "!", ex);
        }

        SQLConnectorSettings model = gson.fromJson(json, SQLConnectorSettings.class);
        return model;
    }

    private void connect() throws SQLConnectorException {
        try {
            sqlConnection = DriverManager.getConnection("jdbc:mysql://" + dbInfo.getHost() + ":" + dbInfo.getPort() + "/"
                            + dbInfo.getDatabase() + "?characterEncoding=" + dbInfo.getEncoding() +
                            "&useLegacyDatetimeCode=false&serverTimezone=" + dbInfo.getTimeZone(),
                    dbInfo.getUsername(), dbInfo.getPassword());
            sqlStatement = sqlConnection.createStatement();
        } catch (SQLException exception) {
            throw new SQLConnectorException("Could not connect to database: " + exception.getMessage());
        }
    }

    public void disconnect() throws SQLConnectorException {
        try {
            sqlStatement.close();
            sqlConnection.close();
        } catch (final SQLException exception) {
            throw new SQLConnectorException("Could not disconnect from database: " + exception.getMessage());
        }
    }

    public final PreparedStatement prepareStatement(final String sqlQuery) throws SQLConnectorException {
        try {
            final PreparedStatement preparedStatement = sqlConnection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            return preparedStatement;
        } catch (final SQLException exception) {
            throw new SQLConnectorException("Could not prepare SQL statement: " + exception.getMessage());
        }
    }

    public int query(final PreparedStatement preparedStatement) throws SQLConnectorException {
        try {
            return queryUpdate(preparedStatement);
        } catch (SQLConnectorException ex) {
            throw new SQLConnectorException("Error performing queryUpdate: " + ex.getMessage());
        }
    }

    public int queryUpdate(final PreparedStatement preparedStatement) throws SQLConnectorException {
        try {
            int numAffectedRows = preparedStatement.executeUpdate();
            if (numAffectedRows > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
            return 0;
        } catch (final SQLException exception) {
            throw new SQLConnectorException("Error performing queryUpdate: " + exception.getMessage());
        }
    }

    public final ResultSet queryResult(final PreparedStatement preparedStatement) throws SQLConnectorException {
        try {
            final ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet;
        } catch (final SQLException exception) {
            throw new SQLConnectorException("Error performing queryResult: " + exception.getMessage());
        }
    }

    public String convertJavaDateTimeToString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public Date convertJavaDateTimeToDate(String dateString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }
}
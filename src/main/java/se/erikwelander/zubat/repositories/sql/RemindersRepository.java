package se.erikwelander.zubat.repositories.sql;

import se.erikwelander.zubat.repositories.sql.exceptions.RemindersRepositoryException;
import se.erikwelander.zubat.repositories.sql.models.ReminderModel;
import se.erikwelander.zubat.repositories.sql.sqlconnector.SQLConnector;
import se.erikwelander.zubat.repositories.sql.sqlconnector.exceptions.SQLConnectorException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RemindersRepository {

    public static final int REMINDER_TYPE_ON_TIMESTAMP = 1;
    public static final int REMINDER_TYPE_ON_JOIN = 2;

    private SQLConnector sqlConnector;

    public RemindersRepository() throws RemindersRepositoryException {
        try {
            sqlConnector = new SQLConnector();
        } catch (SQLConnectorException ex) {
            throw new RemindersRepositoryException(this.getClass().getName() + "Failed to establish database connection! Cause: " + ex.getMessage(), ex);
        }
    }

    public int addReminder(ReminderModel model) throws RemindersRepositoryException {

        try {
            StringBuilder builder = new StringBuilder();
            builder.append("INSERT INTO reminders (reminder_type, reminder_date, reminder_text, reminder_bot_from_user, reminder_bot_to_user, ");
            builder.append("reminder_bot_protocol, reminder_bot_server, reminder_bot_channel) ");
            builder.append("VALUES (?,?,?,?,?,?,?,?);");

            PreparedStatement statement = sqlConnector.prepareStatement(builder.toString());
            statement.setInt(1, model.getType());
            statement.setString(2, sqlConnector.convertJavaDateTimeToString((model.getDate())));
            statement.setString(3, model.getText());
            statement.setString(4, model.getFromUser());
            statement.setString(5, model.getToUser());
            statement.setString(6, model.getProtocol());
            statement.setString(7, model.getServer());
            statement.setString(8, model.getChannel());

            int insertID = sqlConnector.queryUpdate(statement);

            return insertID;
        } catch (SQLException | SQLConnectorException ex) {
            throw new RemindersRepositoryException(this.getClass().getName() + "Failed to insert model! Cause: " + ex.getMessage(), ex);
        }
    }

    public ReminderModel getReminderWithID(final int id) throws RemindersRepositoryException {

        try {
            String sql = "SELECT * FROM reminders WHERE reminder_id = ?;";
            PreparedStatement statement = sqlConnector.prepareStatement(sql);
            statement.setInt(1, id);

            ResultSet result = sqlConnector.queryResult(statement);
            if (!result.next()) {
                return null;
            }
            ReminderModel model = new ReminderModel(
                    result.getInt("reminder_id"),
                    result.getInt("reminder_type"),
                    sqlConnector.convertJavaDateTimeToDate(result.getString("reminder_date")),
                    result.getString("reminder_text"),
                    result.getString("reminder_bot_from_user"),
                    result.getString("reminder_bot_to_user"),
                    result.getString("reminder_bot_protocol"),
                    result.getString("reminder_bot_server"),
                    result.getString("reminder_bot_channel"));

            return model;
        } catch (SQLException | SQLConnectorException ex) {
            throw new RemindersRepositoryException(this.getClass().getName() + "Failed to insert model! Cause: " + ex.getMessage(), ex);
        }
    }

    public List<ReminderModel> getReminders() throws RemindersRepositoryException {

        try {
            String sql = "SELECT * FROM reminders;";
            PreparedStatement statement = sqlConnector.prepareStatement(sql);
            ResultSet result = sqlConnector.queryResult(statement);

            List<ReminderModel> models = new ArrayList<>();
            while (result.next()) {
                ReminderModel model = new ReminderModel(
                        result.getInt("reminder_id"),
                        result.getInt("reminder_type"),
                        sqlConnector.convertJavaDateTimeToDate(result.getString("reminder_date")),
                        result.getString("reminder_text"),
                        result.getString("reminder_bot_from_user"),
                        result.getString("reminder_bot_to_user"),
                        result.getString("reminder_bot_protocol"),
                        result.getString("reminder_bot_server"),
                        result.getString("reminder_bot_channel"));
                models.add(model);
            }

            return models;
        } catch (SQLException | SQLConnectorException ex) {
            throw new RemindersRepositoryException(this.getClass().getName() + "Failed to insert model! Cause: " + ex.getMessage(), ex);
        }
    }

    public void deleteReminder(final int ID) throws RemindersRepositoryException {

        try {
            String sql = "DELETE FROM reminders WHERE reminder_id = ?;";
            PreparedStatement statement = sqlConnector.prepareStatement(sql);
            statement.setInt(1, ID);
            sqlConnector.queryUpdate(statement);
        } catch (SQLException | SQLConnectorException ex) {
            throw new RemindersRepositoryException(this.getClass().getName() + "Failed to remove model! Cause: " + ex.getMessage(), ex);
        }
    }
}

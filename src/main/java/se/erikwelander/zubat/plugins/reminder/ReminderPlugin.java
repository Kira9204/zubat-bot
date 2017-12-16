package se.erikwelander.zubat.plugins.reminder;

import se.erikwelander.zubat.libs.ReggexLib;
import se.erikwelander.zubat.plugins.exceptions.PluginException;
import se.erikwelander.zubat.plugins.interfaces.PluginInterface;
import se.erikwelander.zubat.plugins.models.MessageEventModel;
import se.erikwelander.zubat.repositories.sql.NamesRepository;
import se.erikwelander.zubat.repositories.sql.RemindersRepository;
import se.erikwelander.zubat.repositories.sql.exceptions.NamesRepositoryException;
import se.erikwelander.zubat.repositories.sql.exceptions.RemindersRepositoryException;
import se.erikwelander.zubat.repositories.sql.models.ReminderModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static se.erikwelander.zubat.globals.Globals.TRIGGER;

public class ReminderPlugin implements PluginInterface {

    private static final String REGGEX_REMIND_ADD_JOIN = "^" + TRIGGER + "remind add join (\\w+) (.*)";
    private static final String REGGEX_REMIND_ADD_DATE = "^" + TRIGGER + "remind add date ([0-9-: ]{19}) (.*)";
    private static final String REGGEX_REMIND_DELETE_ID = "^" + TRIGGER + "remind delete (\\d+)";
    private static final String REGGEX_REMIND_HELP = "^" + TRIGGER + "remind help";
    private static Map<Integer, ReminderModel> reminderModelMap = new HashMap<>();
    private RemindersRepository repository;

    public ReminderPlugin() throws PluginException {
        try {
            repository = new RemindersRepository();
        } catch (RemindersRepositoryException e) {
            throw new PluginException("Could not construct repository! Cause: "+e.getMessage(), e);
        }

        reminderModelMap = loadReminders();
    }

    private Map<Integer, ReminderModel> loadReminders() throws PluginException {

        try {
            List<ReminderModel> models = repository.getReminders();
            Map<Integer, ReminderModel> maps = new HashMap<>();
            for (ReminderModel model : models) {
                maps.put(model.getId(), model);
            }

            return maps;
        } catch (RemindersRepositoryException ex) {
            throw new PluginException(this.getClass().getName() + "Failed to load reminders from DB! Cause: " + ex.getMessage(), ex);
        }
    }

    public List<String> triggerReminders(MessageEventModel messageEventModel, int ofType) {

        List<String> toSend = new ArrayList<>();

        try {

            Date currentDate = new Date();
            for (int key : this.reminderModelMap.keySet()) {
                ReminderModel reminderModel = this.reminderModelMap.get(key);
                if (reminderModel.getProtocol().equals(messageEventModel.getProtocol()) &&
                        reminderModel.getServer().equals(messageEventModel.getServer()) &&
                        reminderModel.getChannel().equals(messageEventModel.getChannel())) {

                    if (reminderModel.getType() == ofType &&
                            reminderModel.getType() == RemindersRepository.REMINDER_TYPE_ON_JOIN) {

                        repository.deleteReminder(key);
                        this.reminderModelMap.remove(key);

                        StringBuilder builder = new StringBuilder();
                        builder.append(reminderModel.getToUser() + ": " + reminderModel.getFromUser());
                        builder.append(" Told me to remind you next time i saw you that: ");
                        builder.append(reminderModel.getText());
                        toSend.add(builder.toString());
                    } else if (reminderModel.getType() == ofType &&
                            reminderModel.getType() == RemindersRepository.REMINDER_TYPE_ON_TIMESTAMP &&
                            reminderModel.getDate().before(currentDate)) {

                        repository.deleteReminder(key);
                        this.reminderModelMap.remove(key);

                        StringBuilder builder = new StringBuilder();
                        builder.append("This is an automatic timed reminder from " + reminderModel.getFromUser() + ": ");
                        builder.append("\"" + reminderModel.getText() + "\"");
                        toSend.add(builder.toString());
                    }
                    break;
                }
            }
        } catch (RemindersRepositoryException ex) {
            toSend.add("Exception: Reminders: Failed to remove reminder for database!");
            return toSend;
        }
        return toSend;
    }

    @Override
    public boolean supportsAction(MessageEventModel messageEventModel) {
        String message = messageEventModel.getMessage().toLowerCase();
        if (ReggexLib.match(message, REGGEX_REMIND_ADD_JOIN) ||
                ReggexLib.match(message, REGGEX_REMIND_ADD_DATE) ||
                ReggexLib.match(message, REGGEX_REMIND_DELETE_ID)) {
            return true;
        }
        return false;
    }

    @Override
    public List<String> trigger(MessageEventModel messageEventModel) {

        List<String> toSend = new ArrayList<>();
        if (!supportsAction(messageEventModel)) {
            return toSend;
        }

        //remind date (\w+) ([0-9-: ]+) (.+)
        String message = messageEventModel.getMessage().toLowerCase();
        if (ReggexLib.match(message, REGGEX_REMIND_ADD_JOIN)) {
            String mUser = ReggexLib.find(message, REGGEX_REMIND_ADD_JOIN, 1);
            String mMessage = ReggexLib.find(messageEventModel.getMessage(), REGGEX_REMIND_ADD_JOIN, 2);

            if (mUser.isEmpty() || mMessage.isEmpty()) {
                toSend.add("Failed to add event: Empty user or message!");
                toSend.add("The correct format is: " + TRIGGER + "remind add join <user> <text>");
                return toSend;
            }

            ReminderModel reminderModel = new ReminderModel(
                    repository.REMINDER_TYPE_ON_JOIN,
                    new Date(System.currentTimeMillis()),
                    mMessage,
                    messageEventModel.getUser(),
                    mUser,
                    messageEventModel.getProtocol(),
                    messageEventModel.getServer(),
                    messageEventModel.getChannel());

            int insertID = 0;
            try {
                insertID = repository.addReminder(reminderModel);
                reminderModel.setId(insertID);
                this.reminderModelMap.put(insertID, reminderModel);
            } catch (RemindersRepositoryException ex) {
                toSend.add("Exception: Failed to add reminder to database");
                return toSend;
            }
            toSend.add("OK! I will remind " + mUser + " of that next time he logs in.");
            toSend.add("This reminder has ID: " + insertID);
        } else if (ReggexLib.match(message, REGGEX_REMIND_ADD_DATE)) {
            String mDate = ReggexLib.find(message, REGGEX_REMIND_ADD_DATE, 1);
            String mMessage = ReggexLib.find(messageEventModel.getMessage(), REGGEX_REMIND_ADD_DATE, 2);

            if (mDate.isEmpty() || mMessage.isEmpty()) {
                toSend.add("Failed to add event: Empty user or message!");
                toSend.add("The correct format is: " + TRIGGER + "remind add date <yyyy-MM-dd HH:mm:ss> <text>");
                return toSend;
            }

            int insertID = 0;
            try {
                ReminderModel reminderModel = new ReminderModel(
                        repository.REMINDER_TYPE_ON_TIMESTAMP,
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mDate),
                        mMessage,
                        messageEventModel.getUser(),
                        "",
                        messageEventModel.getProtocol(),
                        messageEventModel.getServer(),
                        messageEventModel.getChannel());
                insertID = repository.addReminder(reminderModel);
                reminderModel.setId(insertID);
                this.reminderModelMap.put(insertID, reminderModel);
            } catch (RemindersRepositoryException | ParseException ex) {
                toSend.add("Exception: Failed to add reminder to database");
                return toSend;
            }
            toSend.add("OK! I will put out this reminder on: " + mDate);
            toSend.add("This reminder has ID: " + insertID);
        } else if (ReggexLib.match(message, REGGEX_REMIND_DELETE_ID)) {
            String mID = ReggexLib.find(message, REGGEX_REMIND_DELETE_ID, 1);

            if (mID.isEmpty()) {
                toSend.add("Failed to remove event: empty ID!");
                toSend.add("The correct format is: " + TRIGGER + "remind delete <id>");
                return toSend;
            }

            int reminderID = 0;
            try {
                reminderID = Integer.parseInt(mID);
            } catch (NumberFormatException ex) {
                toSend.add(mID + " Not a valid number!");
                return toSend;
            }

            try {
                ReminderModel reminderModel = repository.getReminderWithID(reminderID);

                if (null == reminderModel) {
                    toSend.add("There is no reminder with that ID!");
                    return toSend;
                }

                if (reminderModel.getProtocol().equals(messageEventModel.getProtocol()) &&
                        reminderModel.getServer().equals(messageEventModel.getServer()) &&
                        reminderModel.getChannel().equals(messageEventModel.getChannel())) {
                    repository.deleteReminder(reminderID);
                    this.reminderModelMap.remove(reminderID);
                    toSend.add("OK! Reminder removed");
                } else {
                    toSend.add("This reminder does not belong to you");
                    return toSend;
                }

            } catch (RemindersRepositoryException ex) {
                toSend.add("Exception: Failed to modify database");
                return toSend;
            }
        } else if (ReggexLib.match(message, REGGEX_REMIND_HELP)) {
            toSend.add("This plugin allows you to send reminders to users based upon events.");
            toSend.add("Usage: " + TRIGGER + "remind add join <user> <text>. " + TRIGGER + "remind add date <yyyy-MM-dd HH:mm:ss> <text>. " + TRIGGER + "remind delete <id>");
        }
        return toSend;
    }
}

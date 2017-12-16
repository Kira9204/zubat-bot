package se.erikwelander.zubat.services.protocols.irc;

import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import se.erikwelander.zubat.globals.Globals;
import se.erikwelander.zubat.plugins.bored.BoredLinksPlugin;
import se.erikwelander.zubat.plugins.exceptions.PluginException;
import se.erikwelander.zubat.plugins.fortune.FortunePlugin;
import se.erikwelander.zubat.plugins.info.InfoPlugin;
import se.erikwelander.zubat.plugins.interfaces.PluginInterface;
import se.erikwelander.zubat.plugins.linkshorter.LinkShorterPlugin;
import se.erikwelander.zubat.plugins.models.MessageEventModel;
import se.erikwelander.zubat.plugins.name.NamePlugin;
import se.erikwelander.zubat.plugins.reminder.ReminderPlugin;
import se.erikwelander.zubat.plugins.reminder.models.RemindersConfigModel;
import se.erikwelander.zubat.plugins.requestAuth.RequestAuth;
import se.erikwelander.zubat.plugins.webtitle.WebTitlePlugin;
import se.erikwelander.zubat.repositories.sql.RemindersRepository;
import se.erikwelander.zubat.repositories.sql.exceptions.AuthenticationRepositoryException;
import se.erikwelander.zubat.services.protocols.irc.models.IRCConnectionModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class IRCEventListener extends ListenerAdapter implements Runnable {

    private final int botID;
    private IRCConnectionModel connectionModel;
    private RequestAuth requestAuth;
    private ReminderPlugin reminderPlugin;
    private RemindersConfigModel remindersConfigModel;
    private LinkShorterPlugin linkShorterPlugin;
    private List<PluginInterface> plugins;

    public IRCEventListener(final IRCConnectionModel connectionModel, final int botID) {
        this.connectionModel = connectionModel;
        this.botID = botID;

        try {
            requestAuth = new RequestAuth();
        } catch (AuthenticationRepositoryException ex) {
            System.err.println("Failed to load plugin: RequestAuth. Cause: " + ex.getMessage());
        }
        try {
            this.reminderPlugin = new ReminderPlugin();
        } catch (PluginException ex) {
            System.err.println("Failed to load plugin: remindersPlugin. Cause: " + ex.getMessage());
        }

        plugins = new ArrayList<>();
        try {
            plugins.add(new BoredLinksPlugin());
        } catch (PluginException ex) {
            System.err.println("Failed to load plugin: BoredLinksPlugin. Cause: " + ex.getMessage());
        }
        try {
            plugins.add(new WebTitlePlugin());
        } catch (PluginException ex) {
            System.err.println("Failed to load plugin: webTitlePlugin. Cause: " + ex.getMessage());
        }
        try {
            this.linkShorterPlugin = new LinkShorterPlugin();
        } catch (PluginException ex) {
            System.err.println("Failed to load plugin: urlShorterPlugin. Cause: " + ex.getMessage());
        }
        try {
            plugins.add(new NamePlugin());
        } catch (PluginException ex) {
            System.err.println("Failed to load plugin: NamePlugin. Cause: " + ex.getMessage());
        }

        plugins.add(new InfoPlugin());
        plugins.add(new FortunePlugin());
        plugins.add(this.reminderPlugin);

        startReminderThreadIfEnabled();
    }

    public static void sendMessageFromBotID(final int botID, String channel, String message) {
        PircBotX botInstance = IRCService.getBot(botID);
        try {
            botInstance.send().message(channel, message);
        } catch (Exception ex) {
        }
    }

    private void startReminderThreadIfEnabled() {
        String filePath = Globals.PATH_CFG_PLUGIN + "Reminders.json";
        File file = new File(filePath);
        if (!file.isFile()) {
            System.err.println("Failed to load plugin configuration \"Reminders\"");
        }

        Gson gson = new Gson();
        String json = "";
        System.out.println("Loading file: "+file.getAbsoluteFile());
        try {
            json = FileUtils.readFileToString(file.getAbsoluteFile(), "UTF8");
        } catch (IOException ex) {
            System.err.println("Failed to read configuration file: " + file.getAbsolutePath() + "!");
        }

        this.remindersConfigModel = gson.fromJson(json, RemindersConfigModel.class);

        if (!this.remindersConfigModel.isEnabled()) {
            return;
        }
        new Thread(this).start();
    }

    @Override
    public void onJoin(JoinEvent event) {

        MessageEventModel messageEventModel = new MessageEventModel(
                Globals.PROTOCOL_IRC,
                connectionModel.getHost(),
                event.getChannel().getName(),
                event.getUser().getNick(),
                event.getUser().getHostmask(),
                "USER JOINED"
        );


        //Authenticate the user and give it Operator privileges if applicable
        try {
            if (requestAuth.validateUser(messageEventModel)) {
                PircBotX botInstance = IRCService.getBot(botID);
                botInstance.send().mode(messageEventModel.getChannel(), "+o " + messageEventModel.getUser());
            }

        } catch (Exception ex) {
            event.getChannel().send().message("Exception at validateUser()");
        }

        List<String> toSend = reminderPlugin.triggerReminders(messageEventModel,
                RemindersRepository.REMINDER_TYPE_ON_JOIN);
        if (!toSend.isEmpty()) {
            PircBotX botInstance = IRCService.getBot(botID);
            for (String line : toSend) {
                event.getChannel().send().message(line);
            }
        }

    }

    @Override
    public void onMessage(MessageEvent event) {
        if (event.getUser().equals(this.connectionModel.getNickName())) {
            return;
        }

        String message = event.getMessage();
        MessageEventModel messageEventModel = new MessageEventModel(
                Globals.PROTOCOL_IRC,
                connectionModel.getHost(),
                event.getChannel().getName(),
                event.getUser().getNick(),
                event.getUser().getHostmask(),
                message
        );

        List<String> shortURL = linkShorterPlugin.trigger(messageEventModel);
        if (!shortURL.isEmpty()) {
            event.getChannel().send().message("Short URL: " + shortURL.get(0));
            return;
        }

        List<String> pluginSend = new ArrayList<>();
        for (PluginInterface plugin : plugins) {
            pluginSend.addAll(plugin.trigger(messageEventModel));
        }
        if (!pluginSend.isEmpty()) {
            for (String send : pluginSend) {
                event.getChannel().send().message(send);
            }
            return;
        }

        if (message.equals(Globals.TRIGGER + "op")) {
            boolean valid = false;
            try {
                valid = requestAuth.validateUser(messageEventModel);
            } catch (Exception ex) {
                event.getChannel().send().message("Exception at validateUser()");
                return;
            }

            if (valid) {
                PircBotX botInstance = IRCService.getBot(botID);
                botInstance.send().mode(messageEventModel.getChannel(), "+o " + messageEventModel.getUser());
            } else {
                event.getChannel().send().message(messageEventModel.getUser() + " failed to authenticate with me.");
                return;
            }
        } else if (message.equals(Globals.TRIGGER + "deop")) {
            boolean valid = false;
            try {
                valid = requestAuth.validateUser(messageEventModel);
            } catch (Exception ex) {
                event.getChannel().send().message("Exception at validateUser()");
                return;
            }

            if (valid) {
                PircBotX botInstance = IRCService.getBot(botID);
                botInstance.send().mode(messageEventModel.getChannel(), "-o " + messageEventModel.getUser());
            } else {
                event.getChannel().send().message(messageEventModel.getUser() + " failed to authenticate with me.");
            }
        } else if (message.equals(Globals.TRIGGER + "op all")) {
            boolean valid = false;
            try {
                valid = requestAuth.validateUser(messageEventModel);
            } catch (Exception ex) {
                event.getChannel().send().message("Exception at validateUser()");
                return;
            }

            if (valid) {
                PircBotX botInstance = IRCService.getBot(botID);
                ImmutableSortedSet<User> users = event.getChannel().getUsers();
                for (User user : users) {
                    botInstance.send().mode(messageEventModel.getChannel(), "+o " + user.getNick());
                }
            } else {
                event.getChannel().send().message(messageEventModel.getUser() + " failed to authenticate with me.");
            }
        } else if (message.equals(Globals.TRIGGER + "deop all")) {
            boolean valid = false;
            try {
                valid = requestAuth.validateUser(messageEventModel);
            } catch (Exception ex) {
                event.getChannel().send().message("Exception at validateUser()");
                return;
            }

            if (valid) {
                PircBotX botInstance = IRCService.getBot(botID);
                ImmutableSortedSet<User> users = event.getChannel().getUsers();
                for (User user : users) {
                    if (user.getNick().equals(botInstance.getNick())) {
                        continue;
                    }
                    botInstance.send().mode(messageEventModel.getChannel(), "-o " + user.getNick());
                }
            } else {
                event.getChannel().send().message(messageEventModel.getUser() + " failed to authenticate with me.");
            }
        } else if (message.startsWith(Globals.TRIGGER + "notifyall")) {
            boolean valid = false;
            try {
                valid = requestAuth.validateUser(messageEventModel);
            } catch (Exception ex) {
                event.getChannel().send().message("Exception at validateUser()");
                return;
            }
            if (valid) {
                int index = event.getMessage().indexOf(" ");
                if (index > -1) {
                    String toSend = "Message from authenticated user " + messageEventModel.getUser() + " to EVERYONE: " + event.getMessage().substring(index + 1) + ". Users: ";
                    for (String nickName : event.getChannel().getUsersNicks()) {
                        toSend += nickName + " ";
                    }
                    event.getChannel().send().message(toSend);
                } else {
                    event.getChannel().send().message(messageEventModel.getUser() + " Notify channel users of what?");
                }
            } else {
                event.getChannel().send().message(messageEventModel.getUser() + " failed to authenticate with me.");
            }
        } else if (event.getMessage().startsWith(Globals.TRIGGER + "nick ")) {
            boolean valid = false;
            try {
                valid = requestAuth.validateUser(messageEventModel);
            } catch (Exception ex) {
                event.getChannel().send().message("Exception at validateUser()");
                return;
            }

            if (valid) {
                String nick = event.getMessage().split(" ")[1];
                PircBotX botInstance = IRCService.getBot(botID);
                botInstance.send().changeNick(nick);
            } else {
                event.getChannel().send().message(messageEventModel.getUser() + " failed to authenticate with me.");
            }
        } else if (event.getMessage().equals(Globals.TRIGGER + "leave")) {
            boolean valid = false;
            try {
                valid = requestAuth.validateUser(messageEventModel);
            } catch (Exception ex) {
                event.getChannel().send().message("Exception at validateUser()");
                return;
            }
            if (valid) {
                PircBotX botInstance = IRCService.getBot(botID);
                String command = "PART " + event.getChannel().getName() + " :" + messageEventModel.getUser() + " Told me to leave...";
                botInstance.sendRaw().rawLine(command);
            } else {
                event.getChannel().send().message(messageEventModel.getUser() + " failed to authenticate with me.");
            }
        }
    }

    @Override
    public void onInvite(InviteEvent event) {
        final String user = event.getUser().getNick();
        final String channel = event.getChannel();
        PircBotX botInstance = IRCService.getBot(botID);
        botInstance.send().joinChannel(channel);
        botInstance.send().message(channel, "Hello! " + user + " invited me here.");
    }

    @Override
    public void run() {
        try {
            Thread.sleep(this.remindersConfigModel.getRefreshPoll());
        } catch (InterruptedException e) {
        }

        while (true) {
            for (String channel : this.connectionModel.getChannels()) {
                MessageEventModel messageEventModel = new MessageEventModel(
                        Globals.PROTOCOL_IRC,
                        connectionModel.getHost(),
                        channel,
                        "THREAD",
                        "THREAD",
                        "USER JOINED"
                );

                List<String> reminders = this.reminderPlugin.triggerReminders(messageEventModel,
                        RemindersRepository.REMINDER_TYPE_ON_TIMESTAMP);
                if (!reminders.isEmpty()) {
                    for (String reminder : reminders) {
                        PircBotX botInstance = IRCService.getBot(botID);
                        botInstance.send().message(channel, reminder);
                    }
                }
            }
            try {
                Thread.sleep(this.remindersConfigModel.getRefreshPoll());
            } catch (InterruptedException e) {
            }
        }
    }
}

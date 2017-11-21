package se.erikwelander.zubat.plugins.info;

import se.erikwelander.zubat.globals.Globals;
import se.erikwelander.zubat.plugins.interfaces.PluginInterface;
import se.erikwelander.zubat.plugins.models.MessageEventModel;

import java.util.ArrayList;
import java.util.List;

import static se.erikwelander.zubat.globals.Globals.TRIGGER;

public class InfoPlugin implements PluginInterface {

    private static String[] triggers = {
            TRIGGER + "help",
            TRIGGER + "about",
            TRIGGER + "version",
            TRIGGER + "commands"
    };

    @Override
    public boolean supportsAction(MessageEventModel messageEventModel) {
        for (String command : triggers) {
            if (messageEventModel.getMessage().equals(command)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> trigger(MessageEventModel messageEventModel) {
        List<String> lines = new ArrayList<>();
        String message = messageEventModel.getMessage().toLowerCase();

        if (message.equals(TRIGGER + "help") || message.equals(TRIGGER + "about")) {
            lines.add(messageEventModel.getUser() + ": Hello! I am Zubat, a multi-protocol \"modular\" chat bot by Erik Welander (Kira). Version: " + Globals.VERSION);
            lines.add("Available functions include: metadata generation from lot web sites (youtube info, vimeo, steam, trading, pricing etc), Automatic/manual link shortning, cross-talk between protocols and channels, reminders on join/date, automatic operator authentication, name suggestions and probably more!");
            lines.add("See " + TRIGGER + "commands to view a list of available runtime commands");
        } else if (message.equals(TRIGGER + "version")) {
            lines.add("Zubat version: " + Globals.VERSION);
        } else if (message.equals(TRIGGER + "commands")) {
            lines.add(TRIGGER + "short <url>. " + TRIGGER + "op/deop [all] - Authentication. Can be set for personal use or for everyone in the channel. " + TRIGGER + "notifyall - Notifies everyone in a channel. " + TRIGGER + "nick - changes the bot nick. " + TRIGGER + "leave - Leaves the channel. " + TRIGGER + "name <pokemon/soda> [<num>]. " + TRIGGER + "remind join <user> <message>. " + TRIGGER + "remind date <yyyy-MM-dd HH:mm:ss> <message>. " + TRIGGER + "remind delete <num> - Deactivates a reminder with an existing ID. " + TRIGGER + "mumble. " + TRIGGER + "bored <[<num>]. " + TRIGGER + "bored remove <num>. " + TRIGGER + "bored remove last. Notice: Not all of these commands are implemented in all of the protocols supported.");
        }
        return lines;
    }
}

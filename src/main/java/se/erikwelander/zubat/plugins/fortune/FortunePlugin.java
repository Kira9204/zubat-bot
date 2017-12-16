package se.erikwelander.zubat.plugins.fortune;

import se.erikwelander.zubat.libs.ExecLib;
import se.erikwelander.zubat.plugins.interfaces.PluginInterface;
import se.erikwelander.zubat.plugins.models.MessageEventModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static se.erikwelander.zubat.globals.Globals.TRIGGER;

public class FortunePlugin implements PluginInterface {

    private static String[] triggers = {
            TRIGGER + "fortune",
    };

    @Override
    public boolean supportsAction(MessageEventModel messageEventModel) {
        for (String command : triggers) {
            if (messageEventModel.getMessage().toLowerCase().equals(command)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> trigger(MessageEventModel messageEventModel) {
        List<String> lines = new ArrayList<>();
        if(!supportsAction(messageEventModel)) {
            return lines;
        }
        String message = messageEventModel.getMessage().toLowerCase();
        if(message.equals(TRIGGER + "fortune")) {
            try {
                String fortune = ExecLib.execCmd("fortune -a");
                String[] exploded = fortune.split("\r\n");
                for(String s : exploded) {
                    lines.add(s);
                }
            }catch (IOException ex) {
                lines.add("\"fortune\" is not installed on this system.");
            }
        }
        return lines;
    }
}

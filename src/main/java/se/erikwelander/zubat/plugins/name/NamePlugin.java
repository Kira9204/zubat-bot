package se.erikwelander.zubat.plugins.name;

import se.erikwelander.zubat.libs.ReggexLib;
import se.erikwelander.zubat.plugins.interfaces.PluginInterface;
import se.erikwelander.zubat.plugins.models.MessageEventModel;
import se.erikwelander.zubat.repositories.sql.NamesRepository;
import se.erikwelander.zubat.repositories.sql.models.NameModel;

import java.util.ArrayList;
import java.util.List;

import static se.erikwelander.zubat.globals.Globals.TRIGGER;

public class NamePlugin implements PluginInterface {

    private static String REGGEX_NAME_POKEMON_ONE = "^" + TRIGGER + "name pokemon";
    private static String REGGEX_NAME_POKEMON_MANY = "^" + TRIGGER + "name pokemon ([0-9]+)";
    private static String REGGEX_NAME_SODA_ONE = "^" + TRIGGER + "name soda";
    private static String REGGEX_NAME_SODA_MANY = "^" + TRIGGER + "name soda ([0-9]+)";
    private static String REGGEX_NAME_HELP = "^" + TRIGGER + "name help";

    public NamePlugin() {
    }


    @Override
    public boolean supportsAction(MessageEventModel messageEventModel) {
        String message = messageEventModel.getMessage().toLowerCase();
        if (ReggexLib.match(message, REGGEX_NAME_POKEMON_ONE) ||
                ReggexLib.match(message, REGGEX_NAME_POKEMON_MANY) ||
                ReggexLib.match(message, REGGEX_NAME_SODA_ONE) ||
                ReggexLib.match(message, REGGEX_NAME_SODA_MANY) ||
                ReggexLib.match(message, REGGEX_NAME_HELP)) {
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

        String message = messageEventModel.getMessage().toLowerCase();
        if (ReggexLib.match(message, REGGEX_NAME_POKEMON_ONE)) {
            int numberOfNames = 1;
            if (ReggexLib.match(message, REGGEX_NAME_POKEMON_MANY)) {
                String regRes = ReggexLib.find(message, REGGEX_NAME_POKEMON_MANY, 1);
                if (!regRes.isEmpty()) {
                    numberOfNames = Integer.parseInt(regRes);
                }
            }

            NamesRepository repository = new NamesRepository();

            List<String> names = new ArrayList<>();
            List<NameModel> models = new ArrayList<>();
            try {
                models = repository.getRandomNamesOfType(repository.NAME_POKEMON, numberOfNames);
            } catch (Exception ex) {
                names.add(ex.getMessage());
            }

            StringBuilder builder = new StringBuilder();
            for (NameModel model : models) {
                builder.append(model.getName() + ", ");
            }
            builder.replace(builder.length() - 2, builder.length(), "");
            toSend.add(builder.toString());
        } else if (ReggexLib.match(message, REGGEX_NAME_SODA_ONE)) {
            int numberOfNames = 1;
            if (ReggexLib.match(message, REGGEX_NAME_SODA_MANY)) {
                String regRes = ReggexLib.find(message, REGGEX_NAME_SODA_MANY, 1);
                if (!regRes.isEmpty()) {
                    numberOfNames = Integer.parseInt(regRes);
                }
            }

            NamesRepository repository = new NamesRepository();
            List<String> names = new ArrayList<>();
            List<NameModel> models = new ArrayList<>();
            try {
                models = repository.getRandomNamesOfType(repository.NAME_SODA, numberOfNames);
            } catch (Exception ex) {
                names.add(ex.getMessage());
            }

            StringBuilder builder = new StringBuilder();
            for (NameModel model : models) {
                builder.append(model.getName() + ", ");
            }
            builder.replace(builder.length() - 2, builder.length(), "");
            toSend.add(builder.toString());
        } else if (ReggexLib.match(message, REGGEX_NAME_HELP)) {
            toSend.add("Usage: " + TRIGGER + "name <pokemon/soda> [<num>]");
        }
        return toSend;
    }
}

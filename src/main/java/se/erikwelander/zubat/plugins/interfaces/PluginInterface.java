package se.erikwelander.zubat.plugins.interfaces;

import se.erikwelander.zubat.plugins.models.MessageEventModel;

import java.util.List;

public interface PluginInterface {
    boolean supportsAction(MessageEventModel messageEventModel);

    List<String> trigger(MessageEventModel messageEventModel);
}

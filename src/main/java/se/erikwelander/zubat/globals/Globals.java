package se.erikwelander.zubat.globals;

import se.erikwelander.zubat.libs.PathsLib;

public class Globals {
    public static final String VERSION = "2017-07-09 BETA";
    public static final String PATH = PathsLib.getCurrentPath();
    public static final String PATH_CFG = PATH + "/cfg/";
    public static final String PATH_CFG_PROTOCOL = PATH_CFG + "protocols/";
    public static final String PATH_CFG_PLUGIN = PATH_CFG + "plugins/";
    public static final String PATH_CFG_PLUGIN_CHILD = PATH_CFG_PLUGIN + "childPlugins/";
    public static final String PATH_CFG_DATABASE = PATH_CFG + "database/";
    public static final String PROTOCOL_IRC = "irc";
    public static final String PROTOCOL_MUMBLE = "mumble";
    public static final String PROTOCOL_GTALK = "gtalk";
    public static final String REGGEX_IS_VALID_URL = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
    public static String TRIGGER = "";
}

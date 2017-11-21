package se.erikwelander.zubat.plugins.exceptions;

public class PluginException extends Exception {
    private static final long serialVersionUID = 1L;

    public PluginException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PluginException(final String message) {
        super(message);
    }
}

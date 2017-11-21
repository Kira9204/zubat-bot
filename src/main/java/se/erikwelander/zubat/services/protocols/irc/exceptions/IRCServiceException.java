package se.erikwelander.zubat.services.protocols.irc.exceptions;

public class IRCServiceException extends Exception {
    private static final long serialVersionUID = 1L;

    public IRCServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IRCServiceException(final String message) {
        super(message);
    }
}

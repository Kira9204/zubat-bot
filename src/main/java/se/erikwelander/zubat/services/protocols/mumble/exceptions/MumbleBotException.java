package se.erikwelander.zubat.services.protocols.mumble.exceptions;

public class MumbleBotException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MumbleBotException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MumbleBotException(final String message) {
        super(message);
    }
}

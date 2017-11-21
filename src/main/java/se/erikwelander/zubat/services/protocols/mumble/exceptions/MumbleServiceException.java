package se.erikwelander.zubat.services.protocols.mumble.exceptions;

public class MumbleServiceException extends Exception {
    private static final long serialVersionUID = 1L;

    public MumbleServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MumbleServiceException(final String message) {
        super(message);
    }
}

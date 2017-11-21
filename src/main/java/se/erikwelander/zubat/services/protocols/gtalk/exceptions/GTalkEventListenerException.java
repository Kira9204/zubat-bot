package se.erikwelander.zubat.services.protocols.gtalk.exceptions;

public class GTalkEventListenerException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public GTalkEventListenerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public GTalkEventListenerException(final String message) {
        super(message);
    }
}

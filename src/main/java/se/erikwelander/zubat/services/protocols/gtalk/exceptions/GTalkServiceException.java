package se.erikwelander.zubat.services.protocols.gtalk.exceptions;

public class GTalkServiceException extends Exception {
    private static final long serialVersionUID = 1L;

    public GTalkServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public GTalkServiceException(final String message) {
        super(message);
    }
}

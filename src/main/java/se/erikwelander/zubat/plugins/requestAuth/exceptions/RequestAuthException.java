package se.erikwelander.zubat.plugins.requestAuth.exceptions;

public class RequestAuthException extends Exception {
    private static final long serialVersionUID = 1L;

    public RequestAuthException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RequestAuthException(final String message) {
        super(message);
    }
}


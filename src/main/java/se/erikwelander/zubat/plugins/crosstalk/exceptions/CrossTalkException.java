package se.erikwelander.zubat.plugins.crosstalk.exceptions;

public class CrossTalkException extends Exception {
    private static final long serialVersionUID = 1L;

    public CrossTalkException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CrossTalkException(final String message) {
        super(message);
    }
}
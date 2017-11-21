package se.erikwelander.zubat.services.MainService.exceptions;

public class MainServiceException extends Exception {
    private static final long serialVersionUID = 1L;

    public MainServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MainServiceException(final String message) {
        super(message);
    }
}

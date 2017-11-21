package se.erikwelander.zubat.plugins.webtitle.exceptions;

public class WebServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public WebServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public WebServiceException(final String message) {
        super(message);
    }
}


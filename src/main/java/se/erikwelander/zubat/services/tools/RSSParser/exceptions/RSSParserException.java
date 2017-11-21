package se.erikwelander.zubat.services.tools.RSSParser.exceptions;

public class RSSParserException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RSSParserException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RSSParserException(final String message) {
        super(message);
    }
}

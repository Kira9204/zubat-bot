package se.erikwelander.zubat.repositories.sql.exceptions;

public class ShortLinksRepositoryException extends Exception {
    private static final long serialVersionUID = 1L;

    public ShortLinksRepositoryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ShortLinksRepositoryException(final String message) {
        super(message);
    }
}


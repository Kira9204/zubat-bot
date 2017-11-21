package se.erikwelander.zubat.repositories.sql.exceptions;

public class LinksRepositoryException extends Exception {
    private static final long serialVersionUID = 1L;

    public LinksRepositoryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public LinksRepositoryException(final String message) {
        super(message);
    }
}


package se.erikwelander.zubat.repositories.sql.exceptions;

public class NamesRepositoryException extends Exception {
    private static final long serialVersionUID = 1L;

    public NamesRepositoryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NamesRepositoryException(final String message) {
        super(message);
    }
}


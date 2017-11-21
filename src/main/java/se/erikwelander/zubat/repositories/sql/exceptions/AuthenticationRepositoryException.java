package se.erikwelander.zubat.repositories.sql.exceptions;

public class AuthenticationRepositoryException extends Exception {
    private static final long serialVersionUID = 1L;

    public AuthenticationRepositoryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AuthenticationRepositoryException(final String message) {
        super(message);
    }
}


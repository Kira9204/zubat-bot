package se.erikwelander.zubat.repositories.sql.exceptions;

public class RemindersRepositoryException extends Exception {
    private static final long serialVersionUID = 1L;

    public RemindersRepositoryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RemindersRepositoryException(final String message) {
        super(message);
    }
}


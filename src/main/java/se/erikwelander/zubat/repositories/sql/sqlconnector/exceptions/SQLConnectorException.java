package se.erikwelander.zubat.repositories.sql.sqlconnector.exceptions;

public class SQLConnectorException extends Exception {
    private static final long serialVersionUID = 1L;

    public SQLConnectorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SQLConnectorException(final String message) {
        super(message);
    }
}


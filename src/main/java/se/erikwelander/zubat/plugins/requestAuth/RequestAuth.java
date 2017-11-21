package se.erikwelander.zubat.plugins.requestAuth;

import se.erikwelander.zubat.plugins.models.MessageEventModel;
import se.erikwelander.zubat.plugins.requestAuth.exceptions.RequestAuthException;
import se.erikwelander.zubat.repositories.sql.AuthenticationRepository;
import se.erikwelander.zubat.repositories.sql.exceptions.AuthenticationRepositoryException;
import se.erikwelander.zubat.repositories.sql.models.AuthenticationModel;

public class RequestAuth {

    public RequestAuth() {
    }

    public final boolean validateUser(MessageEventModel messageEventModel) throws RequestAuthException {

        AuthenticationRepository repository = new AuthenticationRepository();
        try {
            AuthenticationModel model = repository.getWithMessageEvent(messageEventModel);
            return null != model;
        } catch (AuthenticationRepositoryException ex) {
            throw new RequestAuthException(this.getClass().getName() + "Failed to retrieve data from model: AuthenticationRepository. Cause: " + ex.getMessage());
        }
    }
}

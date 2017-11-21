package se.erikwelander.zubat.services.protocols.gtalk;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import se.erikwelander.zubat.services.protocols.gtalk.exceptions.GTalkEventListenerException;
import se.erikwelander.zubat.services.protocols.gtalk.models.GTalkConnectionModel;

public class GTalkEventListener implements ChatManagerListener, MessageListener {

    private static String nick;

    public GTalkEventListener(GTalkConnectionModel connectionModel) {
        nick = connectionModel.getNick().toLowerCase();
    }

    public static void respond(Chat chat, String message) {
        String toSend = "lol";
        try {
            chat.sendMessage(toSend);
        } catch (XMPPException ex) {
            throw new GTalkEventListenerException("Failed so send message: " + toSend + "! Cause: " + ex.getMessage(), ex);
        }

    }

    @Override
    public void chatCreated(Chat chat, boolean b) {
        chat.addMessageListener(this);
    }

    @Override
    public void processMessage(Chat chat, Message recievedMessage) {

        if (null == recievedMessage.getBody()) {
            return;
        }
        String message = recievedMessage.getBody().toLowerCase();

        if (message.equals("!" + nick + " leave")) {
            System.out.println("Bye!");
            GTalkService.getConnection().disconnect();
        }
        System.out.println("Recieved message: " + message);
        respond(chat, message);
    }
}

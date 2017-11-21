package se.erikwelander.zubat.services.protocols.gtalk;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.jivesoftware.smack.*;
import se.erikwelander.zubat.globals.Globals;
import se.erikwelander.zubat.services.protocols.gtalk.exceptions.GTalkServiceException;
import se.erikwelander.zubat.services.protocols.gtalk.models.GTalkConnectionModel;

import java.io.File;
import java.io.IOException;

public class GTalkService implements Runnable {
    private static boolean running;
    private static Thread thread;
    private static GTalkConnectionModel connectionModel;
    private static XMPPConnection connection;

    public GTalkService() throws GTalkServiceException {
        this.connectionModel = loadConnectionModel();
        this.running = false;
        this.thread = new Thread(this);
        start();
    }

    public static XMPPConnection getConnection() {
        return connection;
    }

    private GTalkConnectionModel loadConnectionModel() throws GTalkServiceException {
        String filePath = Globals.PATH_CFG_PROTOCOL + "gtalk/gtalk.json";
        File file = new File(filePath);
        if (!file.isFile())
            throw new GTalkServiceException("Could not load any configuration! The file " + filePath + " does not exist!");

        Gson gson = new Gson();
        String json = "";
        try {
            json = FileUtils.readFileToString(file.getAbsoluteFile(), "UTF8");
        } catch (IOException ex) {
            throw new GTalkServiceException("Failed to read configuration file: " + file.getAbsolutePath() + "!", ex);
        }

        GTalkConnectionModel model = gson.fromJson(json, GTalkConnectionModel.class);
        return model;
    }

    private void start() {
        this.running = true;
        this.thread.start();
    }

    private void stop() {
        this.running = false;
    }

    private void connect() throws GTalkServiceException {
        ConnectionConfiguration config = new ConnectionConfiguration(this.connectionModel.getHost(),
                this.connectionModel.getPort(), this.connectionModel.getServiceName());
        XMPPConnection connection = new XMPPConnection(config);
        try {
            connection.connect();
            connection.login(this.connectionModel.getUserName(), this.connectionModel.getPassword());
        } catch (XMPPException ex) {
            throw new GTalkServiceException("Failed to connect to GTALK! Cause: " + ex.getMessage(), ex);
        }

        Roster roster = connection.getRoster();
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

        ChatManager chatmanager = connection.getChatManager();
        chatmanager.addChatListener(new GTalkEventListener(connectionModel));
    }

    @Override
    public void run() {
        try {
            connect();
        } catch (GTalkServiceException ex) {
            System.out.println("Receved exception: " + ex.toString());
        }
        while (running) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }
}

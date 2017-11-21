package se.erikwelander.zubat.services.protocols.mumble;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import se.erikwelander.zubat.globals.Globals;
import se.erikwelander.zubat.services.protocols.mumble.exceptions.MumbleServiceException;
import se.erikwelander.zubat.services.protocols.mumble.models.MumbleConnectionModel;
import se.erikwelander.zubat.services.protocols.mumble.protocol.MumbleBot;

import java.io.File;
import java.io.IOException;

public class MumbleService {

    public MumbleService() throws MumbleServiceException {
        MumbleConnectionModel connectionModel[] = loadConnectionModels();
        addBots(connectionModel);
    }

    private void addBots(MumbleConnectionModel connectionModel[]) {
        for (MumbleConnectionModel model : connectionModel) {
            new MumbleBot(model);
        }
    }

    private MumbleConnectionModel[] loadConnectionModels() throws MumbleServiceException {
        String currentPath = Globals.PATH_CFG_PROTOCOL + "mumble/servers";

        File dir = new File(currentPath);
        if (!dir.isDirectory())
            throw new MumbleServiceException("Could not load any configurations! The directory " + currentPath + " does not exist");

        File[] dirFiles = dir.listFiles();
        if (dirFiles.length == 0)
            throw new MumbleServiceException("Could not load any configurations! The directory " + currentPath + " is empty!");

        MumbleConnectionModel[] models = new MumbleConnectionModel[dirFiles.length];
        Gson gson = new Gson();
        for (int i = 0; i < dirFiles.length; i++) {
            String json = null;
            try {
                json = FileUtils.readFileToString(dirFiles[i].getAbsoluteFile(), "UTF8");
            } catch (IOException e) {
                throw new MumbleServiceException("Failed to read configuration file: " + dirFiles[i].getAbsolutePath() + "!");
            }
            models[i] = gson.fromJson(json, MumbleConnectionModel.class);
        }
        return models;
    }
}

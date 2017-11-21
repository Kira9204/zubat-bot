package se.erikwelander.zubat.services.MainService;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import se.erikwelander.zubat.globals.Globals;
import se.erikwelander.zubat.plugins.crosstalk.CrossTalk;
import se.erikwelander.zubat.plugins.crosstalk.exceptions.CrossTalkException;
import se.erikwelander.zubat.plugins.webtitle.WebTitlePlugin;
import se.erikwelander.zubat.services.MainService.exceptions.MainServiceException;
import se.erikwelander.zubat.services.MainService.models.MainServiceModel;
import se.erikwelander.zubat.services.protocols.gtalk.GTalkService;
import se.erikwelander.zubat.services.protocols.irc.IRCService;
import se.erikwelander.zubat.services.protocols.mumble.MumbleService;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MainService {

    public MainService() throws MainServiceException {
        MainServiceModel serviceModel = loadServiceModels();
        Globals.TRIGGER = serviceModel.getTrigger();
        createServices(serviceModel.getEnabledProtocols());
        loadCrossTalks();
        try {
            new WebTitlePlugin();
        } catch (Exception ex) {
        }

        waitForExit();
    }


    private void createServices(String[] serviceNames) {
        for (String serviceName : serviceNames) {
            serviceName = serviceName.toLowerCase();
            switch (serviceName) {
                case Globals.PROTOCOL_IRC:
                    try {
                        new IRCService();
                    } catch (Exception ex) {
                        System.err.println(ex);
                    }
                    break;
                case Globals.PROTOCOL_MUMBLE:
                    try {
                        new MumbleService();
                    } catch (Exception ex) {
                        System.err.println(ex);
                    }
                    break;
                case Globals.PROTOCOL_GTALK:
                    try {
                        new GTalkService();
                    } catch (Exception ex) {
                        System.err.println(ex);
                    }
                    break;
            }
        }
    }

    private MainServiceModel loadServiceModels() throws MainServiceException {
        String filePath = Globals.PATH_CFG + "MainService.json";
        File file = new File(filePath);
        if (!file.isFile())
            throw new MainServiceException("Could not load any configuration! The file " + filePath + " does not exist!");

        Gson gson = new Gson();
        String json = "";
        try {
            json = FileUtils.readFileToString(file.getAbsoluteFile(), "UTF8");
        } catch (IOException ex) {
            throw new MainServiceException("Failed to read configuration file: " + file.getAbsolutePath() + "!", ex);
        }

        MainServiceModel model = gson.fromJson(json, MainServiceModel.class);
        return model;
    }

    private void loadCrossTalks() {
        try {
            CrossTalk.loadCrossTalks();
        } catch (CrossTalkException ex) {
            System.err.println(ex);
        }
    }

    private void waitForExit() {
        System.out.println("Type \"exit\" to close the program...");
        Scanner scanner = new Scanner(System.in);
        while (!scanner.nextLine().equals("exit")) {
        }
        System.out.println("Bye!");
        System.exit(0);
    }
}

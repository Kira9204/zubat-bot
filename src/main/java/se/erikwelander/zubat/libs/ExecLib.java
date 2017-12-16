package se.erikwelander.zubat.libs;

import java.io.IOException;
import java.util.Scanner;

public class ExecLib {
    public static String execCmd(String cmd) throws IOException {
        Process process = Runtime.getRuntime().exec(cmd);
        Scanner scanner = new Scanner(process.getInputStream());
        String output = "";
        while(scanner.hasNextLine()) {
            output += scanner.nextLine()+"\r\n";
        }
        return output;
    }
}

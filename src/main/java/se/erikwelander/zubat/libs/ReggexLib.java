package se.erikwelander.zubat.libs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReggexLib {

    public static boolean match(final String data, final String reggex) {
        Pattern pattern = Pattern.compile(reggex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(data);
        return matcher.find();
    }

    public static String find(final String data, final String reggex, final int group) {
        Pattern pattern = Pattern.compile(reggex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            return matcher.group(group);
        }
        return "";
    }
}

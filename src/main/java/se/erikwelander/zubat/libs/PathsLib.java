package se.erikwelander.zubat.libs;

public class PathsLib {

    public static String getCurrentPath() {
        String path = java.nio.file.Paths.get("").toAbsolutePath().toString();
        path = path.replace('\\', '/'); //Cross platform paths, windows understands this as well.
        return path;
    }
}

/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection;

/**
 * Stores session data.
 */
public class Session {
    private Connection connection;

    private int sessionID;

    private boolean shouldExit;

    private boolean isCryptSetup; // is crypt setup
    private boolean hasReceivedServerSync; // we have received server sync

    private boolean welcomePrinted;

    public Session(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public int getSessionID() {
        return sessionID;
    }

    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    public boolean shouldExit() {
        return shouldExit;
    }

    private boolean isInitialized() {
        return this.isCryptSetup && this.hasReceivedServerSync;
    }

    public boolean isWelcomePrinted() {
        return welcomePrinted;
    }

    public void setWelcomePrinted(boolean welcomePrinted) {
        this.welcomePrinted = welcomePrinted;
    }

    public void setShouldExit(boolean shouldExit) {
        this.shouldExit = shouldExit;
    }

    public void setCryptSetup(boolean cryptSetup) {
        isCryptSetup = cryptSetup;
    }

    public void setHasReceivedServerSync(boolean hasReceivedServerSync) {
        this.hasReceivedServerSync = hasReceivedServerSync;
    }

    public void printHello(String hello) {
        if (this.isWelcomePrinted()) {
            return;
        }

        System.out.println("\nServer says hello, here's the message:\n");
        System.out.println(hello + "\n");
        this.setWelcomePrinted(true);
    }
}

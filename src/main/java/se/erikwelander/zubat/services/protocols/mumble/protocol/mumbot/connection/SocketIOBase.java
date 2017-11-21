/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection;

import java.net.Socket;

/**
 * Base class for SocketReader and SocketWriter. Provides the common loop behavior.
 */
abstract class SocketIOBase implements Runnable {
    private final Socket socket;
    private volatile boolean shouldExit;

    public SocketIOBase(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return this.socket;
    }

    @Override
    public final void run() {
        while (!this.shouldExit) {
            if (this.shouldExit || execute()) {
                break;
            }
        }

        System.out.println("Thread is dying without errors: " + Thread.currentThread().getName());
    }

    /**
     * Executes the Socket IO operation. Implementations either read from or write to sockets.
     *
     * @return true if thread should immediately exit
     */
    protected abstract boolean execute();

    /**
     * Attempts to stop execution by telling the main loop to break.
     */
    public void stop() {
        this.shouldExit = true;
    }
}

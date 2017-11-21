/*
 * mumbot
 * By Kailari (Ilari Tommiska)
 *
 * Copyright (c) 2016. Ilari Tommiska (MIT License)
 */

//See http://www.javased.com/index.php?source_dir=incubator-cordova-android/framework/src/org/apache/cordova/FileTransfer.java
//For socket factory fix.
package se.erikwelander.zubat.services.protocols.mumble.protocol.mumbot.connection;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.security.cert.CertificateException;

/**
 * Helps creating SSLSockets without too much hassle
 */
public class SocketHelper {
    // Create a trust manager that does not validate certificate chains
    private static final TrustManager[] trustAllCerts = new TrustManager[]{new javax.net.ssl.X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }
    }};

    public static Socket createSocket(String hostname, int port) throws IOException {
        SSLSocketFactory factory = createSocketFactory();

        if (factory == null) {
            throw new SocketException("Error creating socket");
        }

        return factory.createSocket(hostname, port);
    }

    private static SSLSocketFactory createSocketFactory() {
        try {
            // Create TLSv1 SSL Context
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLContext.setDefault(sc);

            // Create new socket factory
            return sc.getSocketFactory();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

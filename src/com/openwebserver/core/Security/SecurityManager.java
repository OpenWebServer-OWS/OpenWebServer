package com.openwebserver.core.Security;

import com.openwebserver.core.Connection.ConnectionDescription;
import com.openwebserver.core.Security.SSL.KeyManager;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class SecurityManager {

    private static final SecurityManager manager = new SecurityManager();
    private final ArrayList<ConnectionDescription> descriptions = new ArrayList<>();

    private SecurityManager() {
    }

    public static void registerSocket(ServerSocket serverSocket) throws IOException {
        manager.descriptions.add(new ConnectionDescription(serverSocket));
    }

    public static ServerSocket create(int port, boolean secure) throws IOException {
        ServerSocket serverSocket = null;
        if (secure) {
            try {
                serverSocket = KeyManager.createServerSocket(port);
            } catch (KeyManager.KeyManagerException e) {
                serverSocket = create(80, false);
            }
        } else {
            try {
                serverSocket = new ServerSocket(port);
            } catch (BindException e) {
                System.err.println("Can't bind to port '"+port+"'");
                System.exit(-1);
            }
        }
        registerSocket(serverSocket);
        return serverSocket;
    }

}

package com.openwebserver.core.security;

import com.openwebserver.core.connection.ConnectionDescription;
import com.openwebserver.core.security.SSL.KeyManager;

import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SNIServerName;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class SecurityManager extends SNIMatcher {

    private static final SecurityManager manager = new SecurityManager();
    private final ArrayList<ConnectionDescription> descriptions = new ArrayList<>();
    private final ArrayList<SNIHostName> hosts = new ArrayList<>();

    private SecurityManager() {
        super(1);
    }

    public static void registerSocket(ServerSocket serverSocket) throws IOException {
        manager.descriptions.add(new ConnectionDescription(serverSocket));
    }

    public static void registerHost(String host){
        manager.hosts.add(new SNIHostName(host));
    }

    public static SecurityManager getInstance() {
        return manager;
    }

    public ArrayList<SNIHostName> getHosts() {
        return hosts;
    }

    public ArrayList<SNIServerName> getServerNames() {
        ArrayList<SNIServerName> serverNames = new ArrayList<>();
        for (int i = 0; i < hosts.size(); i++) {
            serverNames.add(new SNIServerName(i, hosts.get(i).getEncoded()) {
                @Override
                public boolean equals(Object other) {
                    return super.equals(other);
                }
            });
        }
        return serverNames;
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

    @Override
    public boolean matches(SNIServerName serverName) {
        return false;
    }
}

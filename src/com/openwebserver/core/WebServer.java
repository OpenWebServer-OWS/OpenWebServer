package com.openwebserver.core;


import FileManager.Folder;
import com.openwebserver.core.connection.Connection;
import com.openwebserver.core.objects.Domain;
import com.openwebserver.core.objects.headers.Header;
import com.openwebserver.core.routing.Router;

import com.openwebserver.core.security.SSL.KeyManager;
import com.openwebserver.core.security.SecurityManager;
import com.tree.TreeArrayList;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;

public class WebServer{

    public static Header serverHeader;
    public static Folder tempFolder;
    public static String rootFilename = "index.html";

    private final TreeArrayList<Integer, com.openwebserver.core.objects.Domain> domains = new TreeArrayList<>();

    static {
        try {
            tempFolder = Folder.Temp("OWS-TEMP-");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean closed = false;

    public WebServer(String name) {
        serverHeader = new Header("Server", name);
    }

    public WebServer()
    {
        this("OpenWebServer");
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public WebServer addDomain(com.openwebserver.core.objects.Domain... domains) {
        try {
            KeyManager.load(domains);
        } catch (KeyManager.KeyManagerException e) {
            e.printStackTrace();
        }
        for (Domain domain : domains) {
            this.domains.addOn(domain.getPort(), domain);
        }
        return this;
    }

    public void start(){
        domains.keySet().forEach(port -> createServerSocket(port,domains.get(port).get(0).isSecure()));
    }

    private void createServerSocket(int port, boolean secure){
        new Thread(() -> {
            ServerSocket ss = null;
            try {
                ss = SecurityManager.create(port, secure);
                do {
                    try {
                        Router.handle(new Connection(ss.accept()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }while (!closed);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    Objects.requireNonNull(ss).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}

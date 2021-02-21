package com.openwebserver.core;

import DevMode.DevConsole;
import DevMode.Label;
import FileManager.Folder;
import Tree.TreeArrayList;
import com.openwebserver.core.Connection.Connection;
import com.openwebserver.core.Objects.Headers.Header;
import com.openwebserver.core.Routing.Router;
import com.openwebserver.core.Security.KeyManager;
import com.openwebserver.core.Security.SecurityManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;

public class WebServer{

    public static DevConsole devConsole = new DevConsole(WebServer.class);

    private final String name;
    public static Header serverHeader;
    public static Folder tempFolder;

    private final TreeArrayList<Integer, Domain> domains = new TreeArrayList<>();

    static {
        try {
            tempFolder = Folder.Temp("OWS-TEMP-");
            devConsole.Log(Label.INFO, "TEMP DIR LOCATION: " + tempFolder.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean closed = false;

    public WebServer(String name) {
        this.name = name;
        serverHeader = new Header("Server", name);
        devConsole.Log(Label.INFO, "name:" + name);
    }

    public WebServer()
    {
        this("OpenWebServer");
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public WebServer addDomain(Domain ... domains) {
        for (Domain domain : domains) {
            this.domains.addOn(domain.getPort(), domain);
        }
        try {
            KeyManager.load(domains);
        } catch (KeyManager.KeyManagerException e) {
            e.printStackTrace();
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
                devConsole.Log(Label.INFO, "New ServerSocket on port '"+port+"' " + (secure? "with SSL" : "without SSL"));
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

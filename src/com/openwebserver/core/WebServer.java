package com.openwebserver.core;


import FileManager.Folder;
import com.openwebserver.core.connection.security.ContextManager;
import com.openwebserver.core.connection.server.SocketManager;
import com.openwebserver.core.http.Header;
import com.openwebserver.core.objects.Domain;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

public class WebServer{

    public static Header serverHeader;
    public static Folder tempFolder;
    public static String rootFilename = "index.html";
    private final ArrayList<Domain> domains = new ArrayList<>();

    static {
        try {
            tempFolder = Folder.Temp("OWS-TEMP-");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WebServer(String name) {
        serverHeader = new Header("Server", name);
    }

    public WebServer()
    {
        this("OpenWebServer");
    }

    public WebServer addDomain(Domain... domains) throws Domain.DomainException {
        for (Domain domain : domains) {
            ContextManager.register(domain);
            SocketManager.require(domain.getPort(), domain.isSecure());
            this.domains.add(domain);
        }
        try {
            ContextManager.init();
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new Domain.DomainException(e);
        }
        return this;
    }

    public ArrayList<Domain> getDomains() {
        return domains;
    }

    public void start(){
        SocketManager.run();
    }

}

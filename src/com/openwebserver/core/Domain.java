package com.openwebserver.core;


import com.openwebserver.core.Handlers.RequestHandler;
import com.openwebserver.core.Routing.Router;
import com.openwebserver.core.Security.SSL.Certificate;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class Domain {

    private final String alias;
    private final boolean secure;
    private final int port;

    private Certificate<X509Certificate> certificate;
    private Certificate<PrivateKey> privateKey;

    public Domain(String alias, int port, boolean secure) {
        this.alias = alias;
        if (port == -1) {
            this.port = secure ? 443 : 80;
        } else {
            this.port = port;
        }

        this.secure = secure;
    }

    public Domain(String alias, boolean secure) {
        this(alias, secure ? 443 : 80, secure);
    }

    public Domain(String url) throws MalformedURLException {
        this(new URL(url).getHost(), new URL(url).getPort(), new URL(url).getProtocol().equals("https"));
    }

    public Domain() {
        this("localhost", 80, false);
    }

    public Domain addHandler(RequestHandler requestHandler) {
        requestHandler.setDomain(this);
        requestHandler.register(handler -> Router.register(this, handler));
        return this;
    }

    public URL getUrl() throws MalformedURLException {
        return new URL(secure ? "https" : "http", alias, port, "");
    }

    public String getProtocol() {
        return secure ? "https" : "http";
    }

    public int getPort() {
        return port;
    }

    public boolean isSecure() {
        return secure;
    }

    public String getAlias() {
        return this.alias;
    }

    public Domain setCertificates(Certificate<X509Certificate> certificate, Certificate<PrivateKey> privateKey){
        this.certificate = certificate;
        this.privateKey = privateKey;
        return this;
    }

    public Certificate<X509Certificate> getCertificate() {
        return certificate;
    }

    public Certificate<PrivateKey> getPrivateKey() {
        return privateKey;
    }

    @Override
    public String toString() {
        return alias;
    }

}

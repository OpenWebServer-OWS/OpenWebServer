package com.openwebserver.core.objects;


import com.openwebserver.core.connection.security.Certificate;
import com.openwebserver.core.connection.security.ContextProvider;
import com.openwebserver.core.handlers.RequestHandler;
import com.openwebserver.core.routing.Router;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class Domain extends ContextProvider {

    private final int port;

    public Domain(String alias, int port, boolean secure) {
        super(alias);
        if (port == -1) {
            this.port = secure ? 443 : 80;
        } else {
            this.port = port;
        }
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
        return new URL(getProtocol(), getAlias(), port, "");
    }

    public String getProtocol() {
        return isSecure() ? "https" : "http";
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return getAlias();
    }

    public Domain setCertificates(Certificate<X509Certificate> certificate, Certificate<PrivateKey> privateKeyCertificate){
        setPrivateKey(privateKeyCertificate);
        setCertificate(certificate);
        return this;
    }

    public Domain setCertificates(String certificatePath, String privateKeyPath){
        return setCertificates(new Certificate<>(certificatePath), new Certificate<>(privateKeyPath));
    }

    public static class DomainException extends Throwable{

        public DomainException(Throwable t){
            super(t);
        }

    }

}

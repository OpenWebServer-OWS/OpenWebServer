package com.openwebserver.core.connection.security;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public abstract class ContextProvider {

    private String alias;
    private Certificate<X509Certificate> certificate;
    private Certificate<PrivateKey> privateKey;

    public ContextProvider(String alias, Certificate<X509Certificate> certificate, Certificate<PrivateKey> privateKey){
        this.alias = alias;
        this.certificate = certificate;
        this.privateKey = privateKey;
        ContextManager.register(this);
    }

    public ContextProvider(String alias){
        this(alias, null, null);
    }

    protected ContextProvider(){}

    protected void setAlias(String alias){
        this.alias = alias;
    }

    public void setPrivateKey(Certificate<PrivateKey> privateKey) {
        this.privateKey = privateKey;
    }

    public void setCertificate(Certificate<X509Certificate> certificate) {
        this.certificate = certificate;
    }

    public boolean isSecure(){
        return certificate != null && privateKey != null;
    }

    public String getAlias(){
        return alias;
    }

    public Certificate<X509Certificate> getCertificate(){
        return certificate;
    }

    public Certificate<PrivateKey> getPrivateKey(){
        return privateKey;
    }

    @Override
    public String toString() {
        return alias;
    }
}

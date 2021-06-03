package com.openwebserver.core.connection.security;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.UUID;

public class ContextManager implements X509KeyManager {

    public final static String StoreType = "PKCS12";
    private static ContextManager context;

    static {
        try {
            context = new ContextManager();
        } catch (KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException | CertificateException | IOException e) {
            e.printStackTrace();
        }
    }

    private final KeyStore keyStore;
    private final String password;
    private X509KeyManager defaultKeyManager;

    private ContextManager(KeyStore keyStore, String password) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, IOException {
        this.keyStore = keyStore;
        this.password = password;
        keyStore.load(null);
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password.toCharArray());
        for (KeyManager keyManager : keyManagerFactory.getKeyManagers()) {
            if (keyManager instanceof X509KeyManager) {
                this.defaultKeyManager = (X509KeyManager) keyManager;
                break;
            }
        }
    }

    private ContextManager() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, IOException {
        this(KeyStore.getInstance(StoreType), UUID.randomUUID().toString());
    }

    public static void reInit(KeyStore keyStore, String password) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        context = new ContextManager(keyStore, password);
    }

    public static void register(ContextProvider contextProvider) {
        if(contextProvider.isSecure()) {
            ContextManager context = getInstance();
            try {
                context.keyStore.setCertificateEntry(contextProvider.getAlias(), contextProvider.getCertificate().get());
                context.keyStore.setKeyEntry(contextProvider.getAlias(), contextProvider.getPrivateKey().get(), context.password.toCharArray(), new X509Certificate[]{contextProvider.getCertificate().get()});
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
        }
    }

    public static ContextManager getInstance(){
        return context;
    }

    public static SSLContext generate() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = SSLContext.getInstance("SSL");
        context.init(new X509KeyManager[]{getInstance()}, null, null);
        return context;
    }

    //region X509KeyManager implementations
    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        return defaultKeyManager.chooseClientAlias(keyType, issuers, socket);
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return defaultKeyManager.getClientAliases(keyType, issuers);
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return defaultKeyManager.getServerAliases(keyType, issuers);
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        if (socket instanceof SSLSocket) {
            ExtendedSSLSession session = ((ExtendedSSLSession)((SSLSocket)socket).getHandshakeSession());
            return new String(session.getRequestedServerNames().get(0).getEncoded(), Charset.defaultCharset());
        }
        return null;
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return defaultKeyManager.getCertificateChain(alias);
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        return defaultKeyManager.getPrivateKey(alias);
    }
    //endregion
}

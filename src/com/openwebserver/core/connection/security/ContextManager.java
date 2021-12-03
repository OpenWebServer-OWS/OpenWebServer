package com.openwebserver.core.connection.security;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.UUID;

public class ContextManager implements X509KeyManager {

    public final static String StoreType = "PKCS12";
    private static final ContextManager manager;
    private static final HashMap<String, ContextProvider> contextProviders = new HashMap<>();

    private KeyStore store;
    private X509KeyManager defaultKeyManager;

    static {
        manager = new ContextManager();
    }



    private ContextManager(){}

    public static void use(KeyStore keyStore, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        for (javax.net.ssl.KeyManager keyManager : keyManagerFactory.getKeyManagers()) {
            if (keyManager instanceof X509KeyManager) {
                manager.defaultKeyManager = (X509KeyManager) keyManager;
                break;
            }
        }
        manager.store = keyStore;
    }

    public static <T extends ContextProvider> void register(T contextProvider){
        if(contextProvider.isSecure()) {
            contextProviders.put(contextProvider.getAlias(), contextProvider);
        }
    }

    public static SSLContext createContext() throws KeyManagerException {
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(new X509KeyManager[]{manager}, null, null);
            return context;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw KeyManagerException.wrap(e);
        }
    }

    public static ServerSocket createServerSocket(int port) throws KeyManagerException {
        try {
            return createContext().getServerSocketFactory().createServerSocket(port);
        } catch (IOException e) {
            throw KeyManagerException.wrap(e);
        }
    }

    public static void init() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance(StoreType);
        keyStore.load(null);
        char[] password = UUID.randomUUID().toString().toCharArray();
        contextProviders.forEach((String alias, ContextProvider provider) ->{
            try {
                keyStore.setCertificateEntry(alias, provider.getCertificate().get());
                keyStore.setKeyEntry(alias, provider.getPrivateKey().get(), password, new X509Certificate[]{provider.getCertificate().get()});
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
        });
        use(keyStore, password);
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

    public static class KeyManagerException extends Throwable {

        public KeyManagerException(Throwable t) {
            super(t);
        }

        public static KeyManagerException wrap(Throwable t) {
            return new KeyManagerException(t);
        }

    }
    //endregion
}


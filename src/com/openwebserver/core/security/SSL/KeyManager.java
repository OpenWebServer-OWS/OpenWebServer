package com.openwebserver.core.security.SSL;

import FileManager.Local;
import com.openwebserver.core.objects.Domain;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.UUID;

public class KeyManager implements X509KeyManager {

    public final static String StoreType = "PKCS12";
    private static KeyManager manager;
    private static KeyStore store = null;
    private X509KeyManager defaultKeyManager;

    private KeyManager(KeyStore keyStore, String password) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password.toCharArray());
        for (javax.net.ssl.KeyManager keyManager : keyManagerFactory.getKeyManagers()) {
            if (keyManager instanceof X509KeyManager) {
                this.defaultKeyManager = (X509KeyManager) keyManager;
                break;
            }
        }
        store = keyStore;
        manager = this;
    }

    public static boolean loaded() {
        return store != null;
    }

    public static KeyManager External(Local store, String password) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        return new KeyManager(KeyStore.getInstance(store.getFile(), password.toCharArray()), password);
    }

    public static void load(Domain... domains) throws KeyManagerException {
        try {
            KeyStore store = KeyStore.getInstance(StoreType);
            store.load(null);
            String password = UUID.randomUUID().toString();
            for (Domain domain : domains) {
                if (domain.isSecure()) {
                    store.setCertificateEntry(domain.getAlias(), domain.getCertificate().get());
                    store.setKeyEntry(domain.getAlias(), domain.getPrivateKey().get(), password.toCharArray(), new X509Certificate[]{domain.getCertificate().get()});
                }
            }
            new KeyManager(store, password);
        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw KeyManagerException.wrap(e);
        }
    }

    public static SSLContext createContext() throws KeyManagerException {
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(new X509KeyManager[]{manager}, null, null);
            return context;
        } catch (Throwable e) {
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

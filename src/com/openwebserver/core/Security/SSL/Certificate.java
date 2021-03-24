package com.openwebserver.core.Security.SSL;

import ByteReader.ByteReader;
import FileManager.Local;


import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class Certificate<T>{

    private byte[] encoded;
    private Type type;

    private T key;

    public Certificate(Local l){
        deserialize(l);
    }

    public Certificate(String path){
        this(new Local(path));
    }

    public T deserialize(Local key) {
        try {
            this.encoded = key.read();
            this.type = getType();
            this.key = createKey((Class<T>) type.returnClass);
            return this.key;
        } catch (IOException | CertificateException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    public T get() {
        return key;
    }

    public Type getType() {
        return isPrivateKey() ? Type.PRIVATE : Type.PUBLIC;
    }

    public boolean isPrivateKey() {
        return new String(encoded).contains(Type.PRIVATE.begin);
    }

    public boolean isPublicKey() {
        return new String(encoded).contains(Type.PUBLIC.begin);
    }

    public T createKey(Class<T> tClass) throws NoSuchAlgorithmException, InvalidKeySpecException, CertificateException {
        switch (type) {
            case PRIVATE: {
                String data = new String(encoded);
                String[] tokens = data.split(Type.PRIVATE.begin);
                tokens = tokens[1].split(Type.PRIVATE.end);
                return (T) KeyFactory.getInstance(Type.PRIVATE.algorithm).generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(tokens[0].replaceAll("\n", ""))));
            }
            case PUBLIC: {
                return (T) CertificateFactory.getInstance("X.509").generateCertificate(new ByteReader(encoded).toInputStream());
            }
        }
        return null;
    }

    public enum Type {
        PRIVATE(PrivateKey.class, "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----", "RSA"),
        PUBLIC(X509Certificate.class, "-----BEGIN CERTIFICATE-----", "-----END CERTIFICATE-----", null);

        private final Class<?> returnClass;
        private final String begin;
        private final String end;
        private final String algorithm;

        Type(Class<?> returnClass, String begin, String end, String algorithm) {
            this.returnClass = returnClass;
            this.begin = begin;
            this.end = end;
            this.algorithm = algorithm;
        }
    }
}

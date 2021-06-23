package com.openwebserver.core.connection.client.utils;



import nl.lownative.bytes.Bytes;

import java.io.IOException;
import java.io.InputStream;

public interface SocketReader {

    InputStream getInputStream() throws IOException;

    void close() throws IOException;

    default Bytes readAll() throws ConnectionReaderException {
        Bytes bytes = new Bytes();
        try {
            bytes.doWhile(() -> {
                try {
                    return (byte) getInputStream().read();
                } catch (IOException e) {
                    throw new PrematureStreamException();
                }
            }, () -> getInputStream().available() > 0);
        } catch (Throwable throwable) {
            if(throwable instanceof PrematureStreamException){
                throw (PrematureStreamException)throwable;
            }else{
                throw new ConnectionReaderException(throwable);
            }
        }
        return bytes;
    }

    default Bytes readUntil(byte[] sequence) throws ConnectionReaderException {
        Bytes bytes = new Bytes();
        try {
            bytes.doWhile(() -> {
                try {
                    return (byte) getInputStream().read();
                } catch (IOException e) {
                    throw new PrematureStreamException();
                }
            }, () -> getInputStream().available() > 0 && !bytes.endsWith(sequence));
        } catch (Throwable throwable) {
            if(throwable instanceof PrematureStreamException){
                throw (PrematureStreamException)throwable;
            }else{
                throw new ConnectionReaderException(throwable);
            }
        }
        if(bytes.get(0)  != -1){
            return bytes;
        }else{
            throw new ConnectionReaderException("Invalid bytes value read from stream");
        }
    }



    default Bytes readFor(int size) throws PrematureStreamException {
        Bytes bytes = new Bytes(size);
        for (int i = 0; i < size; i++) {
            try {
                bytes.add((byte) getInputStream().read());
            } catch (IOException e) {
                throw new PrematureStreamException();
            }
        }
        return bytes;
    }

    class ConnectionReaderException extends Throwable{
        public ConnectionReaderException(String message){
            super(message);
        }

        public ConnectionReaderException(Throwable throwable) {
            super(throwable);
        }
    }

    class PrematureStreamException extends ConnectionReaderException {
        public PrematureStreamException() {
            this("Can't read from stream, stream not mature");
        }

        public PrematureStreamException(String message) {
            super(message);
        }
    }


}

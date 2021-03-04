package com.openwebserver.core.Connection;

import com.openwebserver.core.Objects.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.function.BiConsumer;

public interface ConnectionWriter {

    Connection getConnection();

    default void write(byte... data){
        if(getConnection().isConnected()){
            try {
                getConnection().getOutputStream().write(data);
            } catch (IOException e) {
                close();
            }
        }
    }

    default OutputStream getOutputStream() throws IOException {
        return getConnection().getSocket().getOutputStream();
    }

    default InputStream getInputStream() throws IOException {
        return getConnection().getSocket().getInputStream();
    }

    default void write(String s) {
        write(s.getBytes(Charset.defaultCharset()));
    }

    default void write(ConnectionContent ... content) {
        for (ConnectionContent connectionContent : content) {
            write(connectionContent.get());
        }
        close();
    }

    default void write(ConnectionContent content){
        write(content.get());
        try {
            flush();
            if(!(content instanceof HandOverObject)){
                close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    default void writeOpen(ConnectionContent content) throws IOException {
        write(content.get());
        flush();
    }

    default void flush() throws IOException{
        getOutputStream().flush();
    }

    default void close() {
        getConnection().close();
    }

    default void handle(BiConsumer<Connection, Object[]> writer, Object ... args){
        getConnection().handler = new Thread(() -> writer.accept(getConnection(), args));
        getConnection().handler.start();
    }

    default HandOverObject HandOff(BiConsumer<Connection, Object[]> writer, Object ... args){
        handle(writer, args);
        return new HandOverObject();
    }

    class HandOverObject extends Response {

        public HandOverObject() {
            super();
        }

        @Override
        public byte[] get() {
            return new byte[0];
        }
    }

}

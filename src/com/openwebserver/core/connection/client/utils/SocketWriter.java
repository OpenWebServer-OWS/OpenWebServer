package com.openwebserver.core.connection.client.utils;

import com.openwebserver.core.connection.client.Connection;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public interface SocketWriter {

    OutputStream getOutputStream() throws IOException;

    default void write(byte... data) throws IOException {
        try {
            getOutputStream().write(data);
        } catch (IOException e) {
            getOutputStream().close();
        }
    }

    default void write(String s) throws IOException {
        write(s.getBytes(Charset.defaultCharset()));
    }

    default void tryWrite(SocketContent content){
        try {
            write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    default void write(SocketContent... content) throws IOException {
        boolean close = true;
        for (SocketContent connectionContent : content) {
            if(connectionContent instanceof Connection.HandOver) {
                close = false;
            }else{
                write(connectionContent.get());
            }
        }
        flush();
        if(close) {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Connection handed over");
        }
    }

    default void writeOpen(SocketContent content) throws IOException {
        write(content.get());
        flush();
    }

    default void flush() throws IOException {
        getOutputStream().flush();
    }

    void close() throws IOException;

}

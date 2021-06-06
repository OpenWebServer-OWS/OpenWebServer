package com.openwebserver.core.connection.client;

import com.openwebserver.core.connection.ConnectionManager;


import com.openwebserver.core.connection.client.utils.SocketWrapper;
import com.openwebserver.core.objects.Response;

import java.io.IOException;
import java.net.Socket;
import java.util.function.BiConsumer;

public class Connection extends SocketWrapper {

    private Thread handler;

    public Connection(Socket socket){
        super(socket);
        ConnectionManager.register(this);
    }

    public Thread getHandler() {
        return handler;
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            ConnectionManager.close(this);
        }

    }

    public boolean isConnected(){
        return getSocket().isConnected();
    }

    @Override
    public String toString() {
        return getAddress() + ":" + getPort();
    }

    public void handle(BiConsumer<Connection, Object[]> writer, Object ... args){
        handler = new Thread(() -> writer.accept(this, args));
        handler.start();
    }

    public HandOver HandOff(BiConsumer<Connection, Object[]> writer, Object... args){
        handle(writer, args);
        return new HandOver();
    }

    public static class HandOver extends Response {
    }

}

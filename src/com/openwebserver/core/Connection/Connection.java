package com.openwebserver.core.Connection;

import ByteReader.ByteReader;

import java.io.IOException;
import java.net.Socket;

public class Connection extends ByteReader implements ConnectionWriter {

    protected Thread handler;
    private final Socket socket;

    public Connection(Socket socket) throws IOException {
        super(socket.getInputStream());
        this.socket = socket;
        ConnectionManager.register(this);
    }

    public void close(){
        if(!socket.isClosed()){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ConnectionManager.close(this);
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public Socket getSocket() {
        return socket;
    }

    public String getAddress(){
        return getSocket().getInetAddress().toString();
    }

    public int getPort(){
        return getSocket().getPort();
    }

    public int getLocalPort(){
        return getSocket().getLocalPort();
    }

    public String getConnectionString() {
        return getAddress() + ":"+ getPort();
    }

    @Override
    public Connection getConnection() {
        return this;
    }

}
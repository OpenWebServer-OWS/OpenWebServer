package com.openwebserver.core.connection.server.utils;

import com.openwebserver.core.connection.client.Connection;
import com.openwebserver.core.connection.security.ContextManager;
import com.openwebserver.core.connection.server.SocketManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public abstract class ServerImplantation extends Thread{

    private final int port;
    private boolean secure;
    private volatile boolean closed = false;

    ServerSocket serverSocket = null;


    public ServerImplantation(int port, boolean secure){
        SocketManager.checkPort(port);
        this.port = port;
        this.secure = secure;
    }

    public void setup() throws NoSuchAlgorithmException, KeyManagementException, IOException, ContextManager.KeyManagerException {
        if(secure){
            this.serverSocket = ContextManager.createServerSocket(port);
        }else{
            this.serverSocket = new ServerSocket(port);
        }
    }

    public void enableSSL(boolean secure){
        this.secure = secure;
    }

    public int getPort() {
        return port;
    }

    public boolean isSecure() {
        return secure;
    }

    public abstract void onSetup();

    public abstract void onConnection(Connection connection);

    public void halt(){
        closed = true;
    }

    @Override
    public synchronized void start() {
        if(!closed){
            super.start();
        }
    }

    @Override
    public void run() {
        try {
            setup();
            do {
                Socket s = serverSocket.accept();
                onConnection(new Connection(s));

            }while (!closed);
        } catch (NoSuchAlgorithmException | KeyManagementException | IOException | ContextManager.KeyManagerException e) {
            e.printStackTrace();
        }
    }

}

package com.openwebserver.core.connection.server.utils;

import com.openwebserver.core.connection.client.Connection;
import com.openwebserver.core.connection.security.ContextManager;
import com.openwebserver.core.connection.server.SocketManager;
import com.openwebserver.core.routing.Router;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

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

    public void setup() throws NoSuchAlgorithmException, KeyManagementException, IOException {
        if(secure){
            this.serverSocket = ContextManager.generate().getServerSocketFactory().createServerSocket(port);
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
        } catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
            e.printStackTrace();
        }
    }

}

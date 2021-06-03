package com.openwebserver.core.connection.client.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketWrapper implements SocketWriter, SocketReader {

    private final Socket socket;

    public SocketWrapper(Socket socket){
        this.socket = socket;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    public int getPort(){
        return socket.getPort();
    }

    public int getLocalPort(){
        return socket.getLocalPort();
    }

    public String getAddress(){
        return socket.getInetAddress().toString();
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

}

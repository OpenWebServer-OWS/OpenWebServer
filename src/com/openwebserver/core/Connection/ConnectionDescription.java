package com.openwebserver.core.Connection;

import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;

public class ConnectionDescription {

    private String[] supportedProtocols;
    private boolean wantClientAuth;
    private boolean useClientMode;
    private SSLParameters sslParameters;
    private boolean needClientAuth;
    private boolean enableSessionCreation;
    private String[] enabledProtocols;
    private String[] supportedCipherSuites;
    private String[] enabledCipherSuites;
    private boolean ssl = false;
    private final ServerSocketChannel channel;
    private final InetAddress inetAddress;
    private final int localPort;
    private final SocketAddress localSocketAddress;
    private int receiveBufSize;
    private boolean reuseAddresses;
    private int timeOut;
    private boolean bound;
    private boolean closed;

    public ConnectionDescription(ServerSocket serverSocket) throws IOException {
        if(serverSocket instanceof SSLServerSocket){
            SSLServerSocket sslServerSocket = (SSLServerSocket) serverSocket;
            enabledCipherSuites = sslServerSocket.getEnabledCipherSuites();
            supportedCipherSuites = sslServerSocket.getSupportedCipherSuites();
            enabledProtocols = sslServerSocket.getEnabledProtocols();
            enableSessionCreation = sslServerSocket.getEnableSessionCreation();
            needClientAuth = sslServerSocket.getNeedClientAuth();
            sslParameters = sslServerSocket.getSSLParameters();
            useClientMode = sslServerSocket.getUseClientMode();
            wantClientAuth = sslServerSocket.getWantClientAuth();
            supportedProtocols = sslServerSocket.getSupportedProtocols();
            this.ssl = true;
        }

        this.channel = serverSocket.getChannel();
        this.inetAddress = serverSocket.getInetAddress();
        this.localPort = serverSocket.getLocalPort();
        this.localSocketAddress = serverSocket.getLocalSocketAddress();
        this.receiveBufSize = serverSocket.getReceiveBufferSize();
        this.reuseAddresses = serverSocket.getReuseAddress();
        this.timeOut = serverSocket.getSoTimeout();
        this.bound = serverSocket.isBound();
        this.closed = serverSocket.isClosed();
    }
}

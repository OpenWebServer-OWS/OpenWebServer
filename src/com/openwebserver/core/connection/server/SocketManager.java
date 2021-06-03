package com.openwebserver.core.connection.server;

import com.openwebserver.core.connection.server.utils.ServerImplantation;

import java.util.HashMap;

public class SocketManager {

    private static final SocketManager manager = new SocketManager();
    private final HashMap<Integer, ServerSocket> socketMap = new HashMap<>();

    private SocketManager(){}

    public static SocketManager getInstance(){
        return manager;
    }

    public static void run() {
        getInstance().socketMap.values().forEach(ServerImplantation::start);
    }

    public static void stop(){
        getInstance().socketMap.values().forEach(ServerImplantation::halt);
    }

    public static void checkPort(int port){
        if (port < 0 || port > 0xFFFF)
            throw new IllegalArgumentException(
                    "Port value out of range: " + port);
    }

    public static void require(int port, boolean secure){
        if(!getInstance().socketMap.containsKey(port)){
            getInstance().socketMap.put(port, new ServerSocket(port, secure));
        }
    }

}

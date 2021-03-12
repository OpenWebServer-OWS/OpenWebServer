package com.openwebserver.core.Connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.function.BiConsumer;


public class ConnectionManager{

    public enum Access{
        WRITER(ConnectionWriter.class),
        OUTPUTSTREAM(OutputStream.class),
        INPUTSTREAM(InputStream.class),
        SOCKET(Socket.class),
        CONNECTION(Connection.class);

        private final Class<?> returnType;

        Access(Class<?> type) {
            returnType = type;
        }

        public Class<?> getReturnType() {
            return returnType;
        }

        public static Access Match(Class<?> requested) throws ConnectionManagerException {
            for (Access constant : Access.class.getEnumConstants()) {
                if(constant.getReturnType().equals(requested)){
                    return constant;
                }
            }
            throw new ConnectionManagerException("Can't match requested access type");
        }
    }

    private static final ConnectionManager instance = new ConnectionManager();

    //region instance fields
    private int counter;
    private final HashMap<String, Connection> connectionMap = new HashMap<>();
    //endregion

    private ConnectionManager(){}

    public static void HandOver(String connectionId, BiConsumer<Connection, Object[]> writer) {
        getInstance().connectionMap.get(connectionId).HandOff(writer);
    }

    public static ConnectionManager getInstance() {
        return instance;
    }

    public static <T> T Access(String connectionId, Class<T> type) throws ConnectionManagerException, IOException {
        Connection c = getInstance().connectionMap.getOrDefault(connectionId, null);
        Access access = Access.Match(type);
        if(c == null){
            throw new ConnectionManagerException("Can't find connection");
        }
        switch (access){
            case WRITER:
            case CONNECTION:{
                return (T) c;
            }
            case SOCKET:{
                return (T) c.getSocket();
            }
            case INPUTSTREAM:{
                return (T) c.getSocket().getInputStream();
            }
            case OUTPUTSTREAM:{
                return (T) c.getSocket().getOutputStream();
            }
        }
        return null;
    }

    public static Connection register(Connection c) {
        getInstance().connectionMap.put(c.getConnectionString(), c);
        getInstance().counter++;
        return c;
    }

    public static void close(Connection connection) {
        getInstance().connectionMap.remove(connection.getConnectionString());
    }

    public static class ConnectionManagerException extends Throwable {
        public ConnectionManagerException(String message) {
            super(message);
        }
    }
}

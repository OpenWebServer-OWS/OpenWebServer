package com.openwebserver.core.connection;



import com.openwebserver.core.connection.client.Connection;
import com.openwebserver.core.connection.client.utils.SocketReader;
import com.openwebserver.core.connection.client.utils.SocketWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;


public class ConnectionManager{

    public enum Access{
        WRITER(SocketWriter.class),
        READER(SocketReader.class),
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

    public static ConnectionManager getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    public static <T> T Access(String connectionId, Class<T> type) throws ConnectionManagerException, IOException {
        Connection c = getInstance().connectionMap.getOrDefault(connectionId, null);
        Access access = Access.Match(type);
        if(c == null){
            throw new ConnectionManagerException("Can't find connection");
        }
        switch (access) {
            case WRITER, CONNECTION -> {
                return (T) c;
            }
            case SOCKET -> {
                return (T) c.getSocket();
            }
            case INPUTSTREAM -> {
                return (T) c.getSocket().getInputStream();
            }
            case OUTPUTSTREAM -> {
                return (T) c.getSocket().getOutputStream();
            }
        }
        return null;
    }

    public static Connection register(Connection c) {
        getInstance().connectionMap.put(c.toString(), c);
        getInstance().counter++;
        return c;
    }

    public static void close(Connection connection) {
        getInstance().connectionMap.remove(connection.toString());
    }

    public static class ConnectionManagerException extends Throwable {
        public ConnectionManagerException(String message) {
            super(message);
        }
    }
}

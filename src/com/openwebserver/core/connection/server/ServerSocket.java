package com.openwebserver.core.connection.server;

import com.openwebserver.core.connection.client.Connection;
import com.openwebserver.core.connection.server.utils.ServerImplantation;
import com.openwebserver.core.routing.Router;

public class ServerSocket extends ServerImplantation {

    public ServerSocket(int port, boolean secure) {
        super(port, secure);
    }

    @Override
    public void onConnection(Connection connection) {
        Router.handle(connection);
    }
}

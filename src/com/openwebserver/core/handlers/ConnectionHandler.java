package com.openwebserver.core.handlers;

import com.openwebserver.core.connection.client.utils.SocketContent;
import com.openwebserver.core.objects.Request;

public interface ConnectionHandler<T extends SocketContent> {

    T handle(Request request);

}

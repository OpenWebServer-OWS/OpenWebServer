package com.openwebserver.core.handlers;

import java.util.function.Consumer;

public interface RouteRegister {

    void register(Consumer<RequestHandler> routeConsumer);

}

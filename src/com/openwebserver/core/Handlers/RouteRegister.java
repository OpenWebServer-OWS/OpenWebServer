package com.openwebserver.core.Handlers;

import java.util.function.Consumer;

public interface RouteRegister {

    void register(Consumer<RequestHandler> routeConsumer);

}

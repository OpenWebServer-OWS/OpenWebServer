package com.openwebserver.core.Handlers;

import com.openwebserver.core.Routing.Route;

import java.util.function.Consumer;

public interface RouteRegister {

    void register(Consumer<RequestHandler> routeConsumer);

}

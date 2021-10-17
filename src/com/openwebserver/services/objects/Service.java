package com.openwebserver.services.objects;


import com.openwebserver.core.handlers.RequestHandler;
import com.openwebserver.core.routing.Route;
import com.openwebserver.core.security.authorization.Authorizer;
import com.openwebserver.services.ServiceManager;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Arrays.stream;


public class Service extends RequestHandler implements Consumer<RequestHandler> {

    private final String name;
    private final ArrayList<RequestHandler> routes = new ArrayList<>();

    public Service(String path){
        this(null, path);
    }

    public Service(String name,String path){
        super(new Route(path, Route.Method.UNDEFINED),null);
        this.name = Objects.requireNonNullElseGet(name, () -> getClass().getSimpleName());
        //region create route annotations
        stream(this.getClass().getDeclaredMethods()).filter(method -> method.isAnnotationPresent(com.openwebserver.services.annotations.Route.class)).forEach(method -> RequestHandler.wrap(method, this));
        //endregion
        ServiceManager.register(this);
    }

    public <T extends Service> Service allowPrivateAccess(Supplier<Class<T>> privateSupplier){
        if(privateSupplier != null){
            stream(privateSupplier.get().getDeclaredMethods()).filter(method -> method.isAnnotationPresent(com.openwebserver.services.annotations.Route.class)).forEach(method -> RequestHandler.wrap(method, this));
        }
        return this;
    }

    public String getName() {
        return name;
    }

    public void add(RequestHandler requestHandler){
        requestHandler.addPrefix(this);
        requestHandler.register(routes::add);
    }

    public static <T extends Service> T getService(Class<T> serviceClass) throws ServiceManager.ServiceManagerException {
        return ServiceManager.getService(serviceClass);
    }

    @Override
    public void register(Consumer<RequestHandler> routeConsumer) {
        routes.forEach(handler -> handler.register(routeConsumer));
    }

    @Override
    public void setAuthorizer(Authorizer<?> authorizer) {
        super.setAuthorizer(authorizer);
        routes.forEach(handler -> handler.setAuthorizer(authorizer));
    }

    @Override
    public void addPrefix(Route notation) {
        addPrefix(notation.getPath());
    }

    @Override
    public void addPrefix(String prefix) {
        super.addPrefix(prefix);
        routes.forEach(handler -> handler.addPrefix(prefix));
    }

    @Override
    public void accept(RequestHandler handler) {
        handler.setDomain(this.getDomain());
    }
}

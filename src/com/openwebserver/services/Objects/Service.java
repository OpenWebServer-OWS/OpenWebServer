package com.openwebserver.services.Objects;


import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Handlers.RequestHandler;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.core.Routing.Route;
import com.openwebserver.core.Security.Authorization.Authorizer;
import com.openwebserver.core.WebException;
import com.openwebserver.services.ServiceManager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;


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
        for (java.lang.reflect.Method method : this.getClass().getDeclaredMethods()) {
            if(method.isAnnotationPresent(com.openwebserver.services.Annotations.Route.class)){
                RequestHandler requestHandler = new RequestHandler(new Route(method.getAnnotation(com.openwebserver.services.Annotations.Route.class)), request -> {
                    try {
                        return ((Response) method.invoke(Service.this, request));
                    } catch (InvocationTargetException e) {
                        throw new WebException(e).addRequest(request);
                    }
                });
                requestHandler.setReflection(method);
                requestHandler.setOnRegisterListener(this);
                setSessionHandler(this.getSessionHandler());
                requestHandler.addPrefix(this);
                routes.add(requestHandler);
            }
        }
        ServiceManager.register(this);

        //endregion
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
        routes.forEach(handler -> {
            handler.register(routeConsumer);
        });
    }

    @Override
    public void setAuthorizer(Authorizer<?> authorizer) {
        super.setAuthorizer(authorizer);
        routes.forEach(handler -> {
            handler.setAuthorizer(authorizer);
        });
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

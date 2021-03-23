package com.openwebserver.services.Objects;


import com.openwebserver.core.Annotations.Session;
import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Handlers.RequestHandler;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.core.Routing.Route;
import com.openwebserver.core.Security.Authorization.Authorize;
import com.openwebserver.core.Security.Authorization.Authorizer;
import com.openwebserver.core.Security.CORS.CORS;
import com.openwebserver.core.WebException;
import com.openwebserver.services.ServiceManager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;


public class Service extends RequestHandler {

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
                requestHandler.setSessionSpecification(method.isAnnotationPresent(Session.class)? method.getAnnotation(Session.class): null);
                requestHandler.setSessionHandler(this.getSessionHandler());
                requestHandler.setCORSPolicy(method.isAnnotationPresent(CORS.class)? method.getAnnotation(CORS.class): null);
                requestHandler.setNeedsAuthentication(method.isAnnotationPresent(Authorize.class));
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

    @Override
    public void register(Consumer<RequestHandler> routeConsumer) {
        routes.forEach(handler -> {
            handler.register(routeConsumer);
        });
    }

    @Override
    public void setAuthorizer(Authorizer authorizer) {
        super.setAuthorizer(authorizer);
        routes.forEach(handler -> {
            handler.setAuthorizer(authorizer);
        });
    }

    public void add(RequestHandler requestHandler){
        requestHandler.addPrefix(this);
        requestHandler.register(routes::add);
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

    public static <T> T getService(Class<T> serviceClass) throws ServiceManager.ServiceManagerException {
        return ServiceManager.getService(serviceClass);
    }

    public static class ServiceException extends WebException{

        public ServiceException(String message) {
            super(Code.Service_Unavailable, message);
        }

        public static class NotFoundException extends ServiceException{

            public NotFoundException() {
                super("Service not found");
            }
        }

    }

}

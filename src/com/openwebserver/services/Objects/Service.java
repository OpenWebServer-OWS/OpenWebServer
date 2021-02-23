package com.openwebserver.services.Objects;


import com.openwebserver.core.Annotations.Session;
import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Handlers.ContentHandler;
import com.openwebserver.core.Handlers.RequestHandler;
import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.core.Routing.Route;

import com.openwebserver.core.Security.CORS.CORS;
import com.openwebserver.core.WebException;
import com.openwebserver.services.ServiceManager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

import static Collective.Collective.doForEach;

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
        doForEach(this.getClass().getDeclaredMethods(), (method -> method.isAnnotationPresent(com.openwebserver.services.Annotations.Route.class)), method ->{
            RequestHandler requestHandler = new RequestHandler(new Route(method.getAnnotation(com.openwebserver.services.Annotations.Route.class)),new ContentHandler() {
                @Override
                public Response respond(Request request) throws Throwable {
                    try {
                        return ((Response) method.invoke(Service.this, request));
                    } catch (InvocationTargetException e) {
                        throw new WebException(e).addRequest(request);
                    }
                }
            });
            requestHandler.setSessionSpecification(method.isAnnotationPresent(Session.class)? method.getAnnotation(Session.class): null);
            requestHandler.setSessionHandler(this.getSessionHandler());
            requestHandler.setCORSPolicy(method.isAnnotationPresent(CORS.class)? method.getAnnotation(CORS.class).value(): null);
            requestHandler.addPrefix(this);
            routes.add(requestHandler);
        });
        ServiceManager.register(this);
        //endregion
    }

    public String getName() {
        return name;
    }

    @Override
    public void register(Consumer<RequestHandler> routeConsumer) {
        routes.forEach(handler -> handler.register(routeConsumer));
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

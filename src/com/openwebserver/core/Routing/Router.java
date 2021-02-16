package com.openwebserver.core.Routing;

import ByteReader.ByteReader.ByteReaderException.PrematureStreamException;
import Tree.TreeArrayList;
import com.openwebserver.core.Connection.Connection;
import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Domain;
import com.openwebserver.core.Handlers.RequestHandler;
import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.WebException;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class Router {

    private static final Router router = new Router();

    private final TreeArrayList<Domain, RequestHandler> routes = new TreeArrayList<>();

    private Router(){}


    public static void register(Domain domain, RequestHandler handler){
        router.routes.populate(domain);
        handler.routes.forEach(handler1 -> {
            handler1.setDomain(domain);
            router.routes.addOn(domain, handler1);
        });
    }

    public static void handle(Connection connection){
        connection.handle((self, args) ->{
            try {
                Request request = Request.deserialize(connection);
                self.write(Router.find(request).handle(request));
            } catch (PrematureStreamException e) {
                  self.close();
            } catch (WebException e) {
                connection.write(e.respond());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private static RequestHandler find(Request request) throws RoutingException.NotFoundException {
        AtomicReference<RequestHandler> requestHandler = new AtomicReference<>(null);
        router.routes.Search(domain -> domain.getAlias().equals(request.getAlias()), handlers -> handlers.forEach(handler -> {
            if(handler.matches(request)){
                requestHandler.set(handler);
            }
        }));
        if(requestHandler.get() == null){
            throw new RoutingException.NotFoundException(request);
        }
        return requestHandler.get();
    }

    public static Domain getDomain(String host) {
        for (Domain domain : router.routes.keySet()) {
            if(domain.getAlias().equals(host)){
                return domain;
            }
        }
        return null;
    }

    public static Router getInstance(){
        return router;
    }

    public static void print() {
        System.out.println("=================ROUTES=================");
        getInstance().routes.forEach(((domain, requestHandlers) -> {
            try {
                System.out.println("DOMAIN:\t" + domain.getUrl().toString());
                requestHandlers.forEach((route) -> {
                    try {
                        System.out.println("\t" + route.getMethod().toString() + ":" +  domain.getUrl().toString()+route.getPath());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    System.out.println("\t\tREQUIRED:" + Arrays.toString(route.getRequired()));
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }));
        System.out.println("========================================");
    }

    public static class RoutingException extends WebException {
        public RoutingException(String message) {
            super(Code.Internal_Server_Error, message);
        }

        public RoutingException(Throwable t) {
            super(t);
        }

        public RoutingException(Code code, String message) {
            super(code, message);
        }

        public static class NotFoundException extends RoutingException{

            public NotFoundException(Route route) {
                super(Code.Not_Found,"Can't find route for '"+route.getPath(true)+"'");
            }
        }
    }

}

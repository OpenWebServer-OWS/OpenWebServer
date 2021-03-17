package com.openwebserver.core.Routing;

import ByteReader.ByteReader.ByteReaderException.PrematureStreamException;

import com.openwebserver.core.Connection.Connection;
import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Domain;
import com.openwebserver.core.Handlers.RequestHandler;
import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.WebException;
import com.tree.TreeArrayList;

import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicReference;

public class Router {

    private static final Router router = new Router();

    private final TreeArrayList<Domain, Routes> routes = new TreeArrayList<>();

    private Router(){}

    public static void register(Domain domain, RequestHandler handler){
        getInstance().routes.populate(domain);
        Routes routes = null;
        for (Routes routes1 : getInstance().routes.branch(domain)) {
            if(routes1.getPath().equals("#")){
                System.out.println("Routes wildcard found");
                break;
            }
            if(routes1.getPath().equals(handler.getPath())){
                routes1.add(handler);
                routes = routes1;
            }
        }
        if(routes == null){
            getInstance().routes.addOn(domain,new Routes(handler.getPath(), domain).add(handler));
        }
    }

    public static void handle(Connection connection){
        connection.handle((self, args) ->{
            try {
                Request request = Request.deserialize(self);
                self.write(Router.find(request, self).handle(request));
            } catch (PrematureStreamException e) {
                  self.close();
            } catch (WebException e) {
                self.write(e.respond());
            } catch (Throwable throwable) {
                self.write(new WebException(throwable).respond(true));
            }
        });
    }

    private static Routes find(Request request, Connection connection) throws RoutingException.NotFoundException {
        AtomicReference<Routes> requestHandler = new AtomicReference<>(null);
        router.routes.Search(domain -> domain.getAlias().equals(request.getAlias()) && domain.getPort() == connection.getLocalPort(), handlers -> handlers.forEach(routes -> {
                if(routes.matches(request)){
                    requestHandler.set(routes);
                }
            }));
        if(requestHandler.get() == null){
            throw new RoutingException.NotFoundException(request.getPath(true));
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
                requestHandlers.forEach(Routes::print);
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

            public NotFoundException(String path) {
                super(Code.Not_Found,"Can't find route for '"+path+"'");
            }
        }
    }

}

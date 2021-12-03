package com.openwebserver.core.routing;


import com.openwebserver.core.connection.client.Connection;
import com.openwebserver.core.http.content.Code;
import com.openwebserver.core.objects.Domain;
import com.openwebserver.core.handlers.RequestHandler;
import com.openwebserver.core.objects.Request;
import com.openwebserver.core.WebException;
import com.tree.TreeArrayList;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.openwebserver.core.connection.client.utils.SocketReader.*;

public class Router {

    private static final Router router = new Router();
    public static boolean VERBOSE = false;

    private final TreeArrayList<Domain, Routes> routes = new TreeArrayList<>();

    private Router() {
    }

    public static void register(Domain domain, RequestHandler handler) {
        getInstance().routes.populate(domain);
        Routes routes = null;
        for (Routes routes1 : getInstance().routes.branch(domain)) {
            if (routes1.getPath().equals("#")) {
                System.out.println("Routes wildcard found");
                break;
            }
            if (routes1.getPath().equals(handler.getPath())) {
                routes1.add(handler);
                routes = routes1;
            }
        }
        if (routes == null) {
            getInstance().routes.addOn(domain, new Routes(handler.getPath(), domain).add(handler));
        }
    }

    public static ArrayList<Routes> getRoutes(Domain domain, boolean showHidden) {
        if (showHidden) {
            return getInstance().routes.get(domain);
        }
        return (ArrayList<Routes>) getInstance().routes.get(domain).stream().filter(route -> !route.isHidden()).collect(Collectors.toList());
    }

    public static ArrayList<Routes> getRoutes(Domain domain) {
        return getRoutes(domain, false);
    }

    public static void handle(Connection connection) {
        connection.handle((self, args) -> {
            long start = System.currentTimeMillis();
            try {
                if (VERBOSE) {
                    System.out.println("New connection: " + connection);
                    System.out.println(start);
                }
                Request request = Request.deserialize(self);
                if (VERBOSE) {
                    System.out.println("Request Decoded: " + connection);
                    System.out.println(System.currentTimeMillis() - start);
                }
                self.write(Router.find(request, self).handle(request));
                if (VERBOSE) {
                    System.out.println("Handled Route Decoded: " + connection);
                    System.out.println(System.currentTimeMillis() - start);
                }
            } catch (ConnectionReaderException | IOException e) {
                self.close();
            } catch (WebException e) {
                self.tryWrite(e.respond());
                if (VERBOSE) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                connection.close();
                if (VERBOSE) {
                    e.printStackTrace();
                }
            } finally {
                if (VERBOSE) {
                    System.out.println(connection);
                    System.out.println("DONE " + (System.currentTimeMillis() - start));
                }
            }
        });
    }

    private static Routes find(Request request, Connection connection) throws RoutingException.NotFoundException {
        AtomicReference<Routes> requestHandler = new AtomicReference<>(null);
        router.routes.Search(domain -> domain.getAlias().equals(request.getAlias()) && domain.getPort() == connection.getLocalPort(), handlers -> handlers.forEach(routes -> {
            if (routes.matches(request) && requestHandler.get() == null) {
                requestHandler.set(routes);
            }
        }));
        if (requestHandler.get() == null) {
            throw new RoutingException.NotFoundException(request.getPath(true));
        }
        return requestHandler.get();
    }

    public static Domain getDomain(String host) throws RoutingException {
        for (Domain domain : router.routes.keySet()) {
            if (domain.getAlias().equals(host)) {
                return domain;
            }
        }
        throw new Router.RoutingException("Unknown Domain '" + host + "'");
    }

    public static Router getInstance() {
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

        public static class NotFoundException extends RoutingException {

            public NotFoundException(String path) {
                super(Code.Not_Found, "Can't find route for '" + path + "'");
            }
        }
    }

}

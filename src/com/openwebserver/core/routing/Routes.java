package com.openwebserver.core.routing;


import com.openwebserver.core.security.sessions.annotations.Session;
import com.openwebserver.core.content.Code;
import com.openwebserver.core.objects.Domain;
import com.openwebserver.core.handlers.RequestHandler;
import com.openwebserver.core.objects.Request;
import com.openwebserver.core.objects.Response;
import com.openwebserver.core.WebException;

import java.util.Arrays;
import java.util.HashMap;

public class Routes extends HashMap<Route.Method, RequestHandler>{

    private final Route route;

    public Routes(String path, Domain domain){
        route = new Route(path, null);
        route.setDomain(domain);
    }

    public void print() {
        route.print();
        values().forEach(Routes::Print);
    }

    public static void Print(RequestHandler handler){
        System.out.println("\t\t[" + handler.getMethod().toString() + "]");
        if(handler.getRequired().length > 0) {
            System.out.println("\t\t\tREQUIRED:" + Arrays.toString(handler.getRequired()));
        }
        if(handler.getPolicyName() != null) {
            System.out.println("\t\t\tPOLICY:" + handler.getPolicy());
        }
        if(handler.getSessionSpecification() != null) {
            Session s = handler.getSessionSpecification();
            System.out.println("\t\t\tSESSION:{" +
                    "required=" + Arrays.toString(s.require()) +
                    ", redirect=" + (!s.redirect().equals("")? s.redirect(): "undefined") +
                    "}"
            );
        }
        if(handler.needsAuthentication()) {
            System.out.println("\t\t\tAUTHENTICATION: REQUIRED");
        }
    }

    public Domain getDomain() {
        return route.getDomain();
    }

    public String getPath(){
        return route.getPath();
    }

    public boolean matches(Request request){
        if(route.getPath().endsWith("#") && request.getPath(true).startsWith(route.getPath().replace("#", ""))){
            return true;
        }
        if (Route.RESTDecoder.containsRegex(getPath())) {
            return Route.RESTDecoder.Match(request.getPath(true), route , request.GET()::put);
        } else {
            String cleanPath = request.getPath(true);
            return getPath().equals(cleanPath) || (cleanPath.endsWith("/") && getPath().equals(cleanPath.substring(0, cleanPath.length() - 1))) || getPath().equals(cleanPath + "/");
        }
    }

    public Routes add(RequestHandler handler){
        handler.setDomain(route.getDomain());
        put(handler.getMethod(), handler);
        return this;
    }

    public Response handle(Request request) throws Throwable {
        RequestHandler handler = null;
        if(containsKey(request.getMethod())){
            handler = get(request.getMethod());
        }else if(containsKey(Route.Method.UNDEFINED)){
            handler = get(Route.Method.UNDEFINED);
        }
        if(handler != null){
            if(handler.isEnabled()) {
                try {
                    return handler.handle(request).addHeaders(handler.getHeaders());
                } catch (WebException e) {
                    throw e.addHeaders(handler.getHeaders());
                }
            }else{
                return Response.simple(Code.Disabled, "Route disabled on path '"+handler.getPath()+"' with method '"+handler.getMethod()+"'");
            }
        }
        else{
            throw new WebException(Code.Method_Not_Allowed, "Method not allowed on '"+getPath()+"'").addRequest(request);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getPath());
    }
}

package com.openwebserver.core.Routing;


import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Domain;
import com.openwebserver.core.Handlers.RequestHandler;
import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.core.WebException;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;

public class Routes extends HashMap<Route.Method, RequestHandler>{

    private final Route route;

    public Routes(String path, Domain domain){
        route = new Route(path, null);
        route.setDomain(domain);
    }

    public void print() {
        values().forEach(handler ->{
            try {
                System.out.println("\t" + handler.getMethod().toString() + ":" +  getDomain().getUrl().toString()+handler.getPath());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            System.out.println("\t\tREQUIRED:" + Arrays.toString(handler.getRequired()));
            if(handler.getPolicyName() != null) {
                System.out.println("\t\tPOLICY:" + handler.getPolicy());
            }
            if(handler.needsAuthentication()) {
                System.out.println("\t\tAUTHENTICATION: REQUIRED");
            }
        });
    }

    public Domain getDomain() {
        return route.getDomain();
    }

    public String getPath(){
        return route.getPath();
    }

    public boolean matches(Request request){
        if(route.getPath().equals("#")){
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
        put(handler.getMethod(), handler);
        return this;
    }

    public Response handle(Request request) throws Throwable {
        RequestHandler handler = null;
        if(containsKey(request.getMethod())){
            handler = get(request.getMethod());
        }else if(containsKey(Route.Method.UNDEFINED)){
            handler = get(Route.Method.UNDEFINED);
            return get(Route.Method.UNDEFINED).handle(request);
        }
        if(handler != null){
            try {
                return handler.handle(request).addHeaders(handler.getHeaders());
            }catch (WebException e){
                throw e.addHeaders(handler.getHeaders());
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

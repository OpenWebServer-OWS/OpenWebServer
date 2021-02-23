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
        });
    }

    public Domain getDomain() {
        return route.getDomain();
    }

    public String getPath(){
        return route.getPath();
    }

    public boolean matches(Request request){
        if (Route.RESTDecoder.containsRegex(getPath())) {
            return Route.RESTDecoder.Match(request.getPath(true), route , request.GET()::put);
        } else {
            String cleanPath = request.getPath(true);
            return getPath().equals(cleanPath) || (cleanPath.endsWith("/") && getPath().equals(cleanPath.substring(0, cleanPath.length() - 1)) );
        }
    }

    public Routes add(RequestHandler handler){
        put(handler.getMethod(), handler);
        return this;
    }

    public Response handle(Request request) throws Throwable {
        if(containsKey(request.getMethod())){
            return get(request.getMethod()).handle(request);
        }else if(containsKey(Route.Method.UNDEFINED)){
            return get(Route.Method.UNDEFINED).handle(request);
        }else{
            throw new WebException(Code.Method_Not_Allowed, "Method not allowed on '"+getPath()+"'").addRequest(request);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getPath());
    }

    //
//    private Method method = Method.UNDEFINED;
//    private String[] require;
//    private final HashMap<Integer, String> RESTParams = new HashMap<>();
//    private String path;
//    private boolean REST;
//
//    public Route(String path, Method method, String... require) {
//        if(path != null) {
//            if (!path.startsWith("/") && !path.equals("#")) path = "/" + path;
//            RESTDecoder.PatternReader(path, RESTParams::put);
//        }else{
//            path = "";
//        }
//        this.path = path;
//        this.method = method;
//        this.require = require;
//        REST = !RESTParams.isEmpty();
//    }
//
//    public Route(com.openwebserver.services.Annotations.Route route){
//        this(route.path(), route.method(), route.require());
//    }
//
//    public Route(Route route){
//        this(route.getPath(), route.getMethod(), route.getRequired());
//    }
//
//    public Route(String path, String... require) {
//        this(path, Method.UNDEFINED, require);
//    }
//
//    protected Route(){}
//
//    public Method getMethod() {
//        return method;
//    }
//
//    public String[] getRequired() {
//        return require;
//    }
//
//    protected boolean hasRequired(Request request) {
//        return request.GET().keySet().containsAll(Arrays.asList(getRequired())) || request.POST().keySet().containsAll(Arrays.asList(getRequired()));
//    }
//
//    public String getPath() {
//        return path;
//    }
//
//    public String getPath(boolean clean) {
//        if (clean && getPath().contains("?")) {
//            return getPath().substring(0, getPath().indexOf("?"));
//        }
//        return getPath();
//    }
//
//    public boolean isREST() {
//        return REST;
//    }
//
//    public boolean matches(Request request) {
//        if (method.allows(request.getMethod())) {
//            if (isREST()) {
//                return RESTDecoder.Match(request.getPath(true), this, request.GET()::put);
//            } else {
//                String cleanPath = request.getPath(true);
//                return getPath().equals(cleanPath) || (cleanPath.endsWith("/") && getPath().equals(cleanPath.substring(0, cleanPath.length() - 1)) );
//            }
//        }
//        return false;
//    }
//
//    public void addPrefix(String prefix) {
//        String finalPrefix = prefix.contains("//")?prefix.replaceAll("//", ""): prefix;
//        doIf(!this.path.contains(prefix), o -> this.path = finalPrefix + getPath());
//        doIf(path.contains("//"), o -> path = path.replaceAll("//", "/"));
//        doIf(isREST(), o ->{
//            RESTParams.clear();
//            RESTDecoder.PatternReader(path, RESTParams::put);
//        });
//    }
//
//    public static class RESTDecoder {
//
//        public static Pattern pattern = Pattern.compile("\\{(.*?)}", Pattern.MULTILINE);
//
//        public static boolean containsRegex(String path){
//            return path.contains("{") && path.contains("}");
//        }
//
//        private static boolean Match(String request, Route route, BiConsumer<String, String> paramConsumer) {
//            String[] requestPath = request.split("/");
//            String[] handlerPath = route.getPath().split("/");
//            if (requestPath.length != handlerPath.length) {
//                return false;
//            }
//            for (int i = 0; i < requestPath.length; i++) {
//                if (route.RESTParams.containsKey(i)) {
//                    paramConsumer.accept(route.RESTParams.get(i).replace("{", "").replace("}", ""), requestPath[i]);
//                    continue;
//                }
//                if (!requestPath[i].equals(handlerPath[i])) {
//                    return false;
//                }
//            }
//            return true;
//        }
//
//        private static void PatternReader(String value, BiConsumer<Integer, String> matchConsumer) {
//            Matcher matcher = pattern.matcher(value);
//            ArrayList<String> pathIndexed = new ArrayList<>(Arrays.asList(value.split("/")));
//            while (matcher.find()) {
//                String match = matcher.group(0);
//                matchConsumer.accept(pathIndexed.indexOf(match), match);
//            }
//        }
//
//    }
//
//    public void setDomain(Domain domain) {
//        this.domain = domain;
//    }
//
//    public Domain getDomain() {
//        return domain;
//    }
//
//    //region nested routes
//
//    public final ArrayList<RequestHandler> routes = new ArrayList<>();
//
//    protected void foreach(Consumer<RequestHandler> handlerConsumer){
//        routes.forEach(handler -> {
//            if(handler.getPath() != this.getPath()){
//                handlerConsumer.accept(handler);
//            }
//        });;
//    }
//
//    protected void add(RequestHandler handler) {
//        routes.add(handler);
//    }
//
//    protected void remove(RequestHandler handler){
//        routes.remove(handler);
//    }
//
//    public void setParent(RequestHandler parent) {
//        ArrayList<RequestHandler> routes = (ArrayList<RequestHandler>) this.routes.clone();
//        this.routes.clear();
//        routes.forEach(handler -> {
//            handler.addPrefix(parent.getPath());
//            add(handler);
//        });
//        this.addPrefix(parent.getPath());
//    }
//
//    public void addAll(ArrayList<RequestHandler> handlers) {
//        handlers.forEach(this::add);
//    }
    //endregion

}

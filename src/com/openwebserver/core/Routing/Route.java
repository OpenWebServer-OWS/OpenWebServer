package com.openwebserver.core.Routing;


import com.openwebserver.core.Domain;
import com.openwebserver.core.Handlers.RequestHandler;
import com.openwebserver.core.Objects.Request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Collective.Collective.doIf;

public class Route {

    private Domain domain;


    public enum Method {
        GET,
        HEAD,
        POST,
        PUT,
        DELETE,
        CONNECT,
        OPTIONS,
        TRACE,
        PATCH,
        UNDEFINED;

        public boolean allows(Method method) {
            return method == this || this == Method.UNDEFINED;
        }
    }

    private Method method = Method.UNDEFINED;
    private String[] require;
    private final HashMap<Integer, String> RESTParams = new HashMap<>();
    private String path;
    private boolean REST;

    public Route(String path, Method method, String... require) {
        if(path != null) {
            if (!path.startsWith("/") && !path.equals("#")) path = "/" + path;
            RESTDecoder.PatternReader(path, RESTParams::put);
        }else{
            path = "";
        }
        this.path = path;
        this.method = method;
        this.require = require;
        REST = !RESTParams.isEmpty();
    }

    public Route(com.openwebserver.services.Annotations.Route route){
        this(route.path(), route.method(), route.require());
    }

    public Route(Route route){
        this(route.getPath(), route.getMethod(), route.getRequired());
    }

    public Route(String path, String... require) {
        this(path, Method.UNDEFINED, require);
    }

    protected Route(){}

    public Method getMethod() {
        return method;
    }

    public String[] getRequired() {
        return require;
    }

    protected boolean hasRequired(Request request) {
        return request.GET().keySet().containsAll(Arrays.asList(getRequired())) || request.POST().keySet().containsAll(Arrays.asList(getRequired()));
    }

    public String getPath() {
        return path;
    }

    public String getPath(boolean clean) {
        if (clean && getPath().contains("?")) {
            return getPath().substring(0, getPath().indexOf("?"));
        }
        return getPath();
    }

    public boolean isREST() {
        return REST;
    }

    public boolean matches(Request request) {
        if (method.allows(request.getMethod())) {
            if (isREST()) {
                return RESTDecoder.Match(request.getPath(true), this, request.GET()::put);
            } else {
                String cleanPath = request.getPath(true);
                return getPath().equals(cleanPath) || (cleanPath.endsWith("/") && getPath().equals(cleanPath.substring(0, cleanPath.length() - 1)) );
            }
        }
        return false;
    }

    public void addPrefix(String prefix) {
        String finalPrefix = prefix.contains("//")?prefix.replaceAll("//", ""): prefix;
        doIf(!this.path.contains(prefix), o -> this.path = finalPrefix + getPath());
        doIf(path.contains("//"), o -> path = path.replaceAll("//", "/"));
        doIf(isREST(), o ->{
            RESTParams.clear();
            RESTDecoder.PatternReader(path, RESTParams::put);
        });
    }

    public static class RESTDecoder {

        public static Pattern pattern = Pattern.compile("\\{(.*?)}", Pattern.MULTILINE);

        public static boolean containsRegex(String path){
            return path.contains("{") && path.contains("}");
        }

        private static boolean Match(String request, Route route, BiConsumer<String, String> paramConsumer) {
            String[] requestPath = request.split("/");
            String[] handlerPath = route.getPath().split("/");
            if (requestPath.length != handlerPath.length) {
                return false;
            }
            for (int i = 0; i < requestPath.length; i++) {
                if (route.RESTParams.containsKey(i)) {
                    paramConsumer.accept(route.RESTParams.get(i).replace("{", "").replace("}", ""), requestPath[i]);
                    continue;
                }
                if (!requestPath[i].equals(handlerPath[i])) {
                    return false;
                }
            }
            return true;
        }

        private static void PatternReader(String value, BiConsumer<Integer, String> matchConsumer) {
            Matcher matcher = pattern.matcher(value);
            ArrayList<String> pathIndexed = new ArrayList<>(Arrays.asList(value.split("/")));
            while (matcher.find()) {
                String match = matcher.group(0);
                matchConsumer.accept(pathIndexed.indexOf(match), match);
            }
        }

    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Domain getDomain() {
        return domain;
    }

    //region nested routes

    public final ArrayList<RequestHandler> routes = new ArrayList<>();

    protected void foreach(Consumer<RequestHandler> handlerConsumer){
        routes.forEach(handlerConsumer);;
    }

    protected void add(RequestHandler handler) {
        routes.add(handler);
    }

    protected void remove(RequestHandler handler){
        routes.remove(handler);
    }

    public void setParent(RequestHandler parent) {
        ArrayList<RequestHandler> routes = (ArrayList<RequestHandler>) this.routes.clone();
        this.routes.clear();
        routes.forEach(handler -> {
            handler.addPrefix(parent.getPath());
            add(handler);
        });
        this.addPrefix(parent.getPath());
    }

    public void addAll(ArrayList<RequestHandler> handlers) {
        handlers.forEach(this::add);
    }
    //endregion

}

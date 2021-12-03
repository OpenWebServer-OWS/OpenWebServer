package com.openwebserver.core.routing;


import com.openwebserver.core.objects.Domain;
import com.openwebserver.core.objects.Request;
import org.json.JSONPropertyIgnore;

import java.net.MalformedURLException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Route {




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

    private String[] require;
    private String path;
    private final Route.Method method;
    private HashMap<Integer, String> RESTParams;
    private Domain domain;
    private boolean needsAuthorization = false;
    private boolean enabled = true;
    private boolean hidden = false;

    public Route(String path, Method method, String... require) {
        this.method = method;
        if (require != null && require.length > 0) {
            this.require = require;
        }
        if (path != null) {
            if (!path.startsWith("/") && !path.equals("#")) path = "/" + path;
            this.path = path;
            if (RESTDecoder.containsRegex(path)) {
                this.RESTParams = new HashMap<>();
                RESTDecoder.PatternReader(path, RESTParams::put);
            }
        }
    }

    public Route(com.openwebserver.services.annotations.Route route) {
        this(route.path(), route.method(), route.require());
    }

    public Route(Route route) {
        this(route.getPath(), route.getMethod(), route.getRequired());
        this.require = route.require;
        this.needsAuthorization = route.needsAuthorization;
        this.enabled = route.enabled;
        this.domain = route.domain;
        this.hidden = route.hidden;
        this.RESTParams = route.RESTParams;
        
    }

    public void disable() {
        enabled = false;
    }

    public void enable() {
        enabled = true;
    }

    public boolean needsAuthentication() {
        return needsAuthorization;
    }

    public void setNeedsAuthentication(boolean authorized) {
        this.needsAuthorization = authorized;
    }

    public Method getMethod() {
        return method;
    }

    public String[] getRequired() {
        return require;
    }

    public Collection<String> getRESTParams() {
        if(RESTParams == null){
            return (Collection<String>) Collections.EMPTY_LIST;
        }
        return RESTParams.values();
    }

    public HashMap<Integer, String> getRESTMap() {
        return RESTParams;
    }

    protected boolean hasRequired(Request request) {
        if(getRequired() == null){
            return true;
        }
        return request.GET.keySet().containsAll(Arrays.asList(getRequired())) || request.POST.keySet().containsAll(Arrays.asList(getRequired()));
    }

    public boolean requires() {
        return getRequired().length > 0;
    }

    public boolean isEnabled() {
        return enabled;
    }


    public boolean isHidden() {
        return this.hidden;
    }

    protected void setHidden(boolean hidden){
        this.hidden = hidden;
    }

    public String getPath() {
        return path;
    }

    public void addPrefix(Route notation) {
        addPrefix(notation.getPath());
    }

    public void addPrefix(String prefix) {
        String finalPrefix = prefix.contains("//") ? prefix.replaceAll("//", "") : prefix;
        if (!this.path.contains(prefix)) this.path = finalPrefix + getPath();
        if (path.contains("//")) path = path.replaceAll("//", "/");
        if (isREST()) {
            RESTParams.clear();
            RESTDecoder.PatternReader(path, RESTParams::put);
        }
    }

    public boolean isREST() {
        if(RESTParams == null){
            return false;
        }
        return !RESTParams.isEmpty();
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Domain getDomain() {
        return domain;
    }

    public void print() {
        try {
            System.out.println("\tROUTE:" + getDomain().getUrl().toString() + getPath());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static class RESTDecoder {

        public final static Pattern pattern = Pattern.compile("\\{(.*?)}", Pattern.MULTILINE);

        public static boolean containsRegex(String path) {
            return path.contains("{") && path.contains("}");
        }

        static boolean Match(String request, Route route, BiConsumer<String, String> paramConsumer) {
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

        public static void PatternReader(String value, BiConsumer<Integer, String> matchConsumer) {
            Matcher matcher = pattern.matcher(value);
            ArrayList<String> pathIndexed = new ArrayList<>(Arrays.asList(value.split("/")));
            while (matcher.find()) {
                String match = matcher.group(0);
                matchConsumer.accept(pathIndexed.indexOf(match), match);
            }
        }

    }


}

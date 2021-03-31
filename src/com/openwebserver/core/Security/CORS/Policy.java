package com.openwebserver.core.Security.CORS;

import com.openwebserver.core.Objects.Headers.Header;
import com.openwebserver.core.Objects.Headers.Headers;
import com.openwebserver.core.Routing.Route;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

public class Policy{

    private final String name;
    private String allowedOrigin = null;
    private final ArrayList<String> allowedHeaders = new ArrayList<>();
    private final ArrayList<Route.Method> allowedMethods = new ArrayList<>();
    private final Headers headers = new Headers();

    public Policy(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Policy setOrigin(String origin){
        this.allowedOrigin = origin;
        return this;
    }

    public Policy AllowHeader(String key){
        allowedHeaders.add(key);
        return this;
    }

    public Policy AllowAnyHeader(){
        return AllowHeader("*");
    }

    public Policy AllowMethod(Route.Method... methods){
        allowedMethods.addAll(Arrays.asList(methods));
        return this;
    }

    public Policy AllowAnyMethods(){
        allowedMethods.add(Route.Method.UNDEFINED);
        return this;
    }

    public Policy AllowAnyOrgin(){
        allowedOrigin = "*";
        return this;
    }

    public Policy addHeader(Header header){
        headers.add(header);
        return this;
    }

    public ArrayList<Route.Method> getAllowedMethods() {
        return allowedMethods;
    }

    public ArrayList<String> getAllowedHeaders() {
        return allowedHeaders;
    }

    public String getAllowedOrigin() {
        return allowedOrigin;
    }

    public Headers pack() throws PolicyManager.PolicyException {
        if(allowedOrigin == null){
            throw new PolicyManager.PolicyException("Origin not found for policy '"+name+"'");
        }
        Headers headers = new Headers();
        headers.addAll(this.headers);
        headers.add(new Header("Access-Control-Allow-Origin", allowedOrigin));
        headers.add(new Header("Access-Control-Allow-Methods", between(allowedMethods, method -> {
            if(method.equals(Route.Method.UNDEFINED)){
                return "*";
            }else{
                return method.name();
            }
        }, ",")));
        headers.add(new Header("Access-Control-Allow-Headers", between(allowedHeaders)));
        return headers;
    }

    private static <T> String between(ArrayList<T> collection){
        return between(collection, Object::toString, ",");
    }

    private static <T> String between(ArrayList<T> collection, Function<T, String> editor,String separator){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < collection.size(); i++) {
            builder.append(editor.apply(collection.get(i)));
            if(i != collection.size() -1){
                builder.append(separator);
            }
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", allowedOrigin=" + allowedOrigin +
                ", allowedHeaders=" + allowedHeaders +
                ", allowedMethods=" + allowedMethods +
                ", headers=" + headers +
                '}';
    }
}

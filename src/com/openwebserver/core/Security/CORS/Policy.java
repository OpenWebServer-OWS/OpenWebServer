package com.openwebserver.core.Security.CORS;

import com.openwebserver.core.Objects.Headers.Header;
import com.openwebserver.core.Objects.Headers.Headers;
import com.openwebserver.core.Routing.Route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

public class Policy implements Supplier<Headers> {

    private final String name;
    private ArrayList<String> allowedOrigins = new ArrayList<>();
    private ArrayList<String> allowedHeaders = new ArrayList<>();
    private ArrayList<Route.Method> allowedMethods = new ArrayList<>();

    public Policy(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Policy addOrigin(String ... orgins){
        allowedOrigins.addAll(Arrays.asList(orgins));
        return this;
    }

    public Policy AllowHeader(String key){
        allowedHeaders.add(key);
        return this;
    }

    public Policy AllowAnyHeader(){
        return AllowHeader("*");
    }

    public Policy AllowMethod(Route.Method ... methods){
        allowedMethods.addAll(Arrays.asList(methods));
        return this;
    }

    public Policy AllowAnyMethods(){
        allowedMethods.add(Route.Method.UNDEFINED);
        return this;
    }
    public Policy AllowAnyOrgin(){
        allowedOrigins.add("*");
        return this;
    }

    @Override
    public Headers get() {
        Headers headers = new Headers();
        headers.add(new Header("Access-Control-Allow-Origin", between(allowedOrigins, ",")));
        headers.add(new Header("Access-Control-Allow-Methods", between(allowedMethods, method -> {
            if(method.equals(Route.Method.UNDEFINED)){
                return "*";
            }else{
                return method.name();
            }
        }, ",")));
        headers.add(new Header("Access-Control-Allow-Headers", between(allowedHeaders, ",")));
        return headers;
    }

    private static <T> String between(ArrayList<T> collection, String separator){
        return between(collection, (item) -> item.toString(), separator);
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

}

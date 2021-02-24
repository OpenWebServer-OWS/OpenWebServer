package com.openwebserver.core.Security.Authorization;

import com.openwebserver.core.Objects.Headers.Header;
import com.openwebserver.core.Objects.Request;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Authentication {

    private final Type type;
    private BiFunction<Object, Request, Boolean> handler;

    public enum Type{
        BEARER("Authorization", (header) -> {return header.getValue().split(" ")[1].trim();}),
        BASIC("Authorization", (header) -> header),
        JWT("Authorization", (header) -> header);

        private final String headerName;
        public Function<Header,?> manipulator;

        Type(String headerName, Function<Header,Object> manipulator) {
            this.headerName = headerName;
            this.manipulator = manipulator;
        }

        public String getHeaderName() {
            return headerName;
        }
    }

    public Authentication(Type type){
        this.type =type;
    }

    public Authentication setHandler(BiFunction<Object, Request, Boolean> handler){
        this.handler = handler;
        return this;
    }


    public boolean authorize(Request request) {
        return handler.apply(type.manipulator.apply(request.headers.get(type.getHeaderName())),request);
    }


}

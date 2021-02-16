package com.openwebserver.core.Handlers;


import com.openwebserver.core.Annotations.Session;
import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Objects.Headers.Header;
import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.core.Routing.Route;
import com.openwebserver.core.Sessions.SessionManager;
import com.openwebserver.core.WebException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;


public class RequestHandler extends Route {


    private ContentHandler contentHandler;
    private BiFunction<Session, com.openwebserver.core.Sessions.Session, Boolean> sessionHandler = (annotation, session) -> session.hasRequired(annotation.require());
    private Session sessionSpecification;
    private final ArrayList<Header> headers = new ArrayList<>();


    public RequestHandler(Route r, ContentHandler contentHandler) {
        this(r,contentHandler,null);
    }

    public RequestHandler(Route r, ContentHandler contentHandler, Session sessionSpecification) {
        super(r);
        this.contentHandler = contentHandler;
        this.sessionSpecification = sessionSpecification;
        if(contentHandler != null){
            add(this);
        }
    }

    public void setContentHandler(ContentHandler contentHandler){
        if(this.contentHandler != null){
            remove(this);
        }else{
            this.contentHandler = contentHandler;
            add(this);
        }
    }

    public void setSessionHandler(BiFunction<Session, com.openwebserver.core.Sessions.Session, Boolean> handler){
        this.sessionHandler = handler;
    }

    public void setSessionSpecification(Session sessionSpecification) {
        this.sessionSpecification = sessionSpecification;
    }

    public  BiFunction<Session, com.openwebserver.core.Sessions.Session, Boolean> getSessionHandler() {
        return sessionHandler;
    }

//    public static RequestHandler Folder(Folder folder) {
//        return new RequestHandler(new Route("#", Route.Method.GET), request -> Response.file(new Local(folder.getPath() + request.getPath(true))));
//    }

    public static RequestHandler Inline(String path, Response response) {
        return new RequestHandler(new Route(path), request -> response);
    }

    public void addHeader(Header ... headers){
        this.headers.addAll(Arrays.asList(headers));
        foreach(requestHandler -> {
            requestHandler.addHeader(headers);
        });
    }

    public Response handle(Request request) throws Throwable {
        request.setHandler(this);
        if (!super.hasRequired(request)) {
            throw new WebException(Code.Bad_Request, "method requires arguments").extra("required", request.getRequired()).addRequest(request);
        }
        if(sessionSpecification != null){
            try {
                SessionManager.bind(sessionSpecification, request);
            }catch (com.openwebserver.core.Sessions.Session.SessionException e){
                return Response.simple(Code.Unauthorized);
            }
        }
        return contentHandler.respond(request).addHeader(headers.toArray(Header[]::new));
    }

}

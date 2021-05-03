package com.openwebserver.core.Handlers;

import FileManager.Folder;
import FileManager.Local;
import com.openwebserver.core.Security.Sessions.Annotations.Session;
import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Objects.Headers.Header;
import com.openwebserver.core.Objects.Headers.Headers;
import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.core.Routing.Route;
import com.openwebserver.core.Security.Authorization.Authorize;
import com.openwebserver.core.Security.Authorization.Authorizer;
import com.openwebserver.core.Security.CORS.CORS;
import com.openwebserver.core.Security.CORS.Policy;
import com.openwebserver.core.Security.CORS.PolicyManager;
import com.openwebserver.core.Security.ContentFilter.Accept;
import com.openwebserver.core.Security.Sessions.SessionManager;
import com.openwebserver.core.WebException;
import com.openwebserver.core.WebServer;
import com.openwebserver.services.Objects.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;


public class RequestHandler extends Route implements RouteRegister{

    private ContentHandler contentHandler;
    private final Headers headers = new Headers();
    private java.lang.reflect.Method reflection;
    private Consumer<RequestHandler> registerListener;

    public void setOnRegisterListener(Consumer<RequestHandler> listener){
        this.registerListener = listener;
    }

    public RequestHandler(Route notation, ContentHandler contentHandler) {
        this(notation,contentHandler,null);
    }

    public RequestHandler(Route notation, ContentHandler contentHandler, Session sessionSpecification) {
        super(notation);
        this.contentHandler = contentHandler;
        this.sessionSpecification = sessionSpecification;
    }

    public void setContentHandler(ContentHandler contentHandler){
        this.contentHandler = contentHandler;
    }

    public Response handle(Request request) throws Throwable {
        request.setHandler(this);
        handleCORS(request);
        if (!super.hasRequired(request)) {
            throw new WebException(Code.Bad_Request, "method requires arguments").extra("required", getRequired()).addRequest(request);
        }
        if(acceptedContentType != null){
            //TODO Filter Accepted content with requesthandler
        }
        if(needsAuthentication() && !getAuthorizer().authorize(request)){
            throw new WebException(Code.Unauthorized,"Invalid Token").addRequest(request);
        }
        try {
            SessionManager.bind(sessionSpecification, request);
        }catch (com.openwebserver.core.Security.Sessions.Session.SessionException e){
            if(sessionSpecification != null && !sessionSpecification.redirect().equals("")){
                return Response.simple(Code.Temporary_Redirect).addHeader(new Header("Location", sessionSpecification.redirect())).addHeaders(this.headers);
            }
            throw e;
        }
        return contentHandler.respond(request);
    }

    public Headers getHeaders() {
        return headers;
    }

    @Override
    public void addPrefix(String prefix) {
        super.addPrefix(prefix);
        if(CORS_handler != null){
            CORS_handler.addPrefix(prefix);
        }
    }

    //region sessions
    private SessionHandler sessionHandler = (annotation, session) -> session.hasRequired(annotation.require());
    private Session sessionSpecification;

    public void setSessionHandler(SessionHandler handler){
        this.sessionHandler = handler;
    }

    public void setSessionSpecification(Session sessionSpecification) {
        this.sessionSpecification = sessionSpecification;
    }

    public  SessionHandler getSessionHandler() {
        return sessionHandler;
    }

    public Session getSessionSpecification() {
        return sessionSpecification;
    }
    //endregion

    //region CORS
    public void handleCORS(Request request){
        if(overrideOrigin){
            request.headers.tryGet("Origin", header -> this.headers.replace("Access-Control-Allow-Origin", header.getValue()));
        }
    }

    private Policy policy;
    private boolean overrideOrigin = false;
    private RequestHandler CORS_handler;

    public void setCORSPolicy(CORS policy) {
        if(policy != null) {
            setCORSPolicy(policy.value(), policy.overrideOrigin());
        }
    }
    public void setCORSPolicy(String policy) {
        setCORSPolicy(policy, false);
    }
    public void setCORSPolicy(String policy, boolean overrideOrigin) {
        if(policy == null){
            return;
        }
        try {
            this.policy = PolicyManager.getPolicy(policy);
            this.headers.addAll(this.policy.pack());
            CORS_handler = new RequestHandler(new Route(this.getPath(), Method.OPTIONS), request -> {
                Headers headers = this.policy.pack();
                if(overrideOrigin){
                    request.headers.tryGet("Origin", header -> headers.replace("Access-Control-Allow-Origin", header.getValue()));
                }
                return Response.simple(Code.No_Content).addHeaders(headers);
            });
            this.overrideOrigin = overrideOrigin;
        } catch (PolicyManager.PolicyException.NotFound notFound) {
            notFound.printStackTrace();
        } catch (PolicyManager.PolicyException e) {
            if(!overrideOrigin){
               e.printStackTrace();
            }
        }
    }

    public String getPolicyName() {
        if(getPolicy() != null){
            return getPolicy().getName();
        }
        return null;
    }
    public Policy getPolicy() {
        return policy;
    }
    //endregion

    //region Authentication
    private Authorizer<?> authorizer;
    public void setAuthorizer(Authorizer<?> authorizer){
        if(needsAuthentication()) {
            this.authorizer = authorizer;
        }
    }

    protected Authorizer<?> getAuthorizer() {
        return authorizer;
    }
    //endregion

    //region method reflection
    public void setReflection(java.lang.reflect.Method method){
        setSessionSpecification(method.isAnnotationPresent(Session.class)? method.getAnnotation(Session.class): null);
        setCORSPolicy(method.isAnnotationPresent(CORS.class)? method.getAnnotation(CORS.class): null);
        setNeedsAuthentication(method.isAnnotationPresent(Authorize.class));
        setAcceptContent(method.isAnnotationPresent(Accept.class)? method.getAnnotation(Accept.class): null);
        this.reflection = method;
    }

    public java.lang.reflect.Method getReflection() {
        return reflection;
    }
    //endregion

    //region contenttype
    private Accept acceptedContentType;
    private void setAcceptContent(Accept accept) {
        this.acceptedContentType = accept;
    }
    public void acceptsContent(){

    }
    //endregion

    @Override
    public void register(Consumer<RequestHandler> routeConsumer) {
        routeConsumer.accept(this);
        if(CORS_handler != null){
            routeConsumer.accept(CORS_handler);
        }
        if(registerListener != null){
            registerListener.accept(this);
        }
    }

    public static RequestHandler inline(String path, Method method, ContentHandler handler){
        return new RequestHandler(new Route(path, method), handler);
    }

    public static RequestHandler folder(String pathPrefix, Folder f){
        return new RequestHandler(new Route(pathPrefix + "/#", Method.GET), request -> {
            String path = request.getPath(true);
            if(path.endsWith("/") || !path.contains("."))
            {
                return Response.simple(new Local(f.getPath() + "/" + WebServer.rootFilename));
            }
            return Response.simple(new Local(f.getPath() + path));
        });
    }

    public static RequestHandler folder(Folder folder){
        return folder("", folder);
    }


    public static void wrap(java.lang.reflect.Method method, Service service){
        if(method.isAnnotationPresent(com.openwebserver.services.Annotations.Route.class)) {
            RequestHandler handler = new RequestHandler(new Route(method.getAnnotation(com.openwebserver.services.Annotations.Route.class)), null);
            if (method.getReturnType().equals(Response.class)) {
                handler.setContentHandler(request -> {
                    try {
                        return ((Response) method.invoke(service, request));
                    } catch (InvocationTargetException e) {
                        throw new WebException(e).addRequest(request);
                    }
                });
            } else {
                handler.setContentHandler(request -> {
                    try {
                        return Response.simple(method.invoke(service, request));
                    } catch (InvocationTargetException e) {
                        throw new WebException(e).addRequest(request);
                    }
                });
            }
            handler.setReflection(method);
            handler.setOnRegisterListener(service);
            handler.setSessionHandler(service.getSessionHandler());
            service.add(handler);
        }
    }
}

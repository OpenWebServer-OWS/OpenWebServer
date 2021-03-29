package com.openwebserver.core.Handlers;

import com.openwebserver.core.Annotations.Session;
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
import com.openwebserver.core.Sessions.SessionManager;
import com.openwebserver.core.WebException;

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
        if (!super.hasRequired(request)) {
            throw new WebException(Code.Bad_Request, "method requires arguments").extra("required", getRequired()).addRequest(request);
        }
        if(needsAuthentication() && !getAuthorizer().authorize(request)){
            throw new WebException(Code.Unauthorized,"Invalid Token").addRequest(request);
        }
        handleCORS(request);
        try {
            SessionManager.bind(sessionSpecification, request);
        }catch (com.openwebserver.core.Sessions.Session.SessionException e){
            if(sessionSpecification != null && !sessionSpecification.redirect().equals("")){
                return Response.simple(Code.Temporary_Redirect).addHeader(new Header("Location", sessionSpecification.redirect()));
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
            request.headers.tryGet("Origin", header -> {
                this.headers.replace("Access-Control-Allow-Origin", header.getValue());
            });
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
            CORS_handler = new RequestHandler(new Route(this.getPath(), Method.OPTIONS), request -> Response.simple(Code.No_Content).addHeaders(this.policy.pack()));
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

    public void setReflection(java.lang.reflect.Method method){
        setSessionSpecification(method.isAnnotationPresent(Session.class)? method.getAnnotation(Session.class): null);
        setCORSPolicy(method.isAnnotationPresent(CORS.class)? method.getAnnotation(CORS.class): null);
        setNeedsAuthentication(method.isAnnotationPresent(Authorize.class));
        this.reflection = method;
    }

    public java.lang.reflect.Method getReflection() {
        return reflection;
    }

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
}

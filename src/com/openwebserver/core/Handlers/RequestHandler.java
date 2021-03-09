package com.openwebserver.core.Handlers;


import com.openwebserver.core.Annotations.Session;
import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Objects.Headers.Header;
import com.openwebserver.core.Objects.Headers.Headers;
import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.Objects.Response;
import com.openwebserver.core.Routing.Route;
import com.openwebserver.core.Security.Authorization.Authorizor;
import com.openwebserver.core.Security.CORS.Policy;
import com.openwebserver.core.Security.CORS.PolicyManager;
import com.openwebserver.core.Sessions.SessionManager;
import com.openwebserver.core.WebException;

import java.util.function.Consumer;


public class RequestHandler extends Route implements RouteRegister{

    private ContentHandler contentHandler;
    private SessionHandler sessionHandler = (annotation, session) -> session.hasRequired(annotation.require());
    private Session sessionSpecification;
    private final Headers headers = new Headers();

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
        if(needsAuthentication() && !getAuthorizor().authorize(request)){
            throw new WebException(Code.Unauthorized,"Invalid Token").addRequest(request);
        }
        try {
            SessionManager.bind(sessionSpecification, request);
        }catch (com.openwebserver.core.Sessions.Session.SessionException e){
            if(sessionSpecification != null && !sessionSpecification.redirect().equals("")){
                return Response.simple(Code.Temporary_Redirect).addHeader(new Header("Location", sessionSpecification.redirect()));
            }
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
    public void setSessionHandler(SessionHandler handler){
        this.sessionHandler = handler;
    }

    public void setSessionSpecification(Session sessionSpecification) {
        this.sessionSpecification = sessionSpecification;
    }

    public  SessionHandler getSessionHandler() {
        return sessionHandler;
    }
    //endregion

    //region CORS
    private Policy policy;
    private RequestHandler CORS_handler;

    public void setCORSPolicy(String policyName) {
        if(policyName == null){
            return;
        }
        try {
            this.policy = PolicyManager.getPolicy(policyName);
        } catch (PolicyManager.PolicyException.NotFound notFound) {
            notFound.printStackTrace();
        }
        if(policy != null){
            headers.addAll(policy.pack());
            CORS_handler = new RequestHandler(new Route(this.getPath(), Method.OPTIONS), request -> Response.simple(Code.No_Content).addHeaders(policy.pack()));
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
    private Authorizor authorizor;
    public void setAuthorizor(Authorizor authorizor){
        if(needsAuthentication()) {
            this.authorizor = authorizor;
        }
    }

    protected Authorizor getAuthorizor() {
        return authorizor;
    }
    //endregion

    @Override
    public void register(Consumer<RequestHandler> routeConsumer) {
        routeConsumer.accept(this);
        if(CORS_handler != null){
            routeConsumer.accept(CORS_handler);
        }
    }



}

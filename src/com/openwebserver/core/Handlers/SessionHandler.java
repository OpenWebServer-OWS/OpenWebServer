package com.openwebserver.core.Handlers;

import com.openwebserver.core.Security.Sessions.Annotations.Session;

import com.openwebserver.core.Objects.Request;

public interface SessionHandler {

    boolean check(Session annotation, com.openwebserver.core.Security.Sessions.Session session);

    default com.openwebserver.core.Security.Sessions.Session.SessionException decline(Request request, Throwable t){
        return new com.openwebserver.core.Security.Sessions.Session.SessionException(t.getMessage());
    }

}

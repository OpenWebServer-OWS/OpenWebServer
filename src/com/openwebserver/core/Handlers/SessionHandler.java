package com.openwebserver.core.Handlers;

import com.openwebserver.core.Annotations.Session;

import com.openwebserver.core.Objects.Request;

public interface SessionHandler {

    boolean check(Session annotation, com.openwebserver.core.Sessions.Session session);

    default com.openwebserver.core.Sessions.Session.SessionException decline(Request request, Throwable t){
        return new com.openwebserver.core.Sessions.Session.SessionException(t.getMessage());
    }

}

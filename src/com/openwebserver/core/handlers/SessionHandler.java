package com.openwebserver.core.handlers;

import com.openwebserver.core.security.sessions.annotations.Session;

import com.openwebserver.core.objects.Request;

public interface SessionHandler {

    boolean check(Session annotation, com.openwebserver.core.security.sessions.Session session);

    default com.openwebserver.core.security.sessions.Session.SessionException decline(Request request, Throwable t){
        return new com.openwebserver.core.security.sessions.Session.SessionException(t.getMessage());
    }

}

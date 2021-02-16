package com.openwebserver.core.Sessions;


import com.openwebserver.core.Objects.Headers.Header;
import com.openwebserver.core.Objects.Request;

import java.util.Date;
import java.util.HashMap;


public class SessionManager{

    public static SessionManager manager = new SessionManager();
    private final HashMap<String, Session> sessions = new HashMap<>();

    private SessionManager(){}

    public static void register(Session s){
        manager.sessions.put(s.getId(), s);
    }

    public static Header revoke(String id){
        try {
            return manager.sessions.get(id).clear();
        }finally {
            manager.sessions.remove(id);
        }

    }

    public static HashMap<String,Session> getSessions(){
        return (HashMap<String, Session>) manager.sessions.clone();
    }

    public static void setIdentifier(String name){
        Session.name = name;
    }

    public static void bind(com.openwebserver.core.Annotations.Session annotation, Request request) throws Session.SessionException {
        final Session.SessionException[] exception = {null};
        boolean hasActive = request.headers.tryGet("Cookie", header -> {
            if(header.contains(Session.name)){
                try {
                    Session s = get(header.get(Session.name).getValue());
                    request.getHandler().getSessionHandler().apply(annotation, s);
                    request.session = s;
                    return;
                } catch (Session.SessionException.SessionNotFoundException e) {
                    exception[0] = e;
                }
            }
            exception[0] = new Session.SessionException.SessionNotFoundException();
        });
        if(!hasActive){
            throw new Session.SessionException.SessionNotFoundException();
        }
        if(exception[0] != null){
            throw exception[0];
        }
    }

    public static Session get(String id) throws Session.SessionException.SessionNotFoundException {
        Session s =  manager.sessions.get(id);
        if(s == null){
            throw new Session.SessionException.SessionNotFoundException();
        }
        s.lastSeen = new Date();
        return s;
    }
}

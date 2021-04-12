package com.openwebserver.core.Security.Sessions;



import com.openwebserver.core.Objects.Headers.Header;
import com.openwebserver.core.Objects.Request;

import java.util.Date;
import java.util.HashMap;


public class SessionManager{

    public final static SessionManager manager = new SessionManager();
    private final HashMap<String, com.openwebserver.core.Security.Sessions.Session> sessions = new HashMap<>();

    private SessionManager(){}

    public static void register(com.openwebserver.core.Security.Sessions.Session s){
        manager.sessions.put(s.getId(), s);
    }

    public static Header revoke(String id){
        try {
            if(manager.sessions.containsKey(id)) {
                return manager.sessions.get(id).clear();
            }
            return com.openwebserver.core.Security.Sessions.Session.revoke(id);
        }finally {
            manager.sessions.remove(id);
        }
    }

    public static HashMap<String, com.openwebserver.core.Security.Sessions.Session> getSessions(){
        return (HashMap<String, com.openwebserver.core.Security.Sessions.Session>) manager.sessions.clone();
    }

    public static void setIdentifier(String name){
        com.openwebserver.core.Security.Sessions.Session.name = name;
    }

    public static void bind(com.openwebserver.core.Security.Sessions.Annotations.Session annotation, Request request) throws com.openwebserver.core.Security.Sessions.Session.SessionException {
        if(annotation == null){
            return;
        }
        final com.openwebserver.core.Security.Sessions.Session.SessionException[] exception = {null};
        boolean hasActive = request.headers.tryGet("Cookie", header -> {
            if(header.contains(com.openwebserver.core.Security.Sessions.Session.name)){
                try {
                    com.openwebserver.core.Security.Sessions.Session s = get(header.get(com.openwebserver.core.Security.Sessions.Session.name).getValue());
                    if(request.getHandler().getSessionHandler().check(annotation, s)){
                        request.session = s;
                        request.SESSION = s.store;
                        return;
                    }else {
                        exception[0] = new com.openwebserver.core.Security.Sessions.Session.SessionException("Invalid required parameters");
                        return;
                    }
                } catch (com.openwebserver.core.Security.Sessions.Session.SessionException.SessionNotFoundException e) {
                    exception[0] = e;
                    return;
                }
            }
            exception[0] = new com.openwebserver.core.Security.Sessions.Session.SessionException.SessionNotFoundException();
        });
        if(!hasActive){
            throw new com.openwebserver.core.Security.Sessions.Session.SessionException.SessionNotFoundException();
        }
        if(exception[0] != null){
            throw request.getHandler().getSessionHandler().decline(request, exception[0]);
        }
    }

    public static com.openwebserver.core.Security.Sessions.Session get(String id) throws com.openwebserver.core.Security.Sessions.Session.SessionException.SessionNotFoundException {
        com.openwebserver.core.Security.Sessions.Session s =  manager.sessions.get(id);
        if(s == null){
            throw new Session.SessionException.SessionNotFoundException();
        }
        s.lastSeen = new Date();
        return s;
    }
}

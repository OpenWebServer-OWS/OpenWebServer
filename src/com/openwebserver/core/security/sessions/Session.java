package com.openwebserver.core.security.sessions;


import com.openwebserver.core.content.Code;
import com.openwebserver.core.objects.Cookie;
import com.openwebserver.core.objects.headers.Header;
import com.openwebserver.core.WebException;

import java.util.*;
import java.util.function.Function;

public class Session extends Cookie {

    public static String name = "OWS_SESSION";
    public final String id;
    public final HashMap<String, Object> store = new HashMap<>();
    public Date lastSeen;

    public Session(String id, String path) {
        super(name,id);
        this.id = id;
        setPath(path);
        SessionManager.register(this);
    }

    public Session(){
        this(UUID.randomUUID().toString(), "/");
        SessionManager.register(this);
    }

    private Session(String id){
        super(name, id);
        this.id = id;
    }

    public Session setPath(String path){
        super.setPath(path);
        return this;
    }

    public String getId() {
        return id;
    }

    public Session store(String key, Object o){
        store.put(key,o);
        return this;
    }

    public Session storeAll(Map<String, ?> objectMap){
        store.putAll(objectMap);
        return this;
    }

    @SafeVarargs
    public final <T> Session  storeAll(Function<T, String> indexFunction, T... objects){
        for (T object : objects) {
            store.put(indexFunction.apply(object), object);
        }
        return this;
    }

    public HashMap<String, Object> getStore() {
        return store;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public Header revoke() {
        return SessionManager.revoke(this.getId());
    }

    public static Header revoke(String id){
        return new Session(id).clear();
    }

    public boolean hasRequired(String ... required){
        return store.keySet().containsAll(Arrays.asList(required));
    }

    public static class SessionException extends WebException {
        public SessionException(String message) {
            super(Code.Unauthorized, message);
        }

        public SessionException(Throwable t) {
            super(t);
        }

        public static class SessionNotFoundException extends SessionException {
            public SessionNotFoundException() {
                super("Session not found");
            }
        }

    }
}
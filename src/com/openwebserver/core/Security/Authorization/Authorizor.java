package com.openwebserver.core.Security.Authorization;

import com.openwebserver.core.Content.Code;
import com.openwebserver.core.Objects.Request;
import com.openwebserver.core.WebException;

import java.util.function.BiFunction;

public interface Authorizor<T> {

    public default boolean authorize(Request request) throws AuthorizorException {
        return getValidator().apply(request, decode(request));
    }
    
    public T decode(Request request) throws AuthorizorException;

    public void setValidator(BiFunction<Request, T, Boolean> validator);

    public BiFunction<Request, T, Boolean> getValidator();

    public static class AuthorizorException extends WebException{
        public AuthorizorException(String message) {
            super(Code.Unauthorized, message);
        }
    }

}

package com.openwebserver.core.security.authorization;

import com.openwebserver.core.http.content.Code;
import com.openwebserver.core.objects.Request;
import com.openwebserver.core.WebException;

import java.util.function.BiFunction;

public interface Authorizer<T> {

    default boolean authorize(Request request) throws AuthorizerException {
        return getValidator().apply(request, decode(request));
    }
    
    T decode(Request request) throws AuthorizerException;

    void setValidator(BiFunction<Request, T, Boolean> validator);

    BiFunction<Request, T, Boolean> getValidator();

    class AuthorizerException extends WebException {
        public AuthorizerException(String message) {
            super(Code.Unauthorized, message);
        }
    }

}

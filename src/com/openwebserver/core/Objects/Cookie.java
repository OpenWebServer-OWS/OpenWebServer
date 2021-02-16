package com.openwebserver.core.Objects;


import com.openwebserver.core.Objects.Headers.Attribute;
import com.openwebserver.core.Objects.Headers.Header;

import java.util.Date;

public class Cookie extends Header {

    public Cookie(String key, String value) {
        super("Set-Cookie", null);
        add(key,value);
    }

    protected Cookie add(String key, String value){
        add(new Attribute<>(key,value));
        return this;
    }

    public Cookie Expires(Date date){
        return add("Expires",date.toInstant().toString());
    }

    public Cookie setDomain(String domain){
        return add("Domain", domain);
    }

    public Cookie setPath(String path){
        return add("Path", path);
    }

    public Cookie Secure(){
        return add("Secure",null);
    }

    public Cookie HttpOnly(){
        return add("HttpOnly",null);
    }

    public Cookie MaxAge(int max){
        return add("Max-Age", String.valueOf(max));
    }

    public Cookie SameSite(String mode){
        return add("SameSite", mode);
    }

    public Cookie clear(){
        return MaxAge(-1);
    }

    public static Header Clear(String name){
        return new Cookie(name, "").MaxAge(-1);
    }



}

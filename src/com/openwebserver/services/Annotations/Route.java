package com.openwebserver.services.Annotations;


import com.openwebserver.core.Objects.Request;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface Route{
    String path();
    com.openwebserver.core.Routing.Route.Method method() default com.openwebserver.core.Routing.Route.Method.UNDEFINED;
    String[] require() default {};


}

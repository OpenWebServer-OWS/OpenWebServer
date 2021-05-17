package com.openwebserver.services.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface Route{

    String path();
    com.openwebserver.core.routing.Route.Method method() default com.openwebserver.core.routing.Route.Method.UNDEFINED;
    String[] require() default {};


}

package com.openwebserver.core.security.CORS;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface CORS {
    /**
     * @return Policy name
     */
    String value();
    boolean overrideOrigin() default false;
}

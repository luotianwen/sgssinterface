package com.sgss.www.swagger.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiResponse {
    int code();

    String message();

    Class<?> response() default Void.class;

    String reference() default "";

    ResponseHeader[] responseHeaders() default {@ResponseHeader(
            name = "",
            response = Void.class
    )};

    String responseContainer() default "";
}

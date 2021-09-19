package com.example.bukubesar.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XlsxWriteable {
    int order() default 0;
    String headerName() default "";
    Class<?> type() default Object.class;
}

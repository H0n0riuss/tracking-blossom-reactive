package io.github.honoriuss.blossom.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Track {
    String[] parameterNames() default {};

    String returnName() default "";

    String optKey() default "";

    String optArg() default "";
}

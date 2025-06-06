package io.github.honoriuss.blossom.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TrackResult { //TODO nur beim einfachen Datentypen, ansonsten daraus ein komplettes Object machen
    String returnColName() default "";
}

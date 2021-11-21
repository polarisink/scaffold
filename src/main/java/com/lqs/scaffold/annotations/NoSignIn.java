package com.lqs.scaffold.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * No Sign In Declaration.
 * <p>
 * Put on the controller methods.
 *
 * @author Bill
 * @version 1.0
 * @since 2019-10-24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NoSignIn {
}

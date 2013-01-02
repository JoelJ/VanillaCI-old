package com.vanillaci.slave.restapi.annotations;

import com.vanillaci.slave.restapi.core.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * User: Joel Johnson
 * Date: 12/12/12
 * Time: 9:52 PM
 */
@java.lang.annotation.Target({ElementType.METHOD})
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
public @interface EndPoint {
	String value() default "";
	HttpMethod[] accepts() default { HttpMethod.GET };
}

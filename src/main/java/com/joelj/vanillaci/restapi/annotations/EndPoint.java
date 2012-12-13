package com.joelj.vanillaci.restapi.annotations;

import com.joelj.vanillaci.restapi.core.HttpMethod;

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
	String value();
	HttpMethod[] accepts() default { HttpMethod.GET };
}

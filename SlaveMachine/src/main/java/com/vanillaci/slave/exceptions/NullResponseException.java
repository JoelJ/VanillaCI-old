package com.vanillaci.slave.exceptions;

import com.vanillaci.core.HttpMethod;

import java.io.IOException;

/**
 * User: Joel Johnson
 * Date: 12/12/12
 * Time: 10:25 PM
 */
public class NullResponseException extends IOException {
	public NullResponseException(String pathInfo, HttpMethod httpMethod) {
		super("Response cannot be null. Path: " + pathInfo + " Http Method: " + httpMethod);
	}
}

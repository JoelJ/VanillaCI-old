package com.vanillaci.slave.exceptions;

import com.vanillaci.core.HttpMethod;

import java.io.IOException;

/**
 * User: Joel Johnson
 * Date: 12/12/12
 * Time: 10:28 PM
 */
public class UnboundUrlException extends IOException {
	public UnboundUrlException(String serviceName, String url, HttpMethod method) {
		super(url + " is unbound for method " + method + " in " + serviceName);
	}
}

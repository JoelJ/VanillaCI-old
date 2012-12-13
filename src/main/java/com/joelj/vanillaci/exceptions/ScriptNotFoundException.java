package com.joelj.vanillaci.exceptions;

/**
 * User: Joel Johnson
 * Date: 12/7/12
 * Time: 8:07 PM
 */
public class ScriptNotFoundException extends RuntimeException {
	public ScriptNotFoundException(String message) {
		super(message);
	}
}

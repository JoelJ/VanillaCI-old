package com.vanillaci.slave.exceptions;

import java.io.IOException;

/**
 * User: Joel Johnson
 * Date: 12/19/12
 * Time: 11:16 PM
 */
public class InvalidResponseException extends IOException {
	public InvalidResponseException(Class<?> expected, Class<?> actual) {
		super("Invalid Response. Excepted " + expected.getCanonicalName() + " but was " + actual.getCanonicalName());
	}
}

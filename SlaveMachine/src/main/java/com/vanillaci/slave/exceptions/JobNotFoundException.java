package com.vanillaci.slave.exceptions;

import java.io.File;

/**
 * User: Joel Johnson
 * Date: 2/3/13
 * Time: 9:19 PM
 */
public class JobNotFoundException extends RuntimeException {
	public JobNotFoundException(String name) {
		super(name + " not found.");
	}
}

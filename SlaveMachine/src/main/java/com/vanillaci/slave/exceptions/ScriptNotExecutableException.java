package com.vanillaci.slave.exceptions;

import java.io.File;

/**
 * User: Joel Johnson
 * Date: 12/7/12
 * Time: 9:50 PM
 */
public class ScriptNotExecutableException extends RuntimeException {
	public ScriptNotExecutableException(File file) {
		super(file.getAbsolutePath());
	}
}

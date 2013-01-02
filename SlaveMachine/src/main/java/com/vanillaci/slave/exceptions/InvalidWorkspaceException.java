package com.vanillaci.slave.exceptions;

import java.io.File;

/**
 * User: Joel Johnson
 * Date: 12/11/12
 * Time: 9:45 PM
 */
public class InvalidWorkspaceException extends RuntimeException {
	public InvalidWorkspaceException(File workspaceDir) {
		super(workspaceDir.getAbsolutePath() + " was an invalid directory");
	}
}

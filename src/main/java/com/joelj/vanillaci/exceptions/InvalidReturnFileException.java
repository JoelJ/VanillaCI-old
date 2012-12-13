package com.joelj.vanillaci.exceptions;

import java.io.File;
import java.io.IOException;

/**
 * User: Joel Johnson
 * Date: 12/11/12
 * Time: 10:17 PM
 */
public class InvalidReturnFileException extends Exception {
	public InvalidReturnFileException(File file, IOException e) {
		super(file.getPath() + " was in an invalid format for files.", e);
	}
}

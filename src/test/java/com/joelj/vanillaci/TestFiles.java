package com.joelj.vanillaci;

import java.io.File;

/**
 * User: Joel Johnson
 * Date: 12/7/12
 * Time: 10:57 PM
 */
public class TestFiles {
	public static File getResourceDirectory() {
		return new File("src/test/resources");
		// When running from IntelliJ, the working dir folder is different than Maven.
		// This is probably a stupid workaround. But it works.
//		File current = new File(".");
//		String[] files = current.list();
//		if(files != null) {
//			for (String file : files) {
//				if("src".equals(file)) {
//					return new File("src/test/resources");
//				}
//			}
//		}
//		return new File("VanillaCI/src/test/resources");
	}
}

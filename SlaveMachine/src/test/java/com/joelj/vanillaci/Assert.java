package com.joelj.vanillaci;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Joel Johnson
 * Date: 1/1/13
 * Time: 4:41 PM
 */
public class Assert extends org.testng.Assert {
	public static void assertMatches(String actual, String expectedPattern, String message) {
		assertMatches(actual, Pattern.compile(expectedPattern), message);
	}

	public static void assertMatches(String actual, Pattern expectedPattern, String message) {
		Matcher matcher = expectedPattern.matcher(actual);
		if(!matcher.find()) {
			String errorMessage = "Expected to match regular expression `" + expectedPattern.pattern() + "` Actual: " + actual;
			if(message != null) {
				errorMessage = message + "\n" + errorMessage;
			}
			fail(errorMessage);
		}
	}
}

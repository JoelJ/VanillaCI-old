package com.joelj.vanillaci.util;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Joel Johnson
 * Date: 1/1/13
 * Time: 5:53 PM
 */
public class UuidUtil {
	private static final Pattern uuidPattern = Pattern.compile("[A-Za-z0-9]{8}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{12}");

	public static String generateUuid() {
		return UUID.randomUUID().toString();
	}

	public static boolean isUuid(String input) {
		Matcher matcher = uuidPattern.matcher(input);
		return matcher.matches();
	}
}

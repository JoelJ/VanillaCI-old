package com.vanillaci.slave.util;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: Joel Johnson
 * Date: 12/7/12
 * Time: 5:37 PM
 */
public class Confirm {
	public static File isDirectory(String fieldName, File directory) {
		notNull(fieldName, directory);
		if(!directory.isDirectory()) {
			throw new IllegalArgumentException(fieldName + " must be a directory");
		}
		return directory;
	}

	public static File isFile(String fieldName, File file) {
		notNull(fieldName, file);
		if(!file.isFile()) {
			throw new IllegalArgumentException(fieldName + " must be a file");
		}
		return file;
	}

	/**
	 * Throws an IllegalArgumentException if the given object is null.
	 * @param fieldName The name of the field being verified. Used in the error message.
	 * @param obj The object to verify.
	 * @return The non-null object passed in.
	 */
	public static <T> T notNull(String fieldName, T obj) {
		if(obj == null) {
			throw new IllegalArgumentException(fieldName + " cannot be null");
		}
		return obj;
	}

	/**
	 * Verifies the given list is not null. If it is null, it returns an empty list.
	 * @param list The list to verify.
	 * @return If null is given, an empty collection is returned. Otherwise, the given collection is returned untouched.
	 */
	public static <T> List<T> notNull(List<T> list) {
		if(list == null) {
			return Collections.emptyList();
		}
		return list;
	}

	public static <K,V> Map<K, V> notNull(Map<K, V> parameters) {
		if(parameters == null) {
			return Collections.emptyMap();
		}
		return parameters;
	}

	/**
	 * Throws an IllegalArgumentException if the given value is less-than or equal-to zero.
	 * @param fieldName The name of the field. Used in the error message.
	 * @param value The value to be checked.
	 * @return The given value if it's greater than zero.
	 */
	public static int positive(String fieldName, int value) {
		if(value <= 0) {
			throw new IllegalArgumentException(fieldName +" must be positive, but was " + value);
		}
		return value;
	}

	public static <T> T instanceOf(String fieldName, Class<T> type, Object value) {
		notNull("type", type);
		notNull(fieldName, value);
		if(type.isAssignableFrom(value.getClass())) {
			return type.cast(value);
		}
		throw new IllegalArgumentException(fieldName + " must be a " + type.getCanonicalName());
	}

	public static <T> T instanceOfOrNull(String fieldName, Class<T> type, Object value) {
		if(value == null) {
			return null;
		}
		return instanceOf(fieldName, type, value);
	}
}

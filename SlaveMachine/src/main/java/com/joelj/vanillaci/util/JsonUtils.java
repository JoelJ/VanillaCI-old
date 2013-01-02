package com.joelj.vanillaci.util;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;

/**
 * User: Joel Johnson
 * Date: 1/1/13
 * Time: 6:43 PM
 */
public class JsonUtils {
	private static JsonFactory factory = new JsonFactory();
	private static ObjectMapper mapper = new ObjectMapper(factory);

	public static <T> T parse(String json, TypeReference<T> typeReference) throws IOException {
		Confirm.notNull("json", json);
		Confirm.notNull("typeReference", typeReference);
		return mapper.readValue(json, typeReference);
	}

	public static <T> T parse(String json, Class<T> typeReference) throws IOException {
		Confirm.notNull("json", json);
		Confirm.notNull("typeReference", typeReference);
		return mapper.readValue(json, typeReference);
	}
}

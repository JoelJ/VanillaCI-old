package com.vanillaci.slave.restapi.core;

import com.google.common.collect.ImmutableMap;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * User: Joel Johnson
 * Date: 12/12/12
 * Time: 10:22 PM
 */
public class ServiceResponse {
	public static final String ERROR = "error";
	public static final String SUCCESS = "result";

	private String outermostKey;
	private Object object;

	public ServiceResponse(Throwable t) {
		this(error(t), ERROR);
	}

	private static Map<String, String> error(Throwable t) {
		t.printStackTrace();
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		builder.put("type", t.getClass().getCanonicalName());
		if(t.getMessage() != null && !t.getMessage().isEmpty()) {
			builder.put("message", t.getMessage());
		}
		return builder.build();
	}

	public ServiceResponse(Serializable object) {
		this(object, SUCCESS);
	}

	public ServiceResponse(Collection<? extends Serializable> object) {
		this(object, SUCCESS);
	}

	public ServiceResponse(Map<? extends Serializable, ? extends Serializable> object) {
		this(object, SUCCESS);
	}

	private ServiceResponse(Object object, String outermostKey) {
		this.object = object;
		this.outermostKey = outermostKey;
	}

	public void writeJson(OutputStream outputStream) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(outputStream, ImmutableMap.of(this.outermostKey, this.object));
	}
}

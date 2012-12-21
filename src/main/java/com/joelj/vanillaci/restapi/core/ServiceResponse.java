package com.joelj.vanillaci.restapi.core;

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
	private Object object;

	public ServiceResponse(Throwable t) {
		this((Object)error(t));
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
		this((Object)object);
	}

	public ServiceResponse(Collection<? extends Serializable> object) {
		this((Object)object);
	}

	public ServiceResponse(Map<? extends Serializable, ? extends Serializable> object) {
		this((Object)object);
	}

	private ServiceResponse(Object object) {
		this.object = object;
	}

	public void writeJson(OutputStream outputStream) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(outputStream, this.object);
	}
}

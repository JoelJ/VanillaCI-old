package com.joelj.vanillaci.restapi.core;

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

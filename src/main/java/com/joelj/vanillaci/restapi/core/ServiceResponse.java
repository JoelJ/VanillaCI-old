package com.joelj.vanillaci.restapi.core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: Joel Johnson
 * Date: 12/12/12
 * Time: 10:22 PM
 */
public class ServiceResponse {
	private JsonElement object;

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
		Gson gson = new Gson();
		this.object = gson.toJsonTree(object);
	}

	public JsonElement toJson() {
		return object;
	}
}

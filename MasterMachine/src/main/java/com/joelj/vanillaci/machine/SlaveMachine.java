package com.joelj.vanillaci.machine;

import java.io.Serializable;
import java.net.URI;

/**
 * User: Joel Johnson
 * Date: 12/22/12
 * Time: 4:25 PM
 */
public class SlaveMachine implements Serializable {
	private final String name;
	private final String description;
	private final URI location;

	public SlaveMachine(String name, String description, URI location) {
		this.name = name;
		this.description = description;
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public URI getLocation() {
		return location;
	}
}

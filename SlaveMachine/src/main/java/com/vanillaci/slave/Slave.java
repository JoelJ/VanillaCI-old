package com.vanillaci.slave;

import com.google.common.collect.ImmutableList;
import com.vanillaci.slave.util.Confirm;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.List;

/**
 * User: Joel Johnson
 * Date: 2/2/13
 * Time: 3:22 PM
 */
public class Slave implements Serializable {
	private final String name;
	private final URI location;
	private final List<String> labels;

	public Slave(String name, URI location, Collection<String> labels) {
		this.name = name;
		this.location = location;
		this.labels = ImmutableList.copyOf(Confirm.notNull(labels));
	}

	public String getName() {
		return name;
	}

	public URI getLocation() {
		return location;
	}

	public List<String> getLabels() {
		return labels;
	}

	@Override
	public String toString() {
		return "Slave{" +
				"name='" + name + '\'' +
				", location=" + location +
				'}';
	}
}

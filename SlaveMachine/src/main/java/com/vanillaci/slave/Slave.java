package com.vanillaci.slave;

import com.google.common.collect.ImmutableList;
import com.vanillaci.slave.util.Confirm;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: Joel Johnson
 * Date: 2/2/13
 * Time: 3:22 PM
 */
public class Slave implements Serializable {
	private final String name;
	private final URI uri;
	private final List<String> labels;

	public Slave(String name, URI uri, Collection<String> labels) {
		this.name = name;
		this.uri = uri;
		this.labels = ImmutableList.copyOf(Confirm.notNull(labels));
	}

	public String getName() {
		return name;
	}

	public URI getUri() {
		return uri;
	}

	public List<String> getLabels() {
		return labels;
	}
}

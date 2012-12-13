package com.joelj.vanillaci.job;

import java.io.Serializable;

/**
 * User: Joel Johnson
 * Date: 12/8/12
 * Time: 5:05 PM
 */
public class Parameter implements Serializable {
	private final String name;
	private final String defaultValue;
	private final String description;

	public Parameter(String name, String defaultValue, String description) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "Parameter{" +
				"name='" + name + '\'' +
				", defaultValue='" + defaultValue + '\'' +
				", description='" + description + '\'' +
				'}';
	}
}

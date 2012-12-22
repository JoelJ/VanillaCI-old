package com.joelj.vanillaci.script;

import java.io.Serializable;

/**
 * Simply represents a script's name and hash.
 * Doesn't know anything about whether or not a script has been deployed or where it would be deployed to.
 * This is useful for when that's the only data you need or have about a script.
 * For example, the Job class doesn't care if a script has been deployed, it only cares about the name and hash.
 *
 * User: Joel Johnson
 * Date: 12/8/12
 * Time: 11:25 AM
 */
public class ScriptName implements Serializable {
	private final String name;
	private final String hash;

	/**
	 * Constructor used only for deserialization. Do not ever call this method.
	 */
	@Deprecated
	ScriptName() {
		name = null;
		hash = null;
	}

	public ScriptName(String name, String hash) {
		this.name = name;
		this.hash = hash;
	}

	public String getName() {
		return name;
	}

	public String getHash() {
		return hash;
	}

	@Override
	public String toString() {
		return "ScriptName{" +
				"name='" + getName() + '\'' +
				", hash='" + getHash() + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ScriptName)) return false;

		ScriptName that = (ScriptName) o;

		if (!hash.equals(that.hash)) return false;
		if (!name.equals(that.name)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		//IntelliJ generated hash code
		return (31 * name.hashCode()) + hash.hashCode();
	}
}

package com.vanillaci.slave.script;

import com.google.common.collect.ImmutableMap;
import com.vanillaci.slave.run.Status;
import com.vanillaci.slave.util.Confirm;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * User: Joel Johnson
 * Date: 12/7/12
 * Time: 8:28 PM
 */
public class ScriptManifest implements Serializable {
	private final Map<String, ?> map;

	public static ScriptManifest fromFile(File manifestFile) throws IOException {
		Confirm.isFile("manifestFile", manifestFile);

		FileInputStream fileInputStream = new FileInputStream(manifestFile);
		try {
			Yaml yaml = new Yaml();
			Object o = yaml.load(fileInputStream);
			return new ScriptManifest((Map<String, ?>) o);
		} finally {
			fileInputStream.close();
		}
	}

	private ScriptManifest(Map<String, ?> map) {
		this.map = ImmutableMap.copyOf(map);
	}

	public String getName() {
		return (String) map.get("name");
	}

	public String getVersion() {
		return String.valueOf(map.get("version"));
	}

	public Date getDate() {
		return (Date) map.get("date");
	}

	public String getDescription() {
		return (String) map.get("description");
	}

	public String getAuthor() {
		return (String) map.get("author");
	}

	public String getWebsite() {
		return (String) map.get("website");
	}

	public Status getStatus(int exitCode) {
		Object statusObj = map.get("status");
		if (statusObj != null && statusObj instanceof Map) {
			@SuppressWarnings("unchecked")
			Map status = (Map) statusObj;

			for (Object codes : status.keySet()) {
				String[] split = String.valueOf(codes).split("-", 2);
				int left = Integer.parseInt(split[0]);
				boolean found = false;
				if (split.length == 2) {
					int right = Integer.parseInt(split[1]);

					if (exitCode >= Math.min(left, right) && exitCode <= Math.max(left, right)) {
						found = true;
					}
				} else if(left == exitCode){
					found = true;
				}
				if(found) {
					Object value = status.get(codes);
					return Status.bestValueOf(String.valueOf(value));
				}
			}
		} else {
			if(exitCode != 0) {
				return Status.Failure;
			}
		}

		return Status.Success;
	}

	public Map<String, String> getParameters() {
		Object parametersObj = map.get("parameters");
		if(parametersObj == null || !(parametersObj instanceof Map)) {
			return Collections.emptyMap();
		}

		Map<?,?> parameters = (Map) parametersObj;
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		for (Map.Entry entry : parameters.entrySet()) {
			builder.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
		}
		return builder.build();
	}

	public Object get(String key) {
		return map.get(key);
	}

	@Override
	public String toString() {
		return "ScriptManifest{" +
				"name='" + getName() + '\'' +
				'}';
	}
}
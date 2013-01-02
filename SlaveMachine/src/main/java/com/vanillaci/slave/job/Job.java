package com.vanillaci.slave.job;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.vanillaci.slave.exceptions.InvalidWorkspaceException;
import com.vanillaci.slave.exceptions.UnhandledException;
import com.vanillaci.slave.run.Run;
import com.vanillaci.slave.script.Script;
import com.vanillaci.slave.script.ScriptName;
import com.vanillaci.slave.script.ScriptRepository;
import com.vanillaci.slave.util.Confirm;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * User: Joel Johnson
 * Date: 12/8/12
 * Time: 11:13 AM
 */
public class Job implements Serializable {
	private final String name;
	private final String description;
	private final List<Parameter> parameters;
	private final List<ScriptName> preScripts;
	private final List<ScriptName> scripts;
	private final List<ScriptName> postScripts;

	/**
	 * Constructor used only for deserialization. Do not ever call this method.
	 */
	@Deprecated
	Job() {
		name = null;
		description = null;
		parameters = null;
		preScripts = null;
		scripts = null;
		postScripts = null;
	}

	public Job(String name, String description, List<Parameter> parameters, List<ScriptName> preScripts, List<ScriptName> scripts, List<ScriptName> postScripts) {
		this.name = Confirm.notNull("name", name);
		this.description = Confirm.notNull("description", description);

		this.parameters = ImmutableList.copyOf(Confirm.notNull(parameters));
		this.preScripts = ImmutableList.copyOf(Confirm.notNull(preScripts));
		this.scripts = ImmutableList.copyOf(Confirm.notNull(scripts));
		this.postScripts = ImmutableList.copyOf(Confirm.notNull(postScripts));
	}

	public Run execute(File workspacesDir, ScriptRepository scriptRepository, int buildNumber, Map<String, String> parameters) {
		Map<String, String> environment = new HashMap<String, String>(parameters);
		for (Parameter parameter : this.parameters) {
			if(!environment.containsKey(parameter.getName())) {
				environment.put(parameter.getName(), parameter.getDefaultValue());
			}
		}

		List<Script> buildScripts = new LinkedList<Script>();
		for (ScriptName scriptName : preScripts) {
			Script script = scriptRepository.getScript(scriptName.getName(), scriptName.getHash());
			buildScripts.add(script);
		}
		for (ScriptName scriptName : scripts) {
			Script script = scriptRepository.getScript(scriptName.getName(), scriptName.getHash());
			buildScripts.add(script);
		}

		List<Script> postBuildScripts = new LinkedList<Script>();
		for (ScriptName scriptName : postScripts) {
			Script script = scriptRepository.getScript(scriptName.getName(), scriptName.getHash());
			postBuildScripts.add(script);
		}

		File workspace = new File(workspacesDir, getCleanName() + "/" + buildNumber + "/");
		if(!workspace.mkdirs()) {
			throw new InvalidWorkspaceException(workspace);
		}

		File log = new File(workspace, "__log");
		try {
			Files.touch(log);
		} catch (IOException e) {
			throw new UnhandledException(e);
		}

		Run run = new Run(this.getName(), buildNumber, workspace, log, buildScripts, postBuildScripts, environment);
		run.start();
		return run;
	}

	public String getName() {
		return name;
	}

	/**
	 * Returns the name of the Job that's been cleaned for a file name.
	 * @return The name with all characters that are not alpha-numeric with '_' characters.
	 */
	private String getCleanName() {
		return name.replaceAll("[^a-zA-Z0-9]", "_");
	}

	public String getDescription() {
		return description;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public List<ScriptName> getPreScripts() {
		return preScripts;
	}

	public List<ScriptName> getScripts() {
		return scripts;
	}

	public List<ScriptName> getPostScripts() {
		return postScripts;
	}

	public Set<ScriptName> getRequiredScripts() {
		ImmutableSet.Builder<ScriptName> requiredScripts = ImmutableSet.builder();
		requiredScripts.addAll(getPreScripts());
		requiredScripts.addAll(getScripts());
		requiredScripts.addAll(getPostScripts());
		return requiredScripts.build();
	}

	@Override
	public String toString() {
		return "Job{" +
				"name='" + name + '\'' +
				", description='" + description + '\'' +
				'}';
	}
}

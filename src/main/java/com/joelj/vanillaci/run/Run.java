package com.joelj.vanillaci.run;

import com.joelj.vanillaci.exceptions.InvalidReturnFileException;
import com.joelj.vanillaci.exceptions.UnhandledException;
import com.joelj.vanillaci.script.Script;
import com.joelj.vanillaci.util.Confirm;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;

/**
 * Represents a running job.
 *
 * User: Joel Johnson
 * Date: 12/8/12
 * Time: 11:46 AM
 */
public class Run implements Runnable {
	private final File workspace;
	private final File log;
	private final List<Script> buildScripts;
	private final List<Script> postBuildScripts;
	private final Map<String, String> environment;

	private transient Thread thread;
	private volatile Status status;
	private final String name;
	private final int buildNumber;
	private final String id;

	public Run(String name, int buildNumber, File workspace, File log, List<Script> buildScripts, List<Script> postBuildScripts, Map<String, String> environment) {
		this.name = name;
		this.id = name + "#" + buildNumber;
		this.buildNumber = buildNumber;
		this.workspace = Confirm.isDirectory("workspace", workspace);
		this.log = Confirm.isFile("log", log);
		this.buildScripts = Confirm.notNull(buildScripts);
		this.postBuildScripts = Confirm.notNull(postBuildScripts);
		this.environment = new HashMap<String, String>(Confirm.notNull(environment));

		this.status = Status.Success;
	}

	public void start() {
		if(thread != null) {
			throw new IllegalStateException("run was already run");
		}

		thread = new Thread(this);
		thread.start();
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public int getBuildNumber() {
		return buildNumber;
	}

	public File getWorkspace() {
		return workspace;
	}

	public File getLog() {
		return log;
	}

	public boolean isRunning() {
		return thread.isAlive();
	}

	public Status getStatus() {
		return status;
	}

	public List<Script> getBuildScripts() {
		return buildScripts;
	}

	public List<Script> getPostBuildScripts() {
		return postBuildScripts;
	}

	public Map<String, String> getEnvironment() {
		return environment;
	}

	@Override
	public void run() {
		OutputStream log; //TODO: wrap this is something easier to use
		try {
			log = new FileOutputStream(this.log);
		} catch (FileNotFoundException e) {
			setStatusIfWorse(Status.Error);
			throw new UnhandledException(e);
		}

		try {
			try {
				for (Script buildScript : buildScripts) {
					Status status = executeScript(log, buildScript);
					if(setStatusIfWorse(status)) {
						log.write((buildScript.getManifest().getName() + " changed status to " + status + "\n").getBytes());
					}
					if(status.isWorseThan(Status.Failure_Continue)) {
						break;
					}
				}
			} catch (Exception e) {
				setStatusIfWorse(Status.Error);
				throw new UnhandledException(e);
			}

			try {
				log.write(("Executing PostBuild scripts\n").getBytes());
			} catch (IOException ignore) {}

			for (Script buildScript : postBuildScripts) {
				try {
					Status status = executeScript(log, buildScript);
					if(setStatusIfWorse(status)) {
						log.write((buildScript.getManifest().getName() + " changed status to " + status + "\n").getBytes());
					}
				} catch (Exception e) {
					setStatusIfWorse(Status.Error);
					throw new UnhandledException(e);
				}
			}
		} finally {
			IOUtils.closeQuietly(log);
		}
	}

	private Status executeScript(OutputStream stream, Script buildScript) throws IOException, InterruptedException, InvalidReturnFileException {
		stream.write(("Executing script \"" + buildScript.getManifest().getName() + "\"\n").getBytes());
		int execute = buildScript.execute(Collections.<String>emptyList(), workspace, this.environment, stream);
		File returnEnvironmentValues = new File(workspace, "__results");
		if(returnEnvironmentValues.exists()) {
			injectResults(returnEnvironmentValues);
		}
		Status status = buildScript.getStatus(execute);
		stream.write((buildScript.getManifest().getName() + " " + status + "\n").getBytes());
		return status;
	}

	private void injectResults(File returnEnvironmentValues) throws InvalidReturnFileException {
		try {
			FileInputStream returnEnvironmentValuesStream = new FileInputStream(returnEnvironmentValues);
			try {
				Properties properties = new Properties();
				properties.load(returnEnvironmentValuesStream);
				for (Map.Entry<Object, Object> entry : properties.entrySet()) {
					if(entry.getKey() != null) {
						String key = (String) entry.getKey();
						String value = (String) entry.getValue();
						if(key != null) {
							if(value != null && !value.isEmpty()) {
								this.environment.put(key, value);
							} else {
								this.environment.remove(key);
							}
						}
					}
				}
			} finally {
				IOUtils.closeQuietly(returnEnvironmentValuesStream);
			}
		} catch (IOException e) {
			throw new InvalidReturnFileException(returnEnvironmentValues, e);
		}
	}

	private boolean setStatusIfWorse(Status status) {
		if(Status.Error.isWorseThan(this.getStatus())) {
			this.status = status;
			return true;
		}
		return false;
	}
}

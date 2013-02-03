package com.vanillaci.slave.restapi.config;

import com.vanillaci.slave.SlaveRepository;
import com.vanillaci.slave.exceptions.UnhandledException;
import com.vanillaci.slave.util.Logger;
import com.vanillaci.slave.script.ScriptRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * User: Joel Johnson
 * Date: 12/21/12
 * Time: 2:51 PM
 */
public class Config {
	private static final Logger LOG = Logger.getLogger(Config.class);
	private static final Properties properties = new Properties();
	private static final ScriptRepository scriptRepository;
	private static final File workspace;

	private static final SlaveRepository slaveRepository;

	static {
		String vanillaCiConfigPath = System.getenv("VANILLACI_CONFIG");
		if(vanillaCiConfigPath == null) {
			vanillaCiConfigPath = "config.properties";
		}

		File file = new File(vanillaCiConfigPath);
		if(file.exists() && file.isFile()) {
			FileInputStream fileInputStream = null;
			try {
				fileInputStream = FileUtils.openInputStream(file);
				properties.load(fileInputStream);
			} catch (IOException e) {
				throw new UnhandledException(e);
			} finally {
				IOUtils.closeQuietly(fileInputStream);
			}
		} else {
			LOG.warn("Config file " + file.getAbsolutePath() + " does not exist. Using defaults.");
		}

		File scriptRepoDir = new File(properties.getProperty("scriptrepository.path", "scriptRepo"));
		if(!scriptRepoDir.exists() && !scriptRepoDir.mkdirs()) {
			RuntimeException runtimeException = new RuntimeException("Could not create script repository directory: " + scriptRepoDir.getAbsolutePath());
			LOG.error("Could not create script repository directory: " + scriptRepoDir.getAbsolutePath());
			throw runtimeException; //Typically I don't like throwing exceptions in static init blocks, but this needs to be fatal.
		}
		scriptRepository = new ScriptRepository(scriptRepoDir);

		workspace = new File(properties.getProperty("workspace.path", "workspace"));
		if(!workspace.exists() && !workspace.mkdirs()) {
			throw new RuntimeException("Could not create workspace directory: " + workspace.getAbsolutePath());
		}

		slaveRepository = new SlaveRepository();
	}

	public static ScriptRepository getScriptRepository() {
		return scriptRepository;
	}

	public static File getWorkspaceDirectory() {
		return workspace;
	}

	public static SlaveRepository getSlaveRepository() {
		return slaveRepository;
	}
}

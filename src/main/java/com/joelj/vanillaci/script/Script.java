package com.joelj.vanillaci.script;

import com.joelj.vanillaci.exceptions.ScriptNotExecutableException;
import com.joelj.vanillaci.exceptions.UnhandledException;
import com.joelj.vanillaci.run.Status;
import com.joelj.vanillaci.util.Confirm;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * User: Joel Johnson
 * Date: 12/7/12
 * Time: 8:06 PM
 */
public class Script {
	private final static Logger log = Logger.getLogger(Script.class.getCanonicalName());
	private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("windows");

	private final String name;
	private final String hash;
	private final File scriptRootDir;
	private final File mainScriptFile;
	private final File manifestFile;
	private final ScriptManifest manifest;

	public Script(String name, String hash, File scriptRootDir) {
		this.name = Confirm.notNull("name", name);
		this.hash = Confirm.notNull("hash", hash);

		this.scriptRootDir = Confirm.isDirectory("scriptRootDir", scriptRootDir);

		File mainScriptFile;
		if (IS_WINDOWS) {
			mainScriptFile = new File(scriptRootDir, "main.bat");
		} else {
			mainScriptFile = new File(scriptRootDir, "main");
		}
		this.mainScriptFile = mainScriptFile;
		this.manifestFile = new File(scriptRootDir, "manifest.yaml");
		try {
			this.manifest = ScriptManifest.fromFile(manifestFile);
		} catch (IOException e) {
			throw new UnhandledException(e);
		}
	}

	/**
	 * Executes the script.
	 * <p/>
	 * The following environment variables are available to the script:
	 * <ul>
	 * <li>SCRIPT_ROOT: The root directory of the script.</li>
	 * <li>SCRIPT_NAME: The simple name of the script (Does not include the '.script' extension or the hash. ).</li>
	 * <li>SCRIPT_WORKSPACE: The working directory of the workspace.</li>
	 * </ul>
	 *
	 * @param args                           The args to be passed into the script.
	 * @param workspaceDirectory             The directory context to execute the script.
	 * @param additionalEnvironmentVariables Any addition environment variables to include in the script.
	 * @param output                         The stream to write STDOUT and STDERR to. Will be flushed, but not closed.
	 * @return The exit code of the script.
	 * @throws java.io.IOException          If an IO exception occurs when actually executing the script.
	 * @throws InterruptedException Thrown if the thread is interrupted.
	 */
	public int execute(List<String> args, File workspaceDirectory, Map<String, String> additionalEnvironmentVariables, OutputStream output) throws IOException, InterruptedException {
		Process exec = execute(args, workspaceDirectory, additionalEnvironmentVariables);

		InputStream inputStream = exec.getInputStream();
		try {
			IOUtils.copy(inputStream, output);
		} finally {
			IOUtils.closeQuietly(inputStream);
			output.flush();
		}

		return exec.waitFor();
	}

	private Process execute(List<String> args, File workspaceDirectory, Map<String, String> additionalEnvironmentVariables) throws IOException {
		Map<String, String> environmentVariables = new HashMap<String, String>(additionalEnvironmentVariables);
		environmentVariables.put("SCRIPT_ROOT", scriptRootDir.getAbsolutePath());
		environmentVariables.put("SCRIPT_NAME", getName());
		environmentVariables.put("SCRIPT_WORKSPACE", workspaceDirectory.getAbsolutePath());

		List<String> cmd = new ArrayList<String>(args.size() + 1);
		cmd.add(mainScriptFile.getAbsolutePath());
		cmd.addAll(args);

		if (!mainScriptFile.setExecutable(true)) {
			throw new ScriptNotExecutableException(mainScriptFile);
		}

		ProcessBuilder processBuilder = new ProcessBuilder(cmd);
		processBuilder.environment().putAll(environmentVariables);
		processBuilder.redirectErrorStream(true);
		processBuilder.directory(workspaceDirectory);
		return processBuilder.start();
	}

	private static boolean isProcessStillRunning(Process process) {
		try {
			process.exitValue();
			return false;
		} catch (IllegalThreadStateException e) {
			return true;
		}
	}

	private static String[] flatten(Map<String, String> environmentVariables) {
		String[] result = new String[environmentVariables.size()];
		int i = 0;
		for (Map.Entry<String, String> entry : environmentVariables.entrySet()) {
			result[i++] = entry.getKey() + "=" + entry.getValue();
		}
		return result;
	}

	public String getName() {
		return name;
	}

	public String getHash() {
		return hash;
	}

	public File getScriptRootDir() {
		return scriptRootDir;
	}

	public File getMainScriptFile() {
		return mainScriptFile;
	}

	public File getManifestFile() {
		return manifestFile;
	}

	public ScriptManifest getManifest() {
		return manifest;
	}

	/**
	 * Gets the status for the given exit code.
	 * @param exitCode The exit exit code to get the status for.
	 * @return The status as determined by the script's definition.
	 */
	public Status getStatus(int exitCode) {
		return manifest.getStatus(exitCode);
	}
}

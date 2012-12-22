package com.joelj.vanillaci.script;

import com.google.common.collect.ImmutableList;
import com.joelj.vanillaci.exceptions.ScriptNotFoundException;
import com.joelj.vanillaci.util.Confirm;
import com.joelj.vanillaci.util.HashUtils;
import com.joelj.vanillaci.util.TarUtils;

import java.io.*;
import java.text.MessageFormat;
import java.util.List;

/**
 * Represents a repository of scripts.
 * Allows you to deploy new scripts to be executed.
 *
 * User: Joel Johnson
 * Date: 12/7/12
 * Time: 5:21 PM
 */
public class ScriptRepository {
	private final File repositoryDirectory;

	public ScriptRepository(File repositoryDirectory) {
		this.repositoryDirectory = Confirm.isDirectory("repositoryDirectory", repositoryDirectory);
	}

	/**
	 * Adds the given script file to the script repository.
	 * @param name Name of the script in the repository.
	 * @param hash The hash of the given file. Used for version files in the repository.
	 * @param file The script to be added to the repository.
	 * @return True if the file was added to the repository.
	 * 		   False if the script was not added because it is already in the repository.
	 *
	 * @throws java.io.FileNotFoundException thrown if the given file doesn't exist.
	 * @throws java.io.IOException Thrown if the file couldn't be copied or the directory structure of the destination couldn't be created.
	 */
	public boolean addScript(String name, String hash, File file) throws IOException {
		boolean result;
		Confirm.notNull("name", name);
		Confirm.notNull("hash", hash);
		Confirm.notNull("file", file);

		// The hash provided should be the hash of the file.
		// Since the hash isn't to verify we don't *need* to enforce this when running.
		// But let's only assert because it's expected to match the file, but not necessary.
		assert hash.equals(HashUtils.sha(file)) : "expecting the given hash to match the actual hash of the file";

		File destinationScriptDir = createScriptFile(name, hash);
		if(destinationScriptDir.exists()) {
			//we already have this exact same script. We're done here.
			result = false;
		} else {
			if(!destinationScriptDir.mkdirs()) {
				throw new IOException("Couldn't create directory " + destinationScriptDir.getAbsolutePath() + " maybe there's a permissions problem.");
			}

			TarUtils.untar(file, destinationScriptDir);
			result = true;
		}
		return result;
	}

	/**
	 * Checks if the repository has the given script with the given hash.
	 * @param name Name of the script.
	 * @param hash Hash of the tarred script.
	 * @return True if the repository has the script deployed. Otherwise false.
	 */
	public boolean hasScript(String name, String hash) {
		File destinationScriptDir = createScriptFile(name, hash);
		return destinationScriptDir.exists();
	}

	/**
	 * Gets the script by name and hash.
	 * @param name Name of the script.
	 * @param hash Hash of the tarred script.
	 * @return The Script object representing the script in the repository.
	 * @throws ScriptNotFoundException If the requested script does not exist in the repository,
	 * 								   a ScriptNotFoundException will be thrown.
	 */
	public Script getScript(String name, String hash) throws ScriptNotFoundException {
		File scriptRootDir = createScriptFile(name, hash);
		if(!scriptRootDir.exists()) {
			throw new ScriptNotFoundException(MessageFormat.format("Could not find a script matching the name \"{0}\" and hash \"{1}\"", name, hash)); //TODO: Localize
		}
		if(!scriptRootDir.isDirectory()) {
			throw new ScriptNotFoundException(MessageFormat.format("Found the script, but it's not a directory. Expected a directory. ({0})", scriptRootDir.getPath())); //TODO: Localize
		}

		return new Script(name, hash, scriptRootDir);
	}

	/**
	 * Creates a file object for the given script name and hash. Does not check if the file actually exists.
	 * @param name Name of the script.
	 * @param hash Hash of the tarred script.
	 * @return A new file object representing the script with the given name/hash.
	 */
	private File createScriptFile(String name, String hash) {
		return new File(this.repositoryDirectory, generateScriptName(name, hash));
	}

	private String generateScriptName(String name, String hash) {
		return name + "-" + hash + ".script";
	}

	public List<ScriptName> getScripts() {
		ImmutableList.Builder<ScriptName> builder = ImmutableList.builder();
		String repoPath = repositoryDirectory.getAbsolutePath();

		File[] files = repositoryDirectory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory() && file.getName().endsWith(".script");
			}
		}); //TODO: make recursive

		for (File file : files) {
			if(file.isDirectory()) {
				String filePath = file.getAbsolutePath();
				String fileName = filePath.substring(repoPath.length()+1, filePath.length() - ".script".length());

				int dashIndex = fileName.lastIndexOf("-");
				String scriptName = fileName.substring(0, dashIndex);
				String hash = fileName.substring(dashIndex+1);
				builder.add(new ScriptName(scriptName, hash));
			}
		}
		return builder.build();
	}
}

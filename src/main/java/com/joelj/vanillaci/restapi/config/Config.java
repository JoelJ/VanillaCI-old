package com.joelj.vanillaci.restapi.config;

import com.google.common.io.Files;
import com.joelj.vanillaci.script.ScriptRepository;

import java.io.File;

/**
 * User: Joel Johnson
 * Date: 12/21/12
 * Time: 2:51 PM
 */
public class Config {
	private static class LazyScriptRepository {
		private static ScriptRepository scriptRepository = getScriptRepository();

		private static ScriptRepository getScriptRepository() {
			File scriptRepo = new File("scriptRepo");
			//noinspection ResultOfMethodCallIgnored
			scriptRepo.mkdirs();
			return new ScriptRepository(scriptRepo);
		}
	}

	private static class LazyWorkspace {
		private static File workspace = getScriptRepository();

		private static File getScriptRepository() {
			File scriptRepo = Files.createTempDir();
			scriptRepo.deleteOnExit();
			return scriptRepo;
		}
	}

	public static ScriptRepository getScriptRepository() {
		return LazyScriptRepository.scriptRepository;
	}

	public static File getWorkspaceDirectory() {
		return LazyWorkspace.workspace;
	}
}

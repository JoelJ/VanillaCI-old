package com.joelj.vanillaci.script;

import com.google.common.io.Files;
import com.joelj.vanillaci.TestFiles;
import com.joelj.vanillaci.exceptions.ScriptNotFoundException;
import com.joelj.vanillaci.util.HashUtils;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * User: Joel Johnson
 * Date: 12/7/12
 * Time: 5:18 PM
 */
public class ScriptRepositoryTest {
	private File repositoryDirectory;
	private File tempDir;
	private File demoRepoDirectory; // This should be read-only!.

	@BeforeTest
	public void setUp() {
		repositoryDirectory = Files.createTempDir();
		demoRepoDirectory = new File(TestFiles.getResourceDirectory(), "demoRepo");
	}

	@AfterTest
	public void tearDown() throws IOException {
		if(repositoryDirectory != null) {
			FileUtils.deleteDirectory(repositoryDirectory);
		}

		if(tempDir != null) {
			FileUtils.deleteDirectory(tempDir);
		}
	}

	@Test(expectedExceptions = {ScriptNotFoundException.class})
	public void getScript_wrongHash() {
		ScriptRepository scriptRepository = new ScriptRepository(demoRepoDirectory);
		scriptRepository.getScript("environment", "1234");
	}

	@Test(expectedExceptions = {ScriptNotFoundException.class})
	public void getScript_wrongName() {
		ScriptRepository scriptRepository = new ScriptRepository(demoRepoDirectory);
		scriptRepository.getScript("environment1", "123");
	}

	@Test
	public void getScript_existent() {
		ScriptRepository scriptRepository = new ScriptRepository(demoRepoDirectory);
		Script environment = scriptRepository.getScript("environment", "123");
		Assert.assertEquals("environment", environment.getName());
	}

	@Test
	public void addScript() throws Exception {
		ScriptRepository scriptRepository = new ScriptRepository(repositoryDirectory);
		File file = new File(TestFiles.getResourceDirectory(), "simple.script");

		tempDir = Files.createTempDir();
		File scriptToDeploy = new File(tempDir, file.getName());
		FileUtils.copyFile(file, scriptToDeploy);

		String sha = HashUtils.sha(scriptToDeploy);
		scriptRepository.addScript("simple", sha, scriptToDeploy);

		String expectedName = "simple-" + sha + ".script";

		File scriptDir = new File(repositoryDirectory, expectedName);
		Assert.assertTrue(scriptDir.exists(), "Script exists");
		Assert.assertTrue(scriptDir.isDirectory(), "Script is directory");

		File mainFile = new File(scriptDir, "main");
		Assert.assertTrue(mainFile.exists(), "Script main exists");
		Assert.assertTrue(mainFile.isFile(), "Script main is file");
	}

	@Test
	public void getScripts() {
		ScriptRepository scriptRepository = new ScriptRepository(demoRepoDirectory);
		List<ScriptName> scripts = scriptRepository.getScripts();
		Assert.assertEquals(scripts.size(), 3, "Expected to have given number of scripts. Perhaps a test script was added?");
		Assert.assertTrue(scripts.contains(new ScriptName("environment", "123")), "environment-123");
		Assert.assertTrue(scripts.contains(new ScriptName("exitCode", "1")), "exitCode-1");
		Assert.assertTrue(scripts.contains(new ScriptName("simple", "e3115ca80f0e206ae4cf342a953005ccf2f4bcef")), "simple-e311...");
//		Assert.assertTrue(scripts.contains(new ScriptName("nested/nested", "1")), "nested/nested-1"); //TODO
		System.out.println();
	}
}

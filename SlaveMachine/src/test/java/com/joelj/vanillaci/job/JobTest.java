package com.joelj.vanillaci.job;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.joelj.vanillaci.TestFiles;
import com.joelj.vanillaci.run.Run;
import com.joelj.vanillaci.run.Status;
import com.joelj.vanillaci.script.ScriptName;
import com.joelj.vanillaci.script.ScriptRepository;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * User: Joel Johnson
 * Date: 12/11/12
 * Time: 9:34 PM
 */
public class JobTest {
	private File demoRepoDirectory = new File(TestFiles.getResourceDirectory(), "demoRepo");
	private ScriptRepository scriptRepository = new ScriptRepository(demoRepoDirectory);
	private File workspaceDirectory;

	@BeforeTest
	public void setUp() {
		workspaceDirectory = Files.createTempDir();
	}

	@AfterTest
	public void tearDown() throws IOException {
		if (workspaceDirectory != null) {
			System.out.println(workspaceDirectory.getAbsolutePath());
			FileUtils.deleteDirectory(workspaceDirectory);
		}
	}

	@Test
	public void testExecute() throws Exception {
		List<Parameter> parameters = ImmutableList.of(
				new Parameter("SOME_INJECTED_VALUE", "Some default parameter", "This is a description")
		);

		List<ScriptName> preScripts = ImmutableList.of();
		List<ScriptName> scripts = ImmutableList.of(
				new ScriptName("environment", "123")
		);
		List<ScriptName> postScripts = ImmutableList.of();

		Job job = new Job(
				"Demo Job",
				"This is a demo job",
				parameters,
				preScripts,
				scripts,
				postScripts
		);
		Run run = job.execute(workspaceDirectory, scriptRepository, 10, ImmutableMap.<String, String>of());
		while (run.isRunning()) {
			Thread.sleep(100);
		}

		Assert.assertEquals(run.getStatus(), Status.Failure, "The script exits 1, which should cause the script to fail");

		@SuppressWarnings("unchecked")
		List<String> log = Files.readLines(run.getLog(), Charset.defaultCharset());

		Assert.assertEquals(log.get(0), "Executing script \"Simple Script\"");
		Assert.assertEquals(log.get(4), parameters.get(0).getDefaultValue());
	}

	@Test
	public void testMultipleSteps() throws Exception {
		List<Parameter> parameters = ImmutableList.of();

		List<ScriptName> preScripts = ImmutableList.of();
		List<ScriptName> scripts = ImmutableList.of(
				new ScriptName("exitCode", "1"),
				new ScriptName("exitCode", "1"),
				new ScriptName("exitCode", "1"),
				new ScriptName("exitCode", "1"),
				new ScriptName("exitCode", "1"),
				new ScriptName("exitCode", "1"),
				new ScriptName("exitCode", "1"),
				new ScriptName("exitCode", "1"),
				new ScriptName("exitCode", "1"),
				new ScriptName("exitCode", "1")
		);
		List<ScriptName> postScripts = ImmutableList.of();

		Job job = new Job(
				"Test Job",
				"This is a test job",
				parameters,
				preScripts,
				scripts,
				postScripts
		);

		Run run = job.execute(workspaceDirectory, scriptRepository, 10, ImmutableMap.<String, String>of("EXIT_CODE", "1"));
		while (run.isRunning()) {
			Thread.sleep(100);
		}

		@SuppressWarnings("unchecked")
		List<String> log = Files.readLines(run.getLog(), Charset.defaultCharset());
		for (String s : log) {
			System.out.println(s);
		}

		Assert.assertNotEquals(run.getStatus(), Status.Unstable, "If the job ends as unstable, that means the return value isn't working.");
		Assert.assertEquals(run.getStatus(), Status.Failure, "The job runs the same script several times, which increments the exit code until it finally goes into the Failure range.");
		Assert.assertNotEquals(run.getEnvironment().get("EXIT_CODE"), "6", "Should have only run until EXIT_CODE was 6.");
	}

	@Test
	public void testPostAlwaysRuns() throws Exception {
		List<Parameter> parameters = ImmutableList.of();

		List<ScriptName> preScripts = ImmutableList.of();
		List<ScriptName> scripts = ImmutableList.of(new ScriptName("exitCode", "1"));
		List<ScriptName> postScripts = ImmutableList.of(new ScriptName("exitCode", "1"));

		Job job = new Job(
				"Test Job",
				"This is a test job",
				parameters,
				preScripts,
				scripts,
				postScripts
		);

		Run run = job.execute(workspaceDirectory, scriptRepository, 11, ImmutableMap.<String, String>of("EXIT_CODE", "6"));
		while (run.isRunning()) {
			Thread.sleep(100);
		}

		@SuppressWarnings("unchecked")
		List<String> log = Files.readLines(run.getLog(), Charset.defaultCharset());
		Assert.assertEquals(log.get(2), "Exit Code Script changed status to Failure", "First script should fail the build");
		Assert.assertEquals(run.getStatus(), Status.Failure, "Build should have failed");

		Assert.assertEquals(log.get(3), "Executing PostBuild scripts", "Make sure we still execute PostBuild");
		Assert.assertEquals(log.get(4), "Executing script \"Exit Code Script\"", "Make sure we execute something after the status was set to Failure");
	}
}

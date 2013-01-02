package com.vanillaci.slave.script;

import com.google.common.io.Files;
import com.vanillaci.slave.TestFiles;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Joel Johnson
 * Date: 12/7/12
 * Time: 8:43 PM
 */
public class ScriptTest {
	private static final File repoDir = new File(TestFiles.getResourceDirectory(), "demoRepo");
	private static final File simpleScript = new File(repoDir, "simple-e3115ca80f0e206ae4cf342a953005ccf2f4bcef.script");
	private static final File environmentScript = new File(repoDir, "environment-123.script");

	private File workspace;

	@BeforeTest
	public void setUp() {
		workspace = Files.createTempDir();
	}

	@AfterTest
	public void tearDown() throws IOException {
		if(workspace != null) {
			FileUtils.deleteDirectory(workspace);
		}
	}

	@Test
	public void loadingManifest() throws Exception {
		ScriptManifest scriptManifest = ScriptManifest.fromFile(new File(simpleScript, "manifest.yaml"));
		Assert.assertEquals(scriptManifest.getName(), "Simple Script");
		Assert.assertEquals(scriptManifest.getVersion(), "1.0");
		Assert.assertEquals(scriptManifest.getDate().getTime(), 1354838400000L);
		Assert.assertEquals(scriptManifest.getDescription(), "Just a simple script. It echos a string and all the parameters.");
		Assert.assertEquals(scriptManifest.getAuthor(), "Joel Johnson");
		Assert.assertEquals(scriptManifest.getWebsite(), "http://vanillaci.com");
//		System.out.println(scriptManifest);
	}

	@Test
	public void testParameterHints() throws Exception {
		ScriptManifest scriptManifest = ScriptManifest.fromFile(new File(simpleScript, "manifest.yaml"));
		Map<String, String> parameters = scriptManifest.getParameters();
		Assert.assertEquals(parameters.size(), 3);
		Assert.assertEquals(parameters.get("HINT_1"), "NUMBER", "HINT_1 should be defined as an NUMBER");
		Assert.assertEquals(parameters.get("HINT_2"), "STRING", "HINT_2 should be defined as an STRING");
		Assert.assertEquals(parameters.get("HINT_3"), "BOOLEAN", "HINT_3 should be defined as an BOOLEAN");
	}

	@Test
	public void testScript() throws Exception {
		Script script = new Script("simple", "e3115ca80f0e206ae4cf342a953005ccf2f4bcef", simpleScript);
		Assert.assertEquals(script.getManifestFile(), new File(simpleScript, "manifest.yaml"));
	}

	@Test
	public void simpleExecute() throws IOException, InterruptedException {
		List<String> args = Arrays.asList("Jimmy Stewart", "10/22/1986");
		Map<String, String> env = new HashMap<String, String>();
		Script script = new Script("simple", "e3115ca80f0e206ae4cf342a953005ccf2f4bcef", simpleScript);
		ByteArrayOutputStream stdout = new ByteArrayOutputStream();
		int exitCode = script.execute(args, workspace, env, stdout);

		Assert.assertEquals(0, exitCode);

		String out = new String(stdout.toByteArray());
		Assert.assertEquals("Hello " + args.get(0) + "\nYour birthday is on " + args.get(1) + "\n", out);
	}

	@Test
	public void customEnvArgs() throws IOException, InterruptedException {
		List<String> args = Arrays.asList("hello");
		Map<String, String> env = new HashMap<String, String>();

		String injectedValue = "This should show up in logs";
		env.put("SOME_INJECTED_VALUE", injectedValue);

		String name = "some random name";
		Script script = new Script(name, "123", environmentScript);

		ByteArrayOutputStream stdout = new ByteArrayOutputStream();
		int exitCode = script.execute(args, workspace, env, stdout);

		Assert.assertEquals(1, exitCode);

		String out = new String(stdout.toByteArray());
//		System.out.println(out);

		String[] outSplit = out.split("\n");
		Assert.assertEquals(environmentScript.getAbsolutePath(), outSplit[0]);
		Assert.assertEquals(name, outSplit[1]);
		Assert.assertEquals(workspace.getAbsolutePath(), outSplit[2]);
		Assert.assertEquals(injectedValue, outSplit[3]);
	}
}

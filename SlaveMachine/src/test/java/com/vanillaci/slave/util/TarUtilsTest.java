package com.vanillaci.slave.util;

import com.google.common.io.Files;
import com.vanillaci.slave.TestFiles;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

/**
 * User: Joel Johnson
 * Date: 12/7/12
 * Time: 7:14 PM
 */
public class TarUtilsTest {
	private File destination = null;

	@AfterTest
	public void tearDown() throws IOException {
		if(destination != null) {
			FileUtils.deleteDirectory(destination);
		}
	}

	@Test
	public void testUntar() throws IOException {
		File tar = new File(TestFiles.getResourceDirectory(), "archive.tar");
		destination = Files.createTempDir();
		TarUtils.untar(tar, destination);

		Assert.assertTrue(new File(destination, "file1").exists(), "file1 exists");
		Assert.assertTrue(new File(destination, "file2").exists(), "file2 exists");
		Assert.assertTrue(new File(destination, "dir1/dir2/file3").exists(), "file3 exists");
	}
}

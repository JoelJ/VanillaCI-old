package com.joelj.vanillaci.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.logging.Logger;

/**
 * User: Joel Johnson
 * Date: 12/7/12
 * Time: 6:20 PM
 */
public class TarUtils {
	private final static Logger log = Logger.getLogger(TarUtils.class.getCanonicalName());

	/**
	 * Extracts the given tar file into the given directory.
	 * @param tarFile File to be extracted
	 * @param destinationDirectory Directory to be the destination. This directory is the root of the extracted files.
	 *                                No directory will be made inside this folder unless it is in the tar.
	 * @throws java.io.IOException If a file doesn't exist or if files cannot be created (permissions, disk failure, etc).
	 */
	public static void untar(File tarFile, File destinationDirectory) throws IOException {
		TarArchiveInputStream inputStream = new TarArchiveInputStream(new FileInputStream(tarFile));
		try {
			TarArchiveEntry entry;
			while ((entry = inputStream.getNextTarEntry()) != null) {
				final File outputFile = new File(destinationDirectory, entry.getName());
				if (entry.isDirectory()) {
					log.info(String.format("Attempting to write output directory %s.", outputFile.getAbsolutePath()));
					if (!outputFile.exists()) {
						log.info(String.format("Attempting to create output directory %s.", outputFile.getAbsolutePath()));
						if (!outputFile.mkdirs()) {
							throw new IllegalStateException(String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
						}
					}
				} else {
					log.info(String.format("Creating output file %s.", outputFile.getAbsolutePath()));
					final OutputStream outputFileStream = new FileOutputStream(outputFile);
					IOUtils.copy(inputStream, outputFileStream);
					outputFileStream.close();
				}
			}
		} finally {
			inputStream.close();
		}
	}
}

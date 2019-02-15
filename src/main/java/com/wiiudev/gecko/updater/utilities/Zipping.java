package com.wiiudev.gecko.updater.utilities;

import lombok.val;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class Zipping
{
	public static void extract(String inputArchive, String outputDirectory) throws ZipException
	{
		val zipFile = new ZipFile(inputArchive);
		zipFile.extractAll(outputDirectory);
	}
}

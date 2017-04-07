package com.wiiudev.gecko.updater;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class Zipping
{
	public static void extract(String inputArchive, String outputDirectory) throws ZipException
	{
		ZipFile zipFile = new ZipFile(inputArchive);
		zipFile.extractAll(outputDirectory);
	}
}
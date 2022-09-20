package com.wiiudev.gecko.updater.utilities;

import lombok.val;
import net.lingala.zip4j.ZipFile;

import java.io.IOException;

public class Zipping
{
	public static void extract(String inputArchive, String outputDirectory) throws IOException
	{
		try (val zipFile = new ZipFile(inputArchive))
		{
			zipFile.extractAll(outputDirectory);
		}
	}
}

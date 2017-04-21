package com.wiiudev.gecko.updater.utilities;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class RandomStringUtilities
{
	/**
	 * Generates a random String
	 *
	 * @param charactersCount The desired String length
	 * @return The generated String
	 */
	public static String getRandomString(int charactersCount)
	{
		byte randomBytes[] = new byte[charactersCount];
		Random random = new Random();
		StringBuilder stringBuilder = new StringBuilder();

		random.nextBytes(randomBytes);

		for (byte randomByte : randomBytes)
		{
			byte b1 = (byte) ((randomByte & 0xf0) >> 4);
			byte b2 = (byte) (randomByte & 0x0f);

			if (b1 < 10)
			{
				stringBuilder.append((char) ('0' + b1));
			} else
			{
				stringBuilder.append((char) ('A' + (b1 - 10)));
			}

			if (b2 < 10)
			{
				stringBuilder.append((char) ('0' + b2));
			} else
			{
				stringBuilder.append((char) ('A' + (b2 - 10)));
			}
		}

		return (stringBuilder.toString());
	}

	public static String getRandomFilePath()
	{
		while (true)
		{
			String downloadFileName = getRandomString(20);
			String targetDirectory = getTemporaryDirectory();

			Path downloadedFilePath = Paths.get(targetDirectory + PATH_SEPARATOR + downloadFileName);

			// Make sure the file name isn't taken (for the unlikely case)
			if (!Files.exists(downloadedFilePath))
			{
				return downloadedFilePath.toString();
			}
		}
	}

	private static final String PATH_SEPARATOR = File.separator;

	public static String getTemporaryDirectory()
	{
		String targetDirectory = System.getProperty("java.io.tmpdir");

		if (targetDirectory.endsWith(PATH_SEPARATOR))
		{
			int lastIndex = targetDirectory.length() - PATH_SEPARATOR.length();
			targetDirectory = targetDirectory.substring(0, lastIndex);
		}

		return targetDirectory;
	}
}

package com.wiiudev.gecko.updater.utilities;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

public class DownloadingUtilities
{
	public static Path downloadRaw(String downloadURL) throws IOException
	{
		return download(downloadURL + "?raw=true");
	}

	public static Path download(String downloadURL) throws IOException
	{
		URL website = new URL(downloadURL);
		String fileName = getFileName(downloadURL);

		try (InputStream inputStream = website.openStream())
		{
			Path downloadedFilePath = Paths.get(RandomStringUtilities.getTemporaryDirectory() + File.separator + fileName);
			Files.copy(inputStream, downloadedFilePath, StandardCopyOption.REPLACE_EXISTING);

			return downloadedFilePath;
		}
	}

	private static String getFileName(String downloadURL) throws UnsupportedEncodingException
	{
		String baseName = FilenameUtils.getBaseName(downloadURL);
		String extension = FilenameUtils.getExtension(downloadURL);
		String fileName = baseName + "." + extension;

		int questionMarkIndex = fileName.indexOf("?");
		if (questionMarkIndex != -1)
		{
			fileName = fileName.substring(0, questionMarkIndex);
		}

		fileName = fileName.replaceAll("-", "");

		return URLDecoder.decode(fileName, "UTF-8");
	}
}
package com.wiiudev.gecko.updater.utilities;

import lombok.val;
import lombok.var;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import static java.net.URLDecoder.decode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;

public class DownloadingUtilities
{
	public static TemporaryFilePath downloadRAW(String downloadURL) throws IOException
	{
		return download(downloadURL + "?raw=true");
	}

	public static TemporaryFilePath download(String downloadURL) throws IOException
	{
		val website = new URL(downloadURL);
		val fileName = getFileName(downloadURL);

		try (val inputStream = website.openStream())
		{
			val baseName = getBaseName(fileName);
			val extension = getExtension(fileName);
			val temporaryFile = createTempFile(baseName, extension);
			copy(inputStream, temporaryFile, REPLACE_EXISTING);

			return new TemporaryFilePath(temporaryFile, fileName);
		}
	}

	private static String getFileName(String downloadURL) throws UnsupportedEncodingException
	{
		val baseName = getBaseName(downloadURL);
		val extension = getExtension(downloadURL);
		var fileName = baseName + "." + extension;

		val questionMarkIndex = fileName.indexOf("?");
		if (questionMarkIndex != -1)
		{
			fileName = fileName.substring(0, questionMarkIndex);
		}

		fileName = fileName.replaceAll("-", "");

		return decode(fileName, UTF_8.name());
	}
}

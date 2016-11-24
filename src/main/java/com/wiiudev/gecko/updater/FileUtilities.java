package com.wiiudev.gecko.updater;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtilities
{
	public static void deleteFolder(File folder) throws IOException
	{
		try
		{
			File[] files = folder.listFiles();

			if (files != null)
			{
				for (File file : files)
				{
					if (file.isDirectory())
					{
						deleteFolder(file);
					} else
					{
						Files.delete(file.toPath());
					}
				}
			}

			Files.delete(folder.toPath());
		} catch (Exception ignored)
		{

		}
	}
}

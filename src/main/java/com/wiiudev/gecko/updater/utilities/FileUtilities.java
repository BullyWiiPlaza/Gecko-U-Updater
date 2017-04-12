package com.wiiudev.gecko.updater.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

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

	public static void copyDirectory(Path sourcePath, Path targetPath) throws IOException
	{
		Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult preVisitDirectory(final Path dir,
			                                         final BasicFileAttributes attrs) throws IOException
			{
				Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir)));
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(final Path file,
			                                 final BasicFileAttributes attrs) throws IOException
			{
				Files.copy(file, targetPath.resolve(sourcePath.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
				return FileVisitResult.CONTINUE;
			}
		});
	}
}

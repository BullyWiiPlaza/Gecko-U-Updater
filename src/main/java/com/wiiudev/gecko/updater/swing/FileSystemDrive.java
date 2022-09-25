package com.wiiudev.gecko.updater.swing;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

import static javax.swing.filechooser.FileSystemView.*;

public class FileSystemDrive
{
	private static final FileSystemView FILE_SYSTEM_VIEW = getFileSystemView();

	private final File file;

	FileSystemDrive(File file)
	{
		this.file = file;
	}

	@Override
	public String toString()
	{
		return FILE_SYSTEM_VIEW.getSystemDisplayName(file);
	}

	String getTypeDescription()
	{
		return FILE_SYSTEM_VIEW.getSystemTypeDescription(file);
	}

	String getRoot()
	{
		return file.getAbsolutePath();
	}
}

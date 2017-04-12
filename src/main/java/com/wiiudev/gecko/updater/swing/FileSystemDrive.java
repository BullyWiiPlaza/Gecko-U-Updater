package com.wiiudev.gecko.updater.swing;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class FileSystemDrive
{
	private static final FileSystemView FILE_SYSTEM_VIEW = FileSystemView.getFileSystemView();

	private File file;

	public FileSystemDrive(File file)
	{
		this.file = file;
	}

	@Override
	public String toString()
	{
		return FILE_SYSTEM_VIEW.getSystemDisplayName(file);
	}

	public String getTypeDescription()
	{
		return FILE_SYSTEM_VIEW.getSystemTypeDescription(file);
	}

	public String getRoot()
	{
		return file.getAbsolutePath();
	}
}
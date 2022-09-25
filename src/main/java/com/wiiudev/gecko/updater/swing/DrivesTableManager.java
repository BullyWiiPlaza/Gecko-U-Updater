package com.wiiudev.gecko.updater.swing;

import lombok.val;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

import static com.wiiudev.gecko.updater.swing.JTableUtilities.*;
import static java.io.File.listRoots;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.filechooser.FileSystemView.getFileSystemView;

class DrivesTableManager
{
	private static final String[] COLUMN_HEADER_NAMES = new String[]{"Display Name", "Type Description"};

	private final JTable table;
	private final List<FileSystemDrive> fileSystemDrives;

	DrivesTableManager(JTable table)
	{
		this.table = table;
		val model = getDefaultTableModel();
		table.setModel(model);
		fileSystemDrives = new ArrayList<>();
	}

	void configure()
	{
		configureTable(table, COLUMN_HEADER_NAMES);
		setSingleSelection(table);
	}

	private void addRow(FileSystemDrive drive)
	{
		invokeLater(() ->
		{
			val objects = new Object[]{drive.toString(), drive.getTypeDescription()};
			val tableModel = (DefaultTableModel) table.getModel();
			tableModel.addRow(objects);
		});
	}

	void mountDrives()
	{
		fileSystemDrives.clear();
		deleteAllRows(table);

		val fileSystemView = getFileSystemView();
		val roots = listRoots();

		for (val root : roots)
		{
			val displayName = fileSystemView.getSystemDisplayName(root);

			if (!displayName.isEmpty())
			{
				val drive = new FileSystemDrive(root);
				fileSystemDrives.add(drive);
				addRow(drive);
			}
		}
	}

	FileSystemDrive getSelectedDrive()
	{
		val selectedRow = table.getSelectedRow();
		return fileSystemDrives.get(selectedRow);
	}

	boolean isDriveSelected()
	{
		return table.getSelectedRow() != -1;
	}
}

package com.wiiudev.gecko.updater.swing;

import net.samuelcampos.usbdrivedectector.USBDeviceDetectorManager;
import net.samuelcampos.usbdrivedectector.USBStorageDevice;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DrivesTableManager
{
	private JTable table;
	private List<FileSystemDrive> fileSystemDrives;

	public DrivesTableManager(JTable table)
	{
		this.table = table;
		DefaultTableModel model = JTableUtilities.getDefaultTableModel();
		table.setModel(model);
		fileSystemDrives = new ArrayList<>();
	}

	public void configure()
	{
		String[] columnHeaderNames = new String[]{"Display Name", "Type Description"};
		JTableUtilities.configureTable(table, columnHeaderNames);
		JTableUtilities.setSingleSelection(table);
	}

	private void addRow(FileSystemDrive drive)
	{
		SwingUtilities.invokeLater(() ->
		{
			Object[] objects = new Object[]{drive.toString(), drive.getTypeDescription()};
			DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
			tableModel.addRow(objects);
		});
	}

	public void mountDrives()
	{
		fileSystemDrives.clear();
		JTableUtilities.deleteAllRows(table);

		FileSystemView fileSystemView = FileSystemView.getFileSystemView();
		File[] roots = File.listRoots();

		USBDeviceDetectorManager manager = new USBDeviceDetectorManager();
		List<USBStorageDevice> usbStorageDevices = manager.getRemovableDevices();

		for (File root : roots)
		{
			String displayName = fileSystemView.getSystemDisplayName(root);

			if (!displayName.isEmpty() && isDisplayNameContained(displayName, usbStorageDevices))
			{
				FileSystemDrive drive = new FileSystemDrive(root);
				fileSystemDrives.add(drive);
				addRow(drive);
			}
		}
	}

	private boolean isDisplayNameContained(String displayName, List<USBStorageDevice> usbStorageDevices)
	{
		for (USBStorageDevice usbStorageDevice : usbStorageDevices)
		{
			String currentDisplayName = usbStorageDevice.getSystemDisplayName();
			if (currentDisplayName.equals(displayName))
			{
				return true;
			}
		}

		return false;
	}

	public FileSystemDrive getSelectedDrive()
	{
		int selectedRow = table.getSelectedRow();
		return fileSystemDrives.get(selectedRow);
	}

	public boolean isDriveSelected()
	{
		return table.getSelectedRow() != -1;
	}
}
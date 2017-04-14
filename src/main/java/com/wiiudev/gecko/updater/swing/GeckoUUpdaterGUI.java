package com.wiiudev.gecko.updater.swing;

import com.wiiudev.gecko.updater.*;
import com.wiiudev.gecko.updater.utilities.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GeckoUUpdaterGUI extends JFrame
{
	private JPanel rootPanel;
	private JButton downloadFilesButton;
	private JButton mountDrivesButton;
	private JButton transferButton;
	private JTable drivesTable;
	private JButton informationButton;

	private DrivesTableManager tableManager;
	private boolean downloadingFiles;
	private boolean mountingDrives;
	private boolean transferringFiles;

	public GeckoUUpdaterGUI()
	{
		setFrameProperties();

		tableManager = new DrivesTableManager(drivesTable);
		tableManager.configure();

		mountDrivesButton.addActionListener(actionEvent -> mountDrivesAsynchronously());

		mountDrivesAsynchronously();

		downloadFilesButton.addActionListener(actionEvent ->
		{
			String buttonText = downloadFilesButton.getText();
			downloadFilesButton.setText("Downloading...");
			downloadingFiles = true;
			final boolean[] processSucceeded = {true};

			new SwingWorker<String, String>()
			{
				@Override
				protected String doInBackground() throws Exception
				{
					try
					{
						GeckoUpdater.downloadFiles();
					} catch (Exception exception)
					{
						processSucceeded[0] = false;
						StackTraceUtilities.handleException(rootPane, exception);
					}

					return null;
				}

				@Override
				protected void done()
				{
					if (processSucceeded[0])
					{
						JOptionPane.showMessageDialog(rootPanel,
								"Files successfully downloaded!",
								"Success",
								JOptionPane.INFORMATION_MESSAGE);
					}

					downloadFilesButton.setText(buttonText);
					downloadingFiles = false;
				}
			}.execute();
		});

		transferButton.addActionListener(actionEvent ->
		{
			Path sourcePath = GeckoUpdater.SD_CARD_DIRECTORY;
			FileSystemDrive fileSystemDrive = tableManager.getSelectedDrive();
			String root = fileSystemDrive.getRoot();
			boolean shouldTransfer = reconsiderTransfer(root);

			if (shouldTransfer)
			{
				Path targetPath = Paths.get(fileSystemDrive.getRoot());
				final boolean[] processSucceeded = {true};
				String buttonText = transferButton.getText();
				transferringFiles = true;
				transferButton.setText("Transferring...");

				new SwingWorker<String, String>()
				{
					@Override
					protected String doInBackground() throws Exception
					{
						try
						{
							FileUtilities.copyDirectory(sourcePath, targetPath);
						} catch (IOException exception)
						{
							processSucceeded[0] = false;
							StackTraceUtilities.handleException(rootPane, exception);
						}

						return null;
					}

					@Override
					protected void done()
					{
						if (processSucceeded[0])
						{
							JOptionPane.showMessageDialog(rootPanel,
									"Files successfully transferred!",
									"Success",
									JOptionPane.INFORMATION_MESSAGE);
						}

						transferringFiles = false;
						transferButton.setText(buttonText);
					}
				}.execute();
			}
		});

		informationButton.addActionListener(actionEvent -> visitRepositoryURL());

		startComponentsAvailabilitySetter();
	}

	private boolean reconsiderTransfer(String root)
	{
		if (OperatingSystemUtilities.isWindows())
		{
			try
			{
				List<WindowsDrivesUtilities.Drive> drives = WindowsDrivesUtilities.getDrives();
				String forcedFormatting = "FAT32";

				for (WindowsDrivesUtilities.Drive drive : drives)
				{
					String currentDriveRoot = drive.root.toString();

					if (currentDriveRoot.equals(root))
					{
						String currentFileSystem = drive.fileSystem;

						if (!currentFileSystem.equals(forcedFormatting))
						{
							Object[] options = {"Yes", "No"};
							int selectedOption = JOptionPane.showOptionDialog(this,
									"Windows has detected that the drive is not formatted as " + forcedFormatting + ".\n" +
											"The Wii U's SD card has to be formatted as " + forcedFormatting + " to be recognized, therefore this cannot be a Wii U SD card.\n" +
											"Are you sure you still want to copy the files over?",
									"Continue?",
									JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE,
									null,
									options,
									null);

							if (selectedOption != JOptionPane.YES_OPTION)
							{
								return false;
							}
						}

						break;
					}
				}
			} catch (Exception exception)
			{
				exception.printStackTrace();
			}
		}

		return true;
	}

	private static void visitRepositoryURL()
	{
		try
		{
			Desktop desktop = Desktop.getDesktop();
			URI uri = new URI("https://github.com/BullyWiiPlaza/Gecko-U-Updater");
			desktop.browse(uri);
		} catch (Exception exception)
		{
			StackTraceUtilities.handleException(null, exception);
		}
	}

	private void startComponentsAvailabilitySetter()
	{
		Thread thread = new Thread(() ->
		{
			while (!isShowing())
			{
				try
				{
					Thread.sleep(10);
				} catch (Exception exception)
				{
					StackTraceUtilities.handleException(rootPane, exception);
				}
			}

			while (isShowing())
			{
				SwingUtilities.invokeLater(this::setComponentsAvailability);

				try
				{
					Thread.sleep(50);
				} catch (Exception exception)
				{
					StackTraceUtilities.handleException(rootPane, exception);
				}
			}
		});

		thread.setName("Components Availability Setter");
		thread.start();
	}

	private void setComponentsAvailability()
	{
		boolean filesDownloaded = Files.isDirectory(GeckoUpdater.SD_CARD_DIRECTORY);
		boolean driveSelected = tableManager.isDriveSelected();
		transferButton.setEnabled(filesDownloaded && driveSelected
				&& !transferringFiles && !downloadingFiles && !mountingDrives);
		downloadFilesButton.setEnabled(!downloadingFiles);
		mountDrivesButton.setEnabled(!mountingDrives);
	}

	private void mountDrivesAsynchronously()
	{
		String buttonText = mountDrivesButton.getText();
		mountDrivesButton.setText("Mounting...");
		mountingDrives = true;

		new SwingWorker<String, String>()
		{
			@Override
			protected String doInBackground() throws Exception
			{
				tableManager.mountDrives();

				return null;
			}

			@Override
			protected void done()
			{
				mountingDrives = false;
				mountDrivesButton.setText(buttonText);
			}
		}.execute();
	}

	private void setFrameProperties()
	{
		add(rootPanel);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(450, 250);
		setTitle("Gecko U Updater");
		WindowUtilities.setIconImage(this);
	}
}
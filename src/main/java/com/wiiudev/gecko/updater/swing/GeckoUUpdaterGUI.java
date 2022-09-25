package com.wiiudev.gecko.updater.swing;

import lombok.val;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

import static com.wiiudev.gecko.updater.GeckoUpdater.SD_CARD_DIRECTORY;
import static com.wiiudev.gecko.updater.GeckoUpdater.downloadFiles;
import static com.wiiudev.gecko.updater.utilities.StackTraceUtilities.handleException;
import static com.wiiudev.gecko.updater.utilities.WindowUtilities.setWindowIconImage;
import static com.wiiudev.gecko.updater.utilities.WindowsDrivesUtilities.getDrives;
import static java.lang.Thread.sleep;
import static java.nio.file.Files.isDirectory;
import static javax.swing.JOptionPane.*;
import static javax.swing.SwingUtilities.invokeLater;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;

public class GeckoUUpdaterGUI extends JFrame
{
	private static final String FORCED_FORMATTING = "FAT32";
	private static final String GECKO_U_UPDATER_URL = "https://github.com/BullyWiiPlaza/Gecko-U-Updater";

	private JPanel rootPanel;
	private JButton downloadFilesButton;
	private JButton mountDrivesButton;
	private JButton transferButton;
	private JTable drivesTable;
	private JButton informationButton;

	private final DrivesTableManager tableManager;
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
			val buttonText = downloadFilesButton.getText();
			downloadFilesButton.setText("Downloading...");
			downloadingFiles = true;
			val processSucceeded = new boolean[]{true};

			new SwingWorker<String, String>()
			{
				@Override
				protected String doInBackground()
				{
					try
					{
						downloadFiles();
					} catch (Exception exception)
					{
						processSucceeded[0] = false;
						handleException(rootPane, exception);
					}

					return null;
				}

				@Override
				protected void done()
				{
					if (processSucceeded[0])
					{
						showMessageDialog(rootPanel,
								"Files successfully downloaded!",
								"Success",
								INFORMATION_MESSAGE);
					}

					downloadFilesButton.setText(buttonText);
					downloadingFiles = false;
				}
			}.execute();
		});

		transferButton.addActionListener(actionEvent ->
		{
			val fileSystemDrive = tableManager.getSelectedDrive();
			val root = fileSystemDrive.getRoot();
			val shouldTransfer = reconsiderTransfer(root);

			if (shouldTransfer)
			{
				val targetPath = Paths.get(fileSystemDrive.getRoot());
				val processSucceeded = new boolean[]{true};
				val buttonText = transferButton.getText();
				transferringFiles = true;
				transferButton.setText("Transferring...");

				new SwingWorker<String, String>()
				{
					@Override
					protected String doInBackground()
					{
						try
						{
							copyDirectory(SD_CARD_DIRECTORY.toFile(), targetPath.toFile());
						} catch (IOException exception)
						{
							processSucceeded[0] = false;
							handleException(rootPane, exception);
						}

						return null;
					}

					@Override
					protected void done()
					{
						if (processSucceeded[0])
						{
							showMessageDialog(rootPanel, "Files successfully transferred!",
									"Success", INFORMATION_MESSAGE);
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
		if (IS_OS_WINDOWS)
		{
			try
			{
				val drives = getDrives();

				for (val drive : drives)
				{
					val currentDriveRoot = drive.root.toString();

					if (currentDriveRoot.equals(root))
					{
						val currentFileSystem = drive.fileSystem;

						if (!currentFileSystem.equals(FORCED_FORMATTING))
						{
							val options = new String[]{"Yes", "No"};
							val selectedOption = showOptionDialog(this,
									"Windows has detected that the drive is not formatted as "
											+ FORCED_FORMATTING + ".\n" +
											"The Wii U's SD card has to be formatted as " + FORCED_FORMATTING
											+ " to be recognized,\n" +
											"therefore this cannot be a Wii U SD card.\n" +
											"Are you sure you still want to copy the files over?",
									"Continue?",
									YES_NO_CANCEL_OPTION, QUESTION_MESSAGE, null, options, null);

							if (selectedOption != YES_OPTION)
							{
								return false;
							}
						}

						break;
					}
				}
			} catch (Error error)
			{
				showMessageDialog(this,
						"Please keep the DLLs in the same directory as this application " +
								"for advanced drive detection on Windows.",
						error.getMessage(),
						ERROR_MESSAGE);
			}
		}

		return true;
	}

	private static void visitRepositoryURL()
	{
		try
		{
			val desktop = Desktop.getDesktop();
			val uri = new URI(GECKO_U_UPDATER_URL);
			desktop.browse(uri);
		} catch (Exception exception)
		{
			handleException(null, exception);
		}
	}

	private void startComponentsAvailabilitySetter()
	{
		val thread = new Thread(() ->
		{
			while (!isShowing())
			{
				try
				{
					//noinspection BusyWait
					sleep(10);
				} catch (Exception exception)
				{
					handleException(rootPane, exception);
				}
			}

			while (isShowing())
			{
				invokeLater(this::setComponentsAvailability);

				try
				{
					//noinspection BusyWait
					sleep(50);
				} catch (Exception exception)
				{
					handleException(rootPane, exception);
				}
			}
		}, "Components Availability Setter");

		thread.start();
	}

	private void setComponentsAvailability()
	{
		val filesDownloaded = isDirectory(SD_CARD_DIRECTORY);
		val driveSelected = tableManager.isDriveSelected();
		transferButton.setEnabled(filesDownloaded && driveSelected
				&& !transferringFiles && !downloadingFiles && !mountingDrives);
		downloadFilesButton.setEnabled(!downloadingFiles);
		mountDrivesButton.setEnabled(!mountingDrives);
	}

	private void mountDrivesAsynchronously()
	{
		val buttonText = mountDrivesButton.getText();
		mountDrivesButton.setText("Mounting...");
		mountingDrives = true;

		new SwingWorker<String, String>()
		{
			@Override
			protected String doInBackground()
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
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(450, 250);
		setTitle("Gecko U Updater [09/25/2022]");
		setWindowIconImage(this);
	}
}

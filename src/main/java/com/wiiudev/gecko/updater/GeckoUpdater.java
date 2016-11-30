package com.wiiudev.gecko.updater;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeckoUpdater
{
	public static final String DOWNLOADED_FOLDER_NAME = "Downloaded";
	public static final String COMPUTER_FOLDER_NAME = "Computer";
	public static final String SD_CARD_FOLDER_NAME = "SD Card";

	public static void downloadFiles() throws Exception
	{
		Path downloadedDirectory = Paths.get(DOWNLOADED_FOLDER_NAME);

		FileUtilities.deleteFolder(downloadedDirectory.toFile());

		// Create the necessary folders
		Path computerDirectory = downloadedDirectory.resolve(COMPUTER_FOLDER_NAME);
		Path jGeckoUDirectory = computerDirectory.resolve("JGecko U");
		Files.createDirectories(jGeckoUDirectory);

		Path wiiUDirectory = downloadedDirectory.resolve(SD_CARD_FOLDER_NAME + "/wiiu");
		Path tcpGeckoFolder = wiiUDirectory.resolve("apps/tcpgecko");
		Files.createDirectories(tcpGeckoFolder);

		int threadPoolSize = Runtime.getRuntime().availableProcessors();
		ExecutorService pool = Executors.newFixedThreadPool(threadPoolSize);
		ExecutorCompletionService<String> completionService = new ExecutorCompletionService<>(pool);
		int submittedTasksCount = 0;

		String sourceRepositoryURL = "https://github.com/BullyWiiPlaza/tcpgecko/";
		String masterRepositoryURL = sourceRepositoryURL + "blob/master/";

		completionService.submit(() ->
		{
			String jGeckoUURL = "https://github.com/BullyWiiPlaza/JGeckoU/blob/master/JGecko%20U.jar";
			Path downloadedJGeckoU = DownloadingUtilities.downloadRaw(jGeckoUURL);
			Files.move(downloadedJGeckoU, jGeckoUDirectory.resolve(downloadedJGeckoU));

			return null;
		});

		submittedTasksCount++;

		completionService.submit(() ->
		{
			String iconURL = masterRepositoryURL + "meta/icon.png";
			Path iconFile = DownloadingUtilities.downloadRaw(iconURL);
			Files.move(iconFile, tcpGeckoFolder.resolve(iconFile));

			return null;
		});

		submittedTasksCount++;

		completionService.submit(() ->
		{
			String metaXML = masterRepositoryURL + "meta/meta.xml";
			Path metaXMLFilePath = DownloadingUtilities.downloadRaw(metaXML);
			Files.move(metaXMLFilePath, tcpGeckoFolder.resolve(metaXMLFilePath));

			return null;
		});

		submittedTasksCount++;

		completionService.submit(() ->
		{
			String codeHandler = masterRepositoryURL + "codehandler.bin"; // "http://cosmocortney.ddns.net/wiiu_tools/codehandler.bin";
			Path codeHandlerBinaries = DownloadingUtilities.downloadRaw(codeHandler);
			Files.move(codeHandlerBinaries, tcpGeckoFolder.resolve(codeHandlerBinaries));

			return null;
		});

		submittedTasksCount++;

		completionService.submit(() ->
		{
			String tcpGeckoInstaller = masterRepositoryURL + "tcpgecko.elf";
			Path downloadedTCPGeckoInstaller = DownloadingUtilities.downloadRaw(tcpGeckoInstaller);
			Files.move(downloadedTCPGeckoInstaller, tcpGeckoFolder.resolve(downloadedTCPGeckoInstaller));

			return null;
		});

		submittedTasksCount++;

		for (int tasksIndex = 0; tasksIndex < submittedTasksCount; tasksIndex++)
		{
			completionService.take().get();
		}

		pool.shutdown();
	}
}
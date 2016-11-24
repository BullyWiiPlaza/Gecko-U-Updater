package com.wiiudev.gecko.updater;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeckoUpdater
{
	public static void downloadFiles() throws InterruptedException, ExecutionException, IOException
	{
		// Firstly, clean up
		Path wiiUDirectory = Paths.get("SD Card/wiiu");
		FileUtilities.deleteFolder(wiiUDirectory.toFile());
		Path computerDirectory = Paths.get("Computer");
		FileUtilities.deleteFolder(computerDirectory.toFile());

		// Create the necessary folders
		Path jGeckoUDirectory = computerDirectory.resolve("JGecko U");
		Files.createDirectories(jGeckoUDirectory);
		Path tcpGeckoFolder = wiiUDirectory.resolve("apps/tcpgecko");
		Files.createDirectories(tcpGeckoFolder);

		int threadPoolSize = Runtime.getRuntime().availableProcessors();
		ExecutorService pool = Executors.newFixedThreadPool(threadPoolSize);
		ExecutorCompletionService<String> completionService = new ExecutorCompletionService<>(pool);

		String sourceRepositoryURL = "https://github.com/BullyWiiPlaza/tcpgecko/";
		String masterRepositoryURL = sourceRepositoryURL + "blob/master/";

		completionService.submit(() ->
		{
			String jGeckoUURL = "https://github.com/BullyWiiPlaza/JGeckoU/blob/master/JGecko%20U.jar";
			Path downloadedJGeckoU = DownloadingUtilities.downloadRaw(jGeckoUURL);
			Files.move(downloadedJGeckoU, jGeckoUDirectory.resolve(downloadedJGeckoU));

			return null;
		});

		completionService.submit(() ->
		{
			String iconURL = masterRepositoryURL + "meta/icon.png";
			Path iconFile = DownloadingUtilities.downloadRaw(iconURL);
			Files.move(iconFile, tcpGeckoFolder.resolve(iconFile));

			return null;
		});

		completionService.submit(() ->
		{
			String metaXML = masterRepositoryURL + "meta/meta.xml";
			Path metaXMLFilePath = DownloadingUtilities.downloadRaw(metaXML);
			Files.move(metaXMLFilePath, tcpGeckoFolder.resolve(metaXMLFilePath));

			return null;
		});

		completionService.submit(() ->
		{
			String codeHandler = "http://cosmocortney.ddns.net/wiiu_tools/codehandler.bin";
			Path codeHandlerBinaries = DownloadingUtilities.download(codeHandler);
			Files.move(codeHandlerBinaries, tcpGeckoFolder.resolve(codeHandlerBinaries));

			return null;
		});

		completionService.submit(() ->
		{
			String tcpGeckoInstaller = masterRepositoryURL + "tcpgecko.elf";
			Path downloadedTCPGeckoInstaller = DownloadingUtilities.downloadRaw(tcpGeckoInstaller);
			Files.move(downloadedTCPGeckoInstaller, tcpGeckoFolder.resolve(downloadedTCPGeckoInstaller));

			return null;
		});

		for (int tasksIndex = 0; tasksIndex < 5; tasksIndex++)
		{
			completionService.take().get();
		}

		pool.shutdown();
	}
}

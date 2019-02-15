package com.wiiudev.gecko.updater;

import lombok.val;
import lombok.var;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import static com.wiiudev.gecko.updater.utilities.DownloadingUtilities.download;
import static com.wiiudev.gecko.updater.utilities.DownloadingUtilities.downloadRAW;
import static com.wiiudev.gecko.updater.utilities.ProgramDirectoryUtilities.getProgramDirectory;
import static com.wiiudev.gecko.updater.utilities.Zipping.extract;
import static java.io.File.separator;
import static java.lang.Runtime.getRuntime;
import static java.nio.file.Files.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class GeckoUpdater
{
	private static final Path DOWNLOADED_DIRECTORY = Paths.get(getProgramDirectory() + separator + "Downloaded");
	private static final Path COMPUTER_DIRECTORY = DOWNLOADED_DIRECTORY.resolve("Computer");
	public static final Path SD_CARD_DIRECTORY = DOWNLOADED_DIRECTORY.resolve("SD Card");

	private static final String BROWSER_EXPLOIT_PAYLOAD = "https://static.wiidatabase.de/JSTypeHax-Payload.zip";
	private static final String HOME_BREW_LAUNCHER_DOWNLOAD_URL = "https://github.com/dimok789/homebrew_launcher/releases/download/1.4/homebrew_launcher.v1.4.zip";
	private static final String J_GECKO_U_URL = "https://github.com/BullyWiiPlaza/JGeckoU/blob/master/JGecko%20U.jar";
	private static final String TCP_GECKO_MASTER_REPOSITORY_URL = "https://github.com/BullyWiiPlaza/tcpgecko/blob/master/";
	private static final String ICON_URL = TCP_GECKO_MASTER_REPOSITORY_URL + "meta/icon.png";
	private static final String META_XML = TCP_GECKO_MASTER_REPOSITORY_URL + "meta/meta.xml";
	private static final String TCP_GECKO_INSTALLER_URL = TCP_GECKO_MASTER_REPOSITORY_URL + "tcpgecko.elf";

	public static void downloadFiles() throws Exception
	{
		// Create the necessary folders
		val jGeckoUDirectory = COMPUTER_DIRECTORY.resolve("JGecko U");
		createDirectories(jGeckoUDirectory);
		val wiiUDirectory = SD_CARD_DIRECTORY.resolve("wiiu");
		val appsFolder = wiiUDirectory.resolve("apps");
		val tcpGeckoFolder = appsFolder.resolve("tcpgecko");
		createDirectories(tcpGeckoFolder);

		val threadPoolSize = getProcessorCount();
		val pool = newFixedThreadPool(threadPoolSize * 2);
		val completionService = new ExecutorCompletionService<String>(pool);
		val tasks = new ArrayList<Future<String>>();

		tasks.add(completionService.submit(() ->
		{
			val temporaryFilePath = download(HOME_BREW_LAUNCHER_DOWNLOAD_URL);
			var downloadedHomeBrewLauncherArchive = temporaryFilePath.getFilePath();
			move(downloadedHomeBrewLauncherArchive, SD_CARD_DIRECTORY.resolve(temporaryFilePath.getOriginalFileName()), REPLACE_EXISTING);
			downloadedHomeBrewLauncherArchive = SD_CARD_DIRECTORY.resolve(temporaryFilePath.getOriginalFileName());
			extract(downloadedHomeBrewLauncherArchive.toString(), SD_CARD_DIRECTORY.toString());
			delete(downloadedHomeBrewLauncherArchive);

			return null;
		}));

		tasks.add(completionService.submit(() ->
		{
			downloadRAWAndMove(jGeckoUDirectory, J_GECKO_U_URL);

			return null;
		}));

		tasks.add(completionService.submit(() ->
		{
			downloadRAWAndMove(tcpGeckoFolder, ICON_URL);

			return null;
		}));

		tasks.add(completionService.submit(() ->
		{
			downloadRAWAndMove(tcpGeckoFolder, META_XML);

			return null;
		}));

		tasks.add(completionService.submit(() ->
		{
			val temporaryFilePath = downloadRAW(BROWSER_EXPLOIT_PAYLOAD);
			val downloadedPayloadArchive = temporaryFilePath.getFilePath();
			extract(downloadedPayloadArchive.toString(), wiiUDirectory.toString());
			delete(downloadedPayloadArchive);

			return null;
		}));

		tasks.add(completionService.submit(() ->
		{
			downloadRAWAndMove(tcpGeckoFolder, TCP_GECKO_INSTALLER_URL);

			return null;
		}));

		// Wait for all tasks to finish
		val tasksCount = tasks.size();
		for (var tasksIndex = 0; tasksIndex < tasksCount; tasksIndex++)
		{
			val future = completionService.take();
			future.get();
		}

		pool.shutdown();
	}

	private static void downloadRAWAndMove(Path targetDirectory, String downloadURL) throws IOException
	{
		val temporaryFilePath = downloadRAW(downloadURL);
		val downloadedFile = temporaryFilePath.getFilePath();
		val destinationFilePath = targetDirectory.resolve(temporaryFilePath.getOriginalFileName());
		move(downloadedFile, destinationFilePath, REPLACE_EXISTING);
	}

	private static int getProcessorCount()
	{
		val runtime = getRuntime();
		return runtime.availableProcessors();
	}
}

package com.wiiudev.gecko.updater.utilities;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Path;

@AllArgsConstructor
public class TemporaryFilePath
{
	@Getter
	private Path filePath;

	@Getter
	private String originalFileName;
}

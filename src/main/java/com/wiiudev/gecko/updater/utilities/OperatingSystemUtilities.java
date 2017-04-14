package com.wiiudev.gecko.updater.utilities;

public class OperatingSystemUtilities
{
	private static String operatingSystemName = null;

	private static String getOperatingSystemName()
	{
		if (operatingSystemName == null)
		{
			operatingSystemName = System.getProperty("os.name");
		}

		return operatingSystemName;
	}

	public static boolean isWindows()
	{
		String operatingSystemName = getOperatingSystemName();
		return operatingSystemName.startsWith("Windows");
	}
}
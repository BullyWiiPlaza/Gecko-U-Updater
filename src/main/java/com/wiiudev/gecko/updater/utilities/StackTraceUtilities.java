package com.wiiudev.gecko.updater.utilities;

import javax.swing.*;

public class StackTraceUtilities
{
	private static String toString(Exception exception)
	{
		StringBuilder stringBuilder = new StringBuilder(exception.toString());
		for (StackTraceElement stackTraceElement : exception.getStackTrace())
		{
			stringBuilder.append("\n\tat ");
			stringBuilder.append(stackTraceElement);
		}

		return stringBuilder.toString();
	}

	public static void handleException(JRootPane rootPane, Exception exception)
	{
		JOptionPane.showMessageDialog(rootPane,
				toString(exception),
				"Error",
				JOptionPane.ERROR_MESSAGE);
	}
}
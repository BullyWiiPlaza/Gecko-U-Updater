package com.wiiudev.gecko.updater.utilities;

import lombok.val;

import javax.swing.*;

import static javax.swing.JOptionPane.*;

public class StackTraceUtilities
{
	private static String toString(Exception exception)
	{
		val stringBuilder = new StringBuilder(exception.toString());
		for (val stackTraceElement : exception.getStackTrace())
		{
			stringBuilder.append("\n\tat ");
			stringBuilder.append(stackTraceElement);
		}

		return stringBuilder.toString();
	}

	public static void handleException(JRootPane rootPane, Exception exception)
	{
		showMessageDialog(rootPane, toString(exception), "Error", ERROR_MESSAGE);
	}
}

package com.wiiudev.gecko.updater.utilities;

import lombok.val;

import javax.swing.*;

import static javax.swing.JOptionPane.*;

public class StackTraceUtilities
{
	private static String toString(final Throwable throwable)
	{
		val stringBuilder = new StringBuilder(throwable.toString());
		for (val stackTraceElement : throwable.getStackTrace())
		{
			stringBuilder.append("\n\tat ");
			stringBuilder.append(stackTraceElement);
		}

		return stringBuilder.toString();
	}

	public static void handleException(final JRootPane rootPane, final Throwable exception)
	{
		val stackTrace = toString(exception);
		showMessageDialog(rootPane, stackTrace, "Error", ERROR_MESSAGE);
	}
}

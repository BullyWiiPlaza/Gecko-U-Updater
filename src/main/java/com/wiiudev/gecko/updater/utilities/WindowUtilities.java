package com.wiiudev.gecko.updater.utilities;

import java.awt.*;

public class WindowUtilities
{
	public static void setWindowIconImage(Window window)
	{
		window.setIconImage(Toolkit.getDefaultToolkit().getImage(WindowUtilities.class.getResource("/Icon.png")));
	}
}

import javax.swing.*;

public class UserInterfaceManager
{
	public static void setSystemLookAndFeelAsynchronously()
	{
		Thread thread = new Thread(() ->
		{
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception exception)
			{
				exception.printStackTrace();
			}
		});

		thread.start();
	}
}
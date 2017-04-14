import com.wiiudev.gecko.updater.utilities.WindowsDrivesUtilities;

import java.util.List;

public class DrivesExample
{
	public static void main(String[] arguments)
	{
		List<WindowsDrivesUtilities.Drive> drives = WindowsDrivesUtilities.getDrives();

		for (WindowsDrivesUtilities.Drive drive : drives)
		{
			System.out.println(drive.toString());
		}
	}
}
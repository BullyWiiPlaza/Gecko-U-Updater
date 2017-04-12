import com.wiiudev.gecko.updater.swing.GeckoUUpdaterGUI;
import net.samuelcampos.usbdrivedectector.USBDeviceDetectorManager;
import net.samuelcampos.usbdrivedectector.USBStorageDevice;

import javax.swing.*;
import java.util.List;

public class GeckoUUpdaterClient
{
	public static void main(String[] arguments) throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		SwingUtilities.invokeLater(() ->
		{
			GeckoUUpdaterGUI updaterGUI = new GeckoUUpdaterGUI();
			updaterGUI.setVisible(true);
		});
	}
}
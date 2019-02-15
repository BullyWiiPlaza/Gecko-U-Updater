import com.wiiudev.gecko.updater.swing.GeckoUUpdaterGUI;
import lombok.val;

import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static javax.swing.UIManager.setLookAndFeel;

public class GeckoUUpdaterClient
{
	public static void main(String[] arguments) throws Exception
	{
		setLookAndFeel(getSystemLookAndFeelClassName());

		invokeLater(() ->
		{
			val updaterGUI = new GeckoUUpdaterGUI();
			updaterGUI.setVisible(true);
		});
	}
}

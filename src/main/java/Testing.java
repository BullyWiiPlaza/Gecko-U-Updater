import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class Testing
{
	public static void main(String[] arguments)
	{
		FileSystemView fileSystemView = FileSystemView.getFileSystemView();
		File[] files = File.listRoots();

		for (File file : files)
		{
			System.out.println(fileSystemView.getSystemDisplayName(file));

			if (fileSystemView.getSystemTypeDescription(file).contains("Local Disk")
					|| fileSystemView.getSystemTypeDescription(file).contains(
					"Removable Disk"))
			{

			}
		}
	}
}
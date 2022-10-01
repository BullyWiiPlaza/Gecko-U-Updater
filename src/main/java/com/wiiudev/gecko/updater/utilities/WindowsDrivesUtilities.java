package com.wiiudev.gecko.updater.utilities;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.EnumVariant;
import com.jacob.com.JacobObject;
import com.jacob.com.Variant;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.io.FileUtils.readFileToByteArray;

public class WindowsDrivesUtilities
{
	static
	{
		try
		{
			extractJacobDLL("jacob-1.18-x86.dll");
			extractJacobDLL("jacob-1.18-x64.dll");
		} catch (final Exception exception)
		{
			exception.printStackTrace();
		}
	}

	private static void extractJacobDLL(final String relativeClasspathResourceFilePath) throws Exception
	{
		val fileContentsByteArray = readClasspathResourceToByteArray(relativeClasspathResourceFilePath);
		val workingDirectory = System.getProperty("user.dir");
		val destinationFilePath = Paths.get(workingDirectory).resolve(relativeClasspathResourceFilePath);
		Files.write(destinationFilePath, fileContentsByteArray);
	}

	private static byte[] readClasspathResourceToByteArray(final String relativeClasspathResourceFilePath) throws Exception
	{
		val url = WindowsDrivesUtilities.class.getClassLoader().getResource(relativeClasspathResourceFilePath);
		if (url == null)
		{
			throw new IOException("Classpath resource " + relativeClasspathResourceFilePath + " not found");
		}

		val classpathFile = new File(url.toURI());
		return readFileToByteArray(classpathFile);
	}

	public interface HasNativeValue
	{
		int getNativeValue();
	}

	public enum DriveTypeEnum implements HasNativeValue
	{
		Unknown(0),
		NoRootDirectory(1),
		RemovableDisk(2),
		LocalDisk(3),
		NetworkDrive(4),
		CompactDisc(5),
		RAMDisk(6);

		public final int nativeValue;

		DriveTypeEnum(final int nativeValue)
		{
			this.nativeValue = nativeValue;
		}

		public int getNativeValue()
		{
			return nativeValue;
		}
	}

	private static <T extends Enum<T> & HasNativeValue> T fromNative(@SuppressWarnings("SameParameterValue") final Class<T> clazz, final int value)
	{
		for (val enumConstant : clazz.getEnumConstants())
		{
			if (enumConstant.getNativeValue() == value)
			{
				return enumConstant;
			}
		}
		return null;
	}

	/**
	 * The drive information.
	 */
	public static final class Drive
	{
		/**
		 * File system on the logical disk. Example: NTFS. null if not known.
		 */
		public final String fileSystem;
		/**
		 * Value that corresponds to the type of disk drive this logical disk represents.
		 */
		final DriveTypeEnum driveType;
		/**
		 * The Java file, e.g. "C:\". Never null.
		 */
		public final File root;

		public Drive(String fileSystem, DriveTypeEnum driveType, File root)
		{
			this.fileSystem = fileSystem;
			this.driveType = driveType;
			this.root = root;
		}

		@Override
		public String toString()
		{
			return "Drive{" + root + ", " + driveType + ", fileSystem=" + fileSystem + "}";
		}
	}

	/**
	 * Lists all available Windows drives without actually touching them.
	 * This call should not block on cd-roms, floppies, network drives etc.
	 *
	 * @return a list of drives, never null, may be empty.
	 */
	public static List<Drive> getDrives()
	{
		val result = new ArrayList<Drive>();
		val axWMI = new ActiveXComponent("winmgmts://");

		try
		{
			val devices = axWMI.invoke("ExecQuery", new Variant("Select DeviceID,DriveType,FileSystem from Win32_LogicalDisk"));
			val deviceList = new EnumVariant(devices.toDispatch());
			while (deviceList.hasMoreElements())
			{
				val item = deviceList.nextElement().toDispatch();
				val drive = Dispatch.call(item, "DeviceID").toString().toUpperCase();
				val file = new File(drive + "/");
				val driveType = fromNative(DriveTypeEnum.class, Dispatch.call(item, "DriveType").getInt());
				val fileSystem = Dispatch.call(item, "FileSystem").toString();
				result.add(new Drive(fileSystem, driveType, file));
			}

			return result;
		} finally
		{
			closeQuietly(axWMI);
		}
	}

	private static void closeQuietly(final JacobObject jacobObject)
	{
		try
		{
			jacobObject.safeRelease();
		} catch (final Exception exception)
		{
			exception.printStackTrace();
		}
	}
}

package com.wiiudev.gecko.updater.utilities;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.EnumVariant;
import com.jacob.com.JacobObject;
import com.jacob.com.Variant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WindowsDrivesUtilities
{
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

		DriveTypeEnum(int nativeValue)
		{
			this.nativeValue = nativeValue;
		}

		public int getNativeValue()
		{
			return nativeValue;
		}
	}

	private static <T extends Enum<T> & HasNativeValue> T fromNative(Class<T> clazz, int value)
	{
		for (T c : clazz.getEnumConstants())
		{
			if (c.getNativeValue() == value)
			{
				return c;
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
		public final DriveTypeEnum driveType;
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
	 * Lists all available Windows drives without actually touching them. This call should not block on cd-roms, floppies, network drives etc.
	 *
	 * @return a list of drives, never null, may be empty.
	 */
	public static List<Drive> getDrives()
	{
		List<Drive> result = new ArrayList<>();
		ActiveXComponent axWMI = new ActiveXComponent("winmgmts://");

		try
		{
			Variant devices = axWMI.invoke("ExecQuery", new Variant("Select DeviceID,DriveType,FileSystem from Win32_LogicalDisk"));
			EnumVariant deviceList = new EnumVariant(devices.toDispatch());
			while (deviceList.hasMoreElements())
			{
				Dispatch item = deviceList.nextElement().toDispatch();
				String drive = Dispatch.call(item, "DeviceID").toString().toUpperCase();
				File file = new File(drive + "/");
				DriveTypeEnum driveType = fromNative(DriveTypeEnum.class, Dispatch.call(item, "DriveType").getInt());
				String fileSystem = Dispatch.call(item, "FileSystem").toString();
				result.add(new Drive(fileSystem, driveType, file));
			}

			return result;
		} finally
		{
			closeQuietly(axWMI);
		}
	}

	private static void closeQuietly(JacobObject jacobObject)
	{
		try
		{
			jacobObject.safeRelease();
		} catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}
}
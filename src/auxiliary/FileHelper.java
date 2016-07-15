package auxiliary;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class contains the functions that is needed for working with
 *       files.
 */
public class FileHelper {
	private static String spiliter = null; // This variable keeps slash or back
											// slash for working with pathes
											// based on the OS

	/**
	 * Checks if the given file is exists or not.
	 * 
	 * @param path
	 *            : path of the file
	 * @return true if the file exists.
	 */
	protected static boolean checkFileExitance(String path) {
		File f = new File(path);
		if (f.exists() && !f.isDirectory()) {
			return true;
		}
		return false;
	}

	/**
	 * Makes a directory in the given path.
	 * 
	 * @param directoryPath
	 *            : This parametrs contains the full path of the directory.
	 * @return true if the operation is successed.
	 */
	public static boolean makeDirectory(String directoryPath) {
		File theDir = new File(directoryPath);
		boolean result = false;
		if (theDir.exists()) {
			try {
				theDir.delete();
				// System.out.println("The old directory deleted.");
				theDir.mkdir();
				result = true;
			} catch (SecurityException se) {
				System.err.println("Some problems happened while creating directory for extracting docx file.");
				return false;
			}
		}
		// if the directory does not exist, create it
		if (!theDir.exists()) {
			try {
				theDir.mkdir();
				result = true;
			} catch (SecurityException se) {
				System.err.println("Some problems happened while creating directory for extracting docx file.");
				return false;
			}
		}
		return result;
	}

	/**
	 * Extracts the extention of the given file.
	 * 
	 * @param path
	 *            : the path to the file.
	 * @return a string that contains the extention or null if file doesn't
	 *         exist.
	 */
	public static String getFileExtention(String path) {
		String ext = null;
		File file = new File(path);
		if (file.isFile() && !file.isDirectory()) {
			ext = FilenameUtils.getExtension(path);
		}
		return ext;
	}

	/**
	 * Extracts the file name of the given file
	 * 
	 * @param path
	 *            : path to the file
	 * @return a string that contains the file name or null if file doesn't
	 *         exist.
	 */
	public static String getFileName(String path) {
		String name = null;
		File file = new File(path);
		if (file.isFile() && !file.isDirectory()) {
			name = FilenameUtils.getBaseName(path);
		}
		return name;
	}

	/**
	 * Extracts the file name with its extention
	 * 
	 * @param path
	 *            : path to the file
	 * @return a string that contains the full file name or null or null if file
	 *         doesn't exist.
	 */
	public static String getFileFullName(String path) {
		String name = null;
		File file = new File(path);
		if (file.isFile() && !file.isDirectory()) {
			name = file.getName();
		}
		return name;
	}

	/**
	 * Deletes a folder.
	 * 
	 * @param folder
	 *            : a reference to the folder
	 */
	public static void deleteFolder(File folder) {
		if (!folder.isDirectory())
			return;
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}

	/**
	 * Copies a file to a desired directory with a new name.
	 * 
	 * @param srcFile
	 *            : path to the file
	 * @param targetPath
	 *            : the destination directory
	 * @param newName
	 *            : new name of the file in the destination path
	 * @return a reference to the new file
	 */
	public static File copyFile(String srcFile, String targetPath, String newName) {
		File source = new File(srcFile);
		Path src = source.toPath();
		String destination = newName != null ? (targetPath + getPathSpiliter() + newName)
				: (targetPath + getPathSpiliter() + source.getName());
		File target = new File(destination);
		Path dst = target.toPath();
		try {
			Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
			return target;
		} catch (IOException e) {
			System.out.println("Problem in copy files from A to B.");
			System.out.println("A : " + srcFile);
			System.out.println(destination);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the correct spliter of the pathes based on the OS. For Windows OS
	 * returns back slash (\) for Linux type OS returns (/)
	 * 
	 * @return a string that contains the spiliter
	 */
	public static String getPathSpiliter() {
		if (FileHelper.spiliter != null)
			return FileHelper.spiliter;
		String os = System.getProperty("os.name").toLowerCase();
		if (os == null)
			FileHelper.spiliter = "\\";
		else if (os.contains("win"))
			FileHelper.spiliter = "\\";
		else
			FileHelper.spiliter = "/";
		//
		return FileHelper.spiliter;
	}
}

package auxiliary;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class contains the functions for unziping the comprassed files.
 */
public class Unzipper extends FileHelper {
	/**
	 * Extracts a given file with a specified extension in the desired
	 * destination folder
	 * 
	 * @param pathToFile
	 *            : A string that contains the path to the file
	 * @param fileExtension
	 *            : the extension of the file for checking
	 * @param destinationFolder
	 *            : the desired destination for extraction
	 * @return an array list that contains addresses to the extracted files and
	 *         folders
	 * @throws IOException
	 */
	public static ArrayList<String> unzipFile(String pathToFile, String fileExtension, String destinationFolder)
			throws IOException {
		ArrayList<String> addresses = null;
		if (checkFileExitance(pathToFile)) {
			Path p = Paths.get(pathToFile);
			String fileFullName = p.getFileName().toString();
			String filePath = p.getParent().toString();
			String[] parts = fileFullName.split("\\.");
			String fileName = "";
			if (parts.length > 0)
				fileName = parts[0];
			// check if there is any dot in file name, then add other sections
			// of its name
			for (int i = 1; i < parts.length - 1; i++)
				fileName += '.' + parts[i];
			String targetDirectory = destinationFolder == null ? (filePath + FileHelper.getPathSpiliter() + fileName)
					: destinationFolder;
			String _fileExtension = parts[parts.length - 1];

			if (_fileExtension.toLowerCase().compareTo(fileExtension) == 0) {
				if (makeDirectory(targetDirectory)) {
					addresses = unzipDirectory(pathToFile, targetDirectory);
					addresses.add(targetDirectory); // keep it as the last
													// address
				}
			}
		}
		return addresses;
	}

	/**
	 * Unzip a given ziped directory.
	 * 
	 * @param srcZipFile
	 *            : the string path to the desired ziped file
	 * @param tgtDir
	 *            : the string path to the desired destination folder for
	 *            extracting the ziped file
	 * @return an array list that contains addresses to the extracted files and
	 *         folders
	 * @throws IOException
	 */
	private static ArrayList<String> unzipDirectory(String srcZipFile, String tgtDir) throws IOException {
		return unzipDirectory(new File(srcZipFile), new File(tgtDir));
	}

	/**
	 * Unzip a given ziped directory.
	 * 
	 * @param srcZipFile
	 *            : the actual file path to the desired ziped file
	 * @param tgtDir
	 *            : the actual file path to the desired destination folder for
	 *            extracting the ziped file
	 * @return an array list that contains addresses to the extracted files and
	 *         folders
	 * @throws IOException
	 */
	private static ArrayList<String> unzipDirectory(File srcZipFile, File tgtDir) throws IOException {
		/**
		 * Make target directory if not available already
		 */
		ArrayList<String> addresses = new ArrayList<String>();
		tgtDir.mkdirs();

		ZipFile zipFile = new ZipFile(srcZipFile);
		Enumeration<?> entries = zipFile.entries();
		ZipEntry entry = null;
		File tgtFile = null;

		while (entries.hasMoreElements()) {
			entry = (ZipEntry) entries.nextElement();
			tgtFile = new File(tgtDir, entry.getName());

			if (entry.isDirectory() && !tgtFile.exists()) {
				tgtFile.mkdirs();
				continue;
			}
			addresses.add(tgtFile.toString());
			/**
			 * Handle file and create parent directory if it does'nt exist
			 */
			if (!tgtFile.getParentFile().exists()) {
				tgtFile.getParentFile().mkdirs();
			}

			InputStream inStream = zipFile.getInputStream(entry);
			BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(tgtFile));

			byte[] buffer = new byte[1024];
			int read;

			while ((read = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, read);
			}

			inStream.close();
			outStream.close();
		} // end of while
		zipFile.close();
		return addresses;
	}
}

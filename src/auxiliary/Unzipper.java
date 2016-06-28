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

public class Unzipper extends FileHelper{
	public static ArrayList<String> unzipFile(String pathToFile, String fileExtension, String destinationFolder) throws IOException
	{
		ArrayList<String> addresses = null;
		if(checkFileExitance(pathToFile))
		{
			Path p = Paths.get(pathToFile);
		    String fileFullName = p.getFileName().toString();
		    String filePath = p.getParent().toString();
		    String[] parts = fileFullName.split("\\.");
		    String fileName="";
			    if(parts.length>0)
			    	fileName=parts[0];
			    // check if there is any dot in file name, then add other sections of its name
			    for(int i=1;i<parts.length-1;i++)
			    	fileName += '.'+parts[i]; 
			String targetDirectory = destinationFolder == null ? (filePath+FileHelper.getPathSpiliter()+fileName) : destinationFolder;
		    String _fileExtension = parts[parts.length-1];
		    
		    if(_fileExtension.toLowerCase().compareTo(fileExtension)==0){
		    	if(makeDirectory(targetDirectory)){			    	
			    	addresses = unzipDirectory(pathToFile, targetDirectory);
			    	addresses.add(targetDirectory); // keep it as the last address
			    }
		    }
		}
		return addresses;
	}
	
	private static ArrayList<String> unzipDirectory(String srcZipFile, String tgtDir) throws IOException {
		return unzipDirectory(new File(srcZipFile), new File(tgtDir));
	}

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
			//System.out.println("Unzipping file : " + tgtFile);

			if (entry.isDirectory() && !tgtFile.exists()) {
				tgtFile.mkdirs();
				continue;
			}
			addresses.add(tgtFile.toString());
			/**
			 * Handle file and create parent directory
			 * if it does'nt exist
			 */
			if (!tgtFile.getParentFile().exists()) {
				tgtFile.getParentFile().mkdirs();
			}

			InputStream inStream = zipFile.getInputStream(entry);
			BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(tgtFile));

			byte [] buffer = new byte[1024];
			int read;

			while ((read = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, read);
			}

			inStream.close();
			outStream.close();
		}// end of while
		zipFile.close();
		return addresses;
	}
}

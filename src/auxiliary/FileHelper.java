package auxiliary;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FilenameUtils;

public class FileHelper {
	private static String spiliter = null;
	protected static boolean checkFileExitance(String path){
		File f = new File(path);
		if(f.exists() && !f.isDirectory()) { 
		    return true;
		}
		return false;
	}
	public static boolean makeDirectory(String directoryPath){
		File theDir = new File(directoryPath);
		boolean result = false;
		if (theDir.exists()) {
		    try{
		    	theDir.delete();
		    	//System.out.println("The old directory deleted.");
		        theDir.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		    	System.err.println("Some problems happened while creating directory for extracting docx file.");
		        return false;
		    }        
		}
		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    try{
		        theDir.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		    	System.err.println("Some problems happened while creating directory for extracting docx file.");
		        return false;
		    }        
		}
		return result;
	}
	
	public static String getFileExtention(String path){
		String ext = null;
		File file = new File(path);
		if(file.isFile() && !file.isDirectory()){
			ext = FilenameUtils.getExtension(path);
		}
		return ext;
	}
	
	public static String getFileName(String path){
		String name = null;
		File file = new File(path);
		if(file.isFile() && !file.isDirectory()){
			name = FilenameUtils.getBaseName(path);
		}
		return name;
	}
	
	public static String getFileFullName(String path){
		String name = null;
		File file = new File(path);
		if(file.isFile() && !file.isDirectory()){
			name = file.getName();
		}
		return name;
	}
	
	public static void deleteFolder(File folder) {
		if(!folder.isDirectory())
			return;
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
	public static File copyFile(String srcFile, String targetPath, String newName){
		File source = new File(srcFile);
		Path src = source.toPath();
		String destination = newName != null ? (targetPath+"/"+newName) : (targetPath+"/"+source.getName());
		File target = new File(destination);
		Path dst = target.toPath();
		try {
			Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
			return target;
		} catch (IOException e) {
			System.out.println("Problem in copy files from A to B.");
			System.out.println("A : "+srcFile);
			System.out.println(destination);
			e.printStackTrace();
		}
		return null;
	}
	public static String getPathSpiliter(){
		if(FileHelper.spiliter!=null)
			return FileHelper.spiliter;
		String os = System.getProperty("os.name").toLowerCase();
		if(os ==null)
			FileHelper.spiliter = "\\";
		else if (os.contains("win"))
			FileHelper.spiliter = "\\";
		else
			FileHelper.spiliter = "/";
		//
		return FileHelper.spiliter;
	}
}

package fidusWriter.fileStructure;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role   This class contains functions for working with file system addresses inside of the text files.   
 */
public class Address {
	private String path = null;
	private AddressType type;
	private String fileExtenssion = null;
	private String directory = null;
	private String fileName = null;
	private String fileFullName = null;
	/**
	 * The getters and setters area
	 */
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public AddressType getType() {
		return type;
	}
	public void setType(AddressType type) {
		this.type = type;
	}
	public String getDirectory() {
		return directory;
	}
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileExtenssion() {
		return fileExtenssion;
	}
	public void setFileExtenssion(String fileExtenssion) {
		this.fileExtenssion = fileExtenssion;
	}
	public String getFileFullName() {
		return fileFullName;
	}
	public void setFileFullName(String fileFullName) {
		this.fileFullName = fileFullName;
	}
	// End of getters and setters area
	/**
	 * A constructor that received a string which contains a path
	 * @param path
	 */
	public Address(String path) {
		super();
		this.setPath(path);
		if(this.checkFileExitance(path)){
			this.setType(AddressType.file);
			Path p = Paths.get(path);
			if(p.getParent() != null)
				this.setDirectory(p.getParent().toString());
			String fileFullName = p.getFileName().toString();
			this.setFileFullName(fileFullName);
		    String[] parts = fileFullName.split("\\.");
		    String fileExtension = parts[parts.length-1];
		    if(parts.length>1)
		    	this.setFileExtenssion(fileExtension);
		    String fileName="";
			if(parts.length>0)
			    fileName=parts[0];
			for(int i=1;i<parts.length-1;i++) // check if there is any dot in file name, then add other sections of its name
			    fileName += '.'+parts[i];
			this.setFileName(fileName);
		} else if(this.checkDirectoryExitance(path)){
			this.setType(AddressType.directory);
			this.setDirectory(path);
		} else{
			this.setType(AddressType.invalid);
			return;
		}
	}
	/**
	 * Checks does the passed path is the address of an actual file or not
	 * @param path
	 * @return
	 */
	protected boolean checkFileExitance(String path){
		File f = new File(path);
		if(f.exists() && !f.isDirectory()) { 
		    return true;
		}
		return false;
	}
	/**
	 * Checks does the passed path is the address of an actual directory or not
	 * @param path
	 * @return
	 */
	protected boolean checkDirectoryExitance(String path){
		File f = new File(path);
		if(f.exists() && f.isDirectory()) { 
		    return true;
		}
		return false;
	}
	/**
	 * Prints the data of the object for tesing
	 */
	public void printPathInfo(){
		System.out.println("The path : "+this.getPath());
		if(this.getType() == AddressType.invalid){
			System.out.println("is an "+ this.getType().toString()+" address.");
		} else if(this.getType() == AddressType.file){
			System.out.println("is a "+ this.getType().toString()+".");
			System.out.println("File root : "+this.getDirectory());
			System.out.println("File name : "+this.getFileName());
			System.out.println("File extenssion : "+this.getFileExtenssion());
			System.out.println("File fullname : "+this.getFileFullName());
		} else{
			System.out.println("is a directory.");
		}
	}
	
}

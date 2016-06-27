package fidusWriter.fileStructure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import auxiliary.FileHelper;
import auxiliary.Unzipper;
import auxiliary.Zipper;
import fidusWriter.converter.todocx.FidusToDocx;
import fidusWriter.converter.tofidus.DocxToFidus;
import fidusWriter.model.bibliography.Bibliography;
import fidusWriter.model.document.Document;
import fidusWriter.model.images.Images;

public class Fidus {
	private boolean realFidusFile = false;
	private ArrayList<Address> folders = null;
	private ArrayList<Address> undefaultFiles = null;
	private Address bibliography = null;
	private Address document = null;
	private Address images = null;
	private Address mimetype = null;
	private Address filetype_version = null;
	private Document doc = null;
	private Images img = null;
	private Bibliography bibo = null;
	private String mediaDirectory = null;
	private String imagesDirectory = null;
	private String thumbnailsDirectory = null;
	private String fileParentDirectory = null;
	private String temporaryWorkingFolder = null;
	private String pathToFidusFile = null;
	//
	public Document getDoc() {
		return doc;
	}
	public void setDoc(Document doc) {
		this.doc = doc;
	}
	public ArrayList<Address> getFolders() {
		return folders;
	}
	public ArrayList<Address> getUndefaultFiles() {
		return undefaultFiles;
	}
	public Address getBibliography() {
		return bibliography;
	}
	public void setBibliography(Address bibliography) {
		this.bibliography = bibliography;
	}
	public Address getDocument() {
		return document;
	}
	public void setDocument(Address document) {
		this.document = document;
	}
	public Address getImages() {
		return images;
	}
	public void setImages(Address images) {
		this.images = images;
	}
	public Address getMimetype() {
		return mimetype;
	}
	public void setMimetype(Address mimetype) {
		this.mimetype = mimetype;
	}
	public Address getFiletype_version() {
		return filetype_version;
	}
	public void setFiletype_version(Address filetype_version) {
		this.filetype_version = filetype_version;
	}
	//
	public Fidus(String destinationFolder, String destinationFileName){
		super();
		this.setRealFidusFile(false);
		this.setPathToFidusFile(destinationFolder+FileHelper.getPathSpiliter()+destinationFileName);
		int random = (int )(Math.random() * 50 + 1);
		this.setFileParentDirectory(destinationFolder);	
		String temp = destinationFolder+"/temp_"+random;
		while(!FileHelper.makeDirectory(temp)){
			random = (int )(Math.random() * 50 + 1);
			temp = destinationFolder+"/temp_"+random;
		}
		this.setTemporaryWorkingFolder(temp);
		this.setDocument(new Address(temp+"/document.json"));
		this.setBibliography(new Address(temp+"/bibliography.json"));
		this.setImages(new Address(temp+"/images.json"));
		this.setFiletype_version(new Address(temp+"/filetype-version"));
		this.setMimetype(new Address(temp+"/mimetype"));
		this.doc = new Document("");
		this.bibo = new Bibliography();
		this.img = new Images();
	}
	
	public Fidus(String pathToFidus) throws Exception {
		super();
		this.setRealFidusFile(true);
		this.setPathToFidusFile(pathToFidus);
		ArrayList<String> addresses = null;
		addresses = Unzipper.unzipFile(pathToFidus, "fidus", null);
		if(addresses != null){
			Address add = new Address(pathToFidus);
			this.setFileParentDirectory(add.getDirectory());
		    // create media directory
			this.setMediaDirectory(this.getFileParentDirectory()+"/media");
			FileHelper.makeDirectory(this.getMediaDirectory());
			// create images directory
			this.setImagesDirectory(this.getMediaDirectory()+"/images");
			FileHelper.makeDirectory(this.getImagesDirectory());
			// create thumbnails directory
			this.setThumbnailsDirectory(this.getMediaDirectory()+"/image_thumbnails");
			FileHelper.makeDirectory(this.getThumbnailsDirectory());
			//
			this.undefaultFiles = new ArrayList<Address>();
			this.folders = new ArrayList<Address>();
			for(int i=0; i<addresses.size();i++)
				this.pushAddress(addresses.get(i));
			this.parseFidus();
		}
	}
	//
	private void pushAddress(String address) throws Exception{
		Address add = new Address(address);
		if(add.getType() == AddressType.invalid){
			throw new Exception("The passed path from unziper is invalid : "+address);
		} else if(add.getType() == AddressType.directory){
			this.folders.add(add);
		} else if(address.toLowerCase().contains("document.json")){
			this.setDocument(add);
		} else if(address.toLowerCase().contains("bibliography.json")){
			this.setBibliography(add);
		} else if(address.toLowerCase().contains("images.json")){
			this.setImages(add);
		} else if(address.toLowerCase().contains("filetype-version")){
			this.setFiletype_version(add);
		} else if(address.toLowerCase().contains("mimetype")){
			this.setMimetype(add);
		} else if(address.toLowerCase().contains("_thumbnail.jpg") || 
				  address.toLowerCase().contains("_thumbnail.jpeg") ||
				  address.toLowerCase().contains("_thumbnail.png")  ||
				  address.toLowerCase().contains("_thumbnail.bmp")){
			Files.copy(Paths.get(address), 
					   Paths.get(this.getThumbnailsDirectory()+"/"+add.getFileFullName()),
					   StandardCopyOption.REPLACE_EXISTING);
		} else if(address.toLowerCase().contains(".jpg") || 
				  address.toLowerCase().contains(".jpeg") ||
				  address.toLowerCase().contains(".png")  ||
				  address.toLowerCase().contains(".bmp")){
			Files.copy(Paths.get(address), 
					   Paths.get(this.getImagesDirectory()+"/"+add.getFileFullName()),
					   StandardCopyOption.REPLACE_EXISTING);
		}
		else{
			this.undefaultFiles.add(add);
		}
	}
	private void parseFidus(){
		this.doc = new Document();
		this.doc.importFromFile(this.getDocument().getPath());
		System.out.println("document.json : \n"+this.doc.toString());
		this.img = new Images();
		this.img.importFromFile(this.getImages().getPath());
		System.out.println("images.json : \n"+this.img.toString());
		this.bibo = new Bibliography();
		this.bibo.importFromFile(this.getBibliography().getPath());
		System.out.println("bibliography.json : \n"+this.bibo.toString());
	}
	public void toDocx(String storePath, String templatePath) throws Exception{
		if(this.isRealFidusFile()){
			FidusToDocx ftd = new FidusToDocx(this.getDoc(), this.getImg(), this.getBibo(), this.getFileParentDirectory());
			ftd.startConvert(storePath, templatePath);
		} else
			throw new Exception("It is not a real fidus file to convert.");
	}
	public void createFromDocx(String pathToDocx, Map<String, String> stylesMap) throws Exception{
		if(!this.isRealFidusFile()){
			DocxToFidus d2f = new DocxToFidus(pathToDocx, this.getTemporaryWorkingFolder(), stylesMap);
			List<File> files = d2f.startConversion(this.bibo, this.doc, this.img);
			this.saveCreatedFidusFile(files);
		} else
			throw new Exception("It is a real fidus file. Nothing for conversion.");
	}
	private void saveCreatedFidusFile(List<File> files) {
		// Compress files inside of the temporary folder
		this.createMimetypeFile();
		this.createFiletypeVersionFile();
		this.createDocumentFile();
		this.createImagesFile();
		this.createBibliographyFile();
		files.add(new File(this.getMimetype().getPath()));
		files.add(new File(this.getFiletype_version().getPath()));
		files.add(new File(this.getDocument().getPath()));
		files.add(new File(this.getImages().getPath()));
		files.add(new File(this.getBibliography().getPath()));
		Zipper.zipFiles(files, this.getPathToFidusFile());
		System.out.println("Look at "+this.getFileParentDirectory()+ " directory.");
		System.out.println("The file ["+this.getPathToFidusFile()+"] has been created successfully.");
		System.out.println("Removing temporary folder.");
		FileHelper.deleteFolder(new File(this.getTemporaryWorkingFolder()));
	}
	private boolean createBibliographyFile() {
		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.getBibliography().getPath()), "UTF-8"));
			out.write(this.getBibo().toString());
			out.close();
			return true;
		} catch ( IOException e) {
			e.printStackTrace();
		} 
		return false;
		
	}
	private boolean createImagesFile() {
		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.getImages().getPath()), "UTF-8"));
			out.write(this.getImg().toString());
			out.close();
			return true;
		} catch ( IOException e) {
			e.printStackTrace();
		} 
		return false;
		
	}
	private boolean createDocumentFile() {
		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.getDocument().getPath()), "UTF-8"));
			out.write(this.getDoc().toString());
			out.close();
			return true;
		} catch ( IOException e) {
			e.printStackTrace();
		} 
		return false;
		
	}
	private boolean createFiletypeVersionFile(){
		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.getFiletype_version().getPath()), "UTF-8"));
			out.write("1.2");
			out.close();
			return true;
		} catch ( IOException e) {
			e.printStackTrace();
		} 
		return false;
	}
	private boolean createMimetypeFile() {
		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.getMimetype().getPath()), "UTF-8"));
			out.write("application/fidus+zip");
			out.close();
			return true;
		} catch ( IOException e) {
			e.printStackTrace();
		} 
		return false;
	}
	
	public Images getImg() {
		return img;
	}
	public void setImg(Images img) {
		this.img = img;
	}
	public String getMediaDirectory() {
		return mediaDirectory;
	}
	public void setMediaDirectory(String mediaDirectory) {
		this.mediaDirectory = mediaDirectory;
	}
	public String getImagesDirectory() {
		return imagesDirectory;
	}
	public void setImagesDirectory(String imagesDirectory) {
		this.imagesDirectory = imagesDirectory;
	}
	public String getThumbnailsDirectory() {
		return thumbnailsDirectory;
	}
	public void setThumbnailsDirectory(String thumbnailsDirectory) {
		this.thumbnailsDirectory = thumbnailsDirectory;
	}
	public String getFileParentDirectory() {
		return fileParentDirectory;
	}
	public void setFileParentDirectory(String fileParentDirectory) {
		this.fileParentDirectory = fileParentDirectory;
	}
	public Bibliography getBibo() {
		return bibo;
	}
	public void setBibo(Bibliography bibo) {
		this.bibo = bibo;
	}
	public String getTemporaryWorkingFolder() {
		return temporaryWorkingFolder;
	}
	public void setTemporaryWorkingFolder(String temporaryWorkingFolder) {
		this.temporaryWorkingFolder = temporaryWorkingFolder;
	}
	public boolean isRealFidusFile() {
		return realFidusFile;
	}
	public void setRealFidusFile(boolean realFidusFile) {
		this.realFidusFile = realFidusFile;
	}
	public String getPathToFidusFile() {
		return pathToFidusFile;
	}
	public void setPathToFidusFile(String pathToFidusFile) {
		this.pathToFidusFile = pathToFidusFile;
	}
	
}

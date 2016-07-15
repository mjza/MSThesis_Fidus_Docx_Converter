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

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class contains variables and functions that models a Fidus file
 */
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

	// Getters and setters section
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

	public void setFolders(ArrayList<Address> folders) {
		this.folders = folders;
	}

	public void setUndefaultFiles(ArrayList<Address> undefaultFiles) {
		this.undefaultFiles = undefaultFiles;
	}

	// end of getters and setters

	/**
	 * A constructor for creating a default model of the fidus file for converting docx to fidus
	 * @param destinationFolder
	 * @param destinationFileName
	 */
	public Fidus(String destinationFolder, String destinationFileName) {
		super();
		this.setRealFidusFile(false);
		this.setPathToFidusFile(destinationFolder + FileHelper.getPathSpiliter() + destinationFileName);
		int random = (int) (Math.random() * 50 + 1);
		this.setFileParentDirectory(destinationFolder);
		String temp = destinationFolder + FileHelper.getPathSpiliter() + "temp_" + random;
		while (!FileHelper.makeDirectory(temp)) {
			random = (int) (Math.random() * 50 + 1);
			temp = destinationFolder + FileHelper.getPathSpiliter() + "temp_" + random;
		}
		this.setTemporaryWorkingFolder(temp);
		this.setDocument(new Address(temp + FileHelper.getPathSpiliter() + "document.json"));
		this.setBibliography(new Address(temp + FileHelper.getPathSpiliter() + "bibliography.json"));
		this.setImages(new Address(temp + FileHelper.getPathSpiliter() + "images.json"));
		this.setFiletype_version(new Address(temp + FileHelper.getPathSpiliter() + "filetype-version"));
		this.setMimetype(new Address(temp + FileHelper.getPathSpiliter() + "mimetype"));
		this.doc = new Document("");
		this.bibo = new Bibliography();
		this.img = new Images();
	}

	/**
	 * Receives a path to a fidus file and make a java model of that.
	 * @param pathToFidus
	 * @throws Exception
	 */
	public Fidus(String pathToFidus) throws Exception {
		super();
		this.setRealFidusFile(true);
		this.setPathToFidusFile(pathToFidus);
		ArrayList<String> addresses = null;
		addresses = Unzipper.unzipFile(pathToFidus, "fidus", null);
		if (addresses != null) {
			if (addresses.size() > 0)
				this.setTemporaryWorkingFolder(addresses.get(addresses.size() - 1));
			Address add = new Address(pathToFidus);
			this.setFileParentDirectory(add.getDirectory());
			// create media directory
			this.setMediaDirectory(this.getFileParentDirectory() + FileHelper.getPathSpiliter() + "media");
			FileHelper.makeDirectory(this.getMediaDirectory());
			// create images directory
			this.setImagesDirectory(this.getMediaDirectory() + FileHelper.getPathSpiliter() + "images");
			FileHelper.makeDirectory(this.getImagesDirectory());
			// create thumbnails directory
			this.setThumbnailsDirectory(this.getMediaDirectory() + FileHelper.getPathSpiliter() + "image_thumbnails");
			FileHelper.makeDirectory(this.getThumbnailsDirectory());
			//
			this.undefaultFiles = new ArrayList<Address>();
			this.folders = new ArrayList<Address>();
			for (int i = 0; i < addresses.size(); i++)
				this.pushAddress(addresses.get(i));
			this.parseFidus();
		}
	}

	/**
	 * Receives the addresses from the unziper and put the addresses of fidus file's parts in corect variables
	 * @param address
	 * @throws Exception
	 */
	private void pushAddress(String address) throws Exception {
		Address add = new Address(address);
		if (add.getType() == AddressType.invalid) {
			throw new Exception("The passed path from unziper is invalid : " + address);
		} else if (add.getType() == AddressType.directory) {
			this.folders.add(add);
		} else if (address.toLowerCase().contains("document.json")) {
			this.setDocument(add);
		} else if (address.toLowerCase().contains("bibliography.json")) {
			this.setBibliography(add);
		} else if (address.toLowerCase().contains("images.json")) {
			this.setImages(add);
		} else if (address.toLowerCase().contains("filetype-version")) {
			this.setFiletype_version(add);
		} else if (address.toLowerCase().contains("mimetype")) {
			this.setMimetype(add);
		} else if (address.toLowerCase().contains("_thumbnail.jpg") || address.toLowerCase().contains("_thumbnail.jpeg")
				|| address.toLowerCase().contains("_thumbnail.png")
				|| address.toLowerCase().contains("_thumbnail.bmp")) {
			Files.copy(Paths.get(address),
					Paths.get(this.getThumbnailsDirectory() + FileHelper.getPathSpiliter() + add.getFileFullName()),
					StandardCopyOption.REPLACE_EXISTING);
		} else if (address.toLowerCase().contains(".jpg") || address.toLowerCase().contains(".jpeg")
				|| address.toLowerCase().contains(".png") || address.toLowerCase().contains(".bmp")) {
			Files.copy(Paths.get(address),
					Paths.get(this.getImagesDirectory() + FileHelper.getPathSpiliter() + add.getFileFullName()),
					StandardCopyOption.REPLACE_EXISTING);
		} else {
			this.undefaultFiles.add(add);
		}
	}

	/**
	 * Reads a Fidus file and makes models of document.json, images.json and
	 * bibliography.json
	 */
	private void parseFidus() {
		this.doc = new Document();
		this.doc.importFromFile(this.getDocument().getPath());
		System.out.println("document.json : \n" + this.doc.toString());
		this.img = new Images();
		this.img.importFromFile(this.getImages().getPath());
		System.out.println("images.json : \n" + this.img.toString());
		this.bibo = new Bibliography();
		this.bibo.importFromFile(this.getBibliography().getPath());
		System.out.println("bibliography.json : \n" + this.bibo.toString());
	}

	/**
	 * Converts the Fidus file to the docx file
	 * 
	 * @param storePath
	 * @param templatePath
	 * @throws Exception
	 */
	public void toDocx(String storePath, String templatePath) throws Exception {
		if (this.isRealFidusFile()) {
			FidusToDocx ftd = new FidusToDocx(this.getDoc(), this.getImg(), this.getBibo(),
					this.getFileParentDirectory());
			ftd.startConvert(storePath, templatePath);
		} else
			throw new Exception("It is not a real fidus file to convert.");
	}

	/**
	 * Cereates Fidus file from a docx file
	 * 
	 * @param pathToDocx
	 * @param stylesMap
	 * @throws Exception
	 */
	public void createFromDocx(String pathToDocx, Map<String, String> stylesMap) throws Exception {
		if (!this.isRealFidusFile()) {
			DocxToFidus d2f = new DocxToFidus(pathToDocx, this.getTemporaryWorkingFolder(), stylesMap);
			List<File> files = d2f.startConversion(this.bibo, this.doc, this.img);
			this.saveCreatedFidusFile(files);
		} else
			throw new Exception("It is a real fidus file. Nothing for conversion.");
	}

	/**
	 * Saves the model of the Fidus file as an actual file
	 * 
	 * @param files
	 */
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
		System.out.println("Look at \"" + this.getFileParentDirectory() + "\" directory.");
		System.out.println("The file [" + this.getPathToFidusFile() + "] has been created successfully.");
		System.out.println("Removing temporary folder.");
		FileHelper.deleteFolder(new File(this.getTemporaryWorkingFolder()));
	}

	/**
	 * Creates bibliography.json file in working folder
	 * 
	 * @return
	 */
	private boolean createBibliographyFile() {
		try {
			Writer out = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(this.getBibliography().getPath()), "UTF-8"));
			out.write(this.getBibo().toString());
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * Creates an images.json file in working folder
	 * 
	 * @return
	 */
	private boolean createImagesFile() {
		try {
			Writer out = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(this.getImages().getPath()), "UTF-8"));
			out.write(this.getImg().toString());
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * Creates a document.json file in working folder
	 * 
	 * @return
	 */
	private boolean createDocumentFile() {
		try {
			Writer out = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(this.getDocument().getPath()), "UTF-8"));
			out.write(this.getDoc().toString());
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * creates the filetype-version file of fidus file
	 * 
	 * @return
	 */
	private boolean createFiletypeVersionFile() {
		try {
			Writer out = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(this.getFiletype_version().getPath()), "UTF-8"));
			out.write("1.2");
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Creates the mimetype file of the fidus file
	 * 
	 * @return
	 */
	private boolean createMimetypeFile() {
		try {
			Writer out = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(this.getMimetype().getPath()), "UTF-8"));
			out.write("application/fidus+zip");
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Deletes the temporary working folders
	 */
	public void deleteTemporaryFolders() {
		System.out.println("Removing temporary folders.");
		FileHelper.deleteFolder(new File(this.getTemporaryWorkingFolder()));
		FileHelper.deleteFolder(new File(this.getMediaDirectory()));
	}

}

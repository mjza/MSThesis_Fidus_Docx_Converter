package threads;

import java.util.Map;
import javax.swing.JButton;

import auxiliary.FileHelper;
import fidusWriter.fileStructure.Fidus;

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class contains functions to response the events of the Docx2Fidus
 *       tab.
 */
public class Docx2FidusThread implements Runnable {

	String source = null;
	String destinationFolder = null;
	Map<String, String> stylesMap = null;
	JButton button = null;

	/**
	 * When the user presses the start button for conversion the Docx to Fidus
	 * then this constructor is called to create a new thread for handelling the
	 * conversion.
	 * 
	 * @param source
	 *            : The Docx source file's path
	 * @param destinationFolder
	 *            : the destination forlder's path
	 * @param stylesMap
	 *            : the mapping model for used styles in Docx
	 * @param button
	 *            : a refernce to start button to be inactivated during the
	 *            conversion
	 */
	public Docx2FidusThread(String source, String destinationFolder, Map<String, String> stylesMap, JButton button) {
		super();
		this.source = source;
		this.destinationFolder = destinationFolder;
		this.stylesMap = stylesMap;
		this.button = button;
	}

	/**
	 * Here is the core part of the thread that calls the converter
	 */
	@Override
	public void run() {
		this.convertDocx2Fidus();
		this.button.setEnabled(true);
	}

	/**
	 * This function handels the steps of converting the Docx to Fidus
	 */
	private void convertDocx2Fidus() {
		System.out.println("Start of the W2F module.");
		try {
			// add new data from file
			Fidus fidus = new Fidus(this.destinationFolder, FileHelper.getFileName(this.source) + ".fidus");
			fidus.createFromDocx(this.source, this.stylesMap);
		} catch (Exception e) {
			System.out.println("Some error happen in time of reading docx file.");
			e.printStackTrace();
		}
	}
}

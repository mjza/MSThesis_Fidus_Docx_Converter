package threads;

import java.util.Map;
import javax.swing.JButton;

import auxiliary.FileHelper;
import fidusWriter.fileStructure.Fidus;


public class Docx2FidusThread implements Runnable {

	String source = null;
	String destinationFolder = null;
	Map<String, String> stylesMap = null;
	JButton button = null;
	public Docx2FidusThread(String source, String destinationFolder, Map<String, String> stylesMap, JButton button) {
		super();
		this.source = source;
		this.destinationFolder = destinationFolder;
		this.stylesMap = stylesMap;
		this.button = button;
	}

	@Override
	public void run() {
		this.convertDocx2Fidus();
		this.button.setEnabled(true);
	}
	
	private void convertDocx2Fidus(){
		System.out.println("Start of the W2F module.");
		try {
			// add new data from file
	    	Fidus fidus = new Fidus(this.destinationFolder, FileHelper.getFileName(this.source)+".fidus");
	    	fidus.createFromDocx(this.source, this.stylesMap);	    	
		} catch (Exception e) {
			System.out.println("Some error happen in time of reading docx file.");
			e.printStackTrace();
		}
	}
}

package threads;

import java.awt.Cursor;

import javax.swing.JButton;
import javax.swing.JFrame;

import auxiliary.FileHelper;
import fidusWriter.fileStructure.Fidus;

public class Fidus2DocxThread implements Runnable {

	String template = null;
	String source = null;
	String destination = null;
	JButton button = null;
	JFrame frame = null;
	public Fidus2DocxThread(String template, String source, String destination, JFrame frame, JButton button) {
		super();
		this.template = template;
		this.source = source;
		this.destination = destination;
		this.button = button;
		this.frame = frame;
	}

	@Override
	public void run() {
		this.convertFidus2Docx();
		this.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		this.button.setEnabled(true);
	}
	
	private void convertFidus2Docx(){
		System.out.println("Start of the F2W module.");
		Fidus fidus = null;
		try {
			fidus = new Fidus(this.source);
			System.out.println("The *.fidus file uncompressed successfully.");
			fidus.toDocx(this.destination+FileHelper.getPathSpiliter()+FileHelper.getFileName(source)+".docx",this.template);
			
		} catch (Exception e) {
			System.out.println("Some error happen in time of reading fidus file.");
			e.printStackTrace();
		} finally {
			if(fidus!=null)
				fidus.deleteTemporaryFolders();
		}
	}
}

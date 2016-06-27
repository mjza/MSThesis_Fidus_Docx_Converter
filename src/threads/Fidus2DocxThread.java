package threads;

import javax.swing.JButton;

import auxiliary.FileHelper;
import fidusWriter.fileStructure.Fidus;

public class Fidus2DocxThread implements Runnable {

	String template = null;
	String source = null;
	String destination = null;
	JButton button = null;
	public Fidus2DocxThread(String template, String source, String destination, JButton button) {
		super();
		this.template = template;
		this.source = source;
		this.destination = destination;
		this.button = button;
	}

	@Override
	public void run() {
		this.convertFidus2Docx();
		this.button.setEnabled(true);
	}
	
	private void convertFidus2Docx(){
		System.out.println("Start of the F2W module.");
		try {
			Fidus fidus = new Fidus(this.source);
			System.out.println("The *.fidus file uncompressed successfully.");
			fidus.toDocx(this.destination+"/"+FileHelper.getFileName(source)+".docx",this.template);
		} catch (Exception e) {
			System.out.println("Some error happen in time of reading fidus file.");
			e.printStackTrace();
		}
	}
}

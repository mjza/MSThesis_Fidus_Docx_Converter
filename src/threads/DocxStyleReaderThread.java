package threads;

import java.awt.Cursor;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;

import fidusWriter.converter.tofidus.DocxToFidus;


public class DocxStyleReaderThread implements Runnable {

	String source = null;
	DefaultTableModel dtm = null;
	JFrame frame = null;
	JButton button = null;
	public DocxStyleReaderThread(String source, DefaultTableModel dtm, JFrame frame, JButton button) {
		super();
		this.source = source;
		this.dtm = dtm;
		this.frame = frame;
		this.button = button;
	}

	@Override
	public void run() {
		this.updateDocxTable();
		this.button.setEnabled(true);
	}
	
	private void updateDocxTable(){
		System.out.println("Please wait some seconds to fill the style table.");
		try {
			// add new data from file
	    	DocxToFidus d2f = new DocxToFidus(this.source);
	    	List<String> styles = d2f.getStyles();
	    	for (int i= 0; i < styles.size(); i++) {
	    		String destination = "Normal Text";
	    		String  source = styles.get(i).toLowerCase();
	    		//
	    		if(source.contains("heading1") || source.equals("h1") || source.contains("headline1") || source.equals("1st headline"))
	    			destination = "1st Headline";
	    		else if (source.contains("heading2") || source.equals("h2") || source.contains("headline2") || source.equals("2nd headline"))
	    			destination = "2nd Headline";
	    		else if (source.contains("heading3") || source.equals("h3") || source.contains("headline3") || source.equals("3rd headline"))
	    			destination = "3rd Headline";
	    		else if (source.contains("heading4") || source.equals("h4") || source.contains("headline4") || source.equals("4th headline"))
	    			destination = "4th Headline";
	    		else if (source.contains("heading5") || source.equals("h5") || source.contains("headline5") || source.equals("5th headline"))
	    			destination = "5th Headline";
	    		else if (source.contains("heading6") || source.equals("h6") || source.contains("headline6") || source.equals("6th headline"))
	    			destination = "6th Headline";
	    		else if (source.contains("heading") || source.equals("h7") || source.equals("h8") || source.equals("h9") || source.contains("headline"))
	    			destination = "6th headline";
	    		else if (source.contains("subtitle") )
	    			destination = "Subtitle";
	    		else if (source.contains("title") )
	    			destination = "Title";
	    		else if (source.contains("author") )
	    			destination = "Authors";
	    		else if (source.contains("abstract") )
	    			destination = "Abstract";
	    		else if (source.contains("keyword") )
	    			destination = "Keywords";
	    		else if (source.contains("blockquote") || source.contains("quote"))
	    			destination = "Blockquote";
	    		else if (source.contains("textbeforecitation") )
	    			destination = "TextBeforeCitation";
	    		else if (source.contains("citation") )
	    			destination = "Citation";
	    		else if (source.contains("numbered") || source.contains("ordered"))
	    			destination = "Numbered List";
	    		else if (source.contains("bullet") || (source.contains("list") && !source.contains("no")))
	    			destination = "Bulleted List";
	    		else if (source.contains("equation"))
	    			destination = "Math";
	    		else if (source.contains("caption"))
	    			destination = "Caption";
	    		else if (source.contains("code") )
	    			destination = "Code";
	    		else if (source.contains("figure") || source.contains("chart") || source.contains("picture"))
	    			destination = "Figure";
	    		else if (source.contains("footnote") || source.contains("endnote"))
	    			destination = "Footnote";
	    		else if (source.contains("hyperlink") )
	    			destination = "Hyperlink";
	    		else if (source.contains("bold") )
	    			destination = "Bold Text";
	    		else if (source.contains("italic") || source.contains("emphasis"))
	    			destination = "Italic Text";
	    		//
	    		this.dtm.addRow(new Object[] { styles.get(i), destination});
	    	}
	    	if(styles.size()>0)
	    		System.out.println("Style table is ready to modify.");
		} catch (Exception e) {
			System.out.println("Some error happen in time of reading docx file.");
			e.printStackTrace();
		}
		this.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}

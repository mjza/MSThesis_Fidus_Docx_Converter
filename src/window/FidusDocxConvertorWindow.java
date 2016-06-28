package window;

import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import auxiliary.FileHelper;
import threads.Docx2FidusThread;
import threads.DocxStyleReaderThread;
import threads.Fidus2DocxThread;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class FidusDocxConvertorWindow extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JFrame frame = null;
	private static JTextField templateFileTextField = null;
	private static JTextField fidusSourceFileTextField = null;
	private static JTextField docxDestinationFileTextField = null;
	private static JTextField docxSourceFileTextField = null;
	private static JTextField fidusDestinationFileTextField = null;
	private static JTable table = null;
	private static JButton startConvertD2FB = null;
	private static JTextArea textArea1 = new JTextArea();
	private static JTextArea textArea2 = new JTextArea();
	private static String currentPath = System.getProperty("user.dir");
    public FidusDocxConvertorWindow() {    	
        
    	super(new GridLayout(1, 1));
    	// show console data in window
        this.redirectSystemStreams();
        //
        Color bg = new Color(0,0,0);
        Color fg = new Color(255,255,255);
        FidusDocxConvertorWindow.textArea1.setBackground(bg);
        FidusDocxConvertorWindow.textArea2.setBackground(bg);
        FidusDocxConvertorWindow.textArea1.setForeground(fg);
        FidusDocxConvertorWindow.textArea2.setForeground(fg);
        FidusDocxConvertorWindow.textArea1.setFont(new Font("Consolas", Font.BOLD, 14));
        FidusDocxConvertorWindow.textArea2.setFont(new Font("Consolas", Font.BOLD, 14));
        FidusDocxConvertorWindow.textArea1.append("Console output\n");
        FidusDocxConvertorWindow.textArea2.append("Console output\n");
        //
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(800, 600));
        
        
        JComponent panel1 = new JPanel(false);
        tabbedPane.addTab("Fidus 2 Docx", null, panel1, "Convert files from fidus to docx format.");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        this.initiateFidus2DocxTab(panel1);
        
        JComponent panel2 = new JPanel(false);
        tabbedPane.addTab("Docx 2 Fidus", null, panel2, "Convert files from docx to fidus.");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        this.initiateDocx2FidusTab(panel2);
        
        JComponent panel3 = new JPanel(false);
        tabbedPane.addTab("About", null, panel3, "About the product and manual.");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
        
        //Add the tabbed pane to this panel.
        this.add(tabbedPane);
        
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }
    @SuppressWarnings("serial")
	protected void initiateDocx2FidusTab(JComponent panel){
    	panel.setLayout(null);
    	
    	Color bg = new Color(255,255,255);
        Color fg = new Color(212,226,239);    	
    	FidusDocxConvertorWindow.docxSourceFileTextField = new JTextField("");
    	FidusDocxConvertorWindow.fidusDestinationFileTextField = new JTextField("");
    	FidusDocxConvertorWindow.docxSourceFileTextField.setEditable(false);
    	FidusDocxConvertorWindow.docxSourceFileTextField.setBackground(bg);
    	FidusDocxConvertorWindow.docxSourceFileTextField.setForeground(fg);
    	FidusDocxConvertorWindow.fidusDestinationFileTextField.setEditable(false);
    	FidusDocxConvertorWindow.fidusDestinationFileTextField.setBackground(bg);
    	FidusDocxConvertorWindow.fidusDestinationFileTextField.setForeground(fg);
    	
    	JLabel l1 = new JLabel("Docx Source file: ");
    	
    	JButton changeSourceB = new JButton("Change");
    	int gap = 10;
    	panel.add(l1);
    	panel.add(FidusDocxConvertorWindow.docxSourceFileTextField);
    	panel.add(changeSourceB);

    	Insets insets = panel.getInsets();
    	Dimension sizeL = l1.getPreferredSize();
    	int x = 10 + insets.left;
    	int y = 10 + insets.top;
    	int w = sizeL.width+2;
    	int h = sizeL.height;
    	if(h<25){
    		h = 25;
    		l1.setSize(w,h);
    	}
    	l1.setBounds(x, y, w, h);
    	x += w + gap;
    	w = 570;    	
    	FidusDocxConvertorWindow.docxSourceFileTextField.setBounds(x, y, w, h);
    	
    	
    	Dimension sizeB = changeSourceB.getPreferredSize();
    	x += w + gap;
    	w = sizeB.width;
    	h = sizeB.height;
    	changeSourceB.setBounds(x, y, w, h);
    	
    	changeSourceB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JFileChooser chooser = new JFileChooser();
        	    chooser.setCurrentDirectory(new java.io.File("."));
        	    chooser.setDialogTitle("Choose the Source File");
        	    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        	    FileFilter filter = new FileNameExtensionFilter("*.docx", "docx");
        	    chooser.addChoosableFileFilter(filter);
        	    chooser.setAcceptAllFileFilterUsed(false);
        	    //
        	    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        	    	String path = chooser.getSelectedFile().getPath();
        	    	if(path != null){
        	    		FidusDocxConvertorWindow.docxSourceFileTextField.setText(path);
        	    		FidusDocxConvertorWindow.fillStylesTable(path);
        	    		String folder = chooser.getSelectedFile().getParent();
        	    		FidusDocxConvertorWindow.fidusDestinationFileTextField.setText(folder);
        	    	}
        	    } 
            }          
         });
    	
    	//// Second line

    	
       	JLabel l2 = new JLabel("Destination path:");    	
       	
       	JButton changeDestinationB = new JButton("Change");
       	panel.add(l2);
       	panel.add(FidusDocxConvertorWindow.fidusDestinationFileTextField);
       	panel.add(changeDestinationB);

       	sizeL = l2.getPreferredSize();
       	x = 10 + insets.left;
       	y = 50 + insets.top;
       	w = sizeL.width;
       	h = sizeL.height;
       	if(h<25){
       		h = 25;
       		l2.setSize(w,h);
       	}
       	l2.setBounds(x, y, w, h);
       	
       	x += w + gap;
       	w = 570;    	
       	FidusDocxConvertorWindow.fidusDestinationFileTextField.setBounds(x, y, w, h);    	
       	
       	sizeB = changeDestinationB.getPreferredSize();
       	x += w + gap;
       	w = sizeB.width;
       	h = sizeB.height;
       	changeDestinationB.setBounds(x, y, w, h);
       	
       	changeDestinationB.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
               	JFileChooser chooser = new JFileChooser();
           	    chooser.setCurrentDirectory(new java.io.File("."));
           	    chooser.setDialogTitle("Choose the Destination Path");
           	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
           	    chooser.setAcceptAllFileFilterUsed(false);
           	    //
           	    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
           	    	String path = chooser.getSelectedFile().getPath().toString();
           	    	if(path != null)
           	    		FidusDocxConvertorWindow.fidusDestinationFileTextField.setText(path);
          	    		
           	    }
               }          
            });
       	
       	// Third line
       	JSeparator horizontalLine = new JSeparator();
       	horizontalLine.setBackground(new Color(200,221,242));
       	panel.add(horizontalLine);
       	sizeB = horizontalLine.getPreferredSize();
       	x = insets.left;
       	y = 90 + insets.top;
       	w = 800 ;
       	h = 2;
       	horizontalLine.setBounds(x, y, w, h);
       	
       	// Forth line
       	String[] header = {"Style in Docx File", "Element in Fidus File"};
       	DefaultTableModel tableModel = new DefaultTableModel(0, 2);
		tableModel.setColumnIdentifiers(header);
		FidusDocxConvertorWindow.table = new JTable(tableModel){
		    @Override
		    public boolean isCellEditable(int row, int column) {
		        return column == 0 ? false : true;
		    }
		};
		
		
		TableColumn column = table.getColumnModel().getColumn(1);
		column.setCellEditor(new MapTableCellEditor());
		
		
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);
       	
		 //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        panel.add(scrollPane);
        
        x = 10 + insets.left;
       	y = 100 + insets.top;
       	w = 780;
       	h = 125;
       	scrollPane.setBounds(x, y, w, h);
       	
       	// Fifth line
       	JSeparator horizontalLine2 = new JSeparator();
       	horizontalLine2.setBackground(new Color(200,221,242));
       	panel.add(horizontalLine2);
       	sizeB = horizontalLine2.getPreferredSize();
       	x = insets.left;
       	y = 235 + insets.top;
       	w = 800 ;
       	h = 2;
       	horizontalLine2.setBounds(x, y, w, h);
       	
       	
       	// Sixth line
       	
       	FidusDocxConvertorWindow.startConvertD2FB = new JButton("Start Conversion");
       	panel.add(FidusDocxConvertorWindow.startConvertD2FB);
       	sizeB = FidusDocxConvertorWindow.startConvertD2FB.getPreferredSize();
       	x = 800/2 - sizeB.width/2;
       	y = 245 + insets.top;
       	w = sizeB.width;
       	h = sizeB.height;
       	FidusDocxConvertorWindow.startConvertD2FB.setBounds(x, y, w, h);
       	
       	FidusDocxConvertorWindow.startConvertD2FB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	String source = FidusDocxConvertorWindow.docxSourceFileTextField.getText();
            	String destination = FidusDocxConvertorWindow.fidusDestinationFileTextField.getText();
            	if(source.length()==0){
            		FidusDocxConvertorWindow.infoBox("Please provide the path to the source file.", "Error");
            	} else if(destination.length()==0){
            		FidusDocxConvertorWindow.infoBox("Please provide the path to the destination folder.", "Error");
            	} else{
            		//JButton button = (JButton)e.getSource();
            		//button.setEnabled(false);
            		System.out.println("Please wait ....");
            		DefaultTableModel dm = (DefaultTableModel) FidusDocxConvertorWindow.table.getModel();
                	int row = dm.getRowCount();
                	//Remove rows one by one from the end of the table
                	Map<String, String> stylesMap = new HashMap<String, String>();
                	for (int i=0; i<row; i++) {
                		stylesMap.put(dm.getValueAt(i,0).toString(), dm.getValueAt(i,1).toString());
                	}
                	Docx2FidusThread myD2FConvertor = new Docx2FidusThread(source, destination, stylesMap, FidusDocxConvertorWindow.startConvertD2FB);
                    Thread t = new Thread(myD2FConvertor);
                    t.start();
                    myD2FConvertor = null;
            	}
            }          
         });
       	
       	// Seventh line       	
       	FidusDocxConvertorWindow.textArea1.setBorder(BorderFactory.createLineBorder(Color.BLUE));
       	FidusDocxConvertorWindow.textArea1.setEditable(false);
       	x = 10 + insets.left;
       	y = 280 + insets.top;
       	w = 800-25;
       	h = 290;
       	textArea1.setBounds(x, y, w, h);
       	
       	JScrollPane scroll = new JScrollPane(FidusDocxConvertorWindow.textArea1);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
       	panel.add(scroll);
       	scroll.setBounds(x, y, w, h);
    	
    }
    protected static void fillStylesTable(String source) {
    	FidusDocxConvertorWindow.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    	FidusDocxConvertorWindow.startConvertD2FB.setEnabled(false);
    	DefaultTableModel dm = (DefaultTableModel) FidusDocxConvertorWindow.table.getModel();
    	int rowCount = dm.getRowCount();
    	//Remove rows one by one from the end of the table
    	for (int i = rowCount - 1; i >= 0; i--) {
    	    dm.removeRow(i);
    	}
    	// add new data from file
    	DocxStyleReaderThread myD2FConvertor = new DocxStyleReaderThread(source, dm, FidusDocxConvertorWindow.frame, FidusDocxConvertorWindow.startConvertD2FB);
        Thread t = new Thread(myD2FConvertor);
        t.start();
        myD2FConvertor = null;
    	
	}
	protected void initiateFidus2DocxTab(JComponent panel){
    	panel.setLayout(null);
    	
    	Color bg = new Color(255,255,255);
        Color fg = new Color(212,226,239);
    	
    	FidusDocxConvertorWindow.templateFileTextField = new JTextField(FidusDocxConvertorWindow.currentPath+FileHelper.getPathSpiliter()+"templates"+FileHelper.getPathSpiliter()+"style.docx");
    	FidusDocxConvertorWindow.fidusSourceFileTextField = new JTextField("");
    	FidusDocxConvertorWindow.docxDestinationFileTextField = new JTextField("");
    	FidusDocxConvertorWindow.templateFileTextField.setEditable(false);
    	FidusDocxConvertorWindow.templateFileTextField.setBackground(bg);
    	FidusDocxConvertorWindow.templateFileTextField.setForeground(fg);
    	FidusDocxConvertorWindow.fidusSourceFileTextField.setEditable(false);
    	FidusDocxConvertorWindow.fidusSourceFileTextField.setBackground(bg);
    	FidusDocxConvertorWindow.fidusSourceFileTextField.setForeground(fg);
    	FidusDocxConvertorWindow.docxDestinationFileTextField.setEditable(false);
    	FidusDocxConvertorWindow.docxDestinationFileTextField.setBackground(bg);
    	FidusDocxConvertorWindow.docxDestinationFileTextField.setForeground(fg);
    	
    	File f = new File(FidusDocxConvertorWindow.currentPath+FileHelper.getPathSpiliter()+"temp"+FileHelper.getPathSpiliter()+"fw.fidus");
    	if(f.exists() && !f.isDirectory()) { 
    		FidusDocxConvertorWindow.fidusSourceFileTextField.setText(f.getPath());
    		f = new File(FidusDocxConvertorWindow.currentPath+FileHelper.getPathSpiliter()+"temp");
    		if(!f.isFile() && f.isDirectory()) {
    			FidusDocxConvertorWindow.docxDestinationFileTextField.setText(f.getPath());
    		}
    	}
    	
    	JLabel l1 = new JLabel("Template file: ");
    	    	
    	JButton changeTemplateB = new JButton("Change");
    	int gap = 10;
    	panel.add(l1);
    	panel.add(FidusDocxConvertorWindow.templateFileTextField);
    	panel.add(changeTemplateB);

    	Insets insets = panel.getInsets();
    	Dimension sizeL = l1.getPreferredSize();
    	int x = 10 + insets.left;
    	int y = 10 + insets.top;
    	int w = sizeL.width+22;
    	int h = sizeL.height;
    	if(h<25){
    		h = 25;
    		l1.setSize(w,h);
    	}
    	l1.setBounds(x, y, w, h);
    	x += w + gap;
    	w = 570;    	
    	FidusDocxConvertorWindow.templateFileTextField.setBounds(x, y, w, h);
    	
    	
    	Dimension sizeB = changeTemplateB.getPreferredSize();
    	x += w + gap;
    	w = sizeB.width;
    	h = sizeB.height;
    	changeTemplateB.setBounds(x, y, w, h);
    	
    	changeTemplateB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JFileChooser chooser = new JFileChooser();
        	    chooser.setCurrentDirectory(new java.io.File("."));
        	    chooser.setDialogTitle("Choose a Template DOCX File");
        	    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        	    FileFilter filter = new FileNameExtensionFilter("*.docx", "docx");
        	    chooser.addChoosableFileFilter(filter);
        	    chooser.setAcceptAllFileFilterUsed(false);
        	    //
        	    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        	    	String path = chooser.getSelectedFile().toString();
        	    	if(path != null)
        	    		FidusDocxConvertorWindow.templateFileTextField.setText(path);
        	    } 
            }          
         });
    	
    	
    	
    	//// Second line

    	
    	JLabel l2 = new JLabel("Source file: ");    	
    	
    	JButton changeSourceB = new JButton("Change");
    	panel.add(l2);
    	panel.add(FidusDocxConvertorWindow.fidusSourceFileTextField);
    	panel.add(changeSourceB);

    	sizeL = l2.getPreferredSize();
    	x = 10 + insets.left;
    	y = 50 + insets.top;
    	w = sizeL.width+36;
    	h = sizeL.height;
    	if(h<25){
    		h = 25;
    		l2.setSize(w,h);
    	}
    	l2.setBounds(x, y, w, h);
    	
    	x += w + gap;
    	w = 570;    	
    	FidusDocxConvertorWindow.fidusSourceFileTextField.setBounds(x, y, w, h);    	
    	
    	sizeB = changeSourceB.getPreferredSize();
    	x += w + gap;
    	w = sizeB.width;
    	h = sizeB.height;
    	changeSourceB.setBounds(x, y, w, h);
    	
    	changeSourceB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JFileChooser chooser = new JFileChooser();
        	    chooser.setCurrentDirectory(new java.io.File("."));
        	    chooser.setDialogTitle("Choose the Source FIDUS File");
        	    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        	    FileFilter filter = new FileNameExtensionFilter("*.fidus", "fidus");
        	    chooser.addChoosableFileFilter(filter);
        	    chooser.setAcceptAllFileFilterUsed(false);
        	    //
        	    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        	    	String path = chooser.getSelectedFile().toString();
        	    	if(path != null){
        	    		FidusDocxConvertorWindow.fidusSourceFileTextField.setText(path);
        	    		if(FidusDocxConvertorWindow.docxDestinationFileTextField.getText().length()==0){
        	    			FidusDocxConvertorWindow.docxDestinationFileTextField.setText(chooser.getSelectedFile().getParent().toString());
        	    		}
        	    	}
        	    	
        	    }
            }          
         });
    	
    	//// Third line

    	
       	JLabel l3 = new JLabel("Destination path:");    	
       	
       	JButton changeDestinationB = new JButton("Change");
       	panel.add(l3);
       	panel.add(FidusDocxConvertorWindow.docxDestinationFileTextField);
       	panel.add(changeDestinationB);

       	sizeL = l3.getPreferredSize();
       	x = 10 + insets.left;
       	y = 90 + insets.top;
       	w = sizeL.width;
       	h = sizeL.height;
       	if(h<25){
       		h = 25;
       		l3.setSize(w,h);
       	}
       	l3.setBounds(x, y, w, h);
       	
       	x += w + gap;
       	w = 570;    	
       	FidusDocxConvertorWindow.docxDestinationFileTextField.setBounds(x, y, w, h);    	
       	
       	sizeB = changeDestinationB.getPreferredSize();
       	x += w + gap;
       	w = sizeB.width;
       	h = sizeB.height;
       	changeDestinationB.setBounds(x, y, w, h);
       	
       	changeDestinationB.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
               	JFileChooser chooser = new JFileChooser();
           	    chooser.setCurrentDirectory(new java.io.File("."));
           	    chooser.setDialogTitle("Choose the Destination Path");
           	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
           	    chooser.setAcceptAllFileFilterUsed(false);
           	    //
           	    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
           	    	String path = chooser.getSelectedFile().getPath().toString();
           	    	if(path != null)
           	    		FidusDocxConvertorWindow.docxDestinationFileTextField.setText(path);
          	    		
           	    }
               }          
            });
       	
       	// Third line
       	JSeparator horizontalLine = new JSeparator();
       	horizontalLine.setBackground(new Color(200,221,242));
       	panel.add(horizontalLine);
       	sizeB = horizontalLine.getPreferredSize();
       	x = insets.left;
       	y = 130 + insets.top;
       	w = 800 ;
       	h = 2;
       	horizontalLine.setBounds(x, y, w, h);
       	
       	
        JButton startConvertB = new JButton("Start Conversion");
       	panel.add(startConvertB);
       	sizeB = startConvertB.getPreferredSize();
       	x = 800/2 - sizeB.width/2;
       	y = 140 + insets.top;
       	w = sizeB.width;
       	h = sizeB.height;
       	startConvertB.setBounds(x, y, w, h);
       	
       	startConvertB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	String template = FidusDocxConvertorWindow.templateFileTextField.getText();
            	String source = FidusDocxConvertorWindow.fidusSourceFileTextField.getText();
            	String destination = FidusDocxConvertorWindow.docxDestinationFileTextField.getText();
            	if(template.length()==0){
            		FidusDocxConvertorWindow.infoBox("Please provide the path to the template file.", "Error");
            	} else if(source.length()==0){
            		FidusDocxConvertorWindow.infoBox("Please provide the path to the source file.", "Error");
            	} else if(destination.length()==0){
            		FidusDocxConvertorWindow.infoBox("Please provide the path to the destination folder.", "Error");
            	} else{
            		JButton button = (JButton)e.getSource();
            		button.setEnabled(false);
            		System.out.println("Please wait ....");
            		FidusDocxConvertorWindow.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            		FidusDocxConvertorWindow.convertFidus2Docx(template, source, destination, FidusDocxConvertorWindow.frame, button); 
            	}
            }          
         });
       	
       	// fourth line
       	
       	FidusDocxConvertorWindow.textArea2.setBorder(BorderFactory.createLineBorder(Color.BLUE));
       	FidusDocxConvertorWindow.textArea2.setEditable(false);
       	x = 10 + insets.left;
       	y = 175 + insets.top;
       	w = 800-25;
       	h = 390;
       	FidusDocxConvertorWindow.textArea2.setBounds(x, y, w, h);
       	
       	JScrollPane scroll = new JScrollPane(FidusDocxConvertorWindow.textArea2);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
       	panel.add(scroll);
       	scroll.setBounds(x, y, w, h);
       	
    }
    public static void infoBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
    private void updateTextArea(final String text) {
	  SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
	    	FidusDocxConvertorWindow.textArea1.append(text);
	    	FidusDocxConvertorWindow.textArea2.append(text);
	    }
	  });
    }
    private void redirectSystemStreams() {
    	  OutputStream out = new OutputStream() {
    	    @Override
    	    public void write(int b) throws IOException {
    	      updateTextArea(String.valueOf((char) b));
    	    }
    	 
    	    @Override
    	    public void write(byte[] b, int off, int len) throws IOException {
    	      updateTextArea(new String(b, off, len));
    	    }
    	 
    	    @Override
    	    public void write(byte[] b) throws IOException {
    	      write(b, 0, b.length);
    	    }
    	  };
    	 
    	  System.setOut(new PrintStream(out, true));
    	  System.setErr(new PrintStream(out, true));
    }

    public static void setUIFont (javax.swing.plaf.FontUIResource f){
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
          Object key = keys.nextElement();
          Object value = UIManager.get (key);
          if (value != null && value instanceof javax.swing.plaf.FontUIResource)
            UIManager.put (key, f);
          }
    } 
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = FidusDocxConvertorWindow.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from
     * the event dispatch thread.
     */
    public static void createAndShowGUI() {
        //Create and set up the window.
    	setUIFont (new javax.swing.plaf.FontUIResource("Tahoma",Font.BOLD,12));
    	FidusDocxConvertorWindow.frame = new JFrame("Fidus Docx Convertor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.setResizable(false);
        //Add content to the window.
        frame.add(new FidusDocxConvertorWindow(), BorderLayout.CENTER);
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    private static void convertFidus2Docx(String template, String source, String destination, JFrame frame, JButton button){
    	Fidus2DocxThread myF2DConvertor = new Fidus2DocxThread(template, source, destination, frame, button);
        Thread t = new Thread(myF2DConvertor);
        t.start();
        myF2DConvertor = null;
    }
    
}

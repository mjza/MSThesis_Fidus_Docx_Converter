package fidusWriter.converter.todocx;

import java.io.File;

import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;

public class ImageUtil {
	private static int id1 = 0;
    private static int id2 = 1;
    private static org.docx4j.wml.ObjectFactory wmlObjectFactory= Context.getWmlObjectFactory();
    
	public static org.docx4j.wml.P addImageToDocx(WordprocessingMLPackage wordMLPackage,
									  String path, Long width, 
									  String filenameHint, String altText) throws Exception {
		
		// The image to add
		File file = new File(path);
		
		// Our utility method wants that as a byte array
		java.io.InputStream is = new java.io.FileInputStream(file );
        long length = file.length();    
        // You cannot create an array using a long type.
        // It needs to be an int type.
        if (length > Integer.MAX_VALUE) {
        	System.out.println("File too large!!");
        }
        byte[] bytes = new byte[(int)length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            System.out.println("Could not completely read file "+file.getName());
        }
        is.close();
        
        
        org.docx4j.wml.P p = null;
        if(width == null) 
	        p = newImage( wordMLPackage, bytes, 
	        		filenameHint, altText 
	    			);
        else
        	 p = newImage( wordMLPackage, bytes, 
 	        		filenameHint, altText, 
 	    			width.longValue() );
        //
        wordMLPackage.getMainDocumentPart().getContent().add(p);
        // Create object for pPr
        org.docx4j.wml.PPr ppr = wmlObjectFactory.createPPr(); 
        p.setPPr(ppr); 
        // Create object for pStyle
        org.docx4j.wml.PPrBase.PStyle pprbasepstyle = wmlObjectFactory.createPPrBasePStyle(); 
        ppr.setPStyle(pprbasepstyle); 
        pprbasepstyle.setVal( "FWPicture"); 
		return p;
	}

	/**
	 * Create image, without specifying width
	 */
	private static org.docx4j.wml.P newImage( WordprocessingMLPackage wordMLPackage,
			byte[] bytes,
			String filenameHint, String altText) throws Exception {
		
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);
		
        Inline inline = imagePart.createImageInline( filenameHint, altText, 
    			id1++, id2++, false);
        
        // Now add the inline in w:p/w:r/w:drawing
		org.docx4j.wml.P  p = wmlObjectFactory.createP();
		org.docx4j.wml.R  run = wmlObjectFactory.createR();		
		p.getContent().add(run);        
		org.docx4j.wml.Drawing drawing = wmlObjectFactory.createDrawing();		
		run.getContent().add(drawing);		
		drawing.getAnchorOrInline().add(inline);
		return p;
		
	}	
	
	/**
	 * Create image, specifying width in twips
	 */
	private static org.docx4j.wml.P newImage( WordprocessingMLPackage wordMLPackage,
			byte[] bytes,
			String filenameHint, String altText, 
			long cx) throws Exception {
		
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);
		/**
		 * public Inline createImageInline(String filenameHint,
                       String altText,
                       int id1,
                       int id2,
                       long cx,
                       boolean link)
                         throws Exception
			Create a element suitable for this image, which can be _embedded_ in w:p/w:r/w:drawing.
			Parameters:
			filenameHint - Any text, for example the original filename
			altText - Like HTML's alt text
			id1 - An id unique in the document
			id2 - Another id unique in the document
			cx - Image width in twip
			link - true if this is to be linked not embedded None of these things seem to be exposed in Word 2007's user interface, but Word won't open the document if any of the attributes these go in (except @ desc) aren't present!
			Throws:
			Exception
		 */
        Inline inline = imagePart.createImageInline( filenameHint, altText, 
    			id1++, id2++, cx, false);
        
        // Now add the inline in w:p/w:r/w:drawing
		org.docx4j.wml.P  p = wmlObjectFactory.createP();
		org.docx4j.wml.R  run = wmlObjectFactory.createR();		
		p.getContent().add(run);        
		org.docx4j.wml.Drawing drawing = wmlObjectFactory.createDrawing();		
		run.getContent().add(drawing);		
		drawing.getAnchorOrInline().add(inline);
		
		return p;
	}
}

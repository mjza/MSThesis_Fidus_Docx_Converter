package fidusWriter.converter.todocx;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.TransformerException;

import org.docx4j.XmlUtils;
import org.docx4j.bibliography.CTAuthorType;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.wml.CommentRangeEnd;
import org.docx4j.wml.CommentRangeStart;
import org.docx4j.wml.Comments;

import fidusWriter.model.bibliography.Bibliography;
import fidusWriter.model.bibliography.BibliographyEntry;
import fidusWriter.model.document.Child;
import fidusWriter.model.document.ChildrenTypes;
import fidusWriter.model.document.CommentAnswer;
import fidusWriter.model.document.Document;
import fidusWriter.model.document.MetaData;
import fidusWriter.model.document.NodeJson;
import fidusWriter.model.document.NodeType;
import fidusWriter.model.document.NumberingType;
import fidusWriter.model.document.TextNode;
import fidusWriter.model.images.Images;
import mathEquations.EquationConvertor;

public class FidusToDocx {
	private String mediaDirectoryPrefixPath = null;
	private Document doc = null;
	private Images images = null;
	private Bibliography bibliography = null;
	private org.docx4j.openpackaging.packages.WordprocessingMLPackage wordMLPackage = null;
	private org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart mdp = null;
	private org.docx4j.openpackaging.parts.WordprocessingML.FootnotesPart fnp = null;
	private boolean addHyperlinksToFNP = false;
	private org.docx4j.wml.Comments comments = null;
	private org.docx4j.wml.ObjectFactory wmlObjectFactory = null;
	private org.docx4j.math.ObjectFactory mathObjectFactory = null;
	private org.docx4j.bibliography.ObjectFactory biblioObjectFactory = null;
	private org.docx4j.wml.Numbering numbering;
	private final static long indentStep = 360;
	private EquationConvertor equationConvertor = null;
	//
	private int equationCaptionCounter = 0;
	private int photoCaptionCounter = 0;
	private int tableCaptionCounter = 0;
	private int figureCaptionCounter = 0;
	//
	private int footnoteCounter = 0;
	//
	private final static String sdtPrefixMapping = "xmlns:ns0='http://schemas.openxmlformats.org/package/2006/metadata/core-properties' xmlns:ns1='http://purl.org/dc/elements/1.1/'";

	public Bibliography getBibliography() {
		return this.bibliography;
	}

	public void setBibliography(Bibliography bibliography) {
		this.bibliography = bibliography;
	}

	//
	public Document getDoc() {
		return this.doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public Images getImages() {
		return this.images;
	}

	public void setImages(Images images) {
		this.images = images;
	}

	public org.docx4j.openpackaging.packages.WordprocessingMLPackage getWordMLPackage() {
		return wordMLPackage;
	}

	public void setWordMLPackage(org.docx4j.openpackaging.packages.WordprocessingMLPackage wordMLPackage) {
		this.wordMLPackage = wordMLPackage;
	}

	public org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart getMdp() {
		return mdp;
	}

	public void setMdp(org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart mdp) {
		this.mdp = mdp;
	}

	public org.docx4j.wml.ObjectFactory getWmlObjectFactory() {
		return this.wmlObjectFactory;
	}

	public void setWmlObjectFactory(org.docx4j.wml.ObjectFactory wmlObjectFactory) {
		this.wmlObjectFactory = wmlObjectFactory;
	}

	public org.docx4j.wml.Numbering getNumbering() {
		return numbering;
	}

	public void setNumbering(org.docx4j.wml.Numbering numbering) {
		this.numbering = numbering;
	}

	public String getMediaDirectoryPrefixPath() {
		return mediaDirectoryPrefixPath;
	}

	public void setMediaDirectoryPrefixPath(String mediaDirectoryPrefixPath) {
		this.mediaDirectoryPrefixPath = mediaDirectoryPrefixPath;
	}

	public static long getIndentstep() {
		return indentStep;
	}

	//
	public FidusToDocx(Document doc, Images images, Bibliography bibliography, String mediaDirectoryPrefixPath) {
		super();
		this.setDoc(doc);
		this.setImages(images);
		this.setBibliography(bibliography);
		this.setMediaDirectoryPrefixPath(mediaDirectoryPrefixPath);
		this.wmlObjectFactory = org.docx4j.jaxb.Context.getWmlObjectFactory();
		this.mathObjectFactory = new org.docx4j.math.ObjectFactory();
		this.equationConvertor = new EquationConvertor();
		this.biblioObjectFactory = new org.docx4j.bibliography.ObjectFactory();
	}

	public boolean startConvert(String storePath, String templatePath) {
		System.out.println("Converting to docx is in progress.");
		NodeJson contents = this.getDoc().getContents();
		MetaData metaData = this.getDoc().getMetaData();
		if ("DIV".equalsIgnoreCase(contents.getNodeName())
				&& "document-contents".equalsIgnoreCase(contents.getAttributeValue("id"))) {
			File docxFile = null;
			if (templatePath != null) {
				docxFile = new File(templatePath);
				if (!docxFile.exists() || docxFile.isDirectory())
					docxFile = null;
			}
			try {
				if (docxFile != null)
					this.wordMLPackage = org.docx4j.openpackaging.packages.WordprocessingMLPackage.load(docxFile);
				else
					this.wordMLPackage = org.docx4j.openpackaging.packages.WordprocessingMLPackage.createPackage();
				this.mdp = this.wordMLPackage.getMainDocumentPart();
				this.mdp.getContent().clear(); // clean the text inside of the docx
				try {
					this.createFootnotePart();
				} catch (JAXBException e) {
					System.err.println("\nSome error happen in the time of creating footnote part.");
					e.printStackTrace();
				}
				if (this.doc.getComments() != null && this.doc.getComments().getComments() != null
						&& this.doc.getComments().getComments().size() > 0) {
					this.comments = this.addCommentPart();
				}
				org.docx4j.openpackaging.parts.DocPropsCorePart docPropsCorePart = this.wordMLPackage
						.getDocPropsCorePart();
				org.docx4j.docProps.core.CoreProperties coreProps = docPropsCorePart.getContents();
				coreProps.setVersion(this.doc.getVersion().toString());
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
				coreProps.getCreated().getContent().set(0, sdf.format(date));
				coreProps.getModified().getContent().set(0, sdf.format(date));
				org.docx4j.docProps.coverPageProps.CoverPageProperties coverPagePros = null;
				List<org.docx4j.bibliography.CTSourceType> bibliographyResources = null;
				// Get Cover page out
				HashMap<String, org.docx4j.openpackaging.parts.CustomXmlPart> mp = this.wordMLPackage
						.getCustomXmlDataStorageParts();
				Iterator<Map.Entry<String, org.docx4j.openpackaging.parts.CustomXmlPart>> it = mp.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, org.docx4j.openpackaging.parts.CustomXmlPart> pair = (Map.Entry<String, org.docx4j.openpackaging.parts.CustomXmlPart>) it
							.next();
					if (pair.getValue().getClass().getName()
							.equalsIgnoreCase("org.docx4j.openpackaging.parts.DocPropsCoverPagePart")) {
						org.docx4j.openpackaging.parts.DocPropsCoverPagePart docPropsCoverPagePart = (org.docx4j.openpackaging.parts.DocPropsCoverPagePart) pair
								.getValue();
						coverPagePros = docPropsCoverPagePart.getContents();
					} else if (pair.getValue().getClass().getName()
							.equalsIgnoreCase("org.docx4j.openpackaging.parts.WordprocessingML.BibliographyPart")) {
						org.docx4j.openpackaging.parts.WordprocessingML.BibliographyPart bibliographyPart = 
								(org.docx4j.openpackaging.parts.WordprocessingML.BibliographyPart) pair.getValue();
						JAXBElement<org.docx4j.bibliography.CTSources> bibliography = bibliographyPart.getContents();
						bibliographyResources = bibliography.getValue().getSource();
						bibliographyResources.clear();
					}
				}
				//
				this.createBibliography(bibliographyResources);
				//
				this.mdp.getContent().add(this.addSdtBlock(metaData, coreProps, coverPagePros));
				// clean its numbering content
				this.setNumbering(mdp.getNumberingDefinitionsPart().getContents());
				this.numbering.getAbstractNum().clear();
				this.numbering.getNum().clear();
				this.numbering.getNumPicBullet().clear();
				//
				for (int i = 0; i < contents.getC().size(); i++) {
					this.createPart(contents.getC().get(i));
				}

			} catch (InvalidFormatException e) {
				System.out.println("Error in creating WML package.");
				e.printStackTrace();
			} catch (Docx4JException e) {
				System.out.println("Error in reading docx template.");
				e.printStackTrace();
			}
			boolean result = this.saveDocx(wordMLPackage, storePath);
			if (result)
				System.out.println("The docx file has been saved successfully in [" + storePath + "]");
			else
				System.out.println("The process of conversion has failed.");
			return result;

		}
		return false;
	}
	private void createFootnotePart() throws JAXBException, Docx4JException {
		// Setup FootnotesPart if necessary,
		// along with DocumentSettings
		this.fnp = this.mdp.getFootnotesPart();
		if (this.fnp==null) { // that'll be the case in this example
			// initialise it
			this.fnp = new org.docx4j.openpackaging.parts.WordprocessingML.FootnotesPart();
			this.mdp.addTargetPart(this.fnp);
			org.docx4j.wml.CTFootnotes footnotes = this.createFootnotes();
			this.fnp.setJaxbElement(footnotes);
			
			// Usually the settings part contains footnote properties;
			// so add these if not present
			org.docx4j.openpackaging.parts.WordprocessingML.DocumentSettingsPart dsp = this.mdp.getDocumentSettingsPart();
			if (dsp==null) {
				// create it
				dsp = new org.docx4j.openpackaging.parts.WordprocessingML.DocumentSettingsPart();
				this.mdp.addTargetPart(dsp);
			} 
			org.docx4j.wml.CTSettings settings = dsp.getContents();
			if (settings ==null) {
				settings = this.wmlObjectFactory.createCTSettings(); 
				dsp.setJaxbElement(settings);				
			}
			org.docx4j.wml.CTFtnDocProps ftndocprops = settings.getFootnotePr();
			if (ftndocprops==null ) {
				settings.setFootnotePr(this.createSettingsFootnote());
			}
		}
	}
	private org.docx4j.wml.CTFtnDocProps createSettingsFootnote() {
		org.docx4j.wml.CTFtnDocProps ftndocprops = this.wmlObjectFactory.createCTFtnDocProps(); 
		// Create object for footnote
		org.docx4j.wml.CTFtnEdnSepRef ftnednsepref = this.wmlObjectFactory.createCTFtnEdnSepRef(); 
		ftndocprops.getFootnote().add( ftnednsepref); 
		ftnednsepref.setId( BigInteger.valueOf( -1) ); 
		// Create object for footnote
		org.docx4j.wml.CTFtnEdnSepRef ftnednsepref2 = this.wmlObjectFactory.createCTFtnEdnSepRef(); 
		ftndocprops.getFootnote().add( ftnednsepref2); 
		ftnednsepref2.setId( BigInteger.valueOf( 0) ); 
		return ftndocprops;
	}
	private org.docx4j.wml.CTFootnotes createFootnotes() {
		org.docx4j.wml.CTFootnotes footnotes = this.wmlObjectFactory.createCTFootnotes(); 
		// Create object for footnote
		org.docx4j.wml.CTFtnEdn ftnedn = this.wmlObjectFactory.createCTFtnEdn(); 
		footnotes.getFootnote().add( ftnedn); 
		ftnedn.setId( BigInteger.valueOf( -1) ); 
		ftnedn.setType(org.docx4j.wml.STFtnEdn.SEPARATOR);
		// Create object for p
		org.docx4j.wml.P p = this.wmlObjectFactory.createP(); 
		ftnedn.getContent().add( p); 
		// Create object for pPr
		org.docx4j.wml.PPr ppr = this.wmlObjectFactory.createPPr(); 
		p.setPPr(ppr); 
		// Create object for spacing
		org.docx4j.wml.PPrBase.Spacing pprbasespacing = this.wmlObjectFactory.createPPrBaseSpacing(); 
		ppr.setSpacing(pprbasespacing); 
		pprbasespacing.setBefore( BigInteger.valueOf(0) ); 
		pprbasespacing.setAfter( BigInteger.valueOf(0) );
		// Create object for r
		org.docx4j.wml.R r = this.wmlObjectFactory.createR(); 
		p.getContent().add( r); 
		// Create object for separator (wrapped in JAXBElement) 
		org.docx4j.wml.R.Separator rseparator = this.wmlObjectFactory.createRSeparator(); 
		JAXBElement<org.docx4j.wml.R.Separator> rseparatorWrapped = this.wmlObjectFactory.createRSeparator(rseparator); 
		r.getContent().add( rseparatorWrapped); 
		// Create object for footnote
		org.docx4j.wml.CTFtnEdn ftnedn2 = this.wmlObjectFactory.createCTFtnEdn(); 
		footnotes.getFootnote().add( ftnedn2); 
		ftnedn2.setId( BigInteger.valueOf( 0) ); 
		ftnedn2.setType(org.docx4j.wml.STFtnEdn.CONTINUATION_SEPARATOR);
		// Create object for p
		org.docx4j.wml.P p2 = this.wmlObjectFactory.createP(); 
		ftnedn2.getContent().add( p2); 
		// Create object for pPr
		org.docx4j.wml.PPr ppr2 = this.wmlObjectFactory.createPPr(); 
		p2.setPPr(ppr2); 
		// Create object for spacing
		org.docx4j.wml.PPrBase.Spacing pprbasespacing2 = this.wmlObjectFactory.createPPrBaseSpacing(); 
		ppr2.setSpacing(pprbasespacing2); 
		pprbasespacing2.setBefore( BigInteger.valueOf( 0) ); 
		pprbasespacing2.setAfter( BigInteger.valueOf( 0) );
		// Create object for r
		org.docx4j.wml.R r2 = this.wmlObjectFactory.createR(); 
		p2.getContent().add( r2); 
		// Create object for continuationSeparator (wrapped in JAXBElement) 
		org.docx4j.wml.R.ContinuationSeparator rcontinuationseparator = this.wmlObjectFactory.createRContinuationSeparator(); 
		JAXBElement<org.docx4j.wml.R.ContinuationSeparator> rcontinuationseparatorWrapped = this.wmlObjectFactory.createRContinuationSeparator(rcontinuationseparator); 
		r2.getContent().add( rcontinuationseparatorWrapped); 
		return footnotes;
	}
	private org.docx4j.wml.P addFootnoteParagraph(){
		org.docx4j.wml.CTFtnEdn ftnedn = this.wmlObjectFactory.createCTFtnEdn();
		try {
			org.docx4j.wml.CTFootnotes footnotes = this.fnp.getContents();
			footnotes.getFootnote().add(ftnedn);
		} catch (Docx4JException e) {
			System.err.println("\nome Error happen in the time of a footnote creation.");
			e.printStackTrace();
			return null;
		}		
	    ftnedn.setId( BigInteger.valueOf(++this.footnoteCounter) ); 
	    // Create object for p
	    org.docx4j.wml.P p = this.wmlObjectFactory.createP(); 
	    ftnedn.getContent().add(p);
	    
        // Create object for pPr
	    org.docx4j.wml.PPr ppr = this.wmlObjectFactory.createPPr(); 
        p.setPPr(ppr); 
        // Create object for pStyle
        org.docx4j.wml.PPrBase.PStyle pprbasepstyle = this.wmlObjectFactory.createPPrBasePStyle(); 
        ppr.setPStyle(pprbasepstyle); 
        pprbasepstyle.setVal( "FootnoteText"); 
        // Create object for r
        org.docx4j.wml.R r = this.wmlObjectFactory.createR(); 
        p.getContent().add( r); 
        // Create object for rPr
        org.docx4j.wml.RPr rpr = this.wmlObjectFactory.createRPr(); 
        r.setRPr(rpr); 
        // Create object for rStyle
        org.docx4j.wml.RStyle rstyle = this.wmlObjectFactory.createRStyle(); 
        rpr.setRStyle(rstyle); 
        rstyle.setVal( "FootnoteReference"); 
        // Create object for footnoteRef (wrapped in JAXBElement) 
        org.docx4j.wml.R.FootnoteRef rfootnoteref = this.wmlObjectFactory.createRFootnoteRef(); 
        JAXBElement<org.docx4j.wml.R.FootnoteRef> rfootnoterefWrapped = this.wmlObjectFactory.createRFootnoteRef(rfootnoteref); 
        r.getContent().add( rfootnoterefWrapped); 
        // Create object for r
        org.docx4j.wml.R r2 = this.wmlObjectFactory.createR(); 
        p.getContent().add( r2); 
        // Create object for t (wrapped in JAXBElement) 
        org.docx4j.wml.Text text = this.wmlObjectFactory.createText(); 
        JAXBElement<org.docx4j.wml.Text> textWrapped = this.wmlObjectFactory.createRT(text); 
        r2.getContent().add( textWrapped); 
        text.setValue(" "); 
        text.setSpace("preserve"); 
        return p;
	}
	public org.docx4j.wml.R createFootnoteRun(org.docx4j.wml.P paragraph) {
		org.docx4j.wml.R r = this.createARun(paragraph, null);
		// Create object for rPr
		org.docx4j.wml.RPr rpr = this.wmlObjectFactory.createRPr(); 
		r.setRPr(rpr);
		// Create object for vertAlign
		org.docx4j.wml.CTVerticalAlignRun verticalalignrun = this.wmlObjectFactory.createCTVerticalAlignRun(); 
        rpr.setVertAlign(verticalalignrun); 
        verticalalignrun.setVal(org.docx4j.wml.STVerticalAlignRun.SUPERSCRIPT);
		// Create object for rStyle
		org.docx4j.wml.RStyle rstyle = this.wmlObjectFactory.createRStyle(); 
		rpr.setRStyle(rstyle); 
		rstyle.setVal("FootnoteReference"); 
		// Create object for footnoteReference (wrapped in JAXBElement) 
		org.docx4j.wml.CTFtnEdnRef ftnednref = this.wmlObjectFactory.createCTFtnEdnRef(); 
		JAXBElement<org.docx4j.wml.CTFtnEdnRef> ftnednrefWrapped = this.wmlObjectFactory.createRFootnoteReference(ftnednref); 
		r.getContent().add( ftnednrefWrapped); 
		ftnednref.setId( BigInteger.valueOf(this.footnoteCounter));
		return r;
	}
	private org.docx4j.wml.Comments addCommentPart() throws InvalidFormatException {
		// Create and add a Comments Part
		org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart commentsPart = new org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart();
		this.mdp.addTargetPart(commentsPart);
		// Part must have minimal contents
		Comments comments = this.wmlObjectFactory.createComments();
		commentsPart.setJaxbElement(comments);
		return comments;
	}

	private void createPart(Child child) {
		this.createRuns(null, child, null, 0, BigInteger.valueOf(0), null, null);
	}

	private org.docx4j.wml.R createARun(org.docx4j.wml.P paragraph, org.docx4j.wml.R run) {
		if(run!=null)
			return run;
		paragraph = this.createAParagraph(paragraph);
		run = this.wmlObjectFactory.createR();
		paragraph.getContent().add(run);
		return run;
	}

	private org.docx4j.wml.P createAParagraph(org.docx4j.wml.P paragraph) {
		if (paragraph != null)
			return paragraph;
		paragraph = this.wmlObjectFactory.createP();
		this.mdp.getContent().add(paragraph);
		return paragraph;
	}

	private org.docx4j.wml.P createAParagraphForSdtCB(org.docx4j.wml.SdtContentBlock sdtcontentblock) {
		org.docx4j.wml.P paragraph = this.wmlObjectFactory.createP();
		sdtcontentblock.getContent().add(paragraph);
		return paragraph;
	}

	private org.docx4j.wml.BooleanDefaultTrue bool(boolean value) {
		org.docx4j.wml.BooleanDefaultTrue boolValue = new org.docx4j.wml.BooleanDefaultTrue();
		boolValue.setVal(value);
		return boolValue;
	}

	private org.docx4j.wml.RPr createRunProperties(org.docx4j.wml.R run) {
		org.docx4j.wml.RPr rpr = run.getRPr();
		if(rpr==null){
			rpr = this.wmlObjectFactory.createRPr();
			run.setRPr(rpr);
		}
		return rpr;
	}

	private org.docx4j.wml.P addTextToRun(org.docx4j.wml.P paragraph, org.docx4j.wml.R run, String str) {
		run = this.createARun(paragraph, run);
		org.docx4j.wml.Text text = this.wmlObjectFactory.createText();
		JAXBElement<org.docx4j.wml.Text> textWrapped = this.wmlObjectFactory.createRT(text);
		text.setValue(str);
		text.setSpace("preserve");
		run.getContent().add(textWrapped);
		return paragraph;
	}
	private org.docx4j.wml.PPr getOrCreatePPr(org.docx4j.wml.P paragraph){
		org.docx4j.wml.PPr ppr = paragraph.getPPr();
		if(ppr==null){
			ppr = this.wmlObjectFactory.createPPr();
			paragraph.setPPr(ppr);
		}
		return ppr;
	}
	private org.docx4j.wml.P setHeaderParagraph(org.docx4j.wml.P paragraph, int degree) {
		paragraph = this.createAParagraph(paragraph);
		// Create object for pPr
		org.docx4j.wml.PPr ppr = this.getOrCreatePPr(paragraph);
		// Create object for contextualSpacing
		ppr.setContextualSpacing(this.bool(true));
		// Create object for pStyle
		org.docx4j.wml.PPrBase.PStyle pprbasepstyle = this.wmlObjectFactory.createPPrBasePStyle();
		ppr.setPStyle(pprbasepstyle);
		pprbasepstyle.setVal("Heading" + degree);
		return paragraph;
	}

	private org.docx4j.wml.P createRuns(org.docx4j.wml.P paragraph, Child child, org.docx4j.wml.R run, int level,
			BigInteger listId, Figure figure, ArrayList<org.docx4j.wml.Comments.Comment> comments) {
		boolean footnoteVisited = false;
		org.docx4j.wml.P tempParagraph = null;
		if (child.getType() == ChildrenTypes.textnode) {
			if (comments != null) { 
				if(run != null)
					paragraph.getContent().remove(run);
				for (int i = 0; i < comments.size(); i++)
					this.startComment(paragraph, comments.get(i));
				if(run != null)
					paragraph.getContent().add(run);
			}
			TextNode textNode = (TextNode) child;
			paragraph = this.addTextToRun(paragraph, run, textNode.getT());

			if (comments != null) {
				for (int i = 0; i < comments.size(); i++)
					this.endComment(paragraph, comments.get(i));
				comments = null;
			}
		} else {
			NodeJson element = (NodeJson) child;
			
				// Does not need to create a run!!!
				if (element.getNn().getType() == NodeType.p) {
					paragraph = this.createAParagraph(paragraph);
					for (int i = 0; i < element.getC().size(); i++)
						this.createRuns(paragraph, element.getC().get(i), run, level, listId, figure, comments);
				} else {
					if (element.getNn().getType() == NodeType.b) {
						run = this.createARun(paragraph, run);
						org.docx4j.wml.RPr rpr = this.createRunProperties(run);
						rpr.setB(this.bool(true));
					} else if (element.getNn().getType() == NodeType.i) {
						run = this.createARun(paragraph, run);
						org.docx4j.wml.RPr rpr = this.createRunProperties(run);
						rpr.setI(this.bool(true));
					} else if (element.getNn().getType() == NodeType.h1) {
						paragraph = this.setHeaderParagraph(paragraph, 1);
					} else if (element.getNn().getType() == NodeType.h2) {
						paragraph = this.setHeaderParagraph(paragraph, 2);
					} else if (element.getNn().getType() == NodeType.h3) {
						paragraph = this.setHeaderParagraph(paragraph, 3);
					} else if (element.getNn().getType() == NodeType.h4) {
						paragraph = this.setHeaderParagraph(paragraph, 4);
					} else if (element.getNn().getType() == NodeType.h5) {
						paragraph = this.setHeaderParagraph(paragraph, 5);
					} else if (element.getNn().getType() == NodeType.h6) {
						paragraph = this.setHeaderParagraph(paragraph, 6);
					} else if (element.getNn().getType() == NodeType.blockquote) {
						paragraph = this.setQuoteParagraph(paragraph);
					} else if (element.getNn().getType() == NodeType.ol) {
						long indent = FidusToDocx.getIndentstep() * level;
						listId = this.addNewNumberingList(indent, NumberingType.ol);
						paragraph = null;
						level++;
					} else if (element.getNn().getType() == NodeType.ul) {
						long indent = FidusToDocx.getIndentstep() * level;
						listId = this.addNewNumberingList(indent, NumberingType.ul);
						paragraph = null;
						level++;
					} else if (element.getNn().getType() == NodeType.li) {
						paragraph = this.setListParagraph(paragraph, listId);
					} else if (element.getNn().getType() == NodeType.a) {
						run	= this.createHyperlinkRun(paragraph, run, element);
					} else if (element.getNn().getType() == NodeType.figure) {
						// add an empty paragraph
						this.createAnEmptyParagraph();
						//
						figure = new Figure();
						if (element.getAttributeValue("data-equation") != null)
							figure.setDataEquation(element.getAttributeValue("data-equation"));
						if (element.getAttributeValue("data-image") != null)
							figure.setDataImage(Long.parseLong(element.getAttributeValue("data-image")));
						if (element.getAttributeValue("data-figure-category") != null)
							for (FigureCategory cat : FigureCategory.values())
								if (cat.name().equalsIgnoreCase(element.getAttributeValue("data-figure-category"))) {
									figure.setDataFigureCategory(cat);
									break;
								}
						if (element.getAttributeValue("data-caption") != null)
							figure.setDataCaption(element.getAttributeValue("data-caption"));
						//
					} else if (element.getNn().getType() == NodeType.br) {
						paragraph = this.addBreakLineRun(paragraph, run);
					} else if (element.getNn().getType() == NodeType.img) {
						if (figure != null) {
							figure.setPath(element.getAttributeValue("src"));
							try {
								paragraph = figure.addIt2Docx(this.wordMLPackage, this.images,
										this.getMediaDirectoryPrefixPath());
							} catch (Exception e) {
								e.printStackTrace();
							}
							figure = null;
						}
					} else if (element.getNn().getType() == NodeType.pre) {
						// just capture the PRE to capture its child in CODE
					} else if (element.getNn().getType() == NodeType.code) {
						if(paragraph != null && paragraph.getPPr()!=null 
								&& paragraph.getPPr().getPStyle()!=null
								&& paragraph.getPPr().getPStyle().getVal().equals("FWQuote")){
							// Do Nothing. Just visit this node and let go to the children.
						} else{
							for (int i = 0; i < element.getC().size(); i++)
								this.addCodeBlock(element.getC().get(i), paragraph);
							return null;
						}
					} else if (element.getNn().getType() == NodeType.div) {
						if(figure != null)
						{
							// if does not have any child
							if (element.getC() == null){
								if (element.getAttributeValue("class") != null
										&& element.getAttributeValue("class").equalsIgnoreCase("figure-equation")) {
									if (element.getAttributeValue("data-equation") != null) {
										String latexMath = "$" + element.getAttributeValue("data-equation") + "$";
										paragraph = this.addEquationDiv(paragraph, latexMath);
										figure = null;
										return paragraph; // Especially in this cases (Equations) we have to prevent processing of children of this element because we got data from attributes.
									} else {
										figure = null;
										return null;
									}
								}
							} else if (figure.getDataEquation() != null) {
								if (element.getAttributeValue("class") != null
										&& element.getAttributeValue("class").equalsIgnoreCase("figure-equation")) {
									if (element.getAttributeValue("data-equation") != null) {
										String latexMath = "$" + element.getAttributeValue("data-equation") + "$";
										paragraph = this.addEquationDiv(paragraph, latexMath);
										figure = null;
										return paragraph; // Especially in this cases (Equations) we have to prevent processing of children of this element because we got data from attributes. 
									} else {
										figure = null;
										return null;
									}
								}
							}
						}
						if (figure == null && element.getC() != null) 
							return null;	// just capture the DIV if the document has it. seen a FIGURE element just let it goes ahead to be able to capture IMG
						
					} else if (element.getNn().getType() == NodeType.figcaption) {
						if (paragraph == null && element.getC().size() > 1)
							paragraph = this.createAParagraph(paragraph);
					} else if (element.getNn().getType() == NodeType.span) {						
						if(element.hasAttribute("class") && element.getAttributeValue("class").equalsIgnoreCase("equation") && element.hasAttribute("data-equation")) 
						{
							String latexMath = "$" + element.getAttributeValue("data-equation") + "$";
							paragraph = this.addEquationInline(paragraph, latexMath);
							return paragraph; // Especially in this cases (Equations) we have to prevent processing of children of this element because we got data from attributes.
						}else if (element.hasAttribute("class") && element.getAttributeValue("class").equalsIgnoreCase("citation")) {
							String idStr = element.getAttributeValue("data-bib-entry");
							String textBefore = element.getAttributeValue("data-bib-before");
							String[] textBefores = textBefore.split(",,,");
							String pageNumber = element.getAttributeValue("data-bib-page");
							String[] pageNumbers = pageNumber.split(",,,");
							String[] ids = idStr.split(",");
							List<BibliographyEntry> entries = new ArrayList<BibliographyEntry>();
							for(int i=0; i<ids.length; i++){
								Long id = Long.parseLong(ids[i]);
								BibliographyEntry entry = this.bibliography.getBibliographyEntry(id);
								entries.add(entry);
							}
							paragraph = this.createCitation(paragraph, entries, textBefores, pageNumbers);
						}
						else if (element.hasAttribute("data-figure-category")) 
						{
							FigureCategory category = null;
							for (FigureCategory cat : FigureCategory.values())
								if (cat.name().equalsIgnoreCase(element.getAttributeValue("data-figure-category"))) {
									category = cat;
									break;
								}
							if (category != null) {
								if (category == FigureCategory.figure)
									paragraph = this.setCaptionParagraph(paragraph, category,
											++this.figureCaptionCounter);
								else if (category == FigureCategory.table)
									paragraph = this.setCaptionParagraph(paragraph, category,
											++this.tableCaptionCounter);
								else if (category == FigureCategory.photo)
									paragraph = this.setCaptionParagraph(paragraph, category,
											++this.photoCaptionCounter);
								else if (category == FigureCategory.equation)
									paragraph = this.setCaptionParagraph(paragraph, category,
											++this.equationCaptionCounter);
								return paragraph; // It prevents further
													// analysis of its children
							}
						} else if (paragraph == null && element.hasAttribute("data-caption")) {
							paragraph = this.setCaptionParagraph(paragraph, FigureCategory.none, null);
						} else if (element.hasAttribute("class")
								&& element.getAttributeValue("class").equalsIgnoreCase("comment")) {
							Long commentId = Long.parseLong(element.getAttributeValue("data-id"));
							fidusWriter.model.document.Comment commentObj = this.doc.getComments()
									.getComment(commentId);
							comments = this.createComment(commentObj);
						} else if (element.hasAttribute("class")
								&& element.getAttributeValue("class").equalsIgnoreCase("footnote")) {
							tempParagraph = paragraph;
							footnoteVisited = true;
							this.addHyperlinksToFNP = true;
							paragraph = this.addFootnoteParagraph();
							if(paragraph==null){
								footnoteVisited = false;
								paragraph = tempParagraph;
							} else{
								this.createFootnoteRun(tempParagraph);
							}
						}
					} else {
						return null;
					}
					//
					if (element.getC() != null) 
						for (int i = 0; i < element.getC().size(); i++)
							this.createRuns(paragraph, element.getC().get(i), run, level, listId, figure, comments);
					if(footnoteVisited){
						paragraph = tempParagraph;
						footnoteVisited = false;
						this.addHyperlinksToFNP = false;
					}
					
				}
		}
		return paragraph;
	}
	private org.docx4j.wml.R createHyperlinkRun(org.docx4j.wml.P paragraph, org.docx4j.wml.R run, NodeJson element) {
		paragraph = this.createAParagraph(paragraph);
		String href = null;
		String title = null;
		if(element.hasAttribute("href"))
			href = element.getAttributeValue("href");
		if(element.hasAttribute("title"))
			title = element.getAttributeValue("title");
		// We need to add a relationship to word/_rels/document.xml.rels
		// but since its external, we don't use the 
		// usual wordMLPackage.getMainDocumentPart().addTargetPart
		// mechanism
		org.docx4j.relationships.ObjectFactory relsObjectfactory = new org.docx4j.relationships.ObjectFactory();
		
		org.docx4j.relationships.Relationship rel = relsObjectfactory.createRelationship();
		rel.setType( org.docx4j.openpackaging.parts.relationships.Namespaces.HYPERLINK);
		rel.setTarget(href);
		rel.setTargetMode("External");
		if(this.addHyperlinksToFNP)
			this.fnp.getRelationshipsPart(true).addRelationship(rel);
		else
			this.mdp.getRelationshipsPart(true).addRelationship(rel);
		
		
		
		// Create object for hyperlink (wrapped in JAXBElement) 
		org.docx4j.wml.P.Hyperlink phyperlink = this.wmlObjectFactory.createPHyperlink(); 
	    JAXBElement<org.docx4j.wml.P.Hyperlink> phyperlinkWrapped = this.wmlObjectFactory.createPHyperlink(phyperlink); 
	    paragraph.getContent().add( phyperlinkWrapped); 
	    // Create object for r
	    if(run==null)
	    	run = this.wmlObjectFactory.createR();
	    else{
	    	if(paragraph.getContent().contains(run))
	    		paragraph.getContent().remove(run);
	    }
	    phyperlink.getContent().add(run);
	    // Create object for rPr
	    org.docx4j.wml.RPr rpr = this.createRunProperties(run);
	    // Create object for rStyle
	    org.docx4j.wml.RStyle rstyle = this.wmlObjectFactory.createRStyle(); 
	    rpr.setRStyle(rstyle); 
	    rstyle.setVal( "Hyperlink");
	    // addRelationship sets the rel's @Id
	    phyperlink.setId( rel.getId());
	    if(title!=null && title.trim().length()>0)
	    	phyperlink.setTooltip(title);
		return run;
	}

	private org.docx4j.wml.P createCitation(org.docx4j.wml.P paragraph, List<BibliographyEntry> entries, String[] textBefores, String[] pageNumbers) {
		if(entries==null || entries.size()==0)
			return paragraph;
		//
		paragraph = this.createAParagraph(paragraph);
		//
		for(int i=0; i<textBefores.length;i++)
		{
			org.docx4j.wml.R run = this.createARun(paragraph, null);
			this.addTextToRun(paragraph, run, "["+textBefores[i]+"]");
			// Create object for rPr
		    org.docx4j.wml.RPr rpr = this.createRunProperties(run);
		    // Create object for rStyle
		    org.docx4j.wml.RStyle rstyle = this.wmlObjectFactory.createRStyle(); 
		    rpr.setRStyle(rstyle); 
		    rstyle.setVal("FWTextBeforeCitationChar");
		}
		// Create object for sdt (wrapped in JAXBElement)
		org.docx4j.wml.SdtRun sdtrun = wmlObjectFactory.createSdtRun();
		JAXBElement<org.docx4j.wml.SdtRun> sdtrunWrapped = wmlObjectFactory.createPSdt(sdtrun);
		paragraph.getContent().add(sdtrunWrapped);
		// Create object for sdtPr
		org.docx4j.wml.SdtPr sdtpr = wmlObjectFactory.createSdtPr();
		sdtrun.setSdtPr(sdtpr);
		// Create object for citation (wrapped in JAXBElement)
		org.docx4j.wml.SdtPr.Citation sdtprcitation = wmlObjectFactory.createSdtPrCitation();
		JAXBElement<org.docx4j.wml.SdtPr.Citation> sdtprcitationWrapped = wmlObjectFactory.createSdtPrCitation(sdtprcitation);
		sdtpr.getRPrOrAliasOrLock().add(sdtprcitationWrapped);
		// Create object for sdtContent
		org.docx4j.wml.CTSdtContentRun sdtcontentrun = wmlObjectFactory.createCTSdtContentRun();
		sdtrun.setSdtContent(sdtcontentrun);
		// Create object for r
		org.docx4j.wml.R r = wmlObjectFactory.createR();
		sdtcontentrun.getContent().add(r);
		// Create object for fldChar (wrapped in JAXBElement)
		org.docx4j.wml.FldChar fldchar = wmlObjectFactory.createFldChar();
		JAXBElement<org.docx4j.wml.FldChar> fldcharWrapped = wmlObjectFactory.createRFldChar(fldchar);
		r.getContent().add(fldcharWrapped);
		fldchar.setFldCharType(org.docx4j.wml.STFldCharType.BEGIN);
		// Create object for r
		org.docx4j.wml.R r2 = wmlObjectFactory.createR();
		sdtcontentrun.getContent().add(r2);
		// Create object for instrText (wrapped in JAXBElement)
		org.docx4j.wml.Text text = wmlObjectFactory.createText();
		JAXBElement<org.docx4j.wml.Text> textWrapped = wmlObjectFactory.createRInstrText(text);
		r2.getContent().add(textWrapped);
		String citation = " CITATION ";
		if(entries.size()>0){
			BibliographyEntry entry = entries.get(0);
			citation +=entry.getTag();
			if(pageNumbers!=null && pageNumbers.length>0)
				citation +=" \\p "+pageNumbers[0];
		}
		for(int i=1;i<entries.size();i++){
			BibliographyEntry entry = entries.get(i);
			citation +=" \\m "+entry.getTag();
			if(pageNumbers!=null && pageNumbers.length>i)
				citation +=" \\p "+pageNumbers[i];
		}
		citation +=" \\l 1033 ";
		text.setValue(citation);
		text.setSpace("preserve");
		// Create object for r
		org.docx4j.wml.R r3 = wmlObjectFactory.createR();
		sdtcontentrun.getContent().add(r3);
		// Create object for fldChar (wrapped in JAXBElement)
		org.docx4j.wml.FldChar fldchar2 = wmlObjectFactory.createFldChar();
		JAXBElement<org.docx4j.wml.FldChar> fldcharWrapped2 = wmlObjectFactory.createRFldChar(fldchar2);
		r3.getContent().add(fldcharWrapped2);
		fldchar2.setFldCharType(org.docx4j.wml.STFldCharType.SEPARATE);
		// Create object for r
		org.docx4j.wml.R r4 = wmlObjectFactory.createR();
		sdtcontentrun.getContent().add(r4);
		// Create object for t (wrapped in JAXBElement)
		org.docx4j.wml.Text text2 = wmlObjectFactory.createText();
		text2.setSpace("preserve");
		JAXBElement<org.docx4j.wml.Text> textWrapped2 = wmlObjectFactory.createRT(text2);
		r4.getContent().add(textWrapped2);
		String citationText = " (";
		for(int i=0;i<entries.size();i++){
			if(i>0)
				citationText += "; ";
			BibliographyEntry entry = entries.get(i);
			String title = entry.getAuthorsStr();
			if(title==null){
				title = entry.getSubtitle()!=null ? entry.getSubtitle() : entry.getTitle();
			}
			citationText += title;
			if(entry.getYear()!=null)
				citationText += ", "+entry.getYear();
			if(pageNumbers !=null && pageNumbers.length>i)
				citationText += ", p. "+pageNumbers[i];
		}
		citationText += ") ";
		text2.setValue(citationText);
		// Create object for rPr
		// Set background color
		org.docx4j.wml.RPr rpr2 = wmlObjectFactory.createRPr(); 
		r4.setRPr(rpr2);
		// Create object for shd
		org.docx4j.wml.CTShd shd = wmlObjectFactory.createCTShd(); 
		rpr2.setShd(shd); 
        shd.setFill( "D9D9D9"); 
        shd.setColor( "auto"); 
        shd.setVal(org.docx4j.wml.STShd.CLEAR);
        shd.setThemeFillShade("D9"); 
        shd.setThemeFill(org.docx4j.wml.STThemeColor.BACKGROUND_1);
		// Create object for noProof
		org.docx4j.wml.BooleanDefaultTrue booleandefaulttrue = wmlObjectFactory.createBooleanDefaultTrue();
		rpr2.setNoProof(booleandefaulttrue);
		// Create object for r
		org.docx4j.wml.R r5 = wmlObjectFactory.createR();
		sdtcontentrun.getContent().add(r5);
		// Create object for fldChar (wrapped in JAXBElement)
		org.docx4j.wml.FldChar fldchar3 = wmlObjectFactory.createFldChar();
		JAXBElement<org.docx4j.wml.FldChar> fldcharWrapped3 = wmlObjectFactory.createRFldChar(fldchar3);
		r5.getContent().add(fldcharWrapped3);
		fldchar3.setFldCharType(org.docx4j.wml.STFldCharType.END);
		return paragraph;
	}

	private ArrayList<org.docx4j.wml.Comments.Comment> createComment(fidusWriter.model.document.Comment commentObj) {
		if (commentObj == null) {
			return null;
		}
		ArrayList<org.docx4j.wml.Comments.Comment> comments = new ArrayList<org.docx4j.wml.Comments.Comment>();
		// Add the comment itself
		java.math.BigInteger commentId = new BigInteger(commentObj.getId().toString());
		String author = commentObj.getUserName();
		String user = ""+commentObj.getUser().intValue();
		Date date = new Date(commentObj.getDate());
		String message = commentObj.getComment();
		org.docx4j.wml.Comments.Comment comment = this.createSingleComment(commentId, author, user, date,
				message);
		comments.add(comment);
		// Add its answers also
		if (commentObj.getAnswers() != null && commentObj.getAnswers().getAnswers() != null) {
			ArrayList<CommentAnswer> answers = commentObj.getAnswers().getAnswers();
			for (int i = 0; i < answers.size(); i++) {
				CommentAnswer answerObj = answers.get(i);
				commentId = new BigInteger(answerObj.getId().toString());
				author = answerObj.getUserName();
				user = ""+ answerObj.getUser().intValue();
				date = new Date(answerObj.getDate());
				message = answerObj.getAnswer();
				comment = this.createSingleComment(commentId, author, user, date, message);
				comments.add(comment);
			}
		}
		//
		return comments;
	}

	private org.docx4j.wml.Comments.Comment createSingleComment(java.math.BigInteger commentId, String author,
			String user, Date date, String message) {
		org.docx4j.wml.Comments.Comment comment = this.wmlObjectFactory.createCommentsComment();
		comment.setId(commentId);
		if (author != null) {
			comment.setAuthor(author);
		}
		if (user != null) {
			comment.setInitials(user);
		}
		if (date != null) {
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(date);
			c.setTimeZone(TimeZone.getTimeZone("GMT"));
			XMLGregorianCalendar xgc;
			try {
				xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
				comment.setDate(xgc);
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}
		}
		org.docx4j.wml.P commentP = this.wmlObjectFactory.createP();
		comment.getContent().add(commentP);
		// Create object for pPr
		org.docx4j.wml.PPr ppr = wmlObjectFactory.createPPr();
		commentP.setPPr(ppr);
		// Create object for pStyle
		org.docx4j.wml.PPrBase.PStyle pprbasepstyle = wmlObjectFactory.createPPrBasePStyle();
		ppr.setPStyle(pprbasepstyle);
		pprbasepstyle.setVal("CommentText");
		// Create object for anotationRun
		org.docx4j.wml.R anotationRun = wmlObjectFactory.createR();
		commentP.getContent().add(anotationRun);
		// Create object for annotationRef (wrapped in JAXBElement)
		org.docx4j.wml.R.AnnotationRef rannotationref = wmlObjectFactory.createRAnnotationRef();
		JAXBElement<org.docx4j.wml.R.AnnotationRef> rannotationrefWrapped = wmlObjectFactory
				.createRAnnotationRef(rannotationref);
		anotationRun.getContent().add(rannotationrefWrapped);
		// Create object for rPr
		org.docx4j.wml.RPr rpr = wmlObjectFactory.createRPr();
		anotationRun.setRPr(rpr);
		// Create object for rStyle
		org.docx4j.wml.RStyle rstyle = wmlObjectFactory.createRStyle();
		rpr.setRStyle(rstyle);
		rstyle.setVal("CommentReference");
		// Create object to wrapping the text
		org.docx4j.wml.R commentR = this.wmlObjectFactory.createR();
		commentP.getContent().add(commentR);
		org.docx4j.wml.Text commentText = this.wmlObjectFactory.createText();
		commentR.getContent().add(commentText);
		commentText.setValue(message);
		this.comments.getComment().add(comment);
		return comment;
	}

	private void startComment(org.docx4j.wml.P paragraph, org.docx4j.wml.Comments.Comment comment) {
		// Create object for commentRangeStart
		CommentRangeStart commentrangestart = this.wmlObjectFactory.createCommentRangeStart();
		commentrangestart.setId(comment.getId()); // substitute your comment id
		// The actual content, in the middle
		paragraph.getContent().add(commentrangestart);
	}

	private void endComment(org.docx4j.wml.P paragraph, org.docx4j.wml.Comments.Comment comment) {
		// Create object for commentRangeEnd
		CommentRangeEnd commentrangeend = this.wmlObjectFactory.createCommentRangeEnd();
		commentrangeend.setId(comment.getId()); // substitute your comment id
		paragraph.getContent().add(commentrangeend);
		paragraph.getContent().add(this.createRunCommentReference(comment.getId()));
	}

	private org.docx4j.wml.R createRunCommentReference(java.math.BigInteger commentId) {
		org.docx4j.wml.R run = this.wmlObjectFactory.createR();
		org.docx4j.wml.RPr rpr = wmlObjectFactory.createRPr();
		// Create object for rStyle
		org.docx4j.wml.RStyle rstyle = wmlObjectFactory.createRStyle();
		rpr.setRStyle(rstyle);
		rstyle.setVal("CommentReference");
		run.getContent().add(rpr);
		org.docx4j.wml.R.CommentReference commentRef = this.wmlObjectFactory.createRCommentReference();
		run.getContent().add(commentRef);
		commentRef.setId(commentId);
		return run;
	}

	private void addCodeBlock(Child child, org.docx4j.wml.P paragraph) {
		if (child.getType() == ChildrenTypes.textnode) {
			TextNode textNode = (TextNode) child;
			String code = textNode.getT();
			String lines[] = code.split("\\r?\\n");
			if(lines.length>0){
				this.addALineOfCode(lines[0], paragraph, false);
				for (int i=1; i<lines.length; i++) {
					this.addALineOfCode(lines[i], paragraph, true);
				}
			}
		}
	}

	private void addALineOfCode(String code, org.docx4j.wml.P paragraph, boolean makeNewParagraph) {
		org.docx4j.wml.P parentP = paragraph;
		if(makeNewParagraph || paragraph == null || paragraph.getPPr()==null || paragraph.getPPr().getNumPr()==null)
			paragraph = this.createAParagraph(null);
		org.docx4j.wml.R run = this.createARun(paragraph, null);
		// Create object for t (wrapped in JAXBElement)
		org.docx4j.wml.Text text = wmlObjectFactory.createText();
		JAXBElement<org.docx4j.wml.Text> textWrapped = wmlObjectFactory.createRT(text);
		run.getContent().add(textWrapped);
		text.setValue(code);
		text.setSpace("preserve");
		// Create object for pPr
		org.docx4j.wml.PPr ppr = null;
		ppr = this.getOrCreatePPr(paragraph);
		if(makeNewParagraph == true && parentP != null && parentP.getPPr()!=null && parentP.getPPr().getNumPr()!=null)
		{	// make new numid based on parentP
			this.setListParagraph(paragraph, parentP);			
		}
		// Create object for pBdr
		org.docx4j.wml.PPrBase.PBdr pprbasepbdr = wmlObjectFactory.createPPrBasePBdr();
		ppr.setPBdr(pprbasepbdr);
		// Create object for top
		org.docx4j.wml.CTBorder border = wmlObjectFactory.createCTBorder();
		pprbasepbdr.setTop(border);
		border.setVal(org.docx4j.wml.STBorder.SINGLE);
		border.setSz(BigInteger.valueOf(4));
		border.setColor("auto");
		border.setSpace(BigInteger.valueOf(1));
		// Create object for bottom
		org.docx4j.wml.CTBorder border2 = wmlObjectFactory.createCTBorder();
		pprbasepbdr.setBottom(border2);
		border2.setVal(org.docx4j.wml.STBorder.SINGLE);
		border2.setSz(BigInteger.valueOf(4));
		border2.setColor("auto");
		border2.setSpace(BigInteger.valueOf(1));
		// Create object for left
		org.docx4j.wml.CTBorder border3 = wmlObjectFactory.createCTBorder();
		pprbasepbdr.setLeft(border3);
		border3.setVal(org.docx4j.wml.STBorder.SINGLE);
		border3.setSz(BigInteger.valueOf(4));
		border3.setColor("auto");
		border3.setSpace(BigInteger.valueOf(4));
		// Create object for right
		org.docx4j.wml.CTBorder border4 = wmlObjectFactory.createCTBorder();
		pprbasepbdr.setRight(border4);
		border4.setVal(org.docx4j.wml.STBorder.SINGLE);
		border4.setSz(BigInteger.valueOf(4));
		border4.setColor("auto");
		border4.setSpace(BigInteger.valueOf(4));
		// Create object for pStyle
		org.docx4j.wml.PPrBase.PStyle pprbasepstyle = wmlObjectFactory.createPPrBasePStyle();
		ppr.setPStyle(pprbasepstyle);
		pprbasepstyle.setVal("FWCode");
	}

	private org.docx4j.wml.P addEquationDiv(org.docx4j.wml.P paragraph, String latexMath) {
		paragraph = this.createAParagraph(paragraph);
		// Create object for pPr
		org.docx4j.wml.PPr ppr = this.getOrCreatePPr(paragraph);
		// Create object for pStyle
		org.docx4j.wml.PPrBase.PStyle pprbasepstyle = this.wmlObjectFactory.createPPrBasePStyle();
		ppr.setPStyle(pprbasepstyle);
		pprbasepstyle.setVal("FWEquation");
		// Create object for oMathPara (wrapped in JAXBElement)
		org.docx4j.math.CTOMathPara omathpara = mathObjectFactory.createCTOMathPara();
		JAXBElement<org.docx4j.math.CTOMathPara> omathparaWrapped = mathObjectFactory.createOMathPara(omathpara);
		paragraph.getContent().add(omathparaWrapped);
		// Create object for oMath
		org.docx4j.math.CTOMath omath = null;
		try {
			String omathXML = this.equationConvertor.latex2omml(latexMath, false);
			omath = (org.docx4j.math.CTOMath) ((javax.xml.bind.JAXBElement<?>) XmlUtils.unmarshalString(omathXML))
					.getValue();
			omathpara.getOMath().add(omath);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return paragraph;
	}

	private org.docx4j.wml.P addEquationInline(org.docx4j.wml.P paragraph, String latexMath) {
		paragraph = this.createAParagraph(paragraph);
		// Create object for oMath
		org.docx4j.math.CTOMath omath = null;
		try {
			String omathXML = this.equationConvertor.latex2omml(latexMath, true);
			omath = (org.docx4j.math.CTOMath) ((javax.xml.bind.JAXBElement<?>) XmlUtils.unmarshalString(omathXML))
					.getValue();
			JAXBElement<org.docx4j.math.CTOMath> omathWrapped = mathObjectFactory.createOMath(omath);
			paragraph.getContent().add(omathWrapped);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return paragraph;
	}

	private org.docx4j.wml.P createAnEmptyParagraph() {
		org.docx4j.wml.P p = this.createAParagraph(null);
		return p;
	}

	public String capitalizeFirstLetter(String original) {
		if (original == null || original.length() == 0) {
			return original;
		} else if (original.length() == 1)
			return original.toUpperCase();
		else
			return original.substring(0, 1).toUpperCase() + original.substring(1);
	}

	public org.docx4j.wml.P setCaptionParagraph(org.docx4j.wml.P paragraph, FigureCategory category, Integer counter) {
		String cat = this.capitalizeFirstLetter(category.name());
		paragraph = this.createAParagraph(paragraph);
		// Create object for pPr
		org.docx4j.wml.PPr ppr = this.getOrCreatePPr(paragraph);
		// Create object for pStyle
		org.docx4j.wml.PPrBase.PStyle pprbasepstyle = this.wmlObjectFactory.createPPrBasePStyle();
		ppr.setPStyle(pprbasepstyle);
		pprbasepstyle.setVal("FWCaption");
		if (category != FigureCategory.none) {
			// Create object for r
			org.docx4j.wml.R run = this.createARun(paragraph, null);
			// Create object for t (wrapped in JAXBElement)
			org.docx4j.wml.Text text = this.wmlObjectFactory.createText();
			JAXBElement<org.docx4j.wml.Text> textWrapped = this.wmlObjectFactory.createRT(text);
			run.getContent().add(textWrapped);
			text.setValue(cat + " ");
			text.setSpace("preserve");
			// Create object for r
			run = this.createARun(paragraph, null);
			// Create object for fldChar (wrapped in JAXBElement)
			org.docx4j.wml.FldChar fldchar = this.wmlObjectFactory.createFldChar();
			JAXBElement<org.docx4j.wml.FldChar> fldcharWrapped = this.wmlObjectFactory.createRFldChar(fldchar);
			run.getContent().add(fldcharWrapped);
			fldchar.setFldCharType(org.docx4j.wml.STFldCharType.BEGIN);
			// Create object for r
			run = this.createARun(paragraph, null);
			// Create object for instrText (wrapped in JAXBElement)
			text = this.wmlObjectFactory.createText();
			textWrapped = this.wmlObjectFactory.createRInstrText(text);
			run.getContent().add(textWrapped);
			text.setValue(" SEQ " + cat + " \\* ARABIC ");
			text.setSpace("preserve");
			// Create object for r
			run = this.createARun(paragraph, null);
			// Create object for fldChar (wrapped in JAXBElement)
			fldchar = this.wmlObjectFactory.createFldChar();
			fldcharWrapped = this.wmlObjectFactory.createRFldChar(fldchar);
			run.getContent().add(fldcharWrapped);
			fldchar.setFldCharType(org.docx4j.wml.STFldCharType.SEPARATE);
			// Create object for r
			run = this.createARun(paragraph, null);
			// Create object for t (wrapped in JAXBElement)
			text = this.wmlObjectFactory.createText();
			textWrapped = this.wmlObjectFactory.createRT(text);
			run.getContent().add(textWrapped);
			text.setValue(counter.intValue() + "");
			// Create object for rPr
			org.docx4j.wml.RPr rpr = this.wmlObjectFactory.createRPr();
			run.setRPr(rpr);
			// Create object for noProof
			rpr.setNoProof(this.bool(true));
			// Create object for r
			run = this.createARun(paragraph, null);
			// Create object for fldChar (wrapped in JAXBElement)
			fldchar = this.wmlObjectFactory.createFldChar();
			fldcharWrapped = this.wmlObjectFactory.createRFldChar(fldchar);
			run.getContent().add(fldcharWrapped);
			fldchar.setFldCharType(org.docx4j.wml.STFldCharType.END);
			// Create object for rPr
			rpr = this.wmlObjectFactory.createRPr();
			run.setRPr(rpr);
			// Create object for noProof
			rpr.setNoProof(this.bool(true));
			// Create object for r
			run = this.createARun(paragraph, null);
			// Create object for t (wrapped in JAXBElement)
			text = this.wmlObjectFactory.createText();
			textWrapped = this.wmlObjectFactory.createRT(text);
			run.getContent().add(textWrapped);
			text.setValue(": ");
			text.setSpace("preserve");
		}
		return paragraph;
	}

	private org.docx4j.wml.P setListParagraph(org.docx4j.wml.P paragraph, BigInteger listId) {
		paragraph = this.createAParagraph(paragraph);
		// Create object for pPr
		org.docx4j.wml.PPr ppr = this.getOrCreatePPr(paragraph);
		// Create object for numPr
		org.docx4j.wml.PPrBase.NumPr pprbasenumpr = this.wmlObjectFactory.createPPrBaseNumPr();
		ppr.setNumPr(pprbasenumpr);
		// Create object for numId
		org.docx4j.wml.PPrBase.NumPr.NumId pprbasenumprnumid = this.wmlObjectFactory.createPPrBaseNumPrNumId();
		pprbasenumpr.setNumId(pprbasenumprnumid);
		pprbasenumprnumid.setVal(listId);
		// Create object for ilvl
		org.docx4j.wml.PPrBase.NumPr.Ilvl pprbasenumprilvl = this.wmlObjectFactory.createPPrBaseNumPrIlvl();
		pprbasenumpr.setIlvl(pprbasenumprilvl);
		pprbasenumprilvl.setVal(BigInteger.valueOf(0));
		// Create object for pStyle
		org.docx4j.wml.PPrBase.PStyle pprbasepstyle = this.wmlObjectFactory.createPPrBasePStyle();
		ppr.setPStyle(pprbasepstyle);
		pprbasepstyle.setVal("ListParagraph");
		return paragraph;
	}
	private org.docx4j.wml.P setListParagraph(org.docx4j.wml.P paragraph, org.docx4j.wml.P parentParagraph) {
		paragraph = this.createAParagraph(paragraph);
		// Create object for pPr
		org.docx4j.wml.PPr ppr = this.getOrCreatePPr(paragraph);
		// Create object for numPr
		org.docx4j.wml.PPrBase.NumPr pprbasenumpr = this.wmlObjectFactory.createPPrBaseNumPr();
		ppr.setNumPr(pprbasenumpr);
		// Create object for numId
		org.docx4j.wml.PPrBase.NumPr.NumId pprbasenumprnumid = this.wmlObjectFactory.createPPrBaseNumPrNumId();
		pprbasenumpr.setNumId(pprbasenumprnumid);
		if(parentParagraph != null 
			&& parentParagraph.getPPr() != null
			&& parentParagraph.getPPr().getNumPr() != null
			&& parentParagraph.getPPr().getNumPr().getNumId()!=null
			&& parentParagraph.getPPr().getNumPr().getNumId().getVal() != null){
			pprbasenumprnumid.setVal(parentParagraph.getPPr().getNumPr().getNumId().getVal());
		} else{
			pprbasenumprnumid.setVal(BigInteger.valueOf(1));
		}
		// Create object for ilvl
		org.docx4j.wml.PPrBase.NumPr.Ilvl pprbasenumprilvl = this.wmlObjectFactory.createPPrBaseNumPrIlvl();
		pprbasenumpr.setIlvl(pprbasenumprilvl);
		if(parentParagraph != null 
			&& parentParagraph.getPPr() != null
			&& parentParagraph.getPPr().getNumPr() != null
			&& parentParagraph.getPPr().getNumPr().getIlvl()!=null
			&& parentParagraph.getPPr().getNumPr().getIlvl().getVal() != null){
			pprbasenumprilvl.setVal(parentParagraph.getPPr().getNumPr().getIlvl().getVal());
		} else{
			pprbasenumprilvl.setVal(BigInteger.valueOf(0));
		}
		// Create object for pStyle
		org.docx4j.wml.PPrBase.PStyle pprbasepstyle = this.wmlObjectFactory.createPPrBasePStyle();
		ppr.setPStyle(pprbasepstyle);
		pprbasepstyle.setVal("ListParagraph");
		return paragraph;
	}
	private org.docx4j.wml.P setQuoteParagraph(org.docx4j.wml.P paragraph) {
		paragraph = this.createAParagraph(paragraph);
		org.docx4j.wml.PPr ppr = this.getOrCreatePPr(paragraph);
		// Create object for pStyle
		org.docx4j.wml.PPrBase.PStyle pprbasepstyle = this.wmlObjectFactory.createPPrBasePStyle();
		ppr.setPStyle(pprbasepstyle);
		pprbasepstyle.setVal("FWQuote");
		return paragraph;
	}

	private org.docx4j.wml.P addBreakLineRun(org.docx4j.wml.P paragraph, org.docx4j.wml.R run) {
		paragraph = this.createAParagraph(paragraph);
		run = this.createARun(paragraph, run);
		org.docx4j.wml.Br br = this.wmlObjectFactory.createBr();
		run.getContent().add(br);
		return paragraph;
	}

	/**
	 * 
	 * @param level
	 *            has to be between 0 to 8
	 * @param indent
	 *            has to be a number like 0, 360, 720, 1080, ....
	 * @return
	 */
	private org.docx4j.wml.Lvl createNumberingLevel(int level, long indent) {
		if (level < 0)
			level = 0;
		if (level > 8)
			level = 8;
		indent = indent > 0 ? (indent / FidusToDocx.getIndentstep()) * FidusToDocx.getIndentstep()
				: ((-indent) / FidusToDocx.getIndentstep()) * FidusToDocx.getIndentstep();
		org.docx4j.wml.Lvl lvl = this.wmlObjectFactory.createLvl();
		// Create object for numFmt
		org.docx4j.wml.NumFmt numfmt = this.wmlObjectFactory.createNumFmt();
		lvl.setNumFmt(numfmt);
		numfmt.setVal(org.docx4j.wml.NumberFormat.DECIMAL);
		// Create object for lvlText
		org.docx4j.wml.Lvl.LvlText lvllvltext = this.wmlObjectFactory.createLvlLvlText();
		lvl.setLvlText(lvllvltext);
		lvllvltext.setVal("%" + (level + 1) + ".");
		// Create object for lvlJc
		org.docx4j.wml.Jc jc = this.wmlObjectFactory.createJc();
		lvl.setLvlJc(jc);
		jc.setVal(org.docx4j.wml.JcEnumeration.LEFT);
		// Create object for pPr
		org.docx4j.wml.PPr ppr = this.wmlObjectFactory.createPPr();
		lvl.setPPr(ppr);
		// Create object for ind
		org.docx4j.wml.PPrBase.Ind pprbaseind = this.wmlObjectFactory.createPPrBaseInd();
		ppr.setInd(pprbaseind);
		pprbaseind.setLeft(BigInteger.valueOf(indent + (level + 1) * FidusToDocx.getIndentstep()));
		pprbaseind.setHanging(BigInteger.valueOf(FidusToDocx.getIndentstep()));
		lvl.setIlvl(BigInteger.valueOf(level));
		// Create object for rPr
		org.docx4j.wml.RPr rpr = this.wmlObjectFactory.createRPr();
		lvl.setRPr(rpr);
		// Create object for rFonts
		org.docx4j.wml.RFonts rfonts = this.wmlObjectFactory.createRFonts();
		rpr.setRFonts(rfonts);
		rfonts.setHint(org.docx4j.wml.STHint.DEFAULT);
		// Create object for start
		org.docx4j.wml.Lvl.Start lvlstart = this.wmlObjectFactory.createLvlStart();
		lvl.setStart(lvlstart);
		lvlstart.setVal(BigInteger.valueOf(1));
		return lvl;
	}

	private org.docx4j.wml.Lvl createBulletLevel(int level, long indent) {
		if (level < 0)
			level = 0;
		if (level > 8)
			level = 8;
		indent = indent > 0 ? (indent / FidusToDocx.getIndentstep()) * FidusToDocx.getIndentstep()
				: ((-indent) / FidusToDocx.getIndentstep()) * FidusToDocx.getIndentstep();
		//
		org.docx4j.wml.Lvl lvl = this.wmlObjectFactory.createLvl();
		// Create object for numFmt
		org.docx4j.wml.NumFmt numfmt = this.wmlObjectFactory.createNumFmt();
		lvl.setNumFmt(numfmt);
		numfmt.setVal(org.docx4j.wml.NumberFormat.BULLET);
		// Create object for lvlText
		org.docx4j.wml.Lvl.LvlText lvllvltext = this.wmlObjectFactory.createLvlLvlText();
		lvl.setLvlText(lvllvltext);
		lvllvltext.setVal("");
		// Create object for lvlJc
		org.docx4j.wml.Jc jc = this.wmlObjectFactory.createJc();
		lvl.setLvlJc(jc);
		jc.setVal(org.docx4j.wml.JcEnumeration.LEFT);
		// Create object for pPr
		org.docx4j.wml.PPr ppr = this.wmlObjectFactory.createPPr();
		lvl.setPPr(ppr);
		// Create object for ind
		org.docx4j.wml.PPrBase.Ind pprbaseind = this.wmlObjectFactory.createPPrBaseInd();
		ppr.setInd(pprbaseind);
		pprbaseind.setLeft(BigInteger.valueOf(indent + (level + 1) * FidusToDocx.getIndentstep()));
		pprbaseind.setHanging(BigInteger.valueOf(360));
		lvl.setIlvl(BigInteger.valueOf(level));
		// Create object for rPr
		org.docx4j.wml.RPr rpr = this.wmlObjectFactory.createRPr();
		lvl.setRPr(rpr);
		// Create object for rFonts
		org.docx4j.wml.RFonts rfonts = this.wmlObjectFactory.createRFonts();
		rpr.setRFonts(rfonts);
		rfonts.setHint(org.docx4j.wml.STHint.DEFAULT);
		rfonts.setHAnsi("Wingdings");
		rfonts.setAscii("Wingdings");
		// Create object for start
		org.docx4j.wml.Lvl.Start lvlstart = this.wmlObjectFactory.createLvlStart();
		lvl.setStart(lvlstart);
		lvlstart.setVal(BigInteger.valueOf(1));
		//
		return lvl;
	}

	private org.docx4j.wml.Numbering.AbstractNum createAbstractList(BigInteger abstractNumId, long indent,
			NumberingType type) {
		org.docx4j.wml.Numbering.AbstractNum abstractNum = this.wmlObjectFactory.createNumberingAbstractNum();
		abstractNum.setAbstractNumId(abstractNumId);
		// Create object for lvls[0..8]
		org.docx4j.wml.Lvl lvl = null;
		for (int i = 0; i < 9; i++) {
			if (type == NumberingType.ol) {
				lvl = this.createNumberingLevel(i, indent);
			} else {
				lvl = this.createBulletLevel(i, indent);
			}
			abstractNum.getLvl().add(lvl);
		}
		return abstractNum;
	}

	private BigInteger addNewNumberingList(long indent, NumberingType type) {
		org.docx4j.wml.Numbering numbering = this.getNumbering();
		BigInteger numId = BigInteger.valueOf(0);
		BigInteger abstractNumId = BigInteger.valueOf(-1);
		int size = numbering.getNum().size();
		org.docx4j.wml.Numbering.Num currentNums = null;
		for (int i = 0; i < size; i++) {
			currentNums = numbering.getNum().get(i);
			if (currentNums.getNumId().compareTo(numId) > 0)
				numId = currentNums.getNumId();
			if (currentNums.getAbstractNumId().getVal().compareTo(abstractNumId) > 0)
				abstractNumId = currentNums.getAbstractNumId().getVal();
		}
		// Add 1 to be able to use for new id.
		numId = numId.add(BigInteger.valueOf(1));
		abstractNumId = abstractNumId.add(BigInteger.valueOf(1));
		// Create object for abstractNum
		org.docx4j.wml.Numbering.Num numberingnum = this.wmlObjectFactory.createNumberingNum();
		numbering.getNum().add(numberingnum);
		numberingnum.setNumId(numId);
		// Create object for abstractNumId
		org.docx4j.wml.Numbering.Num.AbstractNumId numberingnumabstractnumid = this.wmlObjectFactory
				.createNumberingNumAbstractNumId();
		numberingnum.setAbstractNumId(numberingnumabstractnumid);
		numberingnumabstractnumid.setVal(abstractNumId);
		// Create object for abstractNum
		org.docx4j.wml.Numbering.AbstractNum abstractNum = this.createAbstractList(abstractNumId, indent, type);
		numbering.getAbstractNum().add(abstractNum);
		return numId;
	}

	private boolean saveDocx(org.docx4j.openpackaging.packages.WordprocessingMLPackage wordMLPackage, String path) {
		return this.saveDocx(wordMLPackage, path, false);
	}

	private boolean saveDocx(org.docx4j.openpackaging.packages.WordprocessingMLPackage wordMLPackage, String path,
			boolean printXML) {
		if (wordMLPackage == null)
			return false;
		try {
			if (printXML == true)
				System.out.println(
						XmlUtils.marshaltoString(wordMLPackage.getMainDocumentPart().getContents(), true, true));
			wordMLPackage.save(new java.io.File(path));
			return true;
		} catch (Docx4JException e) {
			System.out.println("Error in saving docx file.");
			e.printStackTrace();
		}
		return false;
	}

	public org.docx4j.wml.SdtBlock addSdtBlock(MetaData metaData, org.docx4j.docProps.core.CoreProperties coreProps,
			org.docx4j.docProps.coverPageProps.CoverPageProperties coverPagePros) {

		org.docx4j.wml.SdtBlock sdtblock = this.wmlObjectFactory.createSdtBlock();
		// Create object for sdtContent
		org.docx4j.wml.SdtContentBlock sdtcontentblock = this.wmlObjectFactory.createSdtContentBlock();
		sdtblock.setSdtContent(sdtcontentblock);

		// Create object for sdtPr
		org.docx4j.wml.SdtPr sdtpr6 = this.wmlObjectFactory.createSdtPr();
		sdtblock.setSdtPr(sdtpr6);
		// Create object for docPartObj (wrapped in JAXBElement)
		org.docx4j.wml.CTSdtDocPart sdtdocpart = this.wmlObjectFactory.createCTSdtDocPart();
		JAXBElement<org.docx4j.wml.CTSdtDocPart> sdtdocpartWrapped = this.wmlObjectFactory
				.createSdtPrDocPartObj(sdtdocpart);
		sdtpr6.getRPrOrAliasOrLock().add(sdtdocpartWrapped);
		// Create object for docPartGallery
		org.docx4j.wml.CTSdtDocPart.DocPartGallery sdtdocpartdocpartgallery = this.wmlObjectFactory
				.createCTSdtDocPartDocPartGallery();
		sdtdocpart.setDocPartGallery(sdtdocpartdocpartgallery);
		sdtdocpartdocpartgallery.setVal("Cover Pages");
		// Create object for docPartUnique
		org.docx4j.wml.BooleanDefaultTrue booleandefaulttrue2 = this.wmlObjectFactory.createBooleanDefaultTrue();
		sdtdocpart.setDocPartUnique(booleandefaulttrue2);

		//
		org.docx4j.wml.SdtBlock sdtblockTemp = null;
		String title = this.getStringOut(metaData.getTitle(), null);
		String subTitle = this.getStringOut(metaData.getSubtitle(), null);
		String authors = this.getStringOut(metaData.getAuthors(), null);
		String abstraction = this.getStringOut(metaData.getAbstract(), "");
		String keywords = this.getStringOut(metaData.getKeywords(), null);

		if (title != null && title.length()>0) {
			coreProps.getTitle().getValue().getContent().clear();
			coreProps.getTitle().getValue().getContent().add(title);
			sdtblockTemp = this.addTitleToSdtBlock(title);
			sdtcontentblock.getContent().add(sdtblockTemp);
		}
		//
		if (subTitle != null && subTitle.length()>0) {
			coreProps.getSubject().getContent().clear();
			coreProps.getSubject().getContent().add(subTitle);
			sdtblockTemp = this.addSubTitleToSdtBlock(subTitle);
			sdtcontentblock.getContent().add(sdtblockTemp);
		}
		//
		if (authors != null && authors.length()>0) {
			authors = authors.replace(',', ';');
			coreProps.getCreator().getContent().clear();
			coreProps.getCreator().getContent().add(authors);
			sdtblockTemp = this.addAuthorsToSdtBlock(authors);
			sdtcontentblock.getContent().add(sdtblockTemp);
		}
		//
		if (abstraction != null && abstraction.length()>0) {
			if (coverPagePros != null) {
				coverPagePros.setAbstract(abstraction);
			}
			sdtblockTemp = this.addAbstractToSdtBlock(abstraction);
			sdtcontentblock.getContent().add(sdtblockTemp);
		}
		//
		if (keywords != null && keywords.length()>0) {
			keywords = keywords.replace(',', ';');
			coreProps.setKeywords(keywords);
			sdtblockTemp = this.addKeywordsToSdtBlock(keywords);
			sdtcontentblock.getContent().add(sdtblockTemp);
		}
		//
		//
		return sdtblock;
	}

	private String getStringOut(NodeJson nodeJson, String str) {
		if (nodeJson != null && nodeJson.getC() != null && nodeJson.getC().size() > 0) {
			for(int i=0;i<nodeJson.getC().size();i++){
				if (nodeJson.getC().get(i).getType() == ChildrenTypes.textnode) {
					TextNode tn = (TextNode) nodeJson.getC().get(i);
					if (str != null){
						String space = "";
						if(!str.endsWith(" ") && !tn.getT().startsWith(" "))
							space = " ";
						str += space+tn.getT();
					}else
						str = tn.getT();
				} else {
					if (str == null)
						str = "";
					str = this.getStringOut((NodeJson) nodeJson.getC().get(i), str);
				}
			}
			
		}
		return str;
	}

	private org.docx4j.wml.SdtBlock addTitleToSdtBlock(String title) {
		// Create object for sdt
		org.docx4j.wml.SdtBlock sdtblock = this.wmlObjectFactory.createSdtBlock();
		// Create object for sdtContent
		org.docx4j.wml.SdtContentBlock sdtcontentblock = this.wmlObjectFactory.createSdtContentBlock();
		sdtblock.setSdtContent(sdtcontentblock);
		// Create object for p
		org.docx4j.wml.P p = this.createAParagraphForSdtCB(sdtcontentblock);
		// Create object for r
		org.docx4j.wml.R r = this.wmlObjectFactory.createR();
		p.getContent().add(r);
		// Create object for t (wrapped in JAXBElement)
		org.docx4j.wml.Text text = this.wmlObjectFactory.createText();
		JAXBElement<org.docx4j.wml.Text> textWrapped = this.wmlObjectFactory.createRT(text);
		r.getContent().add(textWrapped);
		text.setValue(title);
		// Create object for rPr
		org.docx4j.wml.RPr rpr = this.wmlObjectFactory.createRPr();
		r.setRPr(rpr);
		// Create object for rStyle
		org.docx4j.wml.RStyle rstyle = this.wmlObjectFactory.createRStyle();
		rpr.setRStyle(rstyle);
		rstyle.setVal("FWTitleChar");
		// Create object for sdtPr
		org.docx4j.wml.SdtPr sdtpr = this.wmlObjectFactory.createSdtPr();
		sdtblock.setSdtPr(sdtpr);
		// Create object for rPr (wrapped in JAXBElement)
		org.docx4j.wml.RPr rpr2 = this.wmlObjectFactory.createRPr();
		JAXBElement<org.docx4j.wml.RPr> rprWrapped = this.wmlObjectFactory.createSdtPrRPr(rpr2);
		sdtpr.getRPrOrAliasOrLock().add(rprWrapped);
		// Create object for rFonts
		org.docx4j.wml.RFonts rfonts = this.wmlObjectFactory.createRFonts();
		rpr2.setRFonts(rfonts);
		rfonts.setAsciiTheme(org.docx4j.wml.STTheme.MAJOR_H_ANSI);
		rfonts.setCstheme(org.docx4j.wml.STTheme.MAJOR_BIDI);
		rfonts.setEastAsiaTheme(org.docx4j.wml.STTheme.MAJOR_EAST_ASIA);
		rfonts.setHAnsiTheme(org.docx4j.wml.STTheme.MAJOR_H_ANSI);
		// Create object for alias (wrapped in JAXBElement)
		org.docx4j.wml.SdtPr.Alias sdtpralias = this.wmlObjectFactory.createSdtPrAlias();
		JAXBElement<org.docx4j.wml.SdtPr.Alias> sdtpraliasWrapped = this.wmlObjectFactory.createSdtPrAlias(sdtpralias);
		sdtpr.getRPrOrAliasOrLock().add(sdtpraliasWrapped);
		sdtpralias.setVal("Title");
		// Create object for dataBinding (wrapped in JAXBElement)
		org.docx4j.wml.CTDataBinding databinding2 = this.wmlObjectFactory.createCTDataBinding();
		JAXBElement<org.docx4j.wml.CTDataBinding> databindingWrapped = this.wmlObjectFactory
				.createSdtPrDataBinding(databinding2);
		sdtpr.getRPrOrAliasOrLock().add(databindingWrapped);
		databinding2.setXpath("/ns0:coreProperties[1]/ns1:title[1]");
		databinding2.setPrefixMappings(FidusToDocx.sdtPrefixMapping);
		// Create object for sdtEndPr
		org.docx4j.wml.CTSdtEndPr sdtendpr = this.wmlObjectFactory.createCTSdtEndPr();
		sdtblock.setSdtEndPr(sdtendpr);
		// Create object for rPr
		org.docx4j.wml.RPr rpr3 = this.wmlObjectFactory.createRPr();
		sdtendpr.getRPr().add(rpr3);
		// Create object for sz
		org.docx4j.wml.HpsMeasure hpsmeasure = this.wmlObjectFactory.createHpsMeasure();
		rpr3.setSz(hpsmeasure);
		hpsmeasure.setVal(BigInteger.valueOf(52));
		// Create object for rStyle
		org.docx4j.wml.RStyle rstyle2 = this.wmlObjectFactory.createRStyle();
		rpr3.setRStyle(rstyle2);
		rstyle2.setVal("FWTitleChar");
		// Create object for rFonts
		org.docx4j.wml.RFonts rfonts2 = this.wmlObjectFactory.createRFonts();
		rpr3.setRFonts(rfonts2);
		rfonts2.setAscii("Tahoma");
		rfonts2.setCs("Arial");
		rfonts2.setEastAsia("Arial");
		rfonts2.setHAnsi("Tahoma");
		// Create object for b
		rpr3.setB(this.bool(true));
		// Create object for szCs
		org.docx4j.wml.HpsMeasure hpsmeasure2 = this.wmlObjectFactory.createHpsMeasure();
		rpr3.setSzCs(hpsmeasure2);
		hpsmeasure2.setVal(BigInteger.valueOf(52));
		return sdtblock;
	}

	private org.docx4j.wml.SdtBlock addSubTitleToSdtBlock(String subTitle) {
		// Create object for sdt
		org.docx4j.wml.SdtBlock sdtblock3 = this.wmlObjectFactory.createSdtBlock();
		// Create object for sdtContent
		org.docx4j.wml.SdtContentBlock sdtcontentblock3 = this.wmlObjectFactory.createSdtContentBlock();
		sdtblock3.setSdtContent(sdtcontentblock3);
		// Create object for p
		org.docx4j.wml.P p4 = this.wmlObjectFactory.createP();
		sdtcontentblock3.getContent().add(p4);
		// Create object for r
		org.docx4j.wml.R r2 = this.wmlObjectFactory.createR();
		p4.getContent().add(r2);
		// Create object for t (wrapped in JAXBElement)
		org.docx4j.wml.Text text2 = this.wmlObjectFactory.createText();
		JAXBElement<org.docx4j.wml.Text> textWrapped2 = this.wmlObjectFactory.createRT(text2);
		r2.getContent().add(textWrapped2);
		text2.setValue(subTitle);
		// Create object for rPr
		org.docx4j.wml.RPr rpr4 = this.wmlObjectFactory.createRPr();
		r2.setRPr(rpr4);
		// Create object for rStyle
		org.docx4j.wml.RStyle rstyle3 = this.wmlObjectFactory.createRStyle();
		rpr4.setRStyle(rstyle3);
		rstyle3.setVal("FWSubtitleChar");
		// Create object for pPr
		org.docx4j.wml.PPr ppr = this.wmlObjectFactory.createPPr();
		p4.setPPr(ppr);
		// Create object for ind
		org.docx4j.wml.PPrBase.Ind pprbaseind = this.wmlObjectFactory.createPPrBaseInd();
		ppr.setInd(pprbaseind);
		pprbaseind.setLeft(BigInteger.valueOf(284));
		// Create object for sdtPr
		org.docx4j.wml.SdtPr sdtpr2 = this.wmlObjectFactory.createSdtPr();
		sdtblock3.setSdtPr(sdtpr2);
		// Create object for rPr (wrapped in JAXBElement)
		org.docx4j.wml.RPr rpr5 = this.wmlObjectFactory.createRPr();
		JAXBElement<org.docx4j.wml.RPr> rprWrapped2 = this.wmlObjectFactory.createSdtPrRPr(rpr5);
		sdtpr2.getRPrOrAliasOrLock().add(rprWrapped2);
		// Create object for rStyle
		org.docx4j.wml.RStyle rstyle4 = this.wmlObjectFactory.createRStyle();
		rpr5.setRStyle(rstyle4);
		rstyle4.setVal("FWSubtitleChar");
		// Create object for alias (wrapped in JAXBElement)
		org.docx4j.wml.SdtPr.Alias sdtpralias2 = this.wmlObjectFactory.createSdtPrAlias();
		JAXBElement<org.docx4j.wml.SdtPr.Alias> sdtpraliasWrapped2 = this.wmlObjectFactory
				.createSdtPrAlias(sdtpralias2);
		sdtpr2.getRPrOrAliasOrLock().add(sdtpraliasWrapped2);
		sdtpralias2.setVal("Subtitle");
		// Create object for dataBinding (wrapped in JAXBElement)
		org.docx4j.wml.CTDataBinding databinding4 = this.wmlObjectFactory.createCTDataBinding();
		JAXBElement<org.docx4j.wml.CTDataBinding> databindingWrapped2 = this.wmlObjectFactory
				.createSdtPrDataBinding(databinding4);
		sdtpr2.getRPrOrAliasOrLock().add(databindingWrapped2);
		databinding4.setXpath("/ns0:coreProperties[1]/ns1:subject[1]");
		databinding4.setPrefixMappings(FidusToDocx.sdtPrefixMapping);
		return sdtblock3;
	}

	private org.docx4j.wml.SdtBlock addAuthorsToSdtBlock(String authors) {
		// Create object for sdt
		org.docx4j.wml.SdtBlock sdtblock4 = this.wmlObjectFactory.createSdtBlock();
		// Create object for sdtContent
		org.docx4j.wml.SdtContentBlock sdtcontentblock4 = this.wmlObjectFactory.createSdtContentBlock();
		sdtblock4.setSdtContent(sdtcontentblock4);
		// Create object for p
		org.docx4j.wml.P p6 = this.wmlObjectFactory.createP();
		sdtcontentblock4.getContent().add(p6);
		// Create object for r
		org.docx4j.wml.R r3 = this.wmlObjectFactory.createR();
		p6.getContent().add(r3);
		// Create object for t (wrapped in JAXBElement)
		org.docx4j.wml.Text text3 = this.wmlObjectFactory.createText();
		JAXBElement<org.docx4j.wml.Text> textWrapped3 = this.wmlObjectFactory.createRT(text3);
		r3.getContent().add(textWrapped3);
		text3.setValue(authors);
		// Create object for rPr
		org.docx4j.wml.RPr rpr6 = this.wmlObjectFactory.createRPr();
		r3.setRPr(rpr6);
		// Create object for rStyle
		org.docx4j.wml.RStyle rstyle5 = this.wmlObjectFactory.createRStyle();
		rpr6.setRStyle(rstyle5);
		rstyle5.setVal("FWAuthorsChar");
		// Create object for pPr
		org.docx4j.wml.PPr ppr2 = this.wmlObjectFactory.createPPr();
		p6.setPPr(ppr2);
		// Create object for ind
		org.docx4j.wml.PPrBase.Ind pprbaseind2 = this.wmlObjectFactory.createPPrBaseInd();
		ppr2.setInd(pprbaseind2);
		pprbaseind2.setLeft(BigInteger.valueOf(426));
		// Create object for sdtPr
		org.docx4j.wml.SdtPr sdtpr3 = this.wmlObjectFactory.createSdtPr();
		sdtblock4.setSdtPr(sdtpr3);
		// Create object for rPr (wrapped in JAXBElement)
		org.docx4j.wml.RPr rpr7 = this.wmlObjectFactory.createRPr();
		JAXBElement<org.docx4j.wml.RPr> rprWrapped3 = this.wmlObjectFactory.createSdtPrRPr(rpr7);
		sdtpr3.getRPrOrAliasOrLock().add(rprWrapped3);
		// Create object for rStyle
		org.docx4j.wml.RStyle rstyle6 = this.wmlObjectFactory.createRStyle();
		rpr7.setRStyle(rstyle6);
		rstyle6.setVal("FWAuthorsChar");
		// Create object for alias (wrapped in JAXBElement)
		org.docx4j.wml.SdtPr.Alias sdtpralias3 = this.wmlObjectFactory.createSdtPrAlias();
		JAXBElement<org.docx4j.wml.SdtPr.Alias> sdtpraliasWrapped3 = this.wmlObjectFactory
				.createSdtPrAlias(sdtpralias3);
		sdtpr3.getRPrOrAliasOrLock().add(sdtpraliasWrapped3);
		sdtpralias3.setVal("Author");
		// Create object for dataBinding (wrapped in JAXBElement)
		org.docx4j.wml.CTDataBinding databinding6 = this.wmlObjectFactory.createCTDataBinding();
		JAXBElement<org.docx4j.wml.CTDataBinding> databindingWrapped3 = this.wmlObjectFactory
				.createSdtPrDataBinding(databinding6);
		sdtpr3.getRPrOrAliasOrLock().add(databindingWrapped3);
		databinding6.setXpath("/ns0:coreProperties[1]/ns1:creator[1]");
		databinding6.setPrefixMappings(FidusToDocx.sdtPrefixMapping);
		return sdtblock4;
	}

	private org.docx4j.wml.SdtBlock addAbstractToSdtBlock(String abstraction) {
		org.docx4j.wml.SdtBlock sdtblock = wmlObjectFactory.createSdtBlock();
		// Create object for sdtContent
		org.docx4j.wml.SdtContentBlock sdtcontentblock = wmlObjectFactory.createSdtContentBlock();
		sdtblock.setSdtContent(sdtcontentblock);
		// Create object for p
		org.docx4j.wml.P p = wmlObjectFactory.createP();
		sdtcontentblock.getContent().add(p);
		// Create object for r
		org.docx4j.wml.R r = wmlObjectFactory.createR();
		p.getContent().add(r);
		// Create object for t (wrapped in JAXBElement)
		org.docx4j.wml.Text text = wmlObjectFactory.createText();
		JAXBElement<org.docx4j.wml.Text> textWrapped = wmlObjectFactory.createRT(text);
		r.getContent().add(textWrapped);
		text.setValue(abstraction);
		// Create object for rPr
		org.docx4j.wml.RPr rpr = wmlObjectFactory.createRPr();
		r.setRPr(rpr);
		// Create object for rStyle
		org.docx4j.wml.RStyle rstyle = wmlObjectFactory.createRStyle();
		rpr.setRStyle(rstyle);
		rstyle.setVal("FWAbstractionChar");
		// Create object for pPr
		org.docx4j.wml.PPr ppr = wmlObjectFactory.createPPr();
		p.setPPr(ppr);
		// Create object for ind
		org.docx4j.wml.PPrBase.Ind pprbaseind = wmlObjectFactory.createPPrBaseInd();
		ppr.setInd(pprbaseind);
		pprbaseind.setLeft(BigInteger.valueOf(567));
		// Create object for sdtPr
		org.docx4j.wml.SdtPr sdtpr = wmlObjectFactory.createSdtPr();
		sdtblock.setSdtPr(sdtpr);
		// Create object for rPr (wrapped in JAXBElement)
		org.docx4j.wml.RPr rpr2 = wmlObjectFactory.createRPr();
		JAXBElement<org.docx4j.wml.RPr> rprWrapped = wmlObjectFactory.createSdtPrRPr(rpr2);
		sdtpr.getRPrOrAliasOrLock().add(rprWrapped);
		// Create object for rStyle
		org.docx4j.wml.RStyle rstyle3 = wmlObjectFactory.createRStyle();
		rpr2.setRStyle(rstyle3);
		rstyle3.setVal("FWAbstractionChar");
		// Create object for alias (wrapped in JAXBElement)
		org.docx4j.wml.SdtPr.Alias sdtpralias = wmlObjectFactory.createSdtPrAlias();
		JAXBElement<org.docx4j.wml.SdtPr.Alias> sdtpraliasWrapped = wmlObjectFactory.createSdtPrAlias(sdtpralias);
		sdtpr.getRPrOrAliasOrLock().add(sdtpraliasWrapped);
		sdtpralias.setVal("Abstract");
		// Create object for dataBinding (wrapped in JAXBElement)
		org.docx4j.wml.CTDataBinding databinding2 = wmlObjectFactory.createCTDataBinding();
		JAXBElement<org.docx4j.wml.CTDataBinding> databindingWrapped = wmlObjectFactory
				.createSdtPrDataBinding(databinding2);
		sdtpr.getRPrOrAliasOrLock().add(databindingWrapped);
		databinding2.setXpath("/ns0:CoverPageProperties[1]/ns0:Abstract[1]");
		databinding2.setPrefixMappings("xmlns:ns0='http://schemas.microsoft.com/office/2006/coverPageProps'");
		return sdtblock;
	}

	private org.docx4j.wml.SdtBlock addKeywordsToSdtBlock(String keywords) {
		// Create object for sdt
		org.docx4j.wml.SdtBlock sdtblock6 = this.wmlObjectFactory.createSdtBlock();
		// Create object for sdtContent
		org.docx4j.wml.SdtContentBlock sdtcontentblock6 = this.wmlObjectFactory.createSdtContentBlock();
		sdtblock6.setSdtContent(sdtcontentblock6);
		// Create object for p
		org.docx4j.wml.P p10 = this.wmlObjectFactory.createP();
		sdtcontentblock6.getContent().add(p10);
		// Create object for r
		org.docx4j.wml.R r5 = this.wmlObjectFactory.createR();
		p10.getContent().add(r5);
		// Create object for t (wrapped in JAXBElement)
		org.docx4j.wml.Text text5 = this.wmlObjectFactory.createText();
		JAXBElement<org.docx4j.wml.Text> textWrapped5 = this.wmlObjectFactory.createRT(text5);
		r5.getContent().add(textWrapped5);
		text5.setValue(keywords);
		// Create object for rPr
		org.docx4j.wml.RPr rpr8 = this.wmlObjectFactory.createRPr();
		r5.setRPr(rpr8);
		// Create object for rStyle
		org.docx4j.wml.RStyle rstyle7 = this.wmlObjectFactory.createRStyle();
		rpr8.setRStyle(rstyle7);
		rstyle7.setVal("FWKeywordsChar");
		// Create object for pPr
		org.docx4j.wml.PPr ppr4 = this.wmlObjectFactory.createPPr();
		p10.setPPr(ppr4);
		// Create object for ind
		org.docx4j.wml.PPrBase.Ind pprbaseind4 = this.wmlObjectFactory.createPPrBaseInd();
		ppr4.setInd(pprbaseind4);
		pprbaseind4.setLeft(BigInteger.valueOf(284));
		// Create object for sdtPr
		org.docx4j.wml.SdtPr sdtpr5 = this.wmlObjectFactory.createSdtPr();
		sdtblock6.setSdtPr(sdtpr5);
		// Create object for rPr (wrapped in JAXBElement)
		org.docx4j.wml.RPr rpr9 = this.wmlObjectFactory.createRPr();
		JAXBElement<org.docx4j.wml.RPr> rprWrapped4 = this.wmlObjectFactory.createSdtPrRPr(rpr9);
		sdtpr5.getRPrOrAliasOrLock().add(rprWrapped4);
		// Create object for rStyle
		org.docx4j.wml.RStyle rstyle8 = this.wmlObjectFactory.createRStyle();
		rpr9.setRStyle(rstyle8);
		rstyle8.setVal("FWKeywordsChar");
		// Create object for alias (wrapped in JAXBElement)
		org.docx4j.wml.SdtPr.Alias sdtpralias5 = this.wmlObjectFactory.createSdtPrAlias();
		JAXBElement<org.docx4j.wml.SdtPr.Alias> sdtpraliasWrapped5 = this.wmlObjectFactory
				.createSdtPrAlias(sdtpralias5);
		sdtpr5.getRPrOrAliasOrLock().add(sdtpraliasWrapped5);
		sdtpralias5.setVal("Keywords");
		// Create object for tag
		org.docx4j.wml.Tag tag = this.wmlObjectFactory.createTag();
		sdtpr5.getRPrOrAliasOrLock().add(tag);
		tag.setVal(keywords);
		// Create object for dataBinding (wrapped in JAXBElement)
		org.docx4j.wml.CTDataBinding databinding10 = this.wmlObjectFactory.createCTDataBinding();
		JAXBElement<org.docx4j.wml.CTDataBinding> databindingWrapped5 = this.wmlObjectFactory
				.createSdtPrDataBinding(databinding10);
		sdtpr5.getRPrOrAliasOrLock().add(databindingWrapped5);
		databinding10.setXpath("/ns1:coreProperties[1]/ns1:keywords[1]");
		databinding10.setPrefixMappings(FidusToDocx.sdtPrefixMapping);
		return sdtblock6;
	}

	public void createBibliography(List<org.docx4j.bibliography.CTSourceType> bibliographyResources) {
		for (int i = 0; i < this.bibliography.getBibliographyEntries().size(); i++) {
			BibliographyEntry entry = this.bibliography.getBibliographyEntries().get(i);
			org.docx4j.bibliography.CTSourceType source = this.convertBibliographyEntryToSource(entry, i + 1);
			bibliographyResources.add(source);
		}
	}
	private void addPersonToNameList(org.docx4j.bibliography.CTNameListType cTNameListType, String str) {
		if (str == null)
			return;
		String trim = str.trim();
		if (!trim.isEmpty()) {
			org.docx4j.bibliography.CTPersonType person = this.biblioObjectFactory.createCTPersonType();
			cTNameListType.getPerson().add(person);
			String[] names = trim.split("\\s+");
			if (names.length == 3) {
				person.getFirst().add(names[0]);
				person.getMiddle().add(names[1]);
				person.getLast().add(names[2]);
			} else if (names.length == 2) {
				person.getFirst().add(names[0]);
				person.getLast().add(names[1]);
			} else if (names.length == 1) {
				person.getLast().add(names[0]);
			}
		}
	}
	private void addPersonToNameList(org.docx4j.bibliography.CTNameListType cTNameListType, ArrayList<String> names) {
		if (names == null || names.size()==0)
			return;
		
		org.docx4j.bibliography.CTPersonType person = this.biblioObjectFactory.createCTPersonType();
		cTNameListType.getPerson().add(person);
		if (names.size() >= 3) {
			person.getFirst().add(names.get(0));
			person.getMiddle().add(names.get(1));
			person.getLast().add(names.get(2));
			for(int i=3;i<names.size();i++)
				person.getLast().add(names.get(i));
		} else if (names.size() == 2) {
			person.getFirst().add(names.get(0));
			person.getLast().add(names.get(1));
		} else if (names.size() == 1) {
			person.getLast().add(names.get(0));
		}
	}
	public org.docx4j.bibliography.CTSourceType convertBibliographyEntryToSource(BibliographyEntry entry,
			Integer order) {
		org.docx4j.bibliography.CTSourceType source = this.biblioObjectFactory.createCTSourceType();
		List<JAXBElement<?>> sourceParams = source.getAbbreviatedCaseNumberOrAlbumTitleOrAuthor();
		//
		JAXBElement<String> tag = this.biblioObjectFactory.createCTSourceTypeTag(entry.getTag());
		sourceParams.add(tag);
		//
		String uuid = "{" + java.util.UUID.randomUUID().toString().toUpperCase() + "}";
		JAXBElement<String> guid = this.biblioObjectFactory.createCTSourceTypeGuid(uuid);
		sourceParams.add(guid);
		//
		JAXBElement<org.docx4j.bibliography.STSourceType> stSourceType = this.biblioObjectFactory
				.createCTSourceTypeSourceType(entry.getDocxType());
		sourceParams.add(stSourceType);
		//
		org.docx4j.bibliography.CTAuthorType cTAuthorType = this.biblioObjectFactory.createCTAuthorType();
		JAXBElement<CTAuthorType> author = this.biblioObjectFactory.createCTSourceTypeAuthor(cTAuthorType);
		sourceParams.add(author);
		//
		if (entry.getAuthor() != null) {
			ArrayList<ArrayList<String>> list = entry.getAuthors();
			org.docx4j.bibliography.CTNameOrCorporateType cTNameOrCorporateType = this.biblioObjectFactory.createCTNameOrCorporateType();
			org.docx4j.bibliography.CTNameListType cTNameListType = this.biblioObjectFactory.createCTNameListType();
			cTNameOrCorporateType.setNameList(cTNameListType);
			for(int i=0;i<list.size();i++)
				this.addPersonToNameList(cTNameListType, list.get(i));
			JAXBElement<org.docx4j.bibliography.CTNameOrCorporateType> cTAuthorTypeAuthor = this.biblioObjectFactory
					.createCTAuthorTypeAuthor(cTNameOrCorporateType);
			cTAuthorType.getArtistOrAuthorOrBookAuthor().add(cTAuthorTypeAuthor);
		}
		//
		if (entry.getBookauthor() != null) {
			org.docx4j.bibliography.CTNameType cTNameType = this.biblioObjectFactory.createCTNameType();
			org.docx4j.bibliography.CTNameListType cTNameListType = this.biblioObjectFactory.createCTNameListType();
			cTNameType.setNameList(cTNameListType);
			this.addPersonToNameList(cTNameListType, entry.getBookauthor());
			JAXBElement<org.docx4j.bibliography.CTNameType> cTAuthorTypeWriter = this.biblioObjectFactory
					.createCTAuthorTypeWriter(cTNameType);
			cTAuthorType.getArtistOrAuthorOrBookAuthor().add(cTAuthorTypeWriter);
		}
		//
		if (entry.getEditor() != null) {
			org.docx4j.bibliography.CTNameType cTNameType = this.biblioObjectFactory.createCTNameType();
			org.docx4j.bibliography.CTNameListType cTNameListType = this.biblioObjectFactory.createCTNameListType();
			cTNameType.setNameList(cTNameListType);
			this.addPersonToNameList(cTNameListType, entry.getEditor());
			this.addPersonToNameList(cTNameListType, entry.getEditora());
			this.addPersonToNameList(cTNameListType, entry.getEditorb());
			this.addPersonToNameList(cTNameListType, entry.getEditorc());
			JAXBElement<org.docx4j.bibliography.CTNameType> cTAuthorTypeEditor = this.biblioObjectFactory
					.createCTAuthorTypeEditor(cTNameType);
			cTAuthorType.getArtistOrAuthorOrBookAuthor().add(cTAuthorTypeEditor);
		}
		//
		if (entry.getAnnotator() != null || entry.getCommentator() != null) {
			org.docx4j.bibliography.CTNameType cTNameType = this.biblioObjectFactory.createCTNameType();
			org.docx4j.bibliography.CTNameListType cTNameListType = this.biblioObjectFactory.createCTNameListType();
			cTNameType.setNameList(cTNameListType);
			this.addPersonToNameList(cTNameListType, entry.getAnnotator());
			this.addPersonToNameList(cTNameListType, entry.getCommentator());
			JAXBElement<org.docx4j.bibliography.CTNameType> cTAuthorTypeCounsel = this.biblioObjectFactory
					.createCTAuthorTypeCounsel(cTNameType);
			cTAuthorType.getArtistOrAuthorOrBookAuthor().add(cTAuthorTypeCounsel);
		}
		//
		if (entry.getTranslator() != null) {
			org.docx4j.bibliography.CTNameType cTNameType = this.biblioObjectFactory.createCTNameType();
			org.docx4j.bibliography.CTNameListType cTNameListType = this.biblioObjectFactory.createCTNameListType();
			cTNameType.setNameList(cTNameListType);
			this.addPersonToNameList(cTNameListType, entry.getTranslator());
			JAXBElement<org.docx4j.bibliography.CTNameType> cTAuthorTypeTranslator = this.biblioObjectFactory
					.createCTAuthorTypeTranslator(cTNameType);
			cTAuthorType.getArtistOrAuthorOrBookAuthor().add(cTAuthorTypeTranslator);
		}
		//
		if (entry.getBookTitleStr() != null) {
			JAXBElement<String> bookTitle = this.biblioObjectFactory
					.createCTSourceTypeBookTitle(entry.getBookTitleStr());
			sourceParams.add(bookTitle);
		}
		//
		if (entry.getChapter() != null) {
			JAXBElement<String> chapterNumber = this.biblioObjectFactory
					.createCTSourceTypeChapterNumber(entry.getChapter());
			sourceParams.add(chapterNumber);
		}
		//
		if (entry.getDay() != null) {
			JAXBElement<String> day = this.biblioObjectFactory.createCTSourceTypeDay(entry.getDay());
			sourceParams.add(day);
			//
			JAXBElement<String> dayAccessed = this.biblioObjectFactory.createCTSourceTypeDayAccessed(entry.getDay());
			sourceParams.add(dayAccessed);
		}
		//
		if (entry.getMonth() != null) {
			JAXBElement<String> month = this.biblioObjectFactory.createCTSourceTypeMonth(entry.getMonth());
			sourceParams.add(month);
			//
			JAXBElement<String> monthAccessed = this.biblioObjectFactory
					.createCTSourceTypeMonthAccessed(entry.getMonth());
			sourceParams.add(monthAccessed);
		}
		//
		if (entry.getYear() != null) {
			JAXBElement<String> year = this.biblioObjectFactory.createCTSourceTypeYear(entry.getYear());
			sourceParams.add(year);
			//
			JAXBElement<String> yearAccessed = this.biblioObjectFactory.createCTSourceTypeYearAccessed(entry.getYear());
			sourceParams.add(yearAccessed);
		}
		//
		if (entry.getOrganization() != null) {
			JAXBElement<String> edition = this.biblioObjectFactory.createCTSourceTypeDepartment(entry.getOrganization());
			sourceParams.add(edition);
		}
		//
		if (entry.getEdition() != null) {
			JAXBElement<String> edition = this.biblioObjectFactory.createCTSourceTypeEdition(entry.getEditionStr());
			sourceParams.add(edition);
		}
		//
		if (entry.getHolder() != null) {
			JAXBElement<String> brodcaster = this.biblioObjectFactory.createCTSourceTypeBroadcaster(entry.getHolder());
			sourceParams.add(brodcaster);
			//
			JAXBElement<String> distributer = this.biblioObjectFactory.createCTSourceTypeDistributor(entry.getHolder());
			sourceParams.add(distributer);
			//
			JAXBElement<String> productionCompany = this.biblioObjectFactory.createCTSourceTypeProductionCompany(entry.getHolder());
			sourceParams.add(productionCompany);
		}
		//
		if (entry.getInstitution() != null) {
			JAXBElement<String> institution = this.biblioObjectFactory
					.createCTSourceTypeInstitution(entry.getInstitution());
			sourceParams.add(institution);
		}
		//
		if (entry.getIssue() != null) {
			JAXBElement<String> issue = this.biblioObjectFactory.createCTSourceTypeIssue(entry.getIssue());
			sourceParams.add(issue);
		}
		//
		if (entry.getJournalName() != null) {
			JAXBElement<String> journalName = this.biblioObjectFactory.createCTSourceTypeIssue(entry.getJournalName());
			sourceParams.add(journalName);
		}
		//
		if (entry.getHowpublished() != null) {
			JAXBElement<String> medium = this.biblioObjectFactory.createCTSourceTypeMedium(entry.getHowpublished());
			sourceParams.add(medium);
		}
		//
		if (entry.getLanguageStr() != null) {
			JAXBElement<String> language = this.biblioObjectFactory.createCTSourceTypeLCID(entry.getLanguageStr());
			sourceParams.add(language);
		}
		//
		if (entry.getLocation() != null) {
			JAXBElement<String> city = this.biblioObjectFactory.createCTSourceTypeCity(entry.getLocation());
			sourceParams.add(city);
		}
		//
		if (entry.getPages() != null) {
			JAXBElement<String> pages = this.biblioObjectFactory.createCTSourceTypePages(entry.getPages());
			sourceParams.add(pages);
		}
		//
		if (entry.getPublisher() != null) {
			JAXBElement<String> publisher = this.biblioObjectFactory.createCTSourceTypePublisher(entry.getPublisher());
			sourceParams.add(publisher);
		}
		//
		if (entry.getSubtitleStr() != null) {
			JAXBElement<String> shortTitle = this.biblioObjectFactory
					.createCTSourceTypeShortTitle(entry.getSubtitleStr());
			sourceParams.add(shortTitle);
		}
		//
		if (entry.getTitleStr() != null) {
			JAXBElement<String> title = this.biblioObjectFactory.createCTSourceTypeTitle(entry.getTitleStr());
			sourceParams.add(title);
		}
		//
		if (entry.getType() != null) {
			JAXBElement<String> type = this.biblioObjectFactory.createCTSourceTypeType(entry.getType());
			sourceParams.add(type);
			//
			JAXBElement<String> thesisType = this.biblioObjectFactory.createCTSourceTypeThesisType(entry.getType());
			sourceParams.add(thesisType);
		}
		//
		if (entry.getVenue() != null) {
			JAXBElement<String> venue = this.biblioObjectFactory.createCTSourceTypeStation(entry.getVenue());
			sourceParams.add(venue);
			//
			JAXBElement<String> theater = this.biblioObjectFactory.createCTSourceTypeTheater(entry.getVenue());
			sourceParams.add(theater);
		}
		//
		if (entry.getUrl() != null) {
			JAXBElement<String> url = this.biblioObjectFactory.createCTSourceTypeURL(entry.getUrl());
			sourceParams.add(url);
		}
		//
		if (entry.getIsbn() != null) {
			JAXBElement<String> isbn = this.biblioObjectFactory.createCTSourceTypeStandardNumber(entry.getIsbn());
			sourceParams.add(isbn);
		}
		//
		if (entry.getNumber() != null) {
			JAXBElement<String> caseNumber = this.biblioObjectFactory.createCTSourceTypeCaseNumber(entry.getNumber());
			sourceParams.add(caseNumber);
			//
			JAXBElement<String> patentNumber = this.biblioObjectFactory.createCTSourceTypePatentNumber(entry.getNumber());
			sourceParams.add(patentNumber);
			//
			JAXBElement<String> recordingNumber = this.biblioObjectFactory.createCTSourceTypeRecordingNumber(entry.getNumber());
			sourceParams.add(recordingNumber);
		}
		//
		if (entry.getVersion() != null) {
			JAXBElement<String> version = this.biblioObjectFactory.createCTSourceTypeVersion(entry.getVersion());
			sourceParams.add(version);
		}
		//
		if (entry.getVolume() != null) {
			JAXBElement<String> volume = this.biblioObjectFactory.createCTSourceTypeVolume(entry.getVolume());
			sourceParams.add(volume);
		}
		//
		if (entry.getVolumes() != null) {
			JAXBElement<String> volumes = this.biblioObjectFactory.createCTSourceTypeNumberVolumes(entry.getVolumes());
			sourceParams.add(volumes);
		}
		//
		if (entry.getChapter() != null) {
			JAXBElement<String> chapterNumber = this.biblioObjectFactory.createCTSourceTypeChapterNumber(entry.getChapter());
			sourceParams.add(chapterNumber);
		}
		//
		if (entry.getMaintitle() != null) {
			JAXBElement<String> albumTitle = this.biblioObjectFactory.createCTSourceTypeAlbumTitle(entry.getMaintitle());
			sourceParams.add(albumTitle);
		}
		//
		if (entry.getMainsubtitle() != null) {
			JAXBElement<String> shortTitle = this.biblioObjectFactory.createCTSourceTypeShortTitle(entry.getMainsubtitle());
			sourceParams.add(shortTitle);
		}
		//
		if (entry.getMaintitleaddon() != null) {
			JAXBElement<String> internetSiteTitle = this.biblioObjectFactory.createCTSourceTypeInternetSiteTitle(entry.getMaintitleaddon());
			sourceParams.add(internetSiteTitle);
		}
		//
		if (entry.getCommentStr() != null) {
			JAXBElement<String> comments = this.biblioObjectFactory.createCTSourceTypeComments(entry.getCommentStr());
			sourceParams.add(comments);
		}
		//
		if (order != null) {
			JAXBElement<String> refOrder = this.biblioObjectFactory.createCTSourceTypeRefOrder(order.toString());
			sourceParams.add(refOrder);
		}
		return source;
	}

}

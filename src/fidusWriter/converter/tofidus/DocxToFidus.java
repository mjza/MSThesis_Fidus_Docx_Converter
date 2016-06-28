package fidusWriter.converter.tofidus;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.TransformerException;

import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.wml.CTFtnEdnRef;
import org.docx4j.wml.CommentRangeStart;
import org.docx4j.wml.R.CommentReference;
import org.docx4j.wml.STFldCharType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.docx4j.openpackaging.parts.WordprocessingML.FootnotesPart;
import org.docx4j.bibliography.CTSourceType;
import org.docx4j.wml.Comments;

import auxiliary.FileHelper;
import auxiliary.MapKey;
import auxiliary.NaturalOrderComparator;
import auxiliary.Unzipper;
import fidusWriter.model.bibliography.Bibliography;
import fidusWriter.model.bibliography.BibliographyEntry;
import fidusWriter.model.bibliography.BibliographyEntryType;
import fidusWriter.model.document.Comment;
import fidusWriter.model.document.CommentAnswer;
import fidusWriter.model.document.Document;
import fidusWriter.model.document.NodeJson;
import fidusWriter.model.document.NodeType;
import fidusWriter.model.images.Images;
import mathEquations.EquationConvertor;

public class DocxToFidus {
	private String sourceFilePath = null;
	private String temporaryFolder = null;
	private String wordFolder = null;
	private WordprocessingMLPackage wordMLPackage = null;
	private MainDocumentPart mainDocumentPart = null;
	private FootnotesPart fnp = null;
	private Comments comments = null;
	private Map<String, String> stylesMap = null;
	private Bibliography bibo = null;
	private Document doc = null;
	private Images img = null;
	private NodeJson currentNode = null;
	private NodeJson waitedFigureNode = null;
	private NodeJson currentNodeAccumulater = null;
	private EquationConvertor equationConvertor = null;
	private List<CTSourceType> bibliographyResources = null;
	private NodeJson motherNode = null;
	//
	public String getSourceFilePath() {
		return sourceFilePath;
	}
	public String getWordFolder() {
		return wordFolder;
	}
	public void setWordFolder(String wordFolder) {
		this.wordFolder = wordFolder;
	}
	public Bibliography getBibo() {
		return bibo;
	}
	public void setBibo(Bibliography bibo) {
		this.bibo = bibo;
	}
	public Document getDoc() {
		return doc;
	}
	public void setDoc(Document doc) {
		this.doc = doc;
		if(this.doc !=null && this.doc.getContents()!=null)
			this.motherNode = this.doc.getContents();
	}
	public Images getImg() {
		return img;
	}
	public void setImg(Images img) {
		this.img = img;
	}
	public void setSourceFilePath(String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
	}
	public WordprocessingMLPackage getWordMLPackage() {
		return wordMLPackage;
	}
	public void setWordMLPackage(WordprocessingMLPackage wordMLPackage) {
		this.wordMLPackage = wordMLPackage;
	}
	public MainDocumentPart getMainDocumentPart() {
		return mainDocumentPart;
	}
	public void setMainDocumentPart(MainDocumentPart mainDocumentPart) {
		this.mainDocumentPart = mainDocumentPart;
	}
	public void setTemporaryFolder(String temporaryFolder) {
		this.temporaryFolder = temporaryFolder;
	}
	public DocxToFidus(String sourceFilePath) throws Docx4JException {
		this(sourceFilePath, null, null);
	}
	public DocxToFidus(String sourceFilePath, String temporaryFolder, Map<String, String> stylesMap) throws Docx4JException {
		super();
		this.setSourceFilePath(sourceFilePath);
		this.setTemporaryFolder(temporaryFolder);
		try{			
			this.setWordMLPackage(WordprocessingMLPackage.load(new java.io.File(this.sourceFilePath)));
			this.setMainDocumentPart(this.wordMLPackage.getMainDocumentPart());
			this.setBibliographyResources(this.wordMLPackage);
			this.fnp = this.wordMLPackage.getMainDocumentPart().getFootnotesPart();
			if(this.getMainDocumentPart().getCommentsPart()!=null)
				this.comments = this.getMainDocumentPart().getCommentsPart().getContents();
		} catch(org.docx4j.openpackaging.exceptions.Docx4JException e){
			System.err.println("The file cannot be opeened. It maybe crrupted or empty.");
		}
		this.setStylesMap(stylesMap);
		this.equationConvertor = new EquationConvertor();
	}
	private void setBibliographyResources(WordprocessingMLPackage wordMLPackage) {
		HashMap<String, org.docx4j.openpackaging.parts.CustomXmlPart> mp = 
				wordMLPackage.getCustomXmlDataStorageParts();
		Iterator<Map.Entry<String, org.docx4j.openpackaging.parts.CustomXmlPart>> it = mp.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, org.docx4j.openpackaging.parts.CustomXmlPart> pair = 
					(Map.Entry<String, org.docx4j.openpackaging.parts.CustomXmlPart>) it.next();
			if(pair.getValue() instanceof org.docx4j.openpackaging.parts.WordprocessingML.BibliographyPart) 
			{
				org.docx4j.openpackaging.parts.WordprocessingML.BibliographyPart bibliographyPart = 
						(org.docx4j.openpackaging.parts.WordprocessingML.BibliographyPart) pair.getValue();
				try {
					JAXBElement<org.docx4j.bibliography.CTSources> bibliography = bibliographyPart.getContents();
					this.bibliographyResources = bibliography.getValue().getSource();
				} catch (Docx4JException e) {
					System.err.println("\nProblem in reading bibliography part.");
					e.printStackTrace();
				}
				break;
			}
		}
		
	}
	public List<String> getStyles() {
		List<String> styleNameList = new ArrayList<String>();
		try {
			StyleDefinitionsPart styles = this.mainDocumentPart.getStyleDefinitionsPart();
			org.docx4j.wml.Styles stylesList = styles.getContents();
			List<org.docx4j.wml.Style> styleList = stylesList.getStyle();
			for (int i = 0; i < styleList.size(); i++) {
				// System.out.print(styleList.get(i).getStyleId());
				styleNameList.add(styleList.get(i).getStyleId());
				// styleNameList.add(styleList.get(i).getName().getVal());
			}
			/*
			// This section has been commented because we don't need the unused styles.
			org.docx4j.wml.Styles.LatentStyles latentStyles = stylesList.getLatentStyles();
			List<org.docx4j.wml.Styles.LatentStyles.LsdException> lsdExceptionList = latentStyles.getLsdException();
			for (int i = 0; i < lsdExceptionList.size(); i++) {
				styleNameList.add(lsdExceptionList.get(i).getName());
			}*/
			Collections.sort(styleNameList, new NaturalOrderComparator());
		} catch (Docx4JException e) {
			e.printStackTrace();
		} catch(Exception e){
			System.err.println("Couldn't read style data. Some error happened.");
		}
		return styleNameList;
	}

	public Map<String, String> getStylesMap() {
		return stylesMap;
	}

	public void setStylesMap(Map<String, String> stylesMap) {
		this.stylesMap = stylesMap;
	}
	public String getTemporaryFolder() {
		return temporaryFolder;
	}
	public void addNewParagraphToDocument(String pStyleName){
		String destination = this.stylesMap.get(pStyleName);
		if(pStyleName != null && destination != null && destination.equals("Code") 
			&& this.currentNode !=null && this.currentNode.getNn().getType() == NodeType.code){
			// Do nothing !!! The text will be add to current node.
		} else if(this.motherNode!=null)
			this.currentNode = this.motherNode .addPNode();
	}
	public void addBoldNode(boolean b){
		if(b && this.currentNode==null)
			this.addNewParagraphToDocument(null);
		if(b && this.currentNode!=null)
			this.currentNode = this.currentNode.addBoldNode();
	}
	public void addItalicNode(boolean b){
		if(b && this.currentNode==null)
			this.addNewParagraphToDocument(null);
		if(b && this.currentNode!=null)
			this.currentNode = this.currentNode.addItalicNode();
	}
	public void closeCurrentNode(){
		if(this.currentNode!=null && (this.currentNode.getNn().getType() == NodeType.b || this.currentNode.getNn().getType() == NodeType.i))
			this.currentNode = this.currentNode.getParent();
	}
	protected void addNewOrderedListItem(String numType, BigInteger leftIndent) {
		NodeType orderedListType = NodeType.ol;
		if(numType.equals("UL"))
			orderedListType = NodeType.ul;
		// find nearest li in parents
		while(this.currentNode != null
				&& this.currentNode.getNn().getType() != NodeType.li
				&& this.currentNode.getParent() != null){
			this.currentNode = this.currentNode.getParent();
		}
		//	
		if(this.currentNode != null && this.currentNode.getNn().getType() == NodeType.li )
		{
			if(this.currentNode.getIndent().equals(leftIndent) 
				&& this.currentNode.getParent().getNn().getType() == orderedListType)
			{
				this.currentNode = this.currentNode.getParent().addLiPNode();
			} 
			else if(this.currentNode.getIndent().compareTo(leftIndent)<0){
				if(orderedListType == NodeType.ol){
					this.currentNode = this.currentNode.addOLNode(leftIndent).addLiPNode();
				} else{
					this.currentNode = this.currentNode.addULNode(leftIndent).addLiPNode();
				}
			} else{
				while(this.currentNode.getIndent().compareTo(leftIndent)>0){
					if(this.currentNode.getParent() != null)
						this.currentNode = this.currentNode.getParent();
					else
						break;
				}
				if(this.currentNode.getNn().getType() == NodeType.ol 
					 && orderedListType == NodeType.ol){
					this.currentNode = this.currentNode.addLiPNode();
				}
				else if(this.currentNode.getNn().getType() == NodeType.ul 
					 && orderedListType == NodeType.ul){
					this.currentNode = this.currentNode.addLiPNode();
				} 
				while(this.currentNode.getNn().getType() != NodeType.li){
					if(this.currentNode.getParent() != null)
						this.currentNode = this.currentNode.getParent();
					else
						break;
				}
				if(this.currentNode.getNn().getType() == NodeType.li 
					&& this.currentNode.getParent().getNn().getType() == orderedListType){
						this.currentNode = this.currentNode.getParent().addLiPNode();	
				} else{
					if(orderedListType == NodeType.ol){
						this.currentNode = this.currentNode.addOLNode(leftIndent).addLiPNode();
					} else{
						this.currentNode = this.currentNode.addULNode(leftIndent).addLiPNode();
					}
				}
			}
		}else{
			if(orderedListType == NodeType.ol){
				this.currentNode = this.getDoc().getContents().addOLNode(leftIndent).addLiPNode();
			} else{
				this.currentNode = this.getDoc().getContents().addULNode(leftIndent).addLiPNode();
			}
		}
		
	}
	protected void addCitation(List<String> citationTags, String citationPages, String citationText, String citationTextBefores) {
		if( this.bibo != null){
			String ids = null;
			for(int i=0;i<citationTags.size();i++){
				String citationTag = citationTags.get(i);
				BibliographyEntry entry = this.bibo.getBibliographyEntry(citationTag);
				if(ids==null)
					ids = ""+entry.getId();
				else
					ids += ","+entry.getId();
			}
			if(ids!=null){
				if(this.currentNode == null)
					this.addNewParagraphToDocument(null);
				this.currentNode.addCitationSpanChild(ids,citationPages,citationTextBefores);
			}
		}		
	}
	protected void fillBibliography() 
	{
		if(this.bibliographyResources!=null && this.bibo != null)
		{
			for(int i=0; i<this.bibliographyResources.size();i++)
			{
				BibliographyEntry entry = new BibliographyEntry();
				entry.setId((long)(i+1));
				entry.setLocation("");
				CTSourceType source = this.bibliographyResources.get(i);
				List<JAXBElement<?>> list = source.getAbbreviatedCaseNumberOrAlbumTitleOrAuthor();
				for(int j=0;j<list.size();j++)
				{
					 String part = list.get(j).getName().getLocalPart();
					 if(list.get(j).getValue() instanceof String){
						 String value =  list.get(j).getValue().toString();
						if(part.equalsIgnoreCase("Comments")){
							 JSONParser parser = new JSONParser();
							 try {
								Object obj = parser.parse(value);
								JSONObject jsonObject = (JSONObject) obj;
								entry.fillObject(jsonObject, ""+(i+1));
							 } catch (ParseException e) {
								entry.setNote(value);
							 }
							 break;
						 }
					 }
				}
				//
				for(int j=0;j<list.size();j++)
				{
					 String part = list.get(j).getName().getLocalPart();
					 if(list.get(j).getValue() instanceof String){
						 String value =  list.get(j).getValue().toString();
						 if(part.equalsIgnoreCase("AbbreviatedCaseNumber")){ //
							 entry.setEid(value);
						 } else if(part.equalsIgnoreCase("AlbumTitle")){ //
							 entry.setMaintitle(value);
						 } else if(part.equalsIgnoreCase("BookTitle")){
							 entry.setDay(value);
						 } else if(part.equalsIgnoreCase("Broadcaster")){
							 entry.setHolder(value);
						 } else if(part.equalsIgnoreCase("CaseNumber")){ //
							 entry.setNumber(value);
						 } else if(part.equalsIgnoreCase("ChapterNumber")){ //
							 entry.setChapter(value);
						 } else if(part.equalsIgnoreCase("City")){
							 if(entry.getLocation().length()>0)
								 value = entry.getLocation()+", "+value;
							 entry.setLocation(value);
						 } else if(part.equalsIgnoreCase("ConferenceName")){
							 entry.setEventtitle(value);
						 } else if(part.equalsIgnoreCase("CountryRegion")){
							 if(entry.getLocation().length()>0)
								 value = entry.getLocation()+", "+value;
							 entry.setLocation(value);
						 } else if(part.equalsIgnoreCase("Day")){
							 entry.setDay(value);
						 } else if(part.equalsIgnoreCase("DayAccessed")){
							 entry.setDayAccessed(value);
						 } else if(part.equalsIgnoreCase("Department")){
							 entry.setOrganization(value);
						 } else if(part.equalsIgnoreCase("Distributor")){//
							 if(entry.getHolder()==null)
								 entry.setHolder(value);
						 } else if(part.equalsIgnoreCase("Edition")){
							 entry.setEdition(value);
						 } else if(part.equalsIgnoreCase("Institution")){
							 entry.setInstitution(value);
						 } else if(part.equalsIgnoreCase("InternetSiteTitle")){//
							 entry.setMaintitleaddon(value);
						 } else if(part.equalsIgnoreCase("Issue")){
							 entry.setIssue(value);
						 } else if(part.equalsIgnoreCase("JournalName")){
							 entry.setJournaltitle(value);
						 } else if(part.equalsIgnoreCase("LCID")){
							 entry.setLanguage(value);
						 } else if(part.equalsIgnoreCase("Medium")){
							 entry.setHowpublished(value);
						 } else if(part.equalsIgnoreCase("Month")){
							 entry.setMonth(value);
						 } else if(part.equalsIgnoreCase("MonthAccessed")){
							 entry.setMonthAccessed(value);
						 } else if(part.equalsIgnoreCase("NumberVolumes")){
							 entry.setVolumes(value);
						 } else if(part.equalsIgnoreCase("Pages")){
							 entry.setPages(value);
						 } else if(part.equalsIgnoreCase("PeriodicalTitle")){
							 entry.setTitleaddon(value);
						 } else if(part.equalsIgnoreCase("PatentNumber")){//
							 entry.setNumber(value);
						 } else if(part.equalsIgnoreCase("ProductionCompany")){//
							 if(entry.getHolder()==null)
								 entry.setHolder(value);
						 } else if(part.equalsIgnoreCase("Publisher")){
							 entry.setPublisher(value);
						 } else if(part.equalsIgnoreCase("PublicationTitle")){
							 entry.setIssuetitle(value);
						 } else if(part.equalsIgnoreCase("RecordingNumber")){//
							 entry.setNumber(value);
						 } else if(part.equalsIgnoreCase("ShortTitle")){//
							 entry.setMainsubtitle(value);
						 } else if(part.equalsIgnoreCase("Station")){ //
							 entry.setVenue(value);
						 } else if(part.equalsIgnoreCase("StandardNumber")){ //
							 entry.setIsbn(value);
						 } else if(part.equalsIgnoreCase("StateProvince")){
							 if(entry.getLocation().length()>0)
								 value = entry.getLocation()+", "+value;
							 entry.setLocation(value);
						 } else if(part.equalsIgnoreCase("Tag")){
							 entry.setEntryKey(value); 
						 } else if(part.equalsIgnoreCase("Theater")){ //
							 entry.setVenue(value); 
						 } else if(part.equalsIgnoreCase("ThesisType")){ //
							 entry.setType(value);
						 } else if(part.equalsIgnoreCase("Title")){
							 entry.setTitle(value);
						 } else if(part.equalsIgnoreCase("URL")){
							 entry.setUrl(value);
						 } else if(part.equalsIgnoreCase("Volume")){
							 entry.setVolume(value);
						 } else if(part.equalsIgnoreCase("Version")){
							 entry.setVersion(value);
						 } else if(part.equalsIgnoreCase("Year")){
							 entry.setYear(value);
						 } else if(part.equalsIgnoreCase("YearAccessed")){
							 entry.setYearAccessed(value);
						 }
					 } 
					 else if(list.get(j).getValue() instanceof org.docx4j.bibliography.CTAuthorType)
					 {
						 org.docx4j.bibliography.CTAuthorType cTAuthorType= (org.docx4j.bibliography.CTAuthorType) list.get(j).getValue(); 
						 List<JAXBElement<?>> authors = cTAuthorType.getArtistOrAuthorOrBookAuthor();
						 for(int k=0;k<authors.size();k++)
						 {
							 String roll = authors.get(k).getName().getLocalPart();
							 String value = "";
								 if(authors.get(k).getValue() instanceof org.docx4j.bibliography.CTNameOrCorporateType)
								 {
									 org.docx4j.bibliography.CTNameOrCorporateType cTNameOrCorporateType =
											 (org.docx4j.bibliography.CTNameOrCorporateType) authors.get(k).getValue();
									 if(cTNameOrCorporateType.getCorporate()!=null){
										 String people[] = cTNameOrCorporateType.getCorporate().split(";");
										 for(int l=0;l<people.length;l++){
											 String person[] = people[l].split(",");
											 String fn="", ln="";
											 if(person.length>0)
												 ln = person[0].trim();
											 if(person.length>1){
												 fn = person[1].trim();
											 }
											 value = this.addPerson(value, ln, "", fn);
										 }
									 } 
									 else
									 { 
										 org.docx4j.bibliography.CTNameListType cTNameListType = 
												 cTNameOrCorporateType.getNameList();
										 List<org.docx4j.bibliography.CTPersonType>
										 cTPersonType = cTNameListType.getPerson(); 
										 for(int l=0;l<cTPersonType.size();l++)
										 {
											String fn="", mn="", ln="";
											for(int m=0;m<cTPersonType.get(l).getFirst().size();m++){
												fn += cTPersonType.get(l).getFirst().get(m);
											}
											for(int m=0;m<cTPersonType.get(l).getMiddle().size();m++){
												mn += cTPersonType.get(l).getMiddle().get(m);
											}
											for(int m=0;m<cTPersonType.get(l).getLast().size();m++){
												ln += cTPersonType.get(l).getLast().get(m);
											}
											value = this.addPerson(value, ln, mn, fn);											
										 } 
									 } 
								}else if(authors.get(k).getValue() instanceof org.docx4j.bibliography.CTNameType)
								{ 
									org.docx4j.bibliography.CTNameType cTNameType = (org.docx4j.bibliography.CTNameType) authors.get(k).getValue();
									List<org.docx4j.bibliography.CTPersonType> cTPersonType = cTNameType.getNameList().getPerson();
									for(int l=0;l<cTPersonType.size();l++){
											String fn="", mn="", ln="";
											for(int m=0;m<cTPersonType.get(l).getFirst().size();m++){
												fn += cTPersonType.get(l).getFirst().get(m);
											}
											for(int m=0;m<cTPersonType.get(l).getMiddle().size();m++){
												mn += cTPersonType.get(l).getMiddle().get(m);
											}
											for(int m=0;m<cTPersonType.get(l).getLast().size();m++){
												ln += cTPersonType.get(l).getLast().get(m);
											}
											value = this.addPerson(value, ln, mn, fn);
										} 
								}
							 if(roll.equalsIgnoreCase("Author") || roll.equalsIgnoreCase("Writer") || 
									 roll.equalsIgnoreCase("Director") || roll.equalsIgnoreCase("Inventor")){
								 entry.setAuthor(value);
							 } else if(roll.equalsIgnoreCase("Editor")){
								 entry.setEditor(value);
							 } else if(roll.equalsIgnoreCase("Translator") || roll.equalsIgnoreCase("Compiler")){
								 entry.setTranslator(value);
							 } else if(roll.equalsIgnoreCase("BookAuthor")){
								 entry.setBookauthor(value);
							 } else if(roll.equalsIgnoreCase("ProducerName")){
								 entry.setEditora(value);
							 } else if(roll.equalsIgnoreCase("Artist") || roll.equalsIgnoreCase("Interviewer")){
								 entry.setEditorb(value);
							 } else if( roll.equalsIgnoreCase("Composer") || roll.equalsIgnoreCase("Conductor") ||
									 		roll.equalsIgnoreCase("Counsel")){
								 entry.setAnnotator(value);
							 } else if(roll.equalsIgnoreCase("Performer") || roll.equalsIgnoreCase("Interviewee")){
								 entry.setCommentator(value);
							 }
						 } 
					 }
					 else if(list.get(j).getValue() instanceof org.docx4j.bibliography.STSourceType)
					 {
						 org.docx4j.bibliography.STSourceType sTSourceType = (org.docx4j.bibliography.STSourceType) list.get(j).getValue();
						 entry.setEntryType(BibliographyEntryType.getFidusTypeNumber(sTSourceType));
					 } 
				}
				this.bibo.addBibliographyEntry(entry);
			 } // end of for
		}
	}
	private String addPerson(String value, String ln, String mn, String fn){
		if(value.length()>0)
			value += " and ";
		//
		if(fn.length()>0 && mn.length()>0)
			fn += " "+mn;
		else if(mn.length()>0)
			fn = mn;
		//
		if(fn.length()>0)
			value += (value.endsWith(" ") || value.length()==0)? "{"+fn+"}" : " {"+fn+"}";
		if(ln.length()>0)
			value += (value.endsWith(" ") || value.length()==0)? "{"+ln+"}" : " {"+ln+"}";
		return value;
	}
	public void addTextToCurrentNode(String text){
		if(this.currentNode == null)
			this.addNewParagraphToDocument(null);
		this.currentNode.addTextChild(text);
	}
	protected void applyParagraphStyle(String pStyleName) {
		String destination = this.getEquivalentStyle(pStyleName);
		if(pStyleName != null && destination != null){
			if(destination.equals("1st Headline")){
				this.addHeadingNode(NodeType.h1);
			} else if(destination.equals("2nd Headline")){
				this.addHeadingNode(NodeType.h2);
			} else if(destination.equals("3rd Headline")){
				this.addHeadingNode(NodeType.h3);
			} else if(destination.equals("4th Headline")){
				this.addHeadingNode(NodeType.h4);
			} else if(destination.equals("5th Headline")){
				this.addHeadingNode(NodeType.h5);
			} else if(destination.equals("6th Headline")){
				this.addHeadingNode(NodeType.h6);
			} else if(destination.equals("Code")){
				this.addCodeNode();
			}
		}
	}
	protected String getEquivalentStyle(String styleName){
		return this.stylesMap.get(styleName);
	}
	protected void addCommentedText(String commentedText, List<BigInteger> commentsIds, int callColseAfterPushingComment) {
		if(commentsIds.size()>0){
			if(this.currentNode == null)
				this.addNewParagraphToDocument(null);
			this.currentNode.addCommentSpanChild(commentedText,commentsIds.get(0));
			this.addCommentsToDocument(commentsIds);
		}
		for(int i=0;i<callColseAfterPushingComment;i++)
			this.closeCurrentNode();
	}
	private void addCommentsToDocument(List<BigInteger> commentsIds){
		long parentId = 0;
		Comment fwComment = null;
		CommentAnswer answer = null;
		if(this.comments!=null && this.doc!=null){
			List<Comments.Comment> commentList = this.comments.getComment();
			for(int i=0;i<commentList.size();i++){
				Comments.Comment comment = commentList.get(i);
				BigInteger id = comment.getId();
				for(int j=0;j<commentsIds.size();j++){
					if(id.longValue()==commentsIds.get(j).longValue()){
						long date = comment.getDate().toGregorianCalendar().getTimeInMillis();
						String userName = comment.getAuthor()!=null? comment.getAuthor() : "";
						int user = this.isNumeric(comment.getInitials())? Integer.parseInt(comment.getInitials()) : userName.hashCode();
						user = user<0 ? -1*user : user;
						List<Object> contents = comment.getContent();
						String commentText = this.extractText(contents,"");
						
						if(j==0){
							fwComment = new Comment();
							fwComment.setId(id.longValue());
							fwComment.setDate(date);
							fwComment.setUser(user);
							fwComment.setUserName(userName);
							fwComment.setComment(commentText);
							this.doc.getComments().getComments().add(fwComment);
						}else{
							answer = new CommentAnswer(parentId,id.longValue());
							answer.setId(id.longValue());
							answer.setDate(date);
							answer.setUser(user);
							answer.setUserName(userName);
							answer.setAnswer(commentText);
							fwComment.getAnswers().addAnswer(answer);
						}
						
						
						if(j==0){
							parentId = id.longValue();
						}
						commentList.remove(i);
						i--;
						// Don't remove items from commentsIds
					}
				}
			}
		}
	}

	/*
	 *  The contents object can be of the following type(s).
	 *  Thus this function can be extended to support them.
		JAXBElement<CTMarkupRange> 
		JAXBElement<CTMarkup> 
		JAXBElement<CTTrackChange> 
		JAXBElement<CTTrackChange> 
		RunIns 
		P 
		JAXBElement<CTPerm> 
		JAXBElement<CTMoveBookmark> 
		JAXBElement<RunTrackChange> 
		ProofErr 
		JAXBElement<CTTrackChange> 
		JAXBElement<CTMoveFromRangeEnd> 
		CommentRangeEnd 
		JAXBElement<CTTrackChange> 
		JAXBElement<CTMarkup> 
		RunDel 
		JAXBElement<CTMoveToRangeEnd> 
		SdtBlock 
		JAXBElement<CTOMathPara> 
		JAXBElement<CTMoveBookmark> 
		JAXBElement<RangePermissionStart> 
		CommentRangeStart 
		JAXBElement<CTBookmark> 
		JAXBElement<CTOMath> 
		JAXBElement<CTCustomXmlBlock> 
		JAXBElement<Tbl> 
		JAXBElement<CTMarkup> 
		JAXBElement<RunTrackChange> 
		JAXBElement<CTMarkup> 
		JAXBElement<CTAltChunk>
	*/
	private String extractText(List<Object> contents, String str) {
		Object obj = null;
		for(int i=0;i<contents.size();i++){
			if(contents.get(i) instanceof org.docx4j.wml.Text){
				str += ((org.docx4j.wml.Text) contents.get(i)).getValue();
			} else if(contents.get(i) instanceof org.docx4j.wml.P){
				str = this.extractText(((org.docx4j.wml.P)contents.get(i)).getContent(), str);
			}else if(contents.get(i) instanceof org.docx4j.wml.R){ 
				str = this.extractText(((org.docx4j.wml.R)contents.get(i)).getContent(), str);
			} else if(contents.get(i) instanceof javax.xml.bind.JAXBElement){ 
				if((obj=((javax.xml.bind.JAXBElement<?>)contents.get(i)).getValue()) instanceof org.docx4j.wml.Text){
					str += ((org.docx4j.wml.Text) obj).getValue();
				} 
			}
		}
		return str;
	}
	protected File addFigureImageNode(String imagePk, String title, String imageRealPath) {
		String targetPath = this.getTemporaryFolder();
		String imageOrigin = "/media/images/"+FileHelper.getFileFullName(imageRealPath);
		BufferedImage bi;
		int height = 0;
		int width = 0;
		int hash = 0;
		try {
			bi = ImageIO.read(new File(imageRealPath));
			width = bi.getWidth();
			height = bi.getHeight();
			hash = bi.hashCode();
		} catch (IOException e) {
			System.err.println("Problem in reading image data.");
		}
		File file = FileHelper.copyFile(imageRealPath, targetPath, null);
		if(this.waitedFigureNode!=null && this.waitedFigureNode.getNn().getType()==NodeType.figure
				&& this.currentNode.getNn().getType()==NodeType.p){
			NodeJson parent = this.currentNode.getParent();
			int numberOfChildren = parent.getChildrenNumber();
			parent.removeChild(numberOfChildren-1); // remove current P node
			this.currentNode = this.waitedFigureNode;
			this.waitedFigureNode = null;
		}else{
			this.currentNode = this.currentNode.replaceMeWithNode(NodeType.figure);
		}
		if(this.currentNode.getNn().getType() != NodeType.figure){
			this.currentNode = this.currentNode.addFigureNode();
		}
		if(this.isNumeric(imagePk)){
			imagePk = this.addImageToImagesFile(imageOrigin,title,height,width,hash,imagePk);
		}else{
			imagePk = this.addImageToImagesFile(imageOrigin,title,height,width,hash,"0");
		}		
		if(this.currentNode!=null){
			this.currentNode.addImagePkToFigureNode(imagePk);
			this.currentNode.addImageUrlToFigureNode(imageOrigin);
		}
		return file;
	}
	protected void addFormula(String latex, boolean inlineFormula) {
		if(inlineFormula){
			this.currentNode.addFormulaSpan(latex);
		}else{
			if(this.waitedFigureNode!=null && this.waitedFigureNode.getNn().getType()==NodeType.figure
					&& this.currentNode.getNn().getType()==NodeType.p){
				NodeJson parent = this.currentNode.getParent();
				int numberOfChildren = parent.getChildrenNumber();
				parent.removeChild(numberOfChildren-1); // remove current P node
				this.currentNode = this.waitedFigureNode;
				this.waitedFigureNode = null;
			}else{
				this.currentNode = this.currentNode.replaceMeWithNode(NodeType.figure);
			}
			if(this.currentNode.getNn().getType() != NodeType.figure){
				this.currentNode = this.currentNode.addFigureNode();
			}
			this.currentNode.addFormulaToFigureNode(latex);
		}
	}
	protected void insertFigureCategoryAndCaption(String category, String caption){
		if(this.currentNode.getNn().getType() == NodeType.p){
			NodeJson parent = this.currentNode.getParent();
			int numberOfChildren = parent.getChildrenNumber();
			NodeJson sibil = null;
			if(numberOfChildren>=2 && (sibil = parent.getNodeJsonChild(numberOfChildren-2))!=null 
					&& sibil.getNn().getType()==NodeType.figure && !sibil.hasAttribute("data-figure-category"))
			{
					sibil.addCategoryAndCaptionToFigureNode(category, caption);
					parent.removeChild(numberOfChildren-1); // remove current P node
					this.currentNode = sibil;
			}else{
				this.currentNode = this.currentNode.replaceMeWithNode(NodeType.figure);
				if(this.currentNode.getNn().getType() != NodeType.figure){
					this.currentNode = this.currentNode.addFigureNode();
				}
				this.currentNode.addCategoryAndCaptionToFigureNode(category, caption);
				this.waitedFigureNode = this.currentNode;
			}
			
		}
	}
	private boolean isNumeric(String str)
	{
	  return str.matches("\\d+");
	}
	private String addImageToImagesFile(String path, String title, int height, int width, int hash, String imagePk) {
		Images img = this.getImg();
		if(img !=null){
			return ""+img.addImage(path, title, height, width, hash, Long.parseLong(imagePk));
		}
		return null;
	}
	private String isMetadataPart(String pStyleName) {
		String destination = this.getEquivalentStyle(pStyleName);
		if(pStyleName != null && destination != null){
			if(destination.equals("Title")){
				if(this.getDoc().getMetaData().hasTitle())
					return null;
				else
					return "Title";
			} else if(destination.equals("Subtitle")){
				if(this.getDoc().getMetaData().hasSubtitle())
					return null;
				else
					return "Subtitle";
			} else if(destination.equals("Abstract")){
				if(this.getDoc().getMetaData().hasAbstract()){
					return null;
				}else
					return "Abstract";
			} else if(destination.equals("Authors")){
				if(this.getDoc().getMetaData().hasAuthors())
					return null;
				else
					return "Authors";
			} else if(destination.equals("Keywords")){
				if(this.getDoc().getMetaData().hasKeywords())
					return null;
				else
					return "Keywords";
			}
		}
		return null;
	}
	private void addMetaData(String meta, String type){
		if(type == null || meta==null || meta.length()==0)
			return;
		else if(type.equals("Title")){
			this.getDoc().getMetaData().setTitle(meta);
			this.getDoc().setTitle(meta);
		}else if(type.equals("Subtitle")){
			this.getDoc().getMetaData().setSubtitle(meta);
			this.getDoc().getSettings().setMetadataSubtitle(true);
		}else if(type.equals("Author")){
			this.getDoc().getMetaData().setAuthors(meta);
			this.getDoc().getSettings().setMetadataAuthors(true);
		}else if(type.equals("Abstract")){
			this.getDoc().getMetaData().setAbstract(meta);
			this.getDoc().getSettings().setMetadataAbstract(true);
		}else if(type.equals("Keywords")){
			this.getDoc().getMetaData().setKeywords(meta);
			this.getDoc().getSettings().setMetadataKeywords(true);
		}
	}
	protected void addHyperlink(String hyperlinkHrefAttr, String hyperlinkTitleAttr, boolean bold, boolean italic, String text) {
		if(this.currentNode == null)
			this.addNewParagraphToDocument(null);
		this.currentNode.addHyperlinkNode(hyperlinkHrefAttr, hyperlinkTitleAttr,bold,italic,text);
	}
	protected Object addFootnoteSpan(BigInteger id) {
		if(this.fnp!=null){
			try {
				List<org.docx4j.wml.CTFtnEdn> footnotes = this.fnp.getContents().getFootnote();
				org.docx4j.wml.CTFtnEdn footnote = null;
				for(int i=0;i<footnotes.size();i++){
					if(footnotes.get(i).getId().compareTo(id)==0){
						footnote = footnotes.get(i);
						break;
					}							
				}
				if(footnote != null){
					if(this.currentNode == null)
						this.addNewParagraphToDocument(null);					
					this.currentNodeAccumulater = this.currentNode;
					this.currentNode = this.currentNode.addFootnoteSpanChild();
					this.motherNode = this.currentNode;
					return footnote;
				}
			} catch (Exception e) {
				System.err.println("\nError in fetching footnote data.");
				e.printStackTrace();
			}
		}
		return null;
	}
	protected void closeFootnoteSpan() {
		if(this.currentNodeAccumulater != null)	{		
			this.currentNode = this.currentNodeAccumulater;
			this.motherNode = this.getDoc().getContents();
		}
	}
	protected void addCodeNode(){		
		if(this.currentNode == null)
			this.currentNode = this.getDoc().getContents().addPreNode().addCodeNode();
		else if(this.currentNode.getNn().getType()!=NodeType.code)
		{
			this.currentNode = this.currentNode.replaceMeWithNode(NodeType.pre);
			if(this.currentNode.getNn().getType() != NodeType.pre){
				if(this.currentNode.getParent() !=null)
					this.currentNode = this.currentNode.getParent().addPreNode();
				else
					this.currentNode = this.getDoc().getContents().addPreNode();
				//
				this.currentNode = this.currentNode.addCodeNode();
			} else{
				this.currentNode = this.currentNode.addCodeNode();
			}
		}
	}
	protected void addHeadingNode(NodeType type){
		this.currentNode = this.currentNode.replaceMeWithNode(type);
		if(this.currentNode.getNn().getType() != type){
			if(this.currentNode.getParent() !=null && this.currentNode.getParent().getNn().getType() != NodeType.pre)
				this.currentNode = this.currentNode.getParent().addHeadingNode(type);
			else if(this.currentNode.getParent() !=null && this.currentNode.getParent().getNn().getType() == NodeType.pre && this.currentNode.getParent().getParent() !=null) 
				this.currentNode = this.currentNode.getParent().getParent().addHeadingNode(type);
			else
				this.currentNode = this.getDoc().getContents().addHeadingNode(type);
		}
	}
	private org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart getNumberingPart() {
		org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart numbering = null;
		try {
			numbering = (org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart) this.wordMLPackage.getParts().get(new PartName("/word/numbering.xml"));
			if(numbering == null){
				HashMap<org.docx4j.openpackaging.parts.PartName, org.docx4j.openpackaging.parts.Part> mp = this.wordMLPackage.getParts().getParts();
				Iterator<java.util.Map.Entry<org.docx4j.openpackaging.parts.PartName, org.docx4j.openpackaging.parts.Part>> it = mp.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry<org.docx4j.openpackaging.parts.PartName, org.docx4j.openpackaging.parts.Part> pair = (Map.Entry<org.docx4j.openpackaging.parts.PartName, org.docx4j.openpackaging.parts.Part>) it.next();
			        if(pair.getValue() instanceof org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart)
			        {
			        	numbering = (org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart) pair.getValue();
			        	break;
			        }
			        it.remove();
			    }
			}
		} catch (InvalidFormatException e) {
			System.err.println("Couldn't load numbering part.");
		}
		return numbering;
	}
	class OrderedListPr{
		private String type = null;
		private BigInteger left = null;
		public OrderedListPr(String type, BigInteger left) {
			super();
			this.type = type;
			this.left = left;
		}
		public String getType() {
			return type;
		}
		public BigInteger getLeft() {
			return left;
		}
	}
	private Map<MapKey, OrderedListPr> getNumberingTypesList() throws Docx4JException{
		org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart numbering = this.getNumberingPart();
		Map<MapKey, OrderedListPr> listOfNumberingTypes = null;
		if(numbering != null){
			listOfNumberingTypes = new LinkedHashMap<MapKey, OrderedListPr>();
			List<org.docx4j.wml.Numbering.Num> nums = numbering.getContents().getNum();
			List<org.docx4j.wml.Numbering.AbstractNum> abstracts = numbering.getContents().getAbstractNum();
	    	for(int i=0;i<nums.size();i++){
	    		BigInteger numId = null;
	    		if(nums.get(i).getNumId() != null){
	    			numId = nums.get(i).getNumId();
	    			BigInteger abstractNumId = nums.get(i).getAbstractNumId().getVal();
	    			if(abstractNumId != null){
				    	for(int j=0;j<abstracts.size();j++){
				    		if(abstracts.get(j).getAbstractNumId().compareTo(abstractNumId)==0){
				    			List<org.docx4j.wml.Lvl> lvl = abstracts.get(j).getLvl();
				    			for(int k=0; k<lvl.size();k++){
				    				BigInteger ilvl = lvl.get(k).getIlvl();
			    					org.docx4j.wml.NumFmt numFmt = lvl.get(k).getNumFmt();
			    					org.docx4j.wml.PPr pPr = lvl.get(k).getPPr();
			    					String type = null;
			    					if(numFmt != null){
			    						type = numFmt.getVal().toString();
			    						BigInteger left = null;
			    						if(pPr!=null && pPr.getInd()!=null)
			    							left = pPr.getInd().getLeft();
			    						listOfNumberingTypes.put(new MapKey(numId, ilvl), new OrderedListPr(type, left));
			    					}				    				
				    			}
				    		}
				    	}
			    	}
	    		}
	    	}	
		}
		return listOfNumberingTypes;
	}
	public List<File> startConversion(Bibliography bibo, Document doc, Images img) throws Docx4JException {
		this.setBibo(bibo);
		this.setDoc(doc);
		this.setImg(img);
		final List<File> imagesFiles = new ArrayList<File>();
		if(this.getMainDocumentPart() == null)
			return imagesFiles;
		// extract word to copy images from this folder.
		int random = (int )(Math.random() * 50 + 1);
		String temp = temporaryFolder+"_word_"+random;
		while(!FileHelper.makeDirectory(temp)){
			random = (int )(Math.random() * 50 + 1);
			temp = temporaryFolder+"_word_"+random;
		}
		this.setWordFolder(temp);
		try {
			Unzipper.unzipFile(this.getSourceFilePath(), "docx", temp);
		} catch (IOException e) {
			System.err.println("Couldn't extract the Word file.");
			e.printStackTrace();
		}
		final Map<MapKey, OrderedListPr>  numberingTypesList = this.getNumberingTypesList();
		final RelationshipsPart relationshipsPartMD = this.mainDocumentPart.getRelationshipsPart();
		this.getDoc().getContents().clearChildren();
		this.fillBibliography();
		org.docx4j.wml.Document wmlDocumentEl = this.getMainDocumentPart().getContents();
		org.docx4j.wml.Body body = wmlDocumentEl.getBody();
		final DocxToFidus _this = this;
		new org.docx4j.TraversalUtil(body,
		new org.docx4j.TraversalUtil.Callback() {
			private int depth = 0;
			private String indent = "";
			private boolean lastChild = false;
			private boolean paragraphRemoved = false;
			private boolean stdContent = false;
			private boolean citation = false;
			private List<String> citationTags = new ArrayList<String>();
			private String citationPages = null;
			private String citationTextBefores = "";
			private String category = null;
			private String pStyleName = null;
			private boolean pBold = false;
			private boolean pItalic = false;
			private String pCaption = null;
			private String numType = null;
			private BigInteger numId = null;
			private BigInteger ilvl = null;
			private BigInteger leftIndent = null;
			private String rStyleName = null;
			private boolean rBold = false;
			private boolean rItalic = false;
			private boolean isTitle = false;
			private boolean isTitleP = false;
			private boolean isSubtitle = false;
			private boolean isSubtitleP = false;
			private boolean isAuthor = false;
			private boolean isAuthorP = false;
			private boolean isAbstract = false;
			private boolean isAbstractP = false;
			private boolean isKeywords = false;
			private boolean isKeywordsP = false;
			private String hyperlinkTitleAttr = "";
			private String hyperlinkHrefAttr = null;
			private boolean normalHyperlink = true;
			private String imagePk = null;
			private String imageTitle = null;
			private String imageRealPath = null;
			private boolean inlineFormula = true;
			private boolean commentedRun = false;
			private String commentedText = "";
			private List<BigInteger> commentsIds = new ArrayList<BigInteger>();
			private List<BigInteger> commentsIds2ndSeen = new ArrayList<BigInteger>();
			private int callColseAfterPushingComment = 0;
			private Object temporaryBranch = null;
			private boolean footerPart = false;
			//
			private void resetVariables() {
				this.temporaryBranch = null;
				this.citation = false;
				this.citationTags.clear();
				this.resetParagraphVariables();
				this.rStyleName = null;
				this.rBold = false;
				this.rItalic = false;
				this.resetMetaVariavles();
				this.resetHyperlinkVariables();
				this.resetImageVariables();
			}
			private void resetCommentsVariables(){
				this.commentedRun = false;
				this.commentedText = "";
				this.commentsIds.clear();
				this.commentsIds2ndSeen.clear();
				this.callColseAfterPushingComment = 0;
			}
			private void resetFormulaVariables(){
				this.inlineFormula = true;
			}
			private void resetCitationVariables(){
				this.citation = false;
				this.citationTags.clear();
				this.stdContent = false;
				this.citationPages = null;
			}
			private void resetImageVariables(){
				this.imagePk = null;
				this.imageTitle = null;
				this.imageRealPath = null;
			}
			private void resetHyperlinkVariables(){
				this.hyperlinkTitleAttr = "";
				this.hyperlinkHrefAttr = null;
				this.normalHyperlink = true;
			}
			private boolean isHyperlink(){
				if(this.hyperlinkHrefAttr!=null)
					return true;
				else
					return false;
			}
			private void resetMetaVariavles(){
				this.isTitle = false;
				this.isTitleP = false;
				this.isSubtitle = false;
				this.isSubtitleP = false;
				this.isAuthor = false;
				this.isAuthorP = false;
				this.isAbstract = false;
				this.isAbstractP = false;
				this.isKeywords = false;
				this.isKeywordsP = false;
			}
			private void resetParagraphVariables(){
				this.pStyleName = null;
				this.pBold = false;
				this.pItalic = false;
				this.pCaption = null;
				this.category = null;
				this.numType = null;
				this.numId = null;
				this.ilvl = null;
				this.leftIndent = null;
				this.isTitleP = false;
				this.isSubtitleP = false;
				this.isAuthorP = false;
				this.isAbstractP = false;
				this.isKeywordsP = false;
				this.resetHyperlinkVariables();
				this.resetCitationVariables();
				this.citationTextBefores = "";
				this.resetCommentsVariables();
			}
			private void resetRunVariables(){
				if(this.rItalic == true && !this.commentedRun)
					_this.closeCurrentNode();
				else if (this.rItalic == true && this.commentedRun)
					this.callColseAfterPushingComment  = 1;
				if(this.rBold == true && !this.commentedRun)
					_this.closeCurrentNode();
				else if(this.rBold == true && this.commentedRun)
					this.callColseAfterPushingComment  = 2;
				this.rStyleName = null;
				this.rBold = false;
				this.rItalic = false;
			}
			private boolean isMetadata(){
				return (this.isTitleP ||
						this.isSubtitleP ||
						this.isAuthorP ||
						this.isAbstractP ||
						this.isKeywordsP);
			}
			private String getMetadataType(){
				if(this.isTitleP) 
					return "Title";
				else if(this.isSubtitleP)
					return "Subtitle";
				else if(this.isAuthorP)
					return "Author";
				else if(this.isAbstractP)
					return "Abstract";
				else if(this.isKeywordsP)
					return "Keywords";
				return null;
			}
			private boolean isBold(){
				return (this.rBold || this.pBold);
			}
			private boolean isItalic(){
				return (this.rItalic || this.pItalic);
			}
			private String getNumberingType(BigInteger numId, BigInteger ilvl){
				String type = null;
				if(numberingTypesList != null){
					OrderedListPr item = numberingTypesList.get(new MapKey(numId,ilvl));
					type = item.getType();
					if(type.compareToIgnoreCase("DECIMAL")==0)
						type = "OL";
					else
						type = "UL";
				}
				return type;
			}
			private BigInteger getNumberingLeftIndent(BigInteger numId, BigInteger ilvl){
				BigInteger left = null;
				if(numberingTypesList != null){
					OrderedListPr item = numberingTypesList.get(new MapKey(numId,ilvl));
					left = item.getLeft();
				}
				return left;
			}
			private void processStdBlock(org.docx4j.wml.SdtBlock stdBlock){
				if(stdBlock.getSdtEndPr() != null){
					List<org.docx4j.wml.RPr> rPrs = stdBlock.getSdtEndPr().getRPr();
					for(int i=0; i<rPrs.size();i++){
						if(rPrs.get(i).getRStyle() != null)
							this.pStyleName =  rPrs.get(i).getRStyle().getVal();
					}
				}
				if(stdBlock.getSdtPr() != null){
					List<Object> pros = stdBlock.getSdtPr().getRPrOrAliasOrLock();
					for(int i=0; i<pros.size();i++){
						if(pros.get(i) instanceof javax.xml.bind.JAXBElement){
							javax.xml.bind.JAXBElement<?> jel = (javax.xml.bind.JAXBElement<?>) pros.get(i);
							if(jel.getValue() instanceof org.docx4j.wml.SdtPr.Alias){
								org.docx4j.wml.SdtPr.Alias alias =  (org.docx4j.wml.SdtPr.Alias) jel.getValue();
								String element = alias.getVal();
								if(element.equalsIgnoreCase("Title"))
									this.isTitle = true;
								else if (element.equalsIgnoreCase("Subtitle"))
									this.isSubtitle = true;
								else if (element.equalsIgnoreCase("Author"))
									this.isAuthor = true;
								else if (element.equalsIgnoreCase("Abstract"))
									this.isAbstract = true;
								else if (element.equalsIgnoreCase("Keywords"))
									this.isKeywords = true;
							} else if(jel.getValue() instanceof org.docx4j.wml.RPr){
								org.docx4j.wml.RPr rPr = (org.docx4j.wml.RPr) jel.getValue();
								if(rPr.getRStyle() != null)
									 this.pStyleName = rPr.getRStyle().getVal();
								
							}
							
						}
					}
				}
			}
			private void processInlinePictures(Inline o) {
				org.docx4j.dml.wordprocessingDrawing.Inline inlineDrawing = (org.docx4j.dml.wordprocessingDrawing.Inline) o;
				this.imagePk = inlineDrawing.getDocPr().getName();
				this.imageTitle = inlineDrawing.getDocPr().getDescr();				
			}
			private void processHyperlink(org.docx4j.wml.P.Hyperlink hyperlink) {
				this.resetHyperlinkVariables(); // it is normal hyperlink!
				if(hyperlink!=null){					
					this.hyperlinkTitleAttr = hyperlink.getTooltip();
					String rid = hyperlink.getId();
					if(this.footerPart ){
						if(_this.fnp!=null){
							RelationshipsPart  relationshipsPartFN = _this.fnp.getRelationshipsPart();
							org.docx4j.relationships.Relationship relationship = relationshipsPartFN.getRelationshipByID(rid);
							this.hyperlinkHrefAttr = relationship.getTarget();
						}
					} else{
						org.docx4j.relationships.Relationship relationship = relationshipsPartMD.getRelationshipByID(rid);
						this.hyperlinkHrefAttr = relationship.getTarget();
					}
				}
			}
			private void processCTBlip(org.docx4j.dml.CTBlip ctBlip) {
				if(ctBlip.getEmbed() != null){
					org.docx4j.relationships.Relationship relationship = relationshipsPartMD.getRelationshipByID(ctBlip.getEmbed());
					this.imageRealPath = relationship.getTarget();
					File file = _this.addFigureImageNode(this.imagePk, this.imageTitle, _this.getWordFolder()+FileHelper.getPathSpiliter()+"word"+FileHelper.getPathSpiliter()+this.imageRealPath);
					imagesFiles.add(file);
				}
			}
			private void processSdtRun(org.docx4j.wml.SdtRun o) {
				this.resetCitationVariables();
			}
			private void processFldCharType(STFldCharType chartType) {
				if(chartType.value().compareTo("begin")==0){
					this.stdContent = true;					
				}
				else if(chartType.value().compareTo("end")==0){
					this.stdContent = false;
				}
			}
			private void processCTOMathPara(org.docx4j.math.CTOMathPara ctoMathPara) {
				this.inlineFormula = false;
			}
			private void processCTOMath(org.docx4j.math.CTOMath ctoMath) {
				try {
					String ommlXML = org.docx4j.XmlUtils.marshaltoString( ctoMath, true, true,
						 org.docx4j.jaxb.Context.jc,
						"http://schemas.openxmlformats.org/officeDocument/2006/math", "oMath", org.docx4j.math.CTOMath.class);
					String latex = _this.equationConvertor.omml2latex("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+ommlXML);
					_this.addFormula(latex,this.inlineFormula);
				} catch (UnsupportedEncodingException e) {
					System.err.println("\nProblem in converting formula in Docx to latex.");
					e.printStackTrace();
				} catch (TransformerException e) {
					System.err.println("\nProblem in converting formula in Docx to latex.");
					e.printStackTrace();
				}
				this.resetFormulaVariables();
			}
			private void processCommentRangeStart(CommentRangeStart o) {
				this.commentedRun = true;
				this.commentsIds.add(o.getId());
				
			}
			private void processCommentReference(CommentReference o) {
				this.commentsIds2ndSeen.add(o.getId());
				if(this.commentsIds.size()==this.commentsIds2ndSeen.size()){
					_this.addCommentedText(this.commentedText,this.commentsIds2ndSeen,this.callColseAfterPushingComment);
					this.commentedText = "";
					this.removeVisitedCommentsIds();
				}
			}
			private void removeVisitedCommentsIds() {
				for(int i=0;i<this.commentsIds2ndSeen.size();i++){
					for(int j=0;j<this.commentsIds.size();j++){
						if(this.commentsIds.get(j).compareTo(this.commentsIds2ndSeen.get(i))==0){
							this.commentsIds.remove(j);
							j--;
							this.commentsIds2ndSeen.remove(i);
							i--;
							break;
						}
					}
				}
				this.callColseAfterPushingComment = 0;
				if(this.commentsIds.size()==0)
					this.resetCommentsVariables();
			}
			private void processParagraph(org.docx4j.wml.P p) {
				if(this.pCaption!=null || this.category!=null){
					_this.insertFigureCategoryAndCaption(this.category,this.pCaption);
				}
				//
				this.resetParagraphVariables();
				if(this.isTitle){
					this.isTitleP = true;
					this.isTitle = false;
					return;
				} else if (this.isSubtitle){
					this.isSubtitleP = true;
					this.isSubtitle = false;
					return;
				} else if (this.isAuthor){
					this.isAuthorP = true;
					this.isAuthor = false;
					return;
				} else if (this.isAbstract){
					this.isAbstractP = true;
					this.isAbstract = false;
					return;
				} else if (this.isKeywords){
					this.isKeywordsP = true;
					this.isKeywords = false;
					return;
				}
				org.docx4j.wml.PPr pPr = p.getPPr();
				boolean isOrderedList = false;
				if(pPr != null){
					if(pPr.getPStyle() != null)
						this.pStyleName = pPr.getPStyle().getVal();
					if(pPr.getRPr() != null){
						org.docx4j.wml.ParaRPr rPr = pPr.getRPr();
						if(rPr != null){
							if(rPr.getRStyle() != null)
								this.pStyleName = rPr.getRStyle().getVal();
							if(rPr.getB() != null){
								this.pBold = rPr.getB().isVal();
							}
							if(rPr.getI() != null){
								this.pItalic = rPr.getI().isVal();
							}
						}
					}
					if(pPr.getNumPr() != null){
							org.docx4j.wml.PPrBase.NumPr numPr =  pPr.getNumPr();
							this.numId = numPr.getNumId().getVal();
							this.ilvl = numPr.getIlvl().getVal();
							this.numType = this.getNumberingType(this.numId, this.ilvl);
							this.leftIndent = this.getNumberingLeftIndent(this.numId, this.ilvl);
							isOrderedList = true;
					}
				}
				String meta = _this.isMetadataPart(this.pStyleName);
				if(meta!=null){
					if(meta.equals("Title")){
						this.isTitleP = true;
						return;
					} else if(meta.equals("Subtitle")){
						this.isSubtitleP = true;
						return;
					} else if(meta.equals("Authors")){
						this.isAuthorP = true;
						return;
					} else if(meta.equals("Abstract")){
						this.isAbstractP = true;
						return;
					} else if(meta.equals("Keywords")){
						this.isKeywordsP = true;
						return;
					}					
				}
				if(isOrderedList)
					_this.addNewOrderedListItem(this.numType,this.leftIndent);
				else
					_this.addNewParagraphToDocument(this.pStyleName);
				_this.applyParagraphStyle(this.pStyleName);
			}
			private Object processFootnote(CTFtnEdnRef o) {
				this.temporaryBranch = _this.addFootnoteSpan(o.getId());
				return this.temporaryBranch;
			}
			private void processRun(org.docx4j.wml.R run) {
				if(this.isHyperlink() && this.normalHyperlink && !(run.getParent() instanceof org.docx4j.wml.P.Hyperlink)){
					this.resetHyperlinkVariables();
				}
				this.resetRunVariables();
				String temp = "";
				if((this.pStyleName!=null && (temp=_this.getEquivalentStyle(this.pStyleName))!=null 
						&& temp.equalsIgnoreCase("Caption"))
						|| this.category!=null || this.pCaption!=null){
					return;
				}
				if(!this.isMetadata()){
					org.docx4j.wml.RPr rPr = run.getRPr();
					if(rPr != null){
						if(rPr.getRStyle() != null)
							this.rStyleName = rPr.getRStyle().getVal();
						if(rPr.getB() != null){
							this.rBold = rPr.getB().isVal();
							if(!this.isHyperlink())
								_this.addBoldNode(this.rBold);
						}
						if(rPr.getI() != null){
							this.rItalic = rPr.getI().isVal();
							if(!this.isHyperlink())
								_this.addItalicNode(this.rItalic);
						}
					}
				}
			}
			private String processText(org.docx4j.wml.Text txt){
				String text = null;
				String localPart = ((javax.xml.bind.JAXBElement<?>) txt.getParent()).getName().getLocalPart();
				if(localPart.compareTo("instrText")==0){
					String insertText = txt.getValue();
					String[] parts = insertText.split("\\s+");
					if(insertText.contains("CITATION ")){
						this.citation = true;
						int i=0;
						for(i=0;i<parts.length;i++)
							if(parts[i].equalsIgnoreCase("CITATION"))
								break;
						i++;
						if(i<parts.length)
						  this.citationTags.add(parts[i]);
						for(;i<parts.length;i++){
							if(parts[i].equalsIgnoreCase("\\p")){
								i++;
								if(i<parts.length){
									if(this.citationPages==null)
										this.citationPages = parts[i];
									else
										this.citationPages += ",,,"+parts[i];
								}
							}
							else if(parts[i].equalsIgnoreCase("\\m")){
								i++;
								if(i<parts.length)
									this.citationTags.add(parts[i]);
							}
						}
						
					} else if(insertText.contains("SEQ ")){
						int i=0;
						for(i=0;i<parts.length;i++)
							if(parts[i].equalsIgnoreCase("SEQ"))
								break;
						i++;
						if(i<parts.length){
							if(parts[i].compareTo("Photo")==0)
								this.category = "photo";
							else if (parts[i].compareTo("Table")==0)
								this.category = "table";
							else if (parts[i].compareTo("Figure")==0)
								this.category = "figure";
							else if (parts[i].compareTo("Equation")==0)
								this.category = "equation";
						}
					} else if(insertText.contains("HYPERLINK ")){
						int i=0;
						for(i=0;i<parts.length;i++)
							if(parts[i].equalsIgnoreCase("HYPERLINK"))
								break;
						i++;
						if(i<parts.length){
							this.hyperlinkHrefAttr = parts[i].substring(1, parts[i].length()-2);
							this.normalHyperlink = false;
						}
						i+=2;
						if(i<parts.length){
							this.hyperlinkTitleAttr = parts[i].substring(1, parts[i].length()-2);
						}else
							this.hyperlinkTitleAttr = "";							
					}
				} 
				else	
					text = txt.getValue();
				//
				
				String temp="";
				if((this.pStyleName!=null && (temp=_this.getEquivalentStyle(this.pStyleName))!=null 
						&& temp.equalsIgnoreCase("Caption"))
						|| this.category!=null){
					this.pCaption = text;
					if(this.lastChild){
						if(this.pCaption!=null || this.category!=null){
							_this.insertFigureCategoryAndCaption(this.category,this.pCaption);
							this.pCaption = null;
							this.category = null;
						}
					}
					return text;
				}
				//
				if(text != null){
					if(this.isMetadata() && this.getMetadataType()!=null){
						_this.addMetaData(text, this.getMetadataType());
					} else if(this.rStyleName!=null && this.rStyleName.contains("TextBeforeCitation")){
						if(text.startsWith("["))
							text = text.substring(1, text.length());
						if(text.endsWith("]"))
							text = text.substring(0, text.length()-1);
						if(this.citationTextBefores.length()==0)
							this.citationTextBefores = text;
						else
							this.citationTextBefores += ",,,"+text;
					} else if(this.citation && this.stdContent){
						_this.addCitation(this.citationTags,this.citationPages,text,this.citationTextBefores);
						this.resetCitationVariables();
						this.citationTextBefores = "";
					} else if(this.citationTextBefores.length()>0 && !this.stdContent && (this.rStyleName==null || !this.rStyleName.contains("TextBeforeCitation"))){
						if(text.startsWith("["))
							text = text.substring(1, text.length());
						if(text.endsWith("]"))
							text = text.substring(0, text.length()-1);
						this.citationTextBefores += ",,,"+text;
						_this.addTextToCurrentNode(this.citationTextBefores);
						this.citationTextBefores = "";
					} else if(this.commentedRun){
						if(this.commentsIds2ndSeen.size()>0){
							_this.addCommentedText(this.commentedText,this.commentsIds2ndSeen,this.callColseAfterPushingComment);
							this.commentedText = "";
							this.removeVisitedCommentsIds();
						}
						if(this.commentsIds.size()>0)
							this.commentedText += text;
						else
							_this.addTextToCurrentNode(text);
					} else if(this.isHyperlink()){
						_this.addHyperlink(this.hyperlinkHrefAttr, this.hyperlinkTitleAttr, this.isBold(), this.isItalic(), text);
						this.resetHyperlinkVariables();
					} else
						_this.addTextToCurrentNode(text);
				}
				return text;
			}
			@Override
			public List<Object> apply(Object o) {
				String text = null;
				System.out.print(indent + o.getClass().getName());
				if (o instanceof org.docx4j.wml.SdtBlock){
					org.docx4j.wml.SdtBlock stdBlock = (org.docx4j.wml.SdtBlock) o;
					this.processStdBlock(stdBlock);
				} else if (o instanceof org.docx4j.math.CTOMathPara){
					this.processCTOMathPara((org.docx4j.math.CTOMathPara) o);
				} else if (o instanceof org.docx4j.math.CTOMath){
					this.processCTOMath((org.docx4j.math.CTOMath) o);
				} else if (o instanceof org.docx4j.wml.CommentRangeStart){
					this.processCommentRangeStart((org.docx4j.wml.CommentRangeStart) o);
				} else if (o instanceof org.docx4j.wml.R.CommentReference){
					this.processCommentReference((org.docx4j.wml.R.CommentReference) o);
				} else if (o instanceof org.docx4j.dml.wordprocessingDrawing.Inline){
					this.processInlinePictures((org.docx4j.dml.wordprocessingDrawing.Inline)o);					
				} else if (o instanceof org.docx4j.dml.CTBlip){
					this.processCTBlip((org.docx4j.dml.CTBlip) o);
				} else if (o instanceof org.docx4j.wml.STFldCharType){
					this.processFldCharType((org.docx4j.wml.STFldCharType) o);					
				} else if (o instanceof org.docx4j.wml.SdtRun){
					this.processSdtRun((org.docx4j.wml.SdtRun)o);
				} else if (o instanceof org.docx4j.wml.P.Hyperlink){
					this.processHyperlink((org.docx4j.wml.P.Hyperlink)o);
				} else if (o instanceof org.docx4j.wml.CTFtnEdnRef){
					this.processFootnote((org.docx4j.wml.CTFtnEdnRef) o);
				} else if (o instanceof org.docx4j.wml.P){
					this.processParagraph((org.docx4j.wml.P)o);
				} else if (o instanceof org.docx4j.wml.R){
					this.processRun((org.docx4j.wml.R)o);
				}
				else if (o instanceof org.docx4j.wml.Text){
					org.docx4j.wml.Text txt = (org.docx4j.wml.Text) o;
					text = this.processText(txt);
				}
				if(text != null)
					System.out.println("  \"" + text + "\"");
				else
					System.out.print("\n");
				return null;
			}

			@Override
			public boolean shouldTraverse(Object o) {
				if (o instanceof org.docx4j.math.CTOMath)
					return false;
				else if (o instanceof org.docx4j.math.CTR)
					return false;
				else if (o instanceof org.docx4j.wml.Text)
					return false;
				else
					return true;
			}
			
			public boolean shouldProcess(Object o) {
				if(o instanceof org.docx4j.wml.P && ((org.docx4j.wml.P)o).getContent().size()==0){
					this.paragraphRemoved = true;
					return false;
				} else if (o instanceof org.docx4j.wml.P && this.paragraphRemoved){
					this.paragraphRemoved = false;
					return true;
				} else
					return true;
			}
			// Depth first
			@Override
			public void walkJAXBElements(Object parent) {

				indent += "    ";
				depth++;
				boolean p = parent instanceof org.docx4j.wml.P ? true : false;
				List<Object> children = getChildren(parent);
				if (children != null) {
					int size = children.size();
					int i=0;
					for (Object o : children) {
						i++;
						if(i==size && p)
							this.lastChild = true;
						// if its wrapped in javax.xml.bind.JAXBElement, get its
						// value
						o = org.docx4j.XmlUtils.unwrap(o);
						if(this.shouldProcess(o)){
							this.apply(o);
							if(this.temporaryBranch!=null){
								Object obj = this.temporaryBranch;
								this.temporaryBranch=null;
								this.footerPart  = true;
								walkJAXBElements(obj);
								this.footerPart = false;
								_this.closeFootnoteSpan();
							}								
						}
						if (this.shouldTraverse(o)) {
							walkJAXBElements(o);
						}
						if(this.lastChild && p)
							this.lastChild = false;
					}
				}

				indent = indent.substring(0, indent.length() - 4);
				depth--;
				if(depth==0)
					this.resetVariables();
			}

			

			@Override
			public List<Object> getChildren(Object o) {
				return org.docx4j.TraversalUtil.getChildrenImpl(o);
			}

		}

		);
		FileHelper.deleteFolder(new File(this.getWordFolder()));
		return imagesFiles;
	}
	
}

package fidusWriter.model.bibliography;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class is a prototype for the bibliography enteries.
 */
public class BibliographyEntry {
	/**
	 * A random number generator.
	 */
	private static java.util.Random random = null;
	/**
	 * The following variables model an biblio entry in Fidus The equivalent
	 * property of the Docx also mentioned infront
	 */
	private String tag = null;
	protected Long id = null; // Tag (Tag) : FW_id - if entryKey is empty then
								// use this combination
	protected String entryKey = null; // Tag (Tag) - This element is used as a
										// unique ID for each <source> element
										// by Microsoft Office.
	protected Integer entryType = null; // use BibliographyEntryType for
										// converting
	protected String author = null; // Author> Author> NameList> Person>
	protected String bookauthor = null; // Author> Writer> Corporate> if author
										// is existed then as the second person
										// of NameList
	protected String editor = null; // Author> Editor> NameList> Person>
									// <Last-First>
	protected String editora = null; // Author> Editor> NameList> Person>
										// <Last-First>
	protected String editorb = null; // Author> Editor> NameList> Person>
										// <Last-First>
	protected String editorc = null; // Author> Editor> NameList> Person>
										// <Last-First>
	protected String annotator = null; // Author> Counsel> NameList> Person>
										// <Last-First>
	protected String commentator = null; // Author> Counsel> NameList> Person>
											// <Last-First>
	protected String translator = null; // Author> Translator> NameList> Person>
										// <Last-First>
	protected String booktitle = null; // BookTitle
	protected String booksubtitle = null; // Added to BookTitle
	protected String booktitleaddon = null; // Added to BookTitle
	protected String chapter = null; // ChapterNumber
	protected String date = null; // Day, DayAccessed = 1-31, Month,
									// MonthAccessed = January, Year,
									// YearAccessed = 2016
	protected String edition = null; // Edition
	protected String holder = null; // Broadcaster
	protected String institution = null; // Institution
	protected String issue = null; // Issue
	protected String journaltitle = null; // JournalName
	protected String journalsubtitle = null; // Added to JournalName
	protected String shortjournal = null;
	protected String language = null; // LCID. ST_Lang
	protected String location = null; // City
	protected String pages = null; // Pages
	protected String publisher = null; // Publisher
	protected String subtitle = null; // ShortTitle
	protected String title = null; // Title
	protected String shorttitle = null;
	protected String titleaddon = null; // ShortTitle or Added to Title
	protected String type = null; // Type
	protected String url = null; // URL
	protected String version = null; // Version
	protected String volume = null; // Volume
	protected String file = null;
	protected String afterword = null; // Comments
	protected String addendum = null; // Comment
	protected String issn = null; // StandardNumber (Standard Number) [ISSN or
									// ISBN] or Comments
	protected String isbn = null; // StandardNumber (Standard Number) [ISSN or
									// ISBN] or Comments
	protected String isrn = null; // StandardNumber (Standard Number) [ISSN or
									// ISBN] Or Comments
	protected String doi = null; // StandardNumber (Standard Number) [ISSN or
									// ISBN] or Comments
	protected String eid = null; // Comments
	protected String entryCat = null; // Comments
	protected String eprint = null; // Comments
	protected String eprintclass = null; // Comments
	protected String eprinttype = null; // Comments
	protected String eventdate = null; // Comments
	protected String eventtitle = null; // Comments
	protected String foreword = null; // Comments
	protected String howpublished = null; // Medium
	protected String introduction = null; // Comments
	protected String issuesubtitle = null; // Comments
	protected String issuetitle = null; // Comments
	protected String maintitle = null; // Comments
	protected String mainsubtitle = null; // Comments
	protected String maintitleaddon = null; // Comments
	protected String note = null; // Comments
	protected String number = null; // Comments
	protected String shortauthor = null;
	protected String organization = null; // Department
	protected String origlanguage = null; // LCID. ST_Lang or Comments
	protected String pagetotal = null; // Comments
	protected String part = null; // Comments
	protected String pubstate = null; // Comments
	protected String series = null; // Comments
	protected String urldate = null; // Comments
	protected String venue = null; // Comments
	protected String volumes = null; // NumberVolumns
	// The following variables are not related to Fidus Entry model. They came
	// from Docx model
	private String day = null;
	private String dayAccessed = null;
	private String month = null;
	private String monthAccessed = null;
	private String year = null;
	private String yearAccessed = null;

	/**
	 * A simple constructor. Properties are filled by setters when want to
	 * convert from Docx to Fidus
	 */
	public BibliographyEntry() {
		super();
		if (BibliographyEntry.random == null)
			BibliographyEntry.random = new java.util.Random();
	}

	/**
	 * Use to make a object from an entry in Fidus file. Used for Fidus to Docx
	 * 
	 * @param biboEntry
	 * @param key
	 */
	public BibliographyEntry(JSONObject biboEntry, String key) {
		if (BibliographyEntry.random == null)
			BibliographyEntry.random = new java.util.Random();
		this.fillObject(biboEntry, key);
	}

	/**
	 * Receives an entry from Fidus file and converts it to an entry object
	 * 
	 * @param biboEntry
	 * @param key
	 */
	public void fillObject(JSONObject biboEntry, String key) {
		if (biboEntry != null) {
			Long id = Long.parseLong(key);
			this.setId(id);
			if (biboEntry.get("id") != null) {
				this.setId(Long.parseLong(biboEntry.get("id").toString()));
			}
			if (biboEntry.get("entry_type") != null) {
				this.setEntryType(Integer.parseInt(biboEntry.get("entry_type").toString()));
			}
			if (biboEntry.get("entry_key") != null) {
				this.setEntryKey(biboEntry.get("entry_key").toString());
			}
			if (biboEntry.get("entry_cat") != null) {
				this.setEntryCat(biboEntry.get("entry_cat").toString());
			}
			if (biboEntry.get("number") != null) {
				this.setNumber(biboEntry.get("number").toString());
			}
			if (biboEntry.get("location") != null) {
				this.setLocation(biboEntry.get("location").toString());
			}
			if (biboEntry.get("series") != null) {
				this.setSeries(biboEntry.get("series").toString());
			}
			if (biboEntry.get("editor") != null) {
				this.setEditor(biboEntry.get("editor").toString());
			}
			if (biboEntry.get("pages") != null) {
				this.setPages(biboEntry.get("pages").toString());
			}
			if (biboEntry.get("author") != null) {
				this.setAuthor(biboEntry.get("author").toString());
			}
			if (biboEntry.get("editora") != null) {
				this.setEditora(biboEntry.get("editora").toString());
			}
			if (biboEntry.get("editorb") != null) {
				this.setEditorb(biboEntry.get("editorb").toString());
			}
			if (biboEntry.get("editorc") != null) {
				this.setEditorc(biboEntry.get("editorc").toString());
			}
			if (biboEntry.get("isbn") != null) {
				this.setIsbn(biboEntry.get("isbn").toString());
			}
			if (biboEntry.get("annotator") != null) {
				this.setAnnotator(biboEntry.get("annotator").toString());
			}
			if (biboEntry.get("commentator") != null) {
				this.setCommentator(biboEntry.get("commentator").toString());
			}
			if (biboEntry.get("origlanguage") != null) {
				this.setOriglanguage(biboEntry.get("origlanguage").toString());
			}
			if (biboEntry.get("translator") != null) {
				this.setTranslator(biboEntry.get("translator").toString());
			}
			if (biboEntry.get("volume") != null) {
				this.setVolume(biboEntry.get("volume").toString());
			}
			if (biboEntry.get("publisher") != null) {
				this.setPublisher(biboEntry.get("publisher").toString());
			}
			if (biboEntry.get("chapter") != null) {
				this.setChapter(biboEntry.get("chapter").toString());
			}
			if (biboEntry.get("volumes") != null) {
				this.setVolumes(biboEntry.get("volumes").toString());
			}
			if (biboEntry.get("edition") != null) {
				this.setEdition(biboEntry.get("edition").toString());
			}
			if (biboEntry.get("afterword") != null) {
				this.setAfterword(biboEntry.get("afterword").toString());
			}
			if (biboEntry.get("foreword") != null) {
				this.setForeword(biboEntry.get("foreword").toString());
			}
			if (biboEntry.get("introduction") != null) {
				this.setIntroduction(biboEntry.get("introduction").toString());
			}
			if (biboEntry.get("mainsubtitle") != null) {
				this.setMainsubtitle(biboEntry.get("mainsubtitle").toString());
			}
			if (biboEntry.get("maintitle") != null) {
				this.setMaintitle(biboEntry.get("maintitle").toString());
			}
			if (biboEntry.get("maintitleaddon") != null) {
				this.setMaintitleaddon(biboEntry.get("maintitleaddon").toString());
			}
			if (biboEntry.get("part") != null) {
				this.setPart(biboEntry.get("part").toString());
			}
			if (biboEntry.get("pagetotal") != null) {
				this.setPagetotal(biboEntry.get("pagetotal").toString());
			}
			if (biboEntry.get("version") != null) {
				this.setVersion(biboEntry.get("version").toString());
			}
			if (biboEntry.get("booksubtitle") != null) {
				this.setBooksubtitle(biboEntry.get("booksubtitle").toString());
			}
			if (biboEntry.get("booktitle") != null) {
				this.setBooktitle(biboEntry.get("booktitle").toString());
			}
			if (biboEntry.get("booktitleaddon") != null) {
				this.setBooktitleaddon(biboEntry.get("booktitleaddon").toString());
			}
			if (biboEntry.get("organization") != null) {
				this.setOrganization(biboEntry.get("organization").toString());
			}
			if (biboEntry.get("issn") != null) {
				this.setIssn(biboEntry.get("issn").toString());
			}
			if (biboEntry.get("issue") != null) {
				this.setIssue(biboEntry.get("issue").toString());
			}
			if (biboEntry.get("issuetitle") != null) {
				this.setIssuetitle(biboEntry.get("issuetitle").toString());
			}
			if (biboEntry.get("type") != null) {
				this.setType(biboEntry.get("type").toString());
			}
			if (biboEntry.get("eid") != null) {
				this.setEid(biboEntry.get("eid").toString());
			}
			if (biboEntry.get("journalsubtitle") != null) {
				this.setJournalsubtitle(biboEntry.get("journalsubtitle").toString());
			}
			if (biboEntry.get("journaltitle") != null) {
				this.setJournaltitle(biboEntry.get("journaltitle").toString());
			}
			if (biboEntry.get("bookauthor") != null) {
				this.setBookauthor(biboEntry.get("bookauthor").toString());
			}
			if (biboEntry.get("eventdate") != null) {
				this.setEventdate(biboEntry.get("eventdate").toString());
			}
			if (biboEntry.get("eventtitle") != null) {
				this.setEventtitle(biboEntry.get("eventtitle").toString());
			}
			if (biboEntry.get("howpublished") != null) {
				this.setHowpublished(biboEntry.get("howpublished").toString());
			}
			if (biboEntry.get("venue") != null) {
				this.setVenue(biboEntry.get("venue").toString());
			}
			if (biboEntry.get("institution") != null) {
				this.setInstitution(biboEntry.get("institution").toString());
			}
			if (biboEntry.get("holder") != null) {
				this.setHolder(biboEntry.get("holder").toString());
			}
			if (biboEntry.get("isrn") != null) {
				this.setIsrn(biboEntry.get("isrn").toString());
			}
			if (biboEntry.get("eprinttype") != null) {
				this.setEprinttype(biboEntry.get("eprinttype").toString());
			}
			if (biboEntry.get("eprintclass") != null) {
				this.setEprintclass(biboEntry.get("eprintclass").toString());
			}
			if (biboEntry.get("eprint") != null) {
				this.setEprint(biboEntry.get("eprint").toString());
			}
			if (biboEntry.get("doi") != null) {
				this.setDoi(biboEntry.get("doi").toString());
			}
			if (biboEntry.get("titleaddon") != null) {
				this.setTitleaddon(biboEntry.get("titleaddon").toString());
			}
			if (biboEntry.get("language") != null) {
				this.setLanguage(biboEntry.get("language").toString());
			}
			if (biboEntry.get("urldate") != null) {
				this.setUrldate(biboEntry.get("urldate").toString());
			}
			if (biboEntry.get("url") != null) {
				this.setUrl(biboEntry.get("url").toString());
			}
			if (biboEntry.get("title") != null) {
				this.setTitle(biboEntry.get("title").toString());
			}
			if (biboEntry.get("subtitle") != null) {
				this.setSubtitle(biboEntry.get("subtitle").toString());
			}
			if (biboEntry.get("pubstate") != null) {
				this.setPubstate(biboEntry.get("pubstate").toString());
			}
			if (biboEntry.get("note") != null) {
				this.setNote(biboEntry.get("note").toString());
			}
			if (biboEntry.get("addendum") != null) {
				this.setAddendum(biboEntry.get("addendum").toString());
			}
			if (biboEntry.get("date") != null) {
				this.setDate(biboEntry.get("date").toString());
			}
			if (biboEntry.get("shortauthor") != null) {
				this.setShortauthor(biboEntry.get("shortauthor").toString());
			}
			if (biboEntry.get("shorttitle") != null) {
				this.setShorttitle(biboEntry.get("shorttitle").toString());
			}
			if (biboEntry.get("file") != null) {
				this.setFile(biboEntry.get("file").toString());
			}
			if (biboEntry.get("shortjournal") != null) {
				this.setShortjournal(biboEntry.get("shortjournal").toString());
			}
		}
	}

	// The getters and setters area

	public void setDayAccessed(String dayAccessed) {
		this.dayAccessed = dayAccessed;
	}

	public String getShortjournal() {
		return shortjournal;
	}

	public void setShortjournal(String shortjornal) {
		this.shortjournal = shortjornal;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void setMonthAccessed(String monthAccessed) {
		this.monthAccessed = monthAccessed;
	}

	public void setYearAccessed(String yearAccessed) {
		this.yearAccessed = yearAccessed;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getShortauthor() {
		return shortauthor;
	}

	public void setShortauthor(String shortauthor) {
		this.shortauthor = shortauthor;
	}

	public String getEntryKey() {
		return entryKey;
	}

	public void setEntryKey(String string) {
		this.entryKey = string;
	}

	public String getEntryCat() {
		return entryCat;
	}

	public void setEntryCat(String entryCat) {
		this.entryCat = entryCat;
	}

	public Integer getEntryType() {
		return entryType;
	}

	public void setEntryType(Integer entryType) {
		this.entryType = entryType;
	}

	public String getShorttitle() {
		return shorttitle;
	}

	public void setShorttitle(String shorttitle) {
		this.shorttitle = shorttitle;
	}

	public String getDate() {
		String date = this.date;
		if (date == null) {
			try {
				date = this.makeDate();
			} catch (ParseException e) {
				System.err.println("\nProblem in making date from passed year,month,day.");
				e.printStackTrace();
			}
		}
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getAddendum() {
		return addendum;
	}

	public void setAddendum(String addendum) {
		this.addendum = addendum;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getPubstate() {
		return pubstate;
	}

	public void setPubstate(String pubstate) {
		this.pubstate = pubstate;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrldate() {
		return urldate;
	}

	public void setUrldate(String urldate) {
		this.urldate = urldate;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTitleaddon() {
		return titleaddon;
	}

	public void setTitleaddon(String titleaddon) {
		this.titleaddon = titleaddon;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public String getEprint() {
		return eprint;
	}

	public void setEprint(String eprint) {
		this.eprint = eprint;
	}

	public String getEprintclass() {
		return eprintclass;
	}

	public void setEprintclass(String eprintclass) {
		this.eprintclass = eprintclass;
	}

	public String getEprinttype() {
		return eprinttype;
	}

	public void setEprinttype(String eprinttype) {
		this.eprinttype = eprinttype;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public String getPages() {
		return pages;
	}

	public void setPages(String pages) {
		this.pages = pages;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getEditora() {
		return editora;
	}

	public void setEditora(String editora) {
		this.editora = editora;
	}

	public String getEditorb() {
		return editorb;
	}

	public void setEditorb(String editorb) {
		this.editorb = editorb;
	}

	public String getEditorc() {
		return editorc;
	}

	public void setEditorc(String editorc) {
		this.editorc = editorc;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getAnnotator() {
		return annotator;
	}

	public void setAnnotator(String annotator) {
		this.annotator = annotator;
	}

	public String getCommentator() {
		return commentator;
	}

	public void setCommentator(String commentator) {
		this.commentator = commentator;
	}

	public String getOriglanguage() {
		return origlanguage;
	}

	public void setOriglanguage(String origlanguage) {
		this.origlanguage = origlanguage;
	}

	public String getTranslator() {
		return translator;
	}

	public void setTranslator(String translator) {
		this.translator = translator;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getChapter() {
		return chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	public String getVolumes() {
		return volumes;
	}

	public void setVolumes(String volumes) {
		this.volumes = volumes;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getAfterword() {
		return afterword;
	}

	public void setAfterword(String afterword) {
		this.afterword = afterword;
	}

	public String getForeword() {
		return foreword;
	}

	public void setForeword(String foreword) {
		this.foreword = foreword;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getMainsubtitle() {
		return mainsubtitle;
	}

	public void setMainsubtitle(String mainsubtitle) {
		this.mainsubtitle = mainsubtitle;
	}

	public String getMaintitle() {
		return maintitle;
	}

	public void setMaintitle(String maintitle) {
		this.maintitle = maintitle;
	}

	public String getMaintitleaddon() {
		return maintitleaddon;
	}

	public void setMaintitleaddon(String maintitleaddon) {
		this.maintitleaddon = maintitleaddon;
	}

	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}

	public String getPagetotal() {
		return pagetotal;
	}

	public void setPagetotal(String pagetotal) {
		this.pagetotal = pagetotal;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getBooksubtitle() {
		return booksubtitle;
	}

	public void setBooksubtitle(String booksubtitle) {
		this.booksubtitle = booksubtitle;
	}

	public String getBooktitle() {
		return booktitle;
	}

	public void setBooktitle(String booktitle) {
		this.booktitle = booktitle;
	}

	public String getBooktitleaddon() {
		return booktitleaddon;
	}

	public void setBooktitleaddon(String booktitleaddon) {
		this.booktitleaddon = booktitleaddon;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getIssn() {
		return issn;
	}

	public void setIssn(String issn) {
		this.issn = issn;
	}

	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public String getIssuesubtitle() {
		return issuesubtitle;
	}

	public void setIssuesubtitle(String issuesubtitle) {
		this.issuesubtitle = issuesubtitle;
	}

	public String getIssuetitle() {
		return issuetitle;
	}

	public void setIssuetitle(String issuetitle) {
		this.issuetitle = issuetitle;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	public String getJournalsubtitle() {
		return journalsubtitle;
	}

	public void setJournalsubtitle(String journalsubtitle) {
		this.journalsubtitle = journalsubtitle;
	}

	public String getJournaltitle() {
		return journaltitle;
	}

	public void setJournaltitle(String journaltitle) {
		this.journaltitle = journaltitle;
	}

	public String getBookauthor() {
		return bookauthor;
	}

	public void setBookauthor(String bookauthor) {
		this.bookauthor = bookauthor;
	}

	public String getEventdate() {
		return eventdate;
	}

	public void setEventdate(String eventdate) {
		this.eventdate = eventdate;
	}

	public String getEventtitle() {
		return eventtitle;
	}

	public void setEventtitle(String eventtitle) {
		this.eventtitle = eventtitle;
	}

	public String getHowpublished() {
		return howpublished;
	}

	public void setHowpublished(String howpublished) {
		this.howpublished = howpublished;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getHolder() {
		return holder;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}

	public String getIsrn() {
		return isrn;
	}

	public void setIsrn(String isrn) {
		this.isrn = isrn;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String toString() {
		return this.toString(true);
	}

	public org.docx4j.bibliography.STSourceType getDocxType() {
		return BibliographyEntryType.getDocxType(this.getEntryType());
	}

	public String getYear(String dateStr) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		String year = null;
		try {
			Date date = format.parse(dateStr);
			format = new SimpleDateFormat("yyyy", Locale.ENGLISH);
			year = format.format(date);
		} catch (Exception e) {
			return this.extractYear(dateStr);
		}
		return year;
	}

	public String getYear() {
		return this.getYear(this.getDate());
	}

	public String getMonth(String dateStr) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		String year = null;
		try {
			Date date = format.parse(dateStr);
			format = new SimpleDateFormat("MMMM", Locale.ENGLISH);
			year = format.format(date);
		} catch (Exception e) {
			// do nothing
		}
		return year;
	}

	public String getMonth() {
		return this.getMonth(this.getDate());
	}

	public String getDay(String dateStr) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		String year = null;
		try {
			Date date = format.parse(dateStr);
			format = new SimpleDateFormat("dd", Locale.ENGLISH);
			year = format.format(date);
		} catch (Exception e) {
			// do nothing
		}
		return year;
	}

	public String getDay() {
		return this.getDay(this.getDate());
	}

	public String extractYear() {
		return this.extractYear(this.getDate());
	}

	public String extractYear(String dateStr) {
		String[] date = dateStr.split("-");
		if (date.length > 0)
			return date[0];
		return null;
	}

	public String getTag() {
		if (this.tag != null)
			return this.tag;
		if (this.getEntryKey() != null && this.getEntryKey().trim().length() > 0) {
			this.tag = this.getEntryKey();
		} else if (this.getId() != null) {
			this.tag = "FW_" + this.getId().longValue();
		} else {
			this.tag = "FWR_" + BibliographyEntry.random.nextInt(5000);
		}
		return this.tag;
	}

	public String getStandardNumber() {
		String standardNumber = null;
		if (this.issn != null && this.issn.trim().length() > 0) // StandardNumber
																// (Standard
																// Number) [ISSN
																// or ISBN] or
																// Comments
			standardNumber = this.issn;
		else if (this.isbn != null && this.isbn.trim().length() > 0) // StandardNumber
																		// (Standard
																		// Number)
																		// [ISSN
																		// or
																		// ISBN]
																		// or
																		// Comments
			standardNumber = this.isbn;
		else if (this.isrn != null && this.isrn.trim().length() > 0) // StandardNumber
																		// (Standard
																		// Number)
																		// [ISSN
																		// or
																		// ISBN]
																		// Or
																		// Comments
			standardNumber = this.isrn;
		else if (this.doi != null && this.doi.trim().length() > 0)
			standardNumber = this.doi;
		return standardNumber;
	}

	public ArrayList<ArrayList<String>> getAuthors() {
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		//
		String[] rawAuthors = this.getAuthor().split(" and ");
		for (int i = 0; i < rawAuthors.length; i++) {
			ArrayList<String> names = new ArrayList<String>();
			Matcher m = Pattern.compile("(?<=\\{)(.*?)(?=\\})").matcher(rawAuthors[i]);
			while (m.find()) {
				names.add(m.group());
			}
			list.add(names);
		}
		return list;
	}

	public String getAuthorsStr() {
		String str = "";
		ArrayList<ArrayList<String>> list = this.getAuthors();
		for (int i = 0; i < list.size(); i++) {
			ArrayList<String> person = list.get(i);
			for (int j = 0; j < person.size(); j++)
				str += " " + person.get(j);
			if (i < list.size() - 1)
				str += " &";
		}
		//
		if (str.trim().length() == 0)
			return null;
		return str;
	}

	public String getBookTitleStr() {
		String bookTitleStr = null;
		if (this.booktitle != null && this.booktitle.trim().length() > 0)
			bookTitleStr = this.getBooktitle();
		else if (this.booksubtitle != null && this.booksubtitle.trim().length() > 0) {
			if (bookTitleStr == null)
				bookTitleStr = "";
			bookTitleStr += " _ " + this.getBooksubtitle();
		} else if (this.booktitleaddon != null && this.booktitleaddon.trim().length() > 0) {
			if (bookTitleStr == null)
				bookTitleStr = "";
			bookTitleStr += " _ " + this.getBooktitleaddon();
		}
		return bookTitleStr;
	}

	public String getMainTitle() {
		String mainTitleStr = null;
		if (this.maintitle != null && this.maintitle.trim().length() > 0) {
			mainTitleStr = this.getMaintitle();
		} else if (this.mainsubtitle != null && this.mainsubtitle.trim().length() > 0) {
			if (mainTitleStr == null)
				mainTitleStr = "";
			mainTitleStr += " _ " + this.getMainsubtitle();
		} else if (this.maintitleaddon != null && this.maintitleaddon.trim().length() > 0) {
			if (mainTitleStr == null)
				mainTitleStr = "";
			mainTitleStr += " _ " + this.getMaintitleaddon();
		}
		return mainTitleStr;
	}

	public String getJournalName() {
		String journalName = null;
		if (this.journaltitle != null && this.journaltitle.trim().length() > 0) {
			journalName = this.getJournaltitle();
		} else if (this.journalsubtitle != null && this.journalsubtitle.trim().length() > 0) {
			if (journalName == null)
				journalName = "";
			journalName += " _ " + this.getJournalsubtitle();
		}
		return journalName;
	}

	public String getEditionStr() {
		if (this.getEdition() != null)
			return this.getEdition().toString();
		return null;
	}

	public String getTitleStr() {
		if (this.getSubtitle() != null && this.subtitle.trim().length() > 0 && this.getTitleaddon() != null
				&& this.titleaddon.trim().length() > 0)
			return (this.getTitle() + " _ " + this.getTitleaddon());
		else if (this.title != null && this.title.trim().length() > 0)
			return this.getTitle();
		else
			return null;
	}

	public String getSubtitleStr() {
		if (this.getSubtitle() == null && this.titleaddon != null && this.titleaddon.trim().length() > 0)
			return this.getTitleaddon();
		else if (this.getSubtitle() != null && this.subtitle.trim().length() > 0)
			return this.getSubtitle();
		else
			return null;
	}

	public String getLanguageStr() {
		if (this.getLanguage() == null)
			return this.getOriglanguage();
		else
			return this.getLanguage();
	}

	public String getCommentStr() {
		String str = this.toString(false);
		str = "{ \"id\" : \"" + this.getId() + "\", " + str + " }";
		return str;
	}

	// End of getters and setters area

	/**
	 * Makes sutable date string based on the received date from docx file.
	 * 
	 * @return
	 * @throws ParseException
	 */
	private String makeDate() throws ParseException {
		String str = null;
		if (this.year != null)
			str = this.year;
		if (str != null) {
			if (this.month != null) {
				String monthName = this.month;
				if (this.isNumeric(monthName))
					str += "-" + monthName;
				else {
					SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM");
					Calendar cal = Calendar.getInstance();
					cal.setTime(inputFormat.parse(monthName));
					SimpleDateFormat outputFormat = new SimpleDateFormat("MM"); // 01-12
					monthName = outputFormat.format(cal.getTime());
					str += "-" + monthName;
				}
			} else
				str += "-AA";
			//
			if (this.day != null)
				str += "-" + this.day;
			else
				str += "-AA";

		}
		if (str == null) {
			if (this.yearAccessed != null)
				str = this.yearAccessed;
			if (str != null) {
				if (this.monthAccessed != null) {
					String monthName = this.monthAccessed;
					if (this.isNumeric(monthName))
						str += "-" + monthName;
					else {
						SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM");
						Calendar cal = Calendar.getInstance();
						cal.setTime(inputFormat.parse(monthName));
						SimpleDateFormat outputFormat = new SimpleDateFormat("MM"); // 01-12
						monthName = outputFormat.format(cal.getTime());
						str += "-" + monthName;
					}
				} else
					str += "-AA";
				//
				if (this.dayAccessed != null)
					str += "-" + this.dayAccessed;
				else
					str += "-AA";
			}
		}
		return str;
	}

	/**
	 * Returns true if the passed string is digits
	 * 
	 * @param str
	 * @return
	 */
	private boolean isNumeric(String str) {
		return str.matches("\\d+");
	}

	/**
	 * Converts an entry to string. The output of this function is used for
	 * storing the entry in bibliography.json
	 * 
	 * @param addBrackets
	 * @return
	 */
	public String toString(boolean addBrackets) {
		String str = "";
		if (addBrackets)
			str = "{";
		boolean flag = false;
		if (this.getAddendum() != null) {
			flag = true;
			str += "\"addendum\":\""
					+ this.getAddendum().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getAfterword() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"afterword\":\""
					+ this.getAfterword().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getAnnotator() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"annotator\":\""
					+ this.getAnnotator().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getAuthor() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"author\":\"" + this.getAuthor().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getBookauthor() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"bookauthor\":\""
					+ this.getBookauthor().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getBooksubtitle() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"booksubtitle\":\""
					+ this.getBooksubtitle().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getBooktitle() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"booktitle\":\""
					+ this.getBooktitle().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getBooktitleaddon() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"booktitleaddon\":\""
					+ this.getBooktitleaddon().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getChapter() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"chapter\":\"" + this.getChapter().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getCommentator() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"commentator\":\""
					+ this.getCommentator().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getDate() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"date\":\"" + this.getDate().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getDoi() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"doi\":\"" + this.getDoi().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getEdition() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			if (this.isNumeric(this.getEdition()))
				str += "\"edition\":" + this.getEdition();
			else
				str += "\"edition\":\""
						+ this.getEdition().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getEditor() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"editor\":\"" + this.getEditor().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getEditora() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"editora\":\"" + this.getEditora().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getEditorb() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"editorb\":\"" + this.getEditorb().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getEditorc() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"editorc\":\"" + this.getEditorc().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getEid() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"eid\":\"" + this.getEid().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getEntryCat() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"entry_cat\":\""
					+ this.getEntryCat().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getEntryKey() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"entry_key\":\""
					+ this.getEntryKey().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getEntryType() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"entry_type\":" + this.getEntryType().intValue();
		}
		if (this.getEprint() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"eprint\":\"" + this.getEprint().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getEprintclass() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"eprintclass\":\""
					+ this.getEprintclass().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getEprinttype() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"eprinttype\":\""
					+ this.getEprinttype().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getEventdate() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"eventdate\":\""
					+ this.getEventdate().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getEventtitle() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"eventtitle\":\""
					+ this.getEventtitle().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getForeword() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"foreword\":\""
					+ this.getForeword().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getFile() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"file\":\"" + this.getFile().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getHolder() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"holder\":\"" + this.getHolder().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getHowpublished() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"howpublished\":\""
					+ this.getHowpublished().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getInstitution() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"institution\":\""
					+ this.getInstitution().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getIntroduction() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"introduction\":\""
					+ this.getIntroduction().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getIsbn() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"isbn\":\"" + this.getIsbn().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getIsrn() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"isrn\":\"" + this.getIsrn().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getIssn() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"issn\":\"" + this.getIssn().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getIssue() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"issue\":\"" + this.getIssue().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getIssuesubtitle() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"issuesubtitle\":\""
					+ this.getIssuesubtitle().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getIssuetitle() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"issuetitle\":\""
					+ this.getIssuetitle().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getJournalsubtitle() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"journalsubtitle\":\""
					+ this.getJournalsubtitle().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getShortjournal() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"shortjournal\":\""
					+ this.getShortjournal().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getJournaltitle() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"journaltitle\":\""
					+ this.getJournaltitle().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getLanguage() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"language\":\""
					+ this.getLanguage().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getLocation() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"location\":\""
					+ this.getLocation().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getMainsubtitle() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"mainsubtitle\":\""
					+ this.getMainsubtitle().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getMaintitle() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"maintitle\":\""
					+ this.getMaintitle().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getMaintitleaddon() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"maintitleaddon\":\""
					+ this.getMaintitleaddon().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getNote() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"note\":\"" + this.getNote().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getNumber() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"number\":\"" + this.getNumber().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getOrganization() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"organization\":\""
					+ this.getOrganization().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getOriglanguage() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"origlanguage\":\""
					+ this.getOriglanguage().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getPages() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"pages\":\"" + this.getPages().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getPagetotal() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"pagetotal\":\""
					+ this.getPagetotal().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getPart() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"part\":\"" + this.getPart().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getPublisher() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"publisher\":\""
					+ this.getPublisher().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getPubstate() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"pubstate\":\""
					+ this.getPubstate().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getSeries() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"series\":\"" + this.getSeries().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getShortauthor() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"shortauthor\":\""
					+ this.getShortauthor().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getSubtitle() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"subtitle\":\""
					+ this.getSubtitle().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getTitle() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"title\":\"" + this.getTitle().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getShorttitle() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"shorttitle\":\""
					+ this.getShorttitle().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getTitleaddon() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"titleaddon\":\""
					+ this.getTitleaddon().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getTranslator() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"translator\":\""
					+ this.getTranslator().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getType() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"type\":\"" + this.getType().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getUrl() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"url\":\"" + this.getUrl().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getUrldate() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"urldate\":\"" + this.getUrldate().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getVenue() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"venue\":\"" + this.getVenue().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getVersion() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"version\":\"" + this.getVersion().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getVolume() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"volume\":\"" + this.getVolume().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getVolumes() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"volumes\":\"" + this.getVolumes().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (addBrackets)
			str += "}";
		return str;
	}

}

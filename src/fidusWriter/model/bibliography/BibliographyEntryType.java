package fidusWriter.model.bibliography;
/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role   This class contains some constants for mapping bibliography data between docx and fidus standards.   
 */
public class BibliographyEntryType {
	/**
	 * different types of resources in Fidus
	 * Please note position of the items is important
	 */
	private static String[] fidusTypes = {
			null,										//0 org.docx4j.bibliography.STSourceType.Misc
			"Article",									//1 org.docx4j.bibliography.STSourceType.ArticleInAPeriodical
			"Book",										//2 org.docx4j.bibliography.STSourceType.Book
			"Multi-volume book",						//3 org.docx4j.bibliography.STSourceType.Book
			"In book",									//4 org.docx4j.bibliography.STSourceType.BookSection
			"Book in book",								//5 org.docx4j.bibliography.STSourceType.BookSection
			"Supplemental material in a book",			//6 org.docx4j.bibliography.STSourceType.BookSection
			"Booklet",									//7 org.docx4j.bibliography.STSourceType.Misc
			"Collection",								//8 org.docx4j.bibliography.STSourceType.Misc
			"Multi-volume collection",					//9 org.docx4j.bibliography.STSourceType.Misc
			"In collection",							//10 org.docx4j.bibliography.STSourceType.Misc
			"Supplemental material in a collection",	//11 org.docx4j.bibliography.STSourceType.Misc
			"Manual",									//12 org.docx4j.bibliography.STSourceType.Report
			"Miscellany",								//13 org.docx4j.bibliography.STSourceType.Misc
			"Online resource",							//14 org.docx4j.bibliography.STSourceType.DocumentFromInternetSite
			"Patent",									//15 org.docx4j.bibliography.STSourceType.Patent
			"Periodical",								//16 org.docx4j.bibliography.STSourceType.ArticleInAPeriodical
			"Supplemental material in a periodical",	//17 org.docx4j.bibliography.STSourceType.ArticleInAPeriodical
			"Proceedings",								//18 org.docx4j.bibliography.STSourceType.ConferenceProceedings
			"Multi-volume proceedings",					//19 org.docx4j.bibliography.STSourceType.ConferenceProceedings
			"Article in a proceedings",					//20 org.docx4j.bibliography.STSourceType.ConferenceProceedings
			"Reference",								//21 org.docx4j.bibliography.STSourceType.JournalArticle
			"Multi-volume work of reference",			//22 org.docx4j.bibliography.STSourceType.JournalArticle
			"Article in a work of reference",			//23 org.docx4j.bibliography.STSourceType.JournalArticle
			"Report",									//24 org.docx4j.bibliography.STSourceType.Report
			"Thesis",									//org.docx4j.bibliography.STSourceType.Case
			"Unpublished",								//org.docx4j.bibliography.STSourceType.Misc
			"Magazine article",							//org.docx4j.bibliography.STSourceType.JournalArticle
			"Newspaper article",						//org.docx4j.bibliography.STSourceType.JournalArticle
			"Journal article",							//org.docx4j.bibliography.STSourceType.JournalArticle
			"Encyclopedia entry",						//org.docx4j.bibliography.STSourceType.ElectronicSource
			"Dictionary entry",							//org.docx4j.bibliography.STSourceType.ElectronicSource
			"Blog post",								//org.docx4j.bibliography.STSourceType.InternetSite
			"Forum post"								//org.docx4j.bibliography.STSourceType.InternetSite
	}; 
	/**
	 * Equivalent types in MS Word sources for fidus types
	 * Please note position of the items is important
	 */
	private static org.docx4j.bibliography.STSourceType[] equivalentDocxTypes = {
			org.docx4j.bibliography.STSourceType.MISC      // Default value
			,org.docx4j.bibliography.STSourceType.ARTICLE_IN_A_PERIODICAL
			,org.docx4j.bibliography.STSourceType.BOOK
			,org.docx4j.bibliography.STSourceType.BOOK
			,org.docx4j.bibliography.STSourceType.BOOK_SECTION
			,org.docx4j.bibliography.STSourceType.BOOK_SECTION
			,org.docx4j.bibliography.STSourceType.BOOK_SECTION
			,org.docx4j.bibliography.STSourceType.MISC
			,org.docx4j.bibliography.STSourceType.MISC
			,org.docx4j.bibliography.STSourceType.MISC
			,org.docx4j.bibliography.STSourceType.MISC
			,org.docx4j.bibliography.STSourceType.MISC
			,org.docx4j.bibliography.STSourceType.REPORT
			,org.docx4j.bibliography.STSourceType.MISC
			,org.docx4j.bibliography.STSourceType.DOCUMENT_FROM_INTERNET_SITE
			,org.docx4j.bibliography.STSourceType.PATENT
			,org.docx4j.bibliography.STSourceType.ARTICLE_IN_A_PERIODICAL
			,org.docx4j.bibliography.STSourceType.ARTICLE_IN_A_PERIODICAL
			,org.docx4j.bibliography.STSourceType.CONFERENCE_PROCEEDINGS
			,org.docx4j.bibliography.STSourceType.CONFERENCE_PROCEEDINGS
			,org.docx4j.bibliography.STSourceType.CONFERENCE_PROCEEDINGS
			,org.docx4j.bibliography.STSourceType.JOURNAL_ARTICLE
			,org.docx4j.bibliography.STSourceType.JOURNAL_ARTICLE
			,org.docx4j.bibliography.STSourceType.JOURNAL_ARTICLE
			,org.docx4j.bibliography.STSourceType.REPORT
			,org.docx4j.bibliography.STSourceType.CASE
			,org.docx4j.bibliography.STSourceType.MISC
			,org.docx4j.bibliography.STSourceType.JOURNAL_ARTICLE
			,org.docx4j.bibliography.STSourceType.INTERVIEW
			,org.docx4j.bibliography.STSourceType.JOURNAL_ARTICLE
			,org.docx4j.bibliography.STSourceType.ELECTRONIC_SOURCE
			,org.docx4j.bibliography.STSourceType.ELECTRONIC_SOURCE
			,org.docx4j.bibliography.STSourceType.INTERNET_SITE
			,org.docx4j.bibliography.STSourceType.INTERNET_SITE
	};
	/**
	 * Different possible types in MS Word
	 * Please note position of the items is important
	 */
	private static org.docx4j.bibliography.STSourceType[] docxTypes = {
			null,
			org.docx4j.bibliography.STSourceType.ART
			,org.docx4j.bibliography.STSourceType.ARTICLE_IN_A_PERIODICAL
			,org.docx4j.bibliography.STSourceType.BOOK
			,org.docx4j.bibliography.STSourceType.BOOK_SECTION
			,org.docx4j.bibliography.STSourceType.CASE
			,org.docx4j.bibliography.STSourceType.CONFERENCE_PROCEEDINGS
			,org.docx4j.bibliography.STSourceType.DOCUMENT_FROM_INTERNET_SITE
			,org.docx4j.bibliography.STSourceType.ELECTRONIC_SOURCE
			,org.docx4j.bibliography.STSourceType.FILM
			,org.docx4j.bibliography.STSourceType.INTERNET_SITE
			,org.docx4j.bibliography.STSourceType.INTERVIEW
			,org.docx4j.bibliography.STSourceType.JOURNAL_ARTICLE
			,org.docx4j.bibliography.STSourceType.MISC
			,org.docx4j.bibliography.STSourceType.PATENT
			,org.docx4j.bibliography.STSourceType.PERFORMANCE
			,org.docx4j.bibliography.STSourceType.REPORT
			,org.docx4j.bibliography.STSourceType.SOUND_RECORDING
	};
	/**
	 * Equivalent type in Fidus for Docx types
	 * Please note position of the items is important
	 */
	private static String[] equivalentFidusTypes = {
			null,
			"Miscellany",
			"Article",
			"Book",
			"In book",
			"Miscellany",
			"Proceedings",
			"Online resource",
			"Encyclopedia entry",
			"Miscellany",
			"Forum post",
			"Newspaper article",
			"Journal article",
			"Miscellany",
			"Patent",
			"Miscellany",
			"Report",
			"Miscellany"
	};
	/**
	 * Receives an index and returns the equivalent type in Docx
	 * @param index
	 * @return
	 */
	public static org.docx4j.bibliography.STSourceType getDocxType(Integer index){
		if(index!=null && index.intValue()<equivalentDocxTypes.length && index.intValue()>0)
			return equivalentDocxTypes[index.intValue()];
		return equivalentDocxTypes[0];
	}
	/**
	 * Returns the type for an index in Fidus files
	 * @param index
	 * @return
	 */
	public static String getType(Integer index){
		String str=null;
		if(index!=null && index.intValue()<fidusTypes.length && index.intValue()>0)
			str = fidusTypes[index.intValue()];
		return str;
	}
	/**
	 * Receives a type in Fidus and returns its index
	 * @param str
	 * @return
	 */
	public static Integer getFidusIndex(String str){
		for(int i=0;i<fidusTypes.length;i++){
			if(str.equalsIgnoreCase(fidusTypes[i]))
				return Integer.valueOf(i);
		}
		return null;
	}
	/**
	 * Receives a type in Docx and returns its index
	 * @param type
	 * @return
	 */
	public static Integer getDocxIndex(org.docx4j.bibliography.STSourceType type){
		for(int i=0;i<docxTypes.length;i++){
			if(type == docxTypes[i])
				return Integer.valueOf(i);
		}
		return null;
	}	
	/**
	 * Receives a Docx type and returns the equivalent type in Fidus
	 * @param type
	 * @return
	 */
	public static String getFidusType(org.docx4j.bibliography.STSourceType type){
		Integer index = getDocxIndex(type);
		String str=null;
		if(index!=null && index.intValue()<equivalentFidusTypes.length && index.intValue()>0)
			str = equivalentFidusTypes[index.intValue()];
		return str;
	}
	/**
	 * Receives a Docx type and returns the index of the equivalent type in Fidus
	 * @param type
	 * @return
	 */
	public static Integer getFidusTypeNumber(org.docx4j.bibliography.STSourceType type){
		String str=getFidusType(type);
		return str!=null? getFidusIndex(str) : new Integer(0);
	}
}

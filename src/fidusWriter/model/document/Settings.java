package fidusWriter.model.document;

import org.json.simple.JSONObject;

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class contains functions and properties to model the setting part
 *       of the document.json
 */
public class Settings {
	private Integer papersize = null; // Paper's width in Pixels
	private String citationstyle = null; // the identifier of the citationstyle
											// (installation specific)
	private String documentstyle = null; // the identifier of the documentstyle
											// (installation specific)
	private Boolean metadataSubtitle = null; // Whether or not subtitle fields
												// is enabled
	private Boolean metadataAuthors = null; // Whether or not authors field is
											// enabled
	private Boolean metadataAbstract = null; // Whether or not abstract field is
												// enabled
	private Boolean metadataKeywords = null; // Whether or not keywords field is
												// enabled
	// The getters and setters area

	public Integer getPapersize() {
		return papersize;
	}

	public void setPapersize(int papersize) {
		this.papersize = papersize;
	}

	public String getCitationstyle() {
		return citationstyle;
	}

	public void setCitationstyle(String citationstyle) {
		this.citationstyle = citationstyle;
	}

	public String getDocumentstyle() {
		return documentstyle;
	}

	public void setDocumentstyle(String documentstyle) {
		this.documentstyle = documentstyle;
	}

	public Boolean isMetadataSubtitle() {
		return metadataSubtitle;
	}

	public String getMetadataSubtitle() {
		return metadataSubtitle.booleanValue() ? "true" : "false";
	}

	public void setMetadataSubtitle(boolean metadataSubtitle) {
		this.metadataSubtitle = metadataSubtitle;
	}

	public Boolean isMetadataAuthors() {
		return metadataAuthors;
	}

	public String getMetadataAuthors() {
		return metadataAuthors.booleanValue() ? "true" : "false";
	}

	public void setMetadataAuthors(boolean metadataAuthors) {
		this.metadataAuthors = metadataAuthors;
	}

	public Boolean isMetadataAbstract() {
		return metadataAbstract;
	}

	public String getMetadataAbstract() {
		return metadataAbstract.booleanValue() ? "true" : "false";
	}

	public void setMetadataAbstract(boolean metadataAbstract) {
		this.metadataAbstract = metadataAbstract;
	}

	public Boolean isMetadataKeywords() {
		return metadataKeywords;
	}

	public String getMetadataKeywords() {
		return metadataKeywords.booleanValue() ? "true" : "false";
	}

	public void setMetadataKeywords(boolean metadataKeywords) {
		this.metadataKeywords = metadataKeywords;
	}

	// end of the getters and setters area
	/**
	 * Creates Settings object based on the JSON object from the Fidus file
	 * 
	 * @param settings
	 */
	public Settings(JSONObject settings) {

		Boolean metadataAuthors = new Boolean(false), metadataKeywords = new Boolean(false),
				metadataAbstract = new Boolean(false), metadataSubtitle = new Boolean(false);
		Integer papersize = null;
		String documentstyle = null, citationstyle = null;
		// Reading data
		if (settings.get("metadata-authors") != null)
			metadataAuthors = Boolean.parseBoolean(settings.get("metadata-authors").toString());
		if (settings.get("metadata-keywords") != null)
			metadataKeywords = Boolean.parseBoolean(settings.get("metadata-keywords").toString());
		if (settings.get("metadata-abstract") != null)
			metadataAbstract = Boolean.parseBoolean(settings.get("metadata-abstract").toString());
		if (settings.get("metadata-subtitle") != null)
			metadataSubtitle = Boolean.parseBoolean(settings.get("metadata-subtitle").toString());
		if (settings.get("papersize") != null)
			papersize = Integer.parseInt(settings.get("papersize").toString());
		if (settings.get("documentstyle") != null)
			documentstyle = settings.get("documentstyle").toString();
		if (settings.get("citationstyle") != null)
			citationstyle = settings.get("citationstyle").toString();
		// Set values
		this.papersize = papersize;
		this.citationstyle = citationstyle;
		this.documentstyle = documentstyle;
		this.metadataSubtitle = metadataSubtitle;
		this.metadataAuthors = metadataAuthors;
		this.metadataAbstract = metadataAbstract;
		this.metadataKeywords = metadataKeywords;
	}

	/**
	 * Creates an empty object with initial default data for filling with data
	 * from docx file.
	 */
	public Settings() {
		this(1117, "apa", "elephant");
	}

	/**
	 * Creates the Settings object based on passed data
	 * 
	 * @param papersize
	 *            : the paper size
	 * @param citationstyle
	 *            : the citation style
	 * @param documentstyle
	 *            : the document style
	 */
	public Settings(Integer papersize, String citationstyle, String documentstyle) {
		super();
		this.papersize = papersize;
		this.citationstyle = citationstyle;
		this.documentstyle = documentstyle;
		this.metadataSubtitle = new Boolean(false);
		this.metadataAuthors = new Boolean(false);
		this.metadataAbstract = new Boolean(false);
		this.metadataKeywords = new Boolean(false);
	}

	/**
	 * This function converts the object to the JSON string for storing in
	 * document.json file
	 */
	public String toString() {
		String str = "{";
		boolean flag = false;
		if (this.isMetadataAuthors() != null) {
			flag = true;
			str += "\"metadata-authors\":" + this.getMetadataAuthors();
		}
		if (this.isMetadataKeywords() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"metadata-keywords\":" + this.getMetadataKeywords();
		}
		if (this.getCitationstyle() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"citationstyle\": \""
					+ this.getCitationstyle().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.isMetadataAbstract() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"metadata-abstract\":" + this.getMetadataAbstract();
		}
		if (this.isMetadataSubtitle() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"metadata-subtitle\":" + this.getMetadataSubtitle();
		}
		if (this.getPapersize() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"papersize\":" + this.getPapersize().intValue();
		}
		if (this.getDocumentstyle() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"documentstyle\": \""
					+ this.getDocumentstyle().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		str += "}";
		return str;
	}
}

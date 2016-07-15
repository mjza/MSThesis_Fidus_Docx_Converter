package fidusWriter.model.document;

import java.util.ArrayList;

import org.json.simple.JSONObject;

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class contains
 */
public class MetaData {
	private NodeJson title = null; // The HTML of the title of the document in
									// NodeJson format
	private NodeJson subtitle = null; // The HTML of the subtitle of the
										// document in NodeJson format
	private NodeJson authors = null; // The HTML of the author list of the
										// document in NodeJson format
	private NodeJson _abstract = null; // The HTML of the abstract of the
										// document in NodeJson format
	private NodeJson keywords = null; // The HTML of the keywords list of the
										// document in NodeJson format
	// The getters and setters area

	public NodeJson getTitle() {
		return title;
	}

	public void setTitle(NodeJson title) {
		this.title = title;
	}

	public void setTitle(String title) {
		if (this.title != null && (this.title.getC() != null && this.title.getC().size() == 0)) {
			this.title.getC().add(new TextNode(title));
		}
	}

	public NodeJson getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(NodeJson subtitle) {
		this.subtitle = subtitle;
	}

	public void setSubtitle(String subtitle) {
		if (this.subtitle != null && (this.subtitle.getC() != null && this.subtitle.getC().size() == 0)) {
			this.subtitle.getC().add(new TextNode(subtitle));
		}
	}

	public NodeJson getAuthors() {
		return authors;
	}

	public void setAuthors(NodeJson authors) {
		this.authors = authors;
	}

	public void setAuthors(String authors) {
		if (this.authors != null && (this.authors.getC() != null && this.authors.getC().size() == 0)) {
			this.authors.getC().add(new TextNode(authors));
		}
	}

	public NodeJson getAbstract() {
		return _abstract;
	}

	public void setAbstract(NodeJson _abstract) {
		this._abstract = _abstract;
	}

	public void setAbstract(String _abstract) {
		if (this._abstract != null && (this._abstract.getC() != null && this._abstract.getC().size() > 0)) {
			NodeJson node = (NodeJson) this._abstract.getC().get(0);
			if (node != null) {
				if (node.getC() != null)
					node.getC().add(new TextNode(_abstract));
				else {
					node.setC(new ArrayList<Child>());
					node.getC().add(new TextNode(_abstract));
				}
			}
		}
	}

	public NodeJson getKeywords() {
		return keywords;
	}

	public void setKeywords(NodeJson keywords) {
		this.keywords = keywords;
	}

	public void setKeywords(String keywords) {
		if (this.keywords != null && (this.keywords.getC() != null && this.keywords.getC().size() == 0)) {
			this.keywords.getC().add(new TextNode(keywords));
		}
	}

	// end of the getters and setters area
	/**
	 * Creates Metadata from the passed JSON object from the fidus file.
	 * 
	 * @param metaData
	 */
	public MetaData(JSONObject metaData) {
		if (metaData != null) {
			if (metaData.get("title") != null) {
				JSONObject title = ((JSONObject) metaData.get("title"));
				this.setTitle(new NodeJson(title));
			}
			if (metaData.get("subtitle") != null) {
				JSONObject subtitle = ((JSONObject) metaData.get("subtitle"));
				this.setSubtitle(new NodeJson(subtitle));
			}
			if (metaData.get("authors") != null) {
				JSONObject authors = ((JSONObject) metaData.get("authors"));
				this.setAuthors(new NodeJson(authors));
			}
			if (metaData.get("abstract") != null) {
				JSONObject _abstract = ((JSONObject) metaData.get("abstract"));
				this.setAbstract(new NodeJson(_abstract));
			}
			if (metaData.get("keywords") != null) {
				JSONObject keywords = ((JSONObject) metaData.get("keywords"));
				this.setKeywords(new NodeJson(keywords));
			}
		}
	}

	/**
	 * Creates an initial MetaData object to be filled with data from Docx file.
	 */
	public MetaData() {
		this.setTitle(new NodeJson(NodeType.div, new Attribute("id", "document-title"), null));
		this.setSubtitle(new NodeJson(NodeType.div, new Attribute("id", "document-subtitle"), null));
		this.setAuthors(new NodeJson(NodeType.div, new Attribute("id", "metadata-authors"), null));
		this.setAbstract(new NodeJson(NodeType.div, new Attribute("id", "metadata-abstract"),
				new NodeJson(new NodeName(NodeType.p), null, null)));
		this.setKeywords(new NodeJson(NodeType.div, new Attribute("id", "metadata-keywords"), null));
	}

	/**
	 * This function converts the object to the JSON string for storing in
	 * document.json file
	 */
	public String toString() {
		String str = "{";
		boolean flag = false;
		if (this.getTitle() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"title\":" + this.getTitle().toString();
		}
		if (this.getSubtitle() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"subtitle\":" + this.getSubtitle().toString();
		}
		if (this.getAuthors() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"authors\":" + this.getAuthors().toString();
		}
		if (this.getAbstract() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"abstract\":" + this.getAbstract().toString();
		}
		if (this.getKeywords() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"keywords\":" + this.getKeywords().toString();
		}
		str += "}";
		return str;
	}

	/**
	 * Tests if the Title exist or not
	 * 
	 * @return true if the Title exists
	 */
	public boolean hasTitle() {
		if (this.title != null && this.title.getC() != null && this.title.getC().size() > 0)
			return true;
		else
			return false;
	}

	/**
	 * Tests existance of Subtitle
	 * 
	 * @return true if the subtitle exists
	 */
	public boolean hasSubtitle() {
		if (this.subtitle != null && this.subtitle.getC() != null && this.subtitle.getC().size() > 0)
			return true;
		else
			return false;
	}

	/**
	 * Tests if the Authors exists or not
	 * 
	 * @return true if Authors exists
	 */
	public boolean hasAuthors() {
		if (this.authors != null && this.authors.getC() != null && this.authors.getC().size() > 0)
			return true;
		else
			return false;
	}

	/**
	 * Tests if the Abstracts exists or not
	 * 
	 * @return true if the Abstracts exists
	 */
	public boolean hasAbstract() {
		if (this._abstract != null && this._abstract.getC() != null && this._abstract.getC().size() > 1)
			return true;
		else if (this._abstract != null && this._abstract.getC() != null && this._abstract.getC().size() > 0
				&& this._abstract.getC().get(0).getType() == ChildrenTypes.element
				&& ((NodeJson) this._abstract.getC().get(0)).getChildrenNumber() > 0)
			return true;
		else
			return false;
	}

	/**
	 * Tests if the Keywords exists or not
	 * 
	 * @return true if the Keywords exists
	 */
	public boolean hasKeywords() {
		if (this.keywords != null && this.keywords.getC() != null && this.keywords.getC().size() > 0)
			return true;
		else
			return false;
	}

}

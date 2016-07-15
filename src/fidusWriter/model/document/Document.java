package fidusWriter.model.document;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class contains functions and properties for moddeling the document.json file of the fidus file.
 */
public class Document {
	private Settings settings = null;
	private Integer commentVersion = null; // A version numbering system keeping
											// track of changes to the comments
	private String title = null; // Title of the document (string).
	private AccessRights accessRights = null; // list of access rights
	private Comments comments = null;
	private Integer version = null;
	private Owner owner = null;
	private Integer id = null;
	private Contents contents = null;
	private MetaData metaData = null;
	private String hash = null; // A hash value of the document. This field will
								// be recalculated upon import
	// The getters and setters area

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public Integer getCommentVersion() {
		return commentVersion;
	}

	public void setCommentVersion(int commentVersion) {
		this.commentVersion = commentVersion;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public AccessRights getAccessRights() {
		return accessRights;
	}

	public void setAccessRights(AccessRights accessRights) {
		this.accessRights = accessRights;
	}

	public Comments getComments() {
		return comments;
	}

	public void setComments(Comments comments) {
		this.comments = comments;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Contents getContents() {
		return contents;
	}

	public void setContents(Contents contents) {
		this.contents = contents;
	}

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	// end of the getters and setters area
	/**
	 * Create an empty Document to be filled from Fidus file
	 */
	public Document() {
		super();
	}

	/**
	 * Create an initial point file with default values to be fileed with data
	 * from docx file.
	 * 
	 * @param title
	 */
	public Document(String title) {
		super();
		this.setSettings(new Settings());
		this.setCommentVersion(10);
		this.setTitle(title);
		this.setAccessRights(new AccessRights());
		this.setComments(new Comments());
		this.setVersion(0);
		this.setOwner(new Owner());
		this.setId(1);
		this.setContents(new Contents(NodeType.div, new Attribute("id", "document-contents"),
				new NodeJson(new NodeName(NodeType.p), null, null)));
		this.setMetaData(new MetaData());
		this.setHash("");
	}

	/**
	 * Fills the object by the data from the passed Fidus file's path
	 * 
	 * @param path
	 */
	public void importFromFile(String path) {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(path));
			JSONObject jsonObject = (JSONObject) obj;
			//
			if (jsonObject.get("comment_version") != null) {
				int commentVersion = Integer.parseInt(jsonObject.get("comment_version").toString());
				this.setCommentVersion(commentVersion);
			}
			//
			if (jsonObject.get("title") != null) {
				String title = jsonObject.get("title").toString();
				this.setTitle(title);
			}
			//
			if (jsonObject.get("version") != null) {
				int version = Integer.parseInt(jsonObject.get("version").toString());
				this.setVersion(version);
			}
			//
			if (jsonObject.get("id") != null) {
				int id = Integer.parseInt(jsonObject.get("id").toString());
				this.setId(id);
			}
			//
			if (jsonObject.get("hash") != null) {
				String hash = jsonObject.get("hash").toString();
				this.setHash(hash);
			}
			// convert settings
			if (jsonObject.get("settings") != null) {
				JSONObject settings = ((JSONObject) jsonObject.get("settings"));
				this.setSettings(new Settings(settings));
			}
			// convert comments
			if (jsonObject.get("comments") != null) {
				JSONObject comments = ((JSONObject) jsonObject.get("comments"));
				this.setComments(new Comments(comments));
			}
			// convert access rights
			if (jsonObject.get("access_rights") != null) {
				JSONArray accessRights = ((JSONArray) jsonObject.get("access_rights"));
				this.setAccessRights(new AccessRights(accessRights));
			}
			// convert Owner & team members
			if (jsonObject.get("owner") != null) {
				JSONObject owner = ((JSONObject) jsonObject.get("owner"));
				this.setOwner(new Owner(owner));
			}
			// convert content
			if (jsonObject.get("contents") != null) {
				JSONObject contents = ((JSONObject) jsonObject.get("contents"));
				this.setContents(new Contents(contents));
			}
			// convert metadata
			if (jsonObject.get("metadata") != null) {
				JSONObject metaData = ((JSONObject) jsonObject.get("metadata"));
				this.setMetaData(new MetaData(metaData));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This function converts the object to the JSON string for storing in
	 * document.json file
	 */
	public String toString() {
		String str = "{";
		if (this.getSettings() != null)
			str += "\"settings\":" + this.getSettings().toString() + ",";
		if (this.getCommentVersion() != null)
			str += "\"comment_version\":" + this.getCommentVersion().intValue() + ",";
		if (this.getTitle() != null)
			str += "\"title\":\"" + this.getTitle() + "\",";
		if (this.getAccessRights() != null)
			str += "\"access_rights\":" + this.getAccessRights().toString() + ",";
		if (this.getComments() != null)
			str += "\"comments\":" + this.getComments().toString() + ",";
		if (this.getVersion() != null)
			str += "\"version\":" + this.getVersion().intValue() + ",";
		if (this.getOwner() != null)
			str += "\"owner\":" + this.getOwner().toString() + ",";
		if (this.getId() != null)
			str += "\"id\":" + this.getId().intValue() + ",";
		if (this.getContents() != null)
			str += "\"contents\":" + this.getContents().toString() + ",";
		if (this.getMetaData() != null)
			str += "\"metadata\":" + this.getMetaData().toString() + ",";
		if (this.getHash() != null)
			str += "\"hash\":\"" + this.getHash() + "\"";
		str += "}";
		return str;
	}
}

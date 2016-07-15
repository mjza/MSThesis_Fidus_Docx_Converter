package fidusWriter.model.document;

import org.json.simple.JSONObject;

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class contains functions and attributes to model the AccessRight obect
 */
public class AccessRight {
	private String userName = null;
	private Integer userId = null;
	private String avatar = null;
	private Integer documentId = null;
	private Rights rights = null;

	// The getters and setters area
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Integer getDocumentId() {
		return documentId;
	}

	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}

	public Rights getRights() {
		return rights;
	}

	public void setRights(Rights rights) {
		this.rights = rights;
	}

	// end of the getters and setters area
	/**
	 * Receives data of the AccessRight object and fills the variables
	 * 
	 * @param userName
	 *            : username of the author
	 * @param userId
	 *            : id of the author
	 * @param avatar
	 *            : path of the author's avatar
	 * @param documentId
	 *            : the id of the document
	 * @param rights
	 *            : the type of the right w: write, r:read
	 */
	public AccessRight(String userName, int userId, String avatar, int documentId, Rights rights) {
		super();
		this.userName = userName;
		this.userId = userId;
		this.avatar = avatar;
		this.documentId = documentId;
		this.rights = rights;
	}

	/**
	 * Receives a JSON object and converts it to the AccessRight object
	 * 
	 * @param accessRight
	 */
	public AccessRight(JSONObject accessRight) {
		if (accessRight.get("user_name") != null) {
			this.setUserName(accessRight.get("user_name").toString());
		}
		if (accessRight.get("user_id") != null) {
			this.setUserId(Integer.parseInt(accessRight.get("user_id").toString()));
		}
		if (accessRight.get("avatar") != null) {
			this.setAvatar(accessRight.get("avatar").toString());
		}
		if (accessRight.get("document_id") != null) {
			this.setDocumentId(Integer.parseInt(accessRight.get("document_id").toString()));
		}
		if (accessRight.get("rights") != null) {
			String right = accessRight.get("rights").toString();
			Rights rights = right.toLowerCase().compareTo("r") == 0 ? Rights.r : Rights.w;
			this.setRights(rights);
		}
	}

	/**
	 * This function converts the object to the JSON string for storing in
	 * document.json file
	 */
	public String toString() {
		String str = "{";
		boolean flag = false;
		if (this.getUserName() != null) {
			flag = true;
			str += "\"user_name\":\""
					+ this.getUserName().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getUserId() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"user_id\":" + this.getUserId().intValue();
		}
		if (this.getAvatar() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"avatar\":\"" + this.getAvatar().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getDocumentId() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"document_id\":" + this.getDocumentId().intValue();
		}
		if (this.getRights() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"rights\":\""
					+ this.getRights().name().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		str += "}";
		return str;
	}
}

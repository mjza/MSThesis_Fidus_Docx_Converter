package fidusWriter.model.document;

import org.json.simple.JSONObject;
/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role   This class contains functions and properties to model the team members of the document.json   
 */
public class TeamMember {
	private Integer id = null;
    private String avatar = null;
    private String name = null;
    // The getters and setters area
	public Integer getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	// end of the getters and setters area
	/**
	 * This constructor creates the TeamMember based on the JSON object  
	 * @param tm
	 */
	public TeamMember(JSONObject tm) {
		if(tm.get("id") != null)
			this.setId(Integer.parseInt(tm.get("id").toString()));
		//
		if(tm.get("avatar") != null)
			this.setAvatar(tm.get("avatar").toString());
		//
		if(tm.get("name") != null)
			this.setName(tm.get("name").toString());
	}
	/**
	 * This function converts the object to the JSON string for storing in document.json file
	 */
	public String toString(){
		String str="{";
		boolean flag = false;
		if(this.getId()!=null){
			flag = true;
			str+="\"id\":"+this.getId().intValue();
		}
		if(this.getAvatar()!=null){
			if(flag==true)
				str += ",";
			flag = true;
			str+="\"avatar\":\""+this.getAvatar().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")+"\"";
		}
		if(this.getName()!=null){
			if(flag==true)
				str += ",";
			flag = true;
			str+="\"name\":\""+this.getName().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")+"\"";
		}
		str+="}";
		return str;
	}
}

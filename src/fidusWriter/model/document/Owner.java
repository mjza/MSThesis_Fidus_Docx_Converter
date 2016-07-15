package fidusWriter.model.document;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role   This class contains functions and properties for model the Owner of the contents of the document.json    
 */
public class Owner {
	private ArrayList<TeamMember> teamMembers = null; //new ArrayList<TeamMember>();
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
	public ArrayList<TeamMember> getTeamMembers() {
		return teamMembers;
	}
	public void setTeamMembers(ArrayList<TeamMember> teamMembers) {
		this.teamMembers = teamMembers;
	}
 	// end of the getters and setters area 
    /**
     * Creates the object based on the passed JSON object from the Fidus file
     * @param owner
     */
	public Owner(JSONObject owner) {
		if(owner != null)
		{
			if(owner.get("id") != null)
				this.setId(Integer.parseInt(owner.get("id").toString()));
			if(owner.get("avatar") != null)
				this.setAvatar(owner.get("avatar").toString());
			if(owner.get("name") != null)
				this.setName(owner.get("name").toString());
			if(owner.get("team_members") != null)
			{
				this.teamMembers = new ArrayList<TeamMember>();
				JSONArray teamMembers = ( (JSONArray) owner.get("team_members") );
				for(int i=0; i<teamMembers.size();i++)
				{
					JSONObject tm = ( (JSONObject) teamMembers.get(i) );
					TeamMember teamMember = new TeamMember(tm);
					this.teamMembers.add(teamMember);
				}// end of for
			}
		}
	}
	/**
	 * Creates an empty object for initiate from the docx file.
	 */
	public Owner(){
		this.setTeamMembers(new ArrayList<TeamMember>());
		this.setId(1);
		this.setAvatar("/static/img/default_avatar.png");
		this.setName("MJZ_Convertor");
	}
	/**
	 * This function converts the object to the JSON string for storing in document.json file
	 */
	public String toString(){
		String str="{";
		boolean flag = false;
		if(this.getTeamMembers()!=null){
			if(flag==true)
				str += ",";
			flag = true;
			str+="\"team_members\":[";
			for(int i=0;i<this.teamMembers.size();i++){
				if(i>0)
					str += ",";
				str+=this.teamMembers.get(i).toString();
			}
			str+="]";
		}
		if(this.getId()!=null){
			if(flag==true)
				str += ",";
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

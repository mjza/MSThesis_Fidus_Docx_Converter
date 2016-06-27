package fidusWriter.model.document;

import org.json.simple.JSONObject;

public class TeamMember {
	private Integer id = null;
    private String avatar = null;
    private String name = null;
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
			str+="\"avatar\":\""+this.getAvatar()+"\"";
		}
		if(this.getName()!=null){
			if(flag==true)
				str += ",";
			flag = true;
			str+="\"name\":\""+this.getName()+"\"";
		}
		str+="}";
		return str;
	}
}

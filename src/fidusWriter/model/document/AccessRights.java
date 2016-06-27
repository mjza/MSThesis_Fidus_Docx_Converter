package fidusWriter.model.document;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class AccessRights {
	private ArrayList<AccessRight> accessRights = null;
	
	public ArrayList<AccessRight> getAccessRights() {
		return accessRights;
	}
	public void setAccessRights(ArrayList<AccessRight> accessRights) {
		this.accessRights = accessRights;
	}
	public AccessRights(JSONArray accessRights) {
		if(accessRights != null){
			this.accessRights = new ArrayList<AccessRight>();
			for(int i=0; i<accessRights.size(); i++){
				JSONObject accessRight = ( (JSONObject) accessRights.get(i) );
				this.accessRights.add(new AccessRight(accessRight));
			}
		}
	}
	public AccessRights(){
		this.accessRights = new ArrayList<AccessRight>();
	}
	public String toString(){
		String str = null;
		if(this.getAccessRights()!=null){
			str = "[";
			for(int i=0;i<this.accessRights.size();i++){
				if(i>0)
					str += ",";
				str += this.accessRights.get(i).toString();
			}
			str += "]";	
		}
		return str;
	}
	
	
}

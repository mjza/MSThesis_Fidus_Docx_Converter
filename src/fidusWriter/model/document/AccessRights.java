package fidusWriter.model.document;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role   This class contains functions to model the AccessRights objects    
 */
public class AccessRights {
	private ArrayList<AccessRight> accessRights = null;
    // The getters and setters area
	public ArrayList<AccessRight> getAccessRights() {
		return accessRights;
	}
	public void setAccessRights(ArrayList<AccessRight> accessRights) {
		this.accessRights = accessRights;
	}
 	// end of the getters and setters area 	
	/**
	 * Receives an JSON object and converts it to an AccessRights object
	 * @param accessRights
	 */
	public AccessRights(JSONArray accessRights) {
		if(accessRights != null){
			this.accessRights = new ArrayList<AccessRight>();
			for(int i=0; i<accessRights.size(); i++){
				JSONObject accessRight = ( (JSONObject) accessRights.get(i) );
				this.accessRights.add(new AccessRight(accessRight));
			}
		}
	}
	/**
	 * Creates an empty object
	 */
	public AccessRights(){
		this.accessRights = new ArrayList<AccessRight>();
	}
	/**
	 * This function converts the object to the JSON string for storing in document.json file
	 */
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

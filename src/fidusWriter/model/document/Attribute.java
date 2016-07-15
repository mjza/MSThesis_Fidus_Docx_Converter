package fidusWriter.model.document;
/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role   This class contains functions and attributes to model the attribute of the nodes   
 */
public class Attribute {
	private String name;
	private String value;
    // The getters and setters area
	public String getName() {
		return name;
	}
	public String getValue() {
		return value;
	}
 	// end of the getters and setters area 
	/**
	 * The construcor that receives the variables and forms the object
	 * @param name
	 * @param value
	 */
	public Attribute(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	/**
	 * This function converts the object to the JSON string for storing in document.json file
	 */
	public String toString(){
		String str = "[";
		str += "\""+this.getName()+"\", \""+this.getValue().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")+"\"";
		str += "]";
		return str;
	}
}

package fidusWriter.model.document;
/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role   This class contains functions and properties to model nn of JSON nodes   
 */
public class NodeName {
	private String name = null;
	private NodeType type = null;
    // The getters and setters area
	public String getName() {
		return this.name;
	}
	public NodeType getType() {
		return type;
	}
	public void setType(NodeType type) {
		this.type = type;
	}
 	// end of the getters and setters area
	/**
	 * Creates the NodeName based on NodeType
	 * @param type
	 */
	public NodeName(NodeType type) {
		super();
		this.setType(type); // will set the name also
		// Set suitable name based on the type
		this.name = this.type.name().toUpperCase();
	}
	/**
	 * Creates the NodeName based on its name
	 * @param name
	 */
	public NodeName(String name) {
		super();
		this.name = name;
		for (NodeType type : NodeType.values()) {
			if(name.compareToIgnoreCase(type.name()) == 0){
				this.setType(type);
				break;
			}
		 }
	}
	
}

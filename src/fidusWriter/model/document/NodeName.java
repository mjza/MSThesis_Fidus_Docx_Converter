package fidusWriter.model.document;

public class NodeName {
	private String name = null;
	private NodeType type = null;
	
	public String getName() {
		return this.name;
	}
	public NodeType getType() {
		return type;
	}
	public void setType(NodeType type) {
		this.type = type;
	}
	public NodeName(NodeType type) {
		super();
		this.setType(type); // will set the name also
		// Set suitable name based on the type
		this.name = this.type.name().toUpperCase();
	}
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

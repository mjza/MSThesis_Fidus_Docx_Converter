package fidusWriter.model.document;

public class Child {
	// A class that gives this ability to save different type of children in c element of the NODE_JSON;
	protected ChildrenTypes type = null;
	protected NodeJson parent = null;
	protected int depth = 1;
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public ChildrenTypes getType() {
		return type;
	}
	protected Child(){
		super();
		this.type = ChildrenTypes.element;
	}
	public NodeJson getParent() {
		return parent;
	}
	public void setParent(NodeJson parent) {
		this.parent = parent;
	}
	public void setType(ChildrenTypes type) {
		this.type = type;
	}
	
}

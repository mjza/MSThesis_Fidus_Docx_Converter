package fidusWriter.model.document;

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class gives this ability to save different type of children in c
 *       element of the NODE_JSON;
 */
public class Child {
	protected ChildrenTypes type = null;
	protected NodeJson parent = null;
	protected int depth = 1;

	// The getters and setters area
	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public ChildrenTypes getType() {
		return type;
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

	// end of the getters and setters area
	/**
	 * The constructor.
	 */
	protected Child() {
		super();
		this.type = ChildrenTypes.element;
	}
}

package fidusWriter.model.document;

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class contains
 */
public class TextNode extends Child {
	private String t; // The text contents of the text node.

	/**
	 * @return the actual text
	 */
	public String getT() {
		return t;
	}

	/**
	 * Set the text node value
	 * 
	 * @param t
	 */
	public void setT(String t) {
		this.t = t;
	}

	/**
	 * Adds a line to of text to the node
	 * 
	 * @param t
	 */
	public void addLine(String t) {
		this.t += "\n" + t;
	}

	/**
	 * The constructor that receives text and assigns the text node value
	 * @param t
	 */
	public TextNode(String t) {
		super();
		this.t = t;
		this.type = ChildrenTypes.textnode;
	}

	/**
	 * This function converts the object to the JSON string for storing in
	 * document.json file
	 */
	public String toString() {
		String str = "{\"t\":\"" + this.getT().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"}";
		return str;
	}
}

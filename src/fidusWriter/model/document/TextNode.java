package fidusWriter.model.document;

public class TextNode extends Child {
	private String t; // The text contents of the text node.

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}
	public void addLine(String t) {
		this.t += "\n"+t;
	}
	public TextNode(String t) {
		super();
		this.t = t;
		this.type = ChildrenTypes.textnode;
	}
	
	public String toString(){
		String str ="{\"t\":\""+this.getT().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")+"\"}";
		return str;
	}
}

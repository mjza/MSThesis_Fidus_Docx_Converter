package fidusWriter.model.document;

public class Attribute {
	private String name;
	private String value;
	public String getName() {
		return name;
	}
	public String getValue() {
		return value;
	}
	public Attribute(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	public String toString(){
		String str = "[";
		str += "\""+this.getName()+"\", \""+this.getValue()+"\"";
		str += "]";
		return str;
	}
}

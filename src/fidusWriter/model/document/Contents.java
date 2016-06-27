package fidusWriter.model.document;

import java.util.ArrayList;

import org.json.simple.JSONObject;

public class Contents extends NodeJson {
	
	public Contents(JSONObject contents) {
		super(contents);
	}
	public Contents(NodeName name, ArrayList<Attribute> attributes, ArrayList<Child> children){
		super(name,attributes,children);
	}
	public Contents(NodeType type, Attribute attribute, Child child){
		super(type,attribute,child);
	}
}

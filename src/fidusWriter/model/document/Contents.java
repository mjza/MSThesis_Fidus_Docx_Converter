package fidusWriter.model.document;

import java.util.ArrayList;

import org.json.simple.JSONObject;
/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role   This class contains functions for modeling the Contents part of the document.json   
 */
public class Contents extends NodeJson {
	/**
	 * The constructor for converting Fidus file to the Java model.
	 * @param contents
	 */
	public Contents(JSONObject contents) {
		super(contents);
	}
	/**
	 * Constructor for initiating the empty contents for converting from Docx
	 * @param name
	 * @param attributes
	 * @param children
	 */
	public Contents(NodeName name, ArrayList<Attribute> attributes, ArrayList<Child> children){
		super(name,attributes,children);
	}
	/**
	 * Constructor for initiating the empty contents for converting from Docx
	 * @param name
	 * @param attributes
	 * @param children
	 */
	public Contents(NodeType type, Attribute attribute, Child child){
		super(type,attribute,child);
	}
}

package fidusWriter.model.document;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class contains functions and properties for model the NodeJson
 *       elements of the contents of the document.json file.
 */
public class NodeJson extends Child {
	private NodeName nn = null; // The name of the HTML elements.
	private ArrayList<Attribute> a = null;
	private ArrayList<Child> c = null; // A list of the elements and textnode
										// children in NODE_JSON format.
	private Attribute recentAttribute = null;
	private BigInteger indent = new BigInteger("0", 10);

	// The getters and setters area
	public BigInteger getIndent() {
		return indent;
	}

	public void setIndent(BigInteger indent) {
		this.indent = indent;
	}

	public NodeName getNn() {
		return nn;
	}

	public void setNn(NodeName nn) {
		this.nn = nn;
	}

	public ArrayList<Attribute> getA() {
		return a;
	}

	public void setA(ArrayList<Attribute> a) {
		this.a = a;
	}

	public ArrayList<Child> getC() {
		return c;
	}

	public void setC(ArrayList<Child> c) {
		this.c = c;
	}

	public Attribute getRecentAttribute() {
		return recentAttribute;
	}

	public void setRecentAttribute(Attribute recentAttribute) {
		this.recentAttribute = recentAttribute;
	}
	// end of the getters and setters area

	/**
	 * Receives the JSON object of the contents and extract all of its child
	 * recursively. Also Creates the tree object of the Node objets
	 * 
	 * @param contents
	 */
	public NodeJson(JSONObject contents) {
		this.a = new ArrayList<Attribute>();
		this.c = new ArrayList<Child>();
		if (contents.get("nn") != null)
			this.setNn(new NodeName(contents.get("nn").toString()));
		if (contents.get("a") != null) {
			JSONArray attributes = ((JSONArray) contents.get("a"));
			for (int i = 0; i < attributes.size(); i++) {
				JSONArray attribute = ((JSONArray) attributes.get(i));
				String atName = attribute.get(0).toString();
				String atValue = attribute.get(1).toString();
				Attribute att = new Attribute(atName, atValue);
				this.a.add(att);
			}
		} // end if
		if (contents.get("c") != null) {
			JSONArray cArr = ((JSONArray) contents.get("c"));
			for (int i = 0; i < cArr.size(); i++) {
				JSONObject childObj = ((JSONObject) cArr.get(i));
				if (childObj.get("t") != null) {
					TextNode t = new TextNode(childObj.get("t").toString());
					this.c.add(t);
					this.setDepth(1);
				} else {
					NodeJson element = new NodeJson(childObj);
					this.c.add(element);
					this.setDepth(element.getDepth() + 1);
				}
			}
		}
	}

	/**
	 * Creates an object based on a list of Children and a list of Attributes
	 * 
	 * @param name
	 * @param attributes
	 * @param child
	 */
	public NodeJson(NodeName name, ArrayList<Attribute> attributes, ArrayList<Child> child) {
		super();
		this.nn = name;
		this.a = attributes;
		if (this.a == null)
			this.a = new ArrayList<Attribute>();
		this.c = child;
		if (this.c == null)
			this.c = new ArrayList<Child>();
	}

	/**
	 * Creates the object based on a single attribute and a single child
	 * 
	 * @param type
	 * @param attribute
	 * @param child
	 */
	public NodeJson(NodeType type, Attribute attribute, Child child) {
		super();
		this.nn = new NodeName(type);
		this.a = new ArrayList<Attribute>();
		this.c = new ArrayList<Child>();
		if (attribute != null)
			this.a.add(attribute);
		if (child != null)
			this.c.add(child);
	}

	/**
	 * Creates the object with its type and empty attributes and children
	 * 
	 * @param type
	 */
	public NodeJson(NodeType type) {
		super();
		this.nn = new NodeName(type);
		this.a = new ArrayList<Attribute>();
		this.c = new ArrayList<Child>();
	}

	/**
	 * Calculates depth of the element from the root node
	 */
	public int getDepth() {
		if (this.getC() != null) {
			int max = 0;
			for (int i = 0; i < this.c.size(); i++)
				if (this.c.get(i).getDepth() > max)
					max = this.c.get(i).getDepth();
			this.setDepth(max); // It checks if the max > this.depth then will
								// set the depth to new value.
		}
		return this.depth;
	}

	/**
	 * set the depth of the node
	 */
	public void setDepth(int depth) {
		if (this.depth < depth)
			this.depth = depth;
	}

	/**
	 * This function converts the object to the JSON string for storing in
	 * document.json file
	 */
	public String toString() {
		String str = "{";
		boolean putCamma = false;
		//
		if (this.getNn() != null) {
			str += "\"nn\":\"" + this.getNn().getName().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
			putCamma = true;
		}
		//
		if (this.getA() != null && this.getA().size() > 0) {
			if (putCamma)
				str += ",";
			else
				putCamma = true;
			str += "\"a\":[";
			for (int i = 0; i < this.getA().size(); i++) {
				if (i > 0)
					str += ",";
				str += this.getA().get(i).toString();
			}
			str += "]";
		}
		//
		if (this.getC() != null && this.getC().size() > 0) {
			if (putCamma)
				str += ",";
			str += "\"c\":[";
			for (int i = 0; i < this.getC().size(); i++) {
				if (i > 0)
					str += ",";
				str += this.getC().get(i).toString();
			}
			str += "]";
		}
		str += "}";
		return str;
	}

	/**
	 * Extracts and return the node name from the NN object
	 * 
	 * @return
	 */
	public String getNodeName() {
		if (this.getNn() != null) {
			return this.getNn().getName();
		}
		return null;
	}

	/**
	 * Returns true if the object has a specific attribute
	 * 
	 * @param attributeName
	 *            : The intended attribute name
	 * @return true if the object has the passed attribute
	 */
	public boolean hasAttribute(String attributeName) {
		boolean has = false;
		if (this.getA() != null)
			for (int i = 0; !has && i < this.getA().size(); i++)
				if (this.getA().get(i).getName().equalsIgnoreCase(attributeName)) {
					has = true;
					this.setRecentAttribute(this.getA().get(i));
				}
		return has;
	}

	/**
	 * Returns the value of an attribute if the object has that specific
	 * attribute
	 * 
	 * @param attributeName
	 *            : The intended attribute name
	 * @return the value of an attribute if the object has that specific
	 *         attribute, or null if fail to find
	 */
	public String getAttributeValue(String attributeName) {
		String value = null;
		if (this.getRecentAttribute() != null) {
			if (this.recentAttribute.getName().equalsIgnoreCase(attributeName)) {
				return this.recentAttribute.getValue();
			} else
				this.setRecentAttribute(null);
		}
		if (this.getA() != null)
			for (int i = 0; i < this.getA().size(); i++)
				if (this.getA().get(i).getName().equalsIgnoreCase(attributeName)) {
					value = this.getA().get(i).getValue();
					this.setRecentAttribute(this.getA().get(i));
					break;
				}
		return value;
	}

	/**
	 * Clears all the children of the node
	 */
	public void clearChildren() {
		if (this.c != null)
			this.c.clear();
	}

	/**
	 * Adds a child to the children list
	 * 
	 * @param child
	 */
	public void addChild(Child child) {
		this.c.add(child);
		child.setParent(this);
	}

	/**
	 * Adds a text node child to the currrent node
	 * 
	 * @param text
	 * @return the created TextNode
	 */
	public TextNode addTextChild(String text) {
		if (this.getNn().getType() == NodeType.code && this.getC().size() > 0
				&& this.getC().get(this.getC().size() - 1).getType() == ChildrenTypes.textnode) {
			TextNode t = (TextNode) this.getC().get(this.getC().size() - 1);
			t.addLine(text);
			return t;
		} else {
			TextNode t = new TextNode(text);
			this.addChild(t);
			return t;
		}
	}

	/**
	 * Adds a bold node to the children list
	 * 
	 * @return the created NodeJson node
	 */
	public NodeJson addBoldNode() {
		NodeJson b = null;
		if (this.nn.getType() != NodeType.b) {
			b = new NodeJson(NodeType.b);
			this.addChild(b);
		}
		return b;
	}

	/**
	 * Adds an italic node to the children lsit
	 * 
	 * @return the created NodeJson node
	 */
	public NodeJson addItalicNode() {
		NodeJson i = null;
		if (this.nn.getType() != NodeType.i) {
			i = new NodeJson(NodeType.i);
			this.addChild(i);
		}
		return i;
	}

	/**
	 * Adds a figure node to the children list
	 * 
	 * @return the created NodeJson node
	 */
	public NodeJson addFigureNode() {
		NodeJson figure = null;
		if (this.nn.getType() != NodeType.figure) {
			figure = new NodeJson(NodeType.figure);
			this.addChild(figure);
		}
		return figure;
	}

	/**
	 * Adds a Ol node to the children list
	 * 
	 * @param leftIndent
	 * @return the created NodeJson node
	 */
	public NodeJson addOLNode(BigInteger leftIndent) {
		NodeJson ol = null;
		if (this.nn.getType() != NodeType.ol) {
			ol = new NodeJson(NodeType.ol);
			ol.setIndent(leftIndent);
			this.addChild(ol);
		}
		return ol;
	}

	/**
	 * Adds a Ul node to the children list
	 * 
	 * @param leftIndent
	 * @return the created NodeJson node
	 */
	public NodeJson addULNode(BigInteger leftIndent) {
		NodeJson ul = null;
		if (this.nn.getType() != NodeType.ul) {
			ul = new NodeJson(NodeType.ul);
			ul.setIndent(leftIndent);
			this.addChild(ul);
		}
		return ul;
	}

	/**
	 * Adds a Li node to the children list
	 * 
	 * @return the created NodeJson node
	 */
	private NodeJson addLINode() {
		NodeJson li = null;
		if (this.nn.getType() != NodeType.ul && this.nn.getType() != NodeType.ol) {
			System.err.println("It is not allowed to add LI node to other elements than UL or OL.");
			return li;
		}
		if (this.nn.getType() != NodeType.li) {
			li = new NodeJson(NodeType.li);
			li.setIndent(this.getIndent());
			this.addChild(li);
		}
		return li;
	}

	/**
	 * Adds a Li and a P node to the children list
	 * 
	 * @return the created NodeJson node
	 */
	public NodeJson addLiPNode() {
		NodeJson p = null;
		if (this.nn.getType() == NodeType.ul || this.nn.getType() == NodeType.ol) {
			NodeJson li = this.addLINode();
			p = li.addPNode();
		}
		return p;
	}

	/**
	 * Adds the image url to the figure node
	 * 
	 * @param src
	 * @return
	 */
	public boolean addImageUrlToFigureNode(String src) {
		NodeJson figure = this;
		if (figure.nn.getType() != NodeType.figure || figure.getC().size() > 1) {
			return false;
		} else {
			NodeJson div = figure.addDivNode();
			div.addImgNode(src);
			return true;
		}
	}

	/**
	 * Adds a P node to the children list
	 * 
	 * @return the created NodeJson node
	 */
	public NodeJson addPNode() {
		NodeJson p = this;
		if (p.nn.getType() != NodeType.p) {
			p = new NodeJson(NodeType.p);
			this.addChild(p);
		}
		return p;
	}

	/**
	 * Adds a div node to the children list
	 * 
	 * @return the created NodeJson node
	 */
	private NodeJson addDivNode() {
		NodeJson div = this;
		if (div.nn.getType() != NodeType.div) {
			div = new NodeJson(NodeType.div);
			this.c.add(div);
			div.setParent(this);
		}
		return div;
	}

	/**
	 * Adds an image node to the children list
	 * 
	 * @param src
	 * @return the created NodeJson node
	 */
	private NodeJson addImgNode(String src) {
		NodeJson img = this;
		if (img.nn.getType() != NodeType.img) {
			img = new NodeJson(NodeType.img);
			img.getA().add(new Attribute("src", src));
			this.c.add(img);
			img.setParent(this);
		}
		return img;
	}

	/**
	 * Substitude the current node with a new type
	 * 
	 * @param type
	 * @return the changed NodeJson node
	 */
	public NodeJson replaceMeWithNode(NodeType type) {
		if (this.getC().size() != 0 || this.getNn().getType() == type) {
			return this;
		}
		NodeJson newNode = this;
		NodeJson parent = this.getParent();
		NodeType[] changable = { NodeType.p, NodeType.h1, NodeType.h2, NodeType.h3, NodeType.h4, NodeType.h5,
				NodeType.h6, NodeType.pre, NodeType.figure };
		if (this.listHas(changable, type) && this.listHas(changable, this.getNn().getType())) {
			newNode = new NodeJson(type);
			newNode.setParent(parent);
			for (int i = 0; i < parent.getC().size(); i++) {
				if (parent.getC().get(i) == this) {
					parent.getC().set(i, newNode);
					break;
				}
			}
		}
		return newNode;
	}

	/**
	 * Searches a NodeType in NodeType list and returns true if exists, false otherwise
	 * 
	 * @param arr
	 * @param targetValue
	 * @return
	 */
	private boolean listHas(NodeType[] arr, NodeType targetValue) {
		return Arrays.asList(arr).contains(targetValue);
	}

	/**
	 * Adds a heading node to the children list
	 * 
	 * @param type
	 * @return the created NodeJson node
	 */
	public NodeJson addHeadingNode(NodeType type) {
		NodeJson heading = null;
		if (type == NodeType.h1 || type == NodeType.h2 || type == NodeType.h3 || type == NodeType.h4
				|| type == NodeType.h5 || type == NodeType.h6) {
			heading = new NodeJson(type);
			heading.setParent(this);
			this.getC().add(heading);
		}
		return heading;
	}

	/**
	 * Adds a pre node to the children list
	 * 
	 * @return the created NodeJson node
	 */
	public NodeJson addPreNode() {
		NodeJson pre = new NodeJson(NodeType.pre);
		pre.setParent(this);
		this.getC().add(pre);
		return pre;
	}

	/**
	 * Adds a code node to the children list
	 * 
	 * @return the created NodeJson node
	 */
	public NodeJson addCodeNode() {
		NodeJson code = new NodeJson(NodeType.code);
		code.setParent(this);
		this.getC().add(code);
		return code;
	}

	/**
	 * Adds a hyperlink child
	 * 
	 * @param hyperlinkHrefAttr
	 * @param hyperlinkTitleAttr
	 * @param bold
	 * @param italic
	 * @param text
	 * @return the created NodeJson node
	 */
	public NodeJson addHyperlinkNode(String hyperlinkHrefAttr, String hyperlinkTitleAttr, boolean bold, boolean italic,
			String text) {
		NodeJson a = null;
		int size = this.getC().size();
		if (size > 0 && (this.getC().get(size - 1).getType() == ChildrenTypes.element)
				&& ((NodeJson) this.getC().get(size - 1)).getNn().getType() == NodeType.a
				&& ((NodeJson) this.getC().get(size - 1)).getAttributeValue("href").equals(hyperlinkHrefAttr)
				&& ((NodeJson) this.getC().get(size - 1)).getAttributeValue("title").equals(hyperlinkTitleAttr)) {
			a = (NodeJson) this.getC().get(size - 1);
			NodeJson temp = a;
			if (bold)
				temp = temp.addBoldNode();
			if (italic)
				temp = temp.addItalicNode();
			temp.addTextChild(text);
		} else {
			a = new NodeJson(NodeType.a);
			a.getA().add(new Attribute("href", hyperlinkHrefAttr));
			a.getA().add(new Attribute("title", hyperlinkTitleAttr));
			a.setParent(this);
			this.getC().add(a);
			NodeJson temp = a;
			if (bold)
				temp = temp.addBoldNode();
			if (italic)
				temp = temp.addItalicNode();
			temp.addTextChild(text);
		}
		return a;
	}

	/**
	 * Adds the pirimary key of the image to the current figure node
	 * 
	 * @param imagePk
	 * @return
	 */
	public boolean addImagePkToFigureNode(String imagePk) {
		NodeJson figure = this;
		if (figure.nn.getType() != NodeType.figure || figure.getC().size() > 1) {
			return false;
		} else {
			figure.getA().add(new Attribute("data-image", imagePk));
			return true;
		}
	}

	/**
	 * Adds a category and caption to the figure node
	 * 
	 * @param category
	 * @param caption
	 * @return
	 */
	public boolean addCategoryAndCaptionToFigureNode(String category, String caption) {
		NodeJson figure = this;
		if (figure.nn.getType() != NodeType.figure) {
			return false;
		} else {
			figure.getA().add(new Attribute("data-figure-category", (category == null ? "none" : category)));
			figure.getA().add(new Attribute("data-caption", caption));
			figure.addFigcaptionNodeToFigureNode(category, caption);
			return true;
		}
	}

	/**
	 * Adds the caption to the figure node
	 * 
	 * @param category
	 * @param caption
	 * @return the created NodeJson node
	 */
	private NodeJson addFigcaptionNodeToFigureNode(String category, String caption) {
		NodeJson figure = this;
		if (figure.nn.getType() != NodeType.figure || figure.getC().size() > 1) {
			return null;
		} else {
			NodeJson figcaption = new NodeJson(NodeType.figcaption);
			this.addChild(figcaption);
			//
			if (category != null) {
				NodeJson span = new NodeJson(NodeType.span);
				figcaption.addChild(span);
				span.getA().add(new Attribute("class", "figure-cat-" + category));
				span.getA().add(new Attribute("data-figure-category", category));
				span.addTextChild(category);
			}
			//
			NodeJson span = new NodeJson(NodeType.span);
			figcaption.addChild(span);
			span.getA().add(new Attribute("data-caption", caption));
			span.addTextChild(caption);
			//
			return figcaption;
		}
	}

	/**
	 * Returns the index of the current node
	 * 
	 * @return
	 */
	public int getChildrenNumber() {
		if (this.getC() != null)
			return this.getC().size();
		else
			return 0;
	}

	/**
	 * Receives an index of a child and returns that child if exists
	 * 
	 * @param index
	 * @return the created NodeJson node
	 */
	public NodeJson getNodeJsonChild(int index) {
		if (this.getC() != null && index < this.getC().size()) {
			if (this.getC().get(index) instanceof NodeJson)
				return (NodeJson) this.getC().get(index);
			else
				return null;
		} else
			return null;
	}

	/**
	 * Receives an index of a child and remove that child if exists
	 * 
	 * @param index
	 * @return the created Child node
	 */
	public Child removeChild(int index) {
		if (this.getC() != null && index < this.getC().size()) {
			Child child = this.getC().get(index);
			this.getC().remove(index);
			return child;
		} else
			return null;
	}

	/**
	 * Adds a formula span child to the current node
	 * 
	 * @param latex
	 * @return the created NodeJson node
	 */
	public NodeJson addFormulaSpan(String latex) {
		NodeJson span = new NodeJson(NodeType.span);
		this.addChild(span);
		span.getA().add(new Attribute("class", "equation"));
		span.getA().add(new Attribute("data-equation", latex));
		span.getA().add(new Attribute("contenteditable", "false"));
		return span;
	}

	/**
	 * Adds a formula child to the current figure node
	 * 
	 * @param latex
	 * @return the created NodeJson node
	 */
	public NodeJson addFormulaToFigureNode(String latex) {
		NodeJson figure = this;
		if (figure.nn.getType() != NodeType.figure || figure.getC().size() > 1) {
			return null;
		} else {
			figure.getA().add(new Attribute("data-equation", latex));
			NodeJson div = figure.addDivNode();
			div.getA().add(new Attribute("class", "figure-equation"));
			div.getA().add(new Attribute("data-equation", latex));
			return figure;
		}

	}

	/**
	 * Adds a citation span child to the current node
	 * 
	 * @param ids
	 * @param citationPages
	 * @param citationTextBefores
	 * @return the created NodeJson node
	 */
	public NodeJson addCitationSpanChild(String ids, String citationPages, String citationTextBefores) {
		NodeJson span = new NodeJson(NodeType.span);
		this.addChild(span);
		span.getA().add(new Attribute("class", "citation"));
		span.getA().add(new Attribute("data-bib-format", "autocite"));
		span.getA().add(new Attribute("data-bib-entry", ids));
		span.getA().add(new Attribute("data-bib-before", citationTextBefores));
		span.getA().add(new Attribute("data-bib-page", citationPages));
		return span;
	}

	/**
	 * Adds a comment span child to the current node
	 * 
	 * @param commentedText
	 * @param commentId
	 * @return the created NodeJson node
	 */
	public NodeJson addCommentSpanChild(String commentedText, BigInteger commentId) {
		NodeJson span = new NodeJson(NodeType.span);
		this.addChild(span);
		span.getA().add(new Attribute("class", "comment"));
		span.getA().add(new Attribute("data-id", "" + commentId.longValue()));
		span.addTextChild(commentedText);
		return span;
	}

	/**
	 * Adds a footnote span child to the current node
	 * 
	 * @return the created NodeJson node
	 */
	public NodeJson addFootnoteSpanChild() {
		NodeJson span = new NodeJson(NodeType.span);
		this.addChild(span);
		span.getA().add(new Attribute("class", "footnote"));
		return span;
	}
}

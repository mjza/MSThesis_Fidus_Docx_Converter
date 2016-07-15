package fidusWriter.model.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONObject;

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class contains
 */
public class Comments {

	private Comment recentViewedComment = null;
	private ArrayList<Comment> comments = null;

	// The getters and setters area
	public ArrayList<Comment> getComments() {
		return comments;
	}

	public void setComments(ArrayList<Comment> comments) {
		this.comments = comments;
	}

	// end of the getters and setters area
	/**
	 * Creates the object from the passed JSON from the Fidus file.
	 * 
	 * @param comments
	 */
	public Comments(JSONObject comments) {
		if (comments != null) {
			this.comments = new ArrayList<Comment>();
			@SuppressWarnings("unchecked")
			Set<String> keys = (Set<String>) comments.keySet();
			Iterator<String> commentIterator = (Iterator<String>) keys.iterator();
			while (commentIterator.hasNext()) {
				String key = commentIterator.next();
				JSONObject comment = ((JSONObject) comments.get(key));
				this.comments.add(new Comment(comment));
			}
		}

	}

	/**
	 * Creates an empty object to be filled with data from docx file
	 */
	public Comments() {
		this.comments = new ArrayList<Comment>();
	}

	/**
	 * This function converts the object to the JSON string for storing in
	 * document.json file
	 */
	public String toString() {
		String str = "{";
		if (this.getComments() != null) {
			for (int i = 0; i < this.comments.size(); i++) {
				if (i > 0)
					str += ",";
				str += this.comments.get(i).toString();
			}
		}
		str += "}";
		return str;
	}

	/**
	 * Find the comment based on its id and returns it
	 * 
	 * @param commentId
	 *            : The id of the intended comment
	 * @return the found comment or null if fail
	 */
	public Comment getComment(Long commentId) {
		if (this.recentViewedComment != null) {
			if (this.recentViewedComment.getId().longValue() == commentId.longValue())
				return this.recentViewedComment;
			else
				this.recentViewedComment = null;
		}
		Comment comment = null;
		for (int i = 0; i < this.comments.size(); i++) {
			if (this.comments.get(i).getId().longValue() == commentId.longValue()) {
				comment = this.comments.get(i);
				this.recentViewedComment = comment;
				break;
			}
		}
		return comment;
	}
}

package fidusWriter.model.document;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class contains functions to model the CommentAnswers part of the
 *       Fidus file.
 */
public class CommentAnswers {
	private ArrayList<CommentAnswer> answers = null;

	// The getters and setters area
	public ArrayList<CommentAnswer> getAnswers() {
		return answers;
	}

	public void setAnswers(ArrayList<CommentAnswer> answers) {
		this.answers = answers;
	}

	// end of the getters and setters area
	/**
	 * Creates an empty object to be filled with data from a docx file.
	 */
	public CommentAnswers() {
		this.answers = new ArrayList<CommentAnswer>();
	}

	/**
	 * Creates the CommentAnswers object from a JSON object from the Fidus file.
	 * 
	 * @param answers
	 */
	public CommentAnswers(JSONArray answers) {
		if (answers != null) {
			this.answers = new ArrayList<CommentAnswer>();
			for (int i = 0; i < answers.size(); i++) {
				JSONObject answer = ((JSONObject) answers.get(i));
				this.answers.add(new CommentAnswer(answer));
			}
		}
	}

	/**
	 * Adds a new created CommentAnswer object to the list of CommentAnswers
	 * 
	 * @param answer
	 */
	public void addAnswer(CommentAnswer answer) {
		this.answers.add(answer);
	}

	/**
	 * This function converts the object to the JSON string for storing in
	 * document.json file
	 */
	public String toString() {
		String str = "[";
		if (this.getAnswers() != null) {
			for (int i = 0; i < this.answers.size(); i++) {
				if (i > 0)
					str += ",";
				str += this.answers.get(i).toString();
			}
		}
		str += "]";
		return str;
	}
}

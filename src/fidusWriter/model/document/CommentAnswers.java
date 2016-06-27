package fidusWriter.model.document;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CommentAnswers {
	private ArrayList<CommentAnswer> answers = null;
	public CommentAnswers(){
		this.answers = new ArrayList<CommentAnswer>();
	}
	public CommentAnswers(JSONArray answers) {
		if(answers!=null){
			this.answers = new ArrayList<CommentAnswer>();
		    for(int i=0;i<answers.size();i++){
			    JSONObject answer = ( (JSONObject) answers.get(i) );
			    this.answers.add(new CommentAnswer(answer));
		    }	    
		}
	}
	public void addAnswer(CommentAnswer answer) {
		this.answers.add(answer);
	}
	public ArrayList<CommentAnswer> getAnswers() {
		return answers;
	}
	public void setAnswers(ArrayList<CommentAnswer> answers) {
		this.answers = answers;
	}
	public String toString(){
		String str = "[";
		if(this.getAnswers()!=null){
			for(int i=0;i<this.answers.size();i++){
				if(i>0)
					str += ",";
				str += this.answers.get(i).toString();
			}
		}
		str += "]";
		return str;
	}
}

package fidusWriter.model.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONObject;

public class Comments {
	
	private Comment recentViewedComment = null;
	
	private ArrayList<Comment> comments = null;
	
	public ArrayList<Comment> getComments() {
		return comments;
	}

	public void setComments(ArrayList<Comment> comments) {
		this.comments = comments;
	}

	public Comments(JSONObject comments) {
		if(comments != null){
			this.comments = new ArrayList<Comment>();
			@SuppressWarnings("unchecked")
			Set<String> keys = (Set<String>) comments.keySet();
			Iterator<String> commentIterator = (Iterator<String>) keys.iterator();
			while (commentIterator.hasNext()) {
				String key = commentIterator.next();
				JSONObject comment = ( (JSONObject) comments.get(key) );
				this.comments.add(new Comment(comment));			    
			}
		}
			
	}
	public Comments(){
		this.comments = new ArrayList<Comment>();
	}
	public String toString(){
		String str = "{";
		if(this.getComments()!=null){
			for(int i=0;i<this.comments.size();i++){
				if(i>0)
					str += ",";				
				str +=this.comments.get(i).toString();
			}
		}
		str += "}";
		return str;
	}
	public Comment getComment(Long commentId){
		if(this.recentViewedComment!=null){
			if(this.recentViewedComment.getId().longValue()==commentId.longValue())
				return this.recentViewedComment;
			else
				this.recentViewedComment = null;
		}
		Comment comment = null;
		for(int i=0;i<this.comments.size();i++){
			if(this.comments.get(i).getId().longValue()==commentId.longValue()){
				comment = this.comments.get(i);
				this.recentViewedComment = comment;
				break;
			}
		}
		return comment;
	}
}

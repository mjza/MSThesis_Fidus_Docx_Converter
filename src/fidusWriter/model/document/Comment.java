package fidusWriter.model.document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Comment {
	private Long id = null;            // A random ID for the comment that is unique within the document.
	private Integer user = null;       // PK value of comment author
	private String userName = null;    // Username of comment author
	private String userAvatar = null;  // Avatar of comment author
	private Long date = null;          // Javascript timestamp of when comment was written.
	private String comment = null;	   // The text contents of the comment.
	private CommentAnswers answers = null;
	private Boolean reviewIsMajor = null;
	private Boolean hidden =  null;
	//
	public Comment(){
		this.setUserAvatar("/static/img/default_avatar.png");
		this.answers = new CommentAnswers();
		this.setReviewIsMajor(false);
		this.setHidden(false);
	}
	public Comment(JSONObject comment) {
		if(comment.get("id")!=null){
			this.setId(Long.parseLong(comment.get("id").toString()));
		}
		if(comment.get("user")!=null){
			this.setUser(Integer.parseInt(comment.get("user").toString()));
		}
		if(comment.get("userName")!=null){
			this.setUserName(comment.get("userName").toString());
		}
		if(comment.get("userAvatar")!=null){
			this.setUserAvatar(comment.get("userAvatar").toString());
		}
		if(comment.get("date")!=null){
			this.setDate(Long.parseLong(comment.get("date").toString()));
		}
		if(comment.get("comment")!=null){
	    	this.setComment(comment.get("comment").toString());
		}
		if(comment.get("answers")!=null){
			JSONArray answers = ( (JSONArray) comment.get("answers") );
			this.setAnswers(new CommentAnswers(answers));
		}
		if(comment.get("review:isMajor")!=null){
			this.setReviewIsMajor(Boolean.parseBoolean(comment.get("review:isMajor").toString()));
		}
	}
	public Long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Integer getUser() {
		return user;
	}
	public void setUser(int user) {
		this.user = user;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserAvatar() {
		return userAvatar;
	}
	public void setUserAvatar(String userAvatar) {
		this.userAvatar = userAvatar;
	}
	public Long getDate() {
		return date;
	}
	public String getDateFormatted() {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(this.getDate());
		return formatter.format(calendar.getTime()); 
	}
	public void setDate(long date) {
		this.date = date;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public CommentAnswers getAnswers() {
		return answers;
	}
	public void setAnswers(CommentAnswers answers) {
		this.answers = answers;
	}
	public Boolean isReviewIsMajor() {
		return reviewIsMajor;
	}
	public String getReviewIsMajor() {
		return reviewIsMajor.booleanValue() ? "true" : "false";
	}
	public void setReviewIsMajor(boolean reviewIsMajor) {
		this.reviewIsMajor = reviewIsMajor;
	}
	public Boolean isHidden() {
		return hidden.booleanValue();
	}
	public String getHidden() {
		return hidden.booleanValue() ? "true" : "false";
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	public String toString(){
		String str = "{";
		boolean flag = false;
		if(this.getId()!=null){			
			flag = true;
			str="\""+this.getId().longValue()+"\":{";
			str+="\"id\":"+this.getId().longValue();
		}
		if(this.getUser()!=null){	
			if(flag==true)
				str += ",";
			flag = true;
			str+="\"user\":"+this.getUser().intValue();
		}
		if(this.getUserName()!=null){
			if(flag==true)
				str += ",";
			flag = true;
			str+="\"userName\":\""+this.getUserName().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")+"\"";
		}
		if(this.getUserAvatar()!=null){
			if(flag==true)
				str += ",";
			flag = true;
			str+="\"userAvatar\":\""+this.getUserAvatar().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")+"\"";
		}
		if(this.getDate()!=null){	
			if(flag==true)
				str += ",";
			flag = true;
			str+="\"date\":"+this.getDate().longValue();
		}
		if(this.getComment()!=null){
			if(flag==true)
				str += ",";
			flag = true;
			str+="\"comment\":\""+this.getComment().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")+"\"";
		}
		if(this.getAnswers()!=null){
			if(flag==true)
				str += ",";
			flag = true;
			str+="\"answers\":"+this.getAnswers().toString();
		}
		if(this.isReviewIsMajor()!=null){
			if(flag==true)
				str += ",";
			flag = true;
			str+="\"review:isMajor\":"+this.getReviewIsMajor();
		}
		str+="}";
		return str;
	}
	
	
}

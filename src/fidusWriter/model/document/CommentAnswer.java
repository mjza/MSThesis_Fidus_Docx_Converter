package fidusWriter.model.document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.simple.JSONObject;

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class contains contains functions for moddeling commentAnswer
 */
public class CommentAnswer {
	private Long commentId = null; // The id of the parent comment.
	private Integer user = null; // PK value of comment answer author
	private String userName = null; // Username of comment answer author
	private String userAvatar = null; // Avatar of comment answer author
	private Long date = null; // Javascript timestamp of when comment answer was
								// written.
	private String answer = null; // The text contents of the comment answer.
	private Long id = null; // A random ID for the comment answer that is unique
							// within the document.
	// The getters and setters area

	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(long commentId) {
		this.commentId = commentId;
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

	public void setDate(long date) {
		this.date = date;
	}

	public String getDateFormatted() {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(this.getDate());
		return formatter.format(calendar.getTime());
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	// end of the getters and setters area
	/**
	 * Initiates a CommentAnswer object for filling by data from docx file
	 * 
	 * @param parentId
	 * @param id
	 */
	public CommentAnswer(long parentId, long id) {
		this.setCommentId(parentId);
		this.setId(id);
	}

	/**
	 * Create CommentsAnswer from a JSON object from Fidus file.
	 * 
	 * @param answer
	 */
	public CommentAnswer(JSONObject answer) {
		if (answer != null) {
			if (answer.get("commentId") != null) {
				this.setCommentId(Long.parseLong(answer.get("commentId").toString()));
			}
			if (answer.get("user") != null) {
				this.setUser(Integer.parseInt(answer.get("user").toString()));
			}
			if (answer.get("userName") != null) {
				this.setUserName(answer.get("userName").toString());
			}
			if (answer.get("userAvatar") != null) {
				this.setUserAvatar(answer.get("userAvatar").toString());
			}
			if (answer.get("date") != null) {
				this.setDate(Long.parseLong(answer.get("date").toString()));
			}
			if (answer.get("answer") != null) {
				this.setAnswer(answer.get("answer").toString());
			}
			if (answer.get("id") != null) {
				this.setId(Long.parseLong(answer.get("id").toString()));
			}
		}
	}

	/**
	 * This function converts the object to the JSON string for storing in
	 * document.json file
	 */
	public String toString() {
		String str = "{";
		boolean flag = false;
		if (this.getUserName() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"userName\":\""
					+ this.getUserName().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getAnswer() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"answer\":\"" + this.getAnswer().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
					+ "\"";
		}
		if (this.getUserAvatar() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"userAvatar\":\""
					+ this.getUserAvatar().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
		}
		if (this.getUser() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"user\":" + this.getUser().intValue();
		}

		if (this.getDate() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"date\":" + this.getDate().longValue();
		}
		if (this.getCommentId() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"commentId\":" + this.getCommentId().intValue();
		}
		if (this.getId() != null) {
			if (flag == true)
				str += ",";
			flag = true;
			str += "\"id\":" + this.getId().longValue();
		}
		str += "}";
		return str;
	}
}

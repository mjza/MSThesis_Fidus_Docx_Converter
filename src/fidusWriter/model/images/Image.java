package fidusWriter.model.images;

import org.json.simple.JSONObject;

public class Image {
	private Long added = null;
    private String title = null;
    private String fileType = null;
    private Long checksum = null;
    private String image = null;
    private Integer height = null;
    private Integer width = null;
    private Long pk = null;
    private String thumbnail = null;
	
    public Long getAdded() {
		return added;
	}
	public void setAdded(Long added) {
		this.added = added;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public Long getChecksum() {
		return checksum;
	}
	public void setChecksum(Long checksum) {
		this.checksum = checksum;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Long getPk() {
		return pk;
	}
	public void setPk(Long pk) {
		this.pk = pk;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public Image(JSONObject img){
    	super();
    	if(img.get("added")!=null){
			this.setAdded(Long.parseLong(img.get("added").toString()));
		}
    	if(img.get("title")!=null){
    		this.setTitle(img.get("title").toString());
    	}
    	if(img.get("file_type")!=null){
    		this.setFileType(img.get("file_type").toString());
    	}
    	if(img.get("checksum")!=null){
			this.setChecksum(Long.parseLong(img.get("checksum").toString()));
		}
    	if(img.get("image")!=null){
    		this.setImage(img.get("image").toString());
    	}
    	if(img.get("height")!=null){
			this.setHeight(Integer.parseInt(img.get("height").toString()));
		}
    	if(img.get("width")!=null){
			this.setWidth(Integer.parseInt(img.get("width").toString()));
		}
    	if(img.get("pk")!=null){
			this.setPk(Long.parseLong(img.get("pk").toString()));
		}
    	if(img.get("thumbnail")!=null){
    		this.setThumbnail(img.get("thumbnail").toString());
    	}
    }
    public Image(String path, String title, int height, int width, int hash, long imagePk) {
    	long time = System.currentTimeMillis();
		this.setAdded(time);
		if(title == null || title.length()==0)
			title = ""+time;
		this.setTitle(title);
		this.setFileType(this.getMIMEType(path));
		this.setImage(path);
		this.setPk(imagePk);
		this.setThumbnail("");
		this.setChecksum(time+hash);
        this.setHeight(height);
		this.setWidth(width);
	}
    private String getMIMEType(String path){
    	if(path.endsWith(".bmp") || path.endsWith(".bm"))
    		return "image/bmp";
    	else if(path.endsWith(".gif"))
    		return "image/gif";
    	else if(path.endsWith(".ico"))
    		return "image/x-icon";
    	else if(path.endsWith(".jfif"))
    		return "image/jpeg";
    	else if(path.endsWith(".jpe"))
    		return "image/jpeg";
    	else if(path.endsWith(".jpeg"))
    		return "image/jpeg";
    	else if(path.endsWith(".jpg"))
    		return "image/jpeg";
    	else if(path.endsWith(".png"))
    		return "image/png";
    	else if(path.endsWith(".tif"))
    		return "image/tiff";
    	else if(path.endsWith(".tiff"))
    		return "image/tiff";
    	return "";	
    }
	public String toString(){
    	String str = "{";
    	boolean flag = false;
    	if(this.getAdded()!=null){
    		flag = true;
    		str+="\"added\":"+this.getAdded().longValue();
		}
    	if(this.getTitle()!=null){
    		if(flag==true)
				str += ",";
			flag = true;
			str+="\"title\":\""+this.getTitle().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")+"\"";
    	}
    	if(this.getFileType()!=null){
    		if(flag==true)
				str += ",";
			flag = true;
			str+="\"file_type\":\""+this.getFileType().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")+"\"";
    	}
    	if(this.getChecksum()!=null){
    		if(flag==true)
				str += ",";
			flag = true;
			str+="\"checksum\":"+this.getChecksum().longValue();
		}
    	if(this.getImage()!=null){
    		if(flag==true)
				str += ",";
			flag = true;
			str+="\"image\":\""+this.getImage().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")+"\"";
    	}
    	if(this.getHeight()!=null){
    		if(flag==true)
				str += ",";
			flag = true;
			str+="\"height\":"+this.getHeight().intValue();
		}
    	if(this.getWidth()!=null){
    		if(flag==true)
				str += ",";
			flag = true;
			str+="\"width\":"+this.getWidth().intValue();
		}
    	if(true){
    		if(flag==true)
				str += ",";
			flag = true;
			str+="\"cats\":["+"\"\""+"]";
		}
    	if(this.getPk()!=null){
    		if(flag==true)
				str += ",";
			flag = true;
			str+="\"pk\":"+this.getPk().longValue();
		}
    	if(this.getThumbnail()!=null){
    		if(flag==true)
				str += ",";
			flag = true;
			str+="\"thumbnail\":\""+this.getThumbnail().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")+"\"";
    	}
    	str += "}";
    	return str;
    }
}

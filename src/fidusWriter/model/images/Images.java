package fidusWriter.model.images;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Images {
	private ArrayList<Image> images=null;
	private Image recentImage = null;
	public Images() {
		super();
		this.images = new ArrayList<Image>();
	}
	
	public Image getImage(long pk){
		if(this.recentImage!=null){
			if(this.recentImage.getPk()==pk)
				return this.recentImage;
			else
				this.recentImage = null;
		}
		for(int i=0;i<this.images.size();i++){
			Image img = this.images.get(i);
			if(img.getPk() == pk){
				this.recentImage = img;
				return img;
			}
		}
		return null;
	}
	public long getMaxPk(){
		long max = 0;
		for(int i=0;i<this.images.size();i++){
			Image img = this.images.get(i);
			if(img.getPk() > max){
				max = img.getPk();
			}
		}
		return max;
	}
	public void importFromFile(String path){
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(path));
			JSONArray jsonArray = (JSONArray) obj;
			if(jsonArray!=null)
				for(int i=0;i<jsonArray.size();i++)
					this.images.add(new Image((JSONObject) jsonArray.get(i)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public String toString(){
		String str="[";
		if(this.images.size()>0)
			str+=this.images.get(0).toString();
		for(int i=1;i<this.images.size();i++)
			str+=","+this.images.get(i).toString();
		str+="]";
		return str;
	}

	public long addImage(String path, String title, int height, int width, int hash, long imagePk) {
		if(this.getImage(imagePk) != null){
			imagePk = this.getMaxPk()+1;
		}
		this.images.add(new Image(path,title,height,width,hash,imagePk));
		return imagePk;
	}
}

package fidusWriter.model.images;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role   This class contains functions for modelling the images.json file of the fidus file.   
 */
public class Images {
	private ArrayList<Image> images=null;
	private Image recentImage = null;
	/**
	 * Creates an empty images object
	 */
	public Images() {
		super();
		this.images = new ArrayList<Image>();
	}
	/**
	 * Returns the image object based on the primary key
	 * @param pk
	 * @return
	 */
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
	/**
	 * Returns the maximum images's primary key 
	 * @return
	 */
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
	/**
	 * Receives the path to the images.json file and makes a model of its images
	 * @param path
	 */
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
	/**
	 * Converts the object to JSON string
	 */
	public String toString(){
		String str="[";
		if(this.images.size()>0)
			str+=this.images.get(0).toString();
		for(int i=1;i<this.images.size();i++)
			str+=","+this.images.get(i).toString();
		str+="]";
		return str;
	}
	/**
	 * This function receives data of an image and adds an image object to the images object
	 * @param path
	 *            : path to the actual file
	 * @param title
	 *            : title of the image
	 * @param height
	 *            : hight in pixels
	 * @param width
	 *            : width in pixels
	 * @param hash
	 *            : a hash value for the picture
	 * @param imagePk
	 *            : a unique id for the image
	 * @return
	 */
	public long addImage(String path, String title, int height, int width, int hash, long imagePk) {
		if(this.getImage(imagePk) != null){
			imagePk = this.getMaxPk()+1;
		}
		this.images.add(new Image(path,title,height,width,hash,imagePk));
		return imagePk;
	}
}

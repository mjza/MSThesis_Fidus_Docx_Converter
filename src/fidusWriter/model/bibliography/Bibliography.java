package fidusWriter.model.bibliography;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role   This class contains    
 */
public class Bibliography {
	private ArrayList<BibliographyEntry> bibliographyEntries = new ArrayList<BibliographyEntry>();
	private BibliographyEntry recentEntry = null;
	// Getters and setters area
	public ArrayList<BibliographyEntry> getBibliographyEntries() {
		return bibliographyEntries;
	}
	public void setBibliographyEntries(ArrayList<BibliographyEntry> bibliographyEntry) {
		this.bibliographyEntries = bibliographyEntry;
	}
	// end of getters and setters 
	/**
	 * Get a path to the bibliography file and imports all entries inside of it.
	 * @param path
	 */
	public void importFromFile(String path){ 
		JSONParser parser = new JSONParser();
		try{
			Object obj = parser.parse(new FileReader(path));
			JSONObject jsonObject = (JSONObject) obj;
			@SuppressWarnings("unchecked")
			Set<String> keys = (Set<String>) jsonObject.keySet();
			Iterator<String> biboIterator = (Iterator<String>) keys.iterator();
			while (biboIterator.hasNext()) {
				String key = biboIterator.next();
				JSONObject biboEntry = ( (JSONObject) jsonObject.get(key) );
				this.bibliographyEntries.add(new BibliographyEntry(biboEntry, key));			    
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Converts the object to string for storing in bibliography.json file.
	 */
	public String toString(){
		String str = "{";
		for(int i=0;i<this.bibliographyEntries.size();i++){
			str += "\""+this.bibliographyEntries.get(i).getId().longValue()+"\": "+this.bibliographyEntries.get(i).toString();
			if(i<this.bibliographyEntries.size()-1)
				str +=",";
		}
		str += "}";
		return str;
	}
	/**
	 * Receives an id of an Entry and returns it
	 * @param id
	 * @return
	 */
	public BibliographyEntry getBibliographyEntry(long id){
		if(this.recentEntry!=null){
			if(this.recentEntry.getId().longValue()==id)
				return this.recentEntry;
			else
				this.recentEntry = null;
		}
		for(int i=0;i<this.bibliographyEntries.size();i++)
			if(this.bibliographyEntries.get(i).getId().longValue()==id)
			{
				this.recentEntry = this.bibliographyEntries.get(i);
				break;
			}
		return this.recentEntry;
	}
	/**
	 * Receives a tag of an entry and returns it.
	 * @param tag
	 * @return
	 */
	public BibliographyEntry getBibliographyEntry(String tag){
		if(this.recentEntry!=null){
			if(this.recentEntry.getTag().compareTo(tag)==0)
				return this.recentEntry;
			else
				this.recentEntry = null;
		}
		for(int i=0;i<this.bibliographyEntries.size();i++)
			if(this.bibliographyEntries.get(i).getTag().compareTo(tag)==0)
			{
				this.recentEntry = this.bibliographyEntries.get(i);
				break;
			}
		return this.recentEntry;
	}
	/**
	 * This function is used for storing Docx resources as enteries
	 * @param entry
	 */
	public void addBibliographyEntry(BibliographyEntry entry){
		this.bibliographyEntries.add(entry);
	}
}

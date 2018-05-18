package resources;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.mit.jwi.item.ISynset;

/*
 * This class saves the information (about a concept) used to generate the text files.
 */
public class Utilities {

//Attributes
	
	//This map saves the retrieved synsets and its bag of words, for a concept
	private Map<ISynset, List<String>> synsetCntxt;
	//The number of synsets retrieved for a concept
	private int numSy;
	//This list saves the averages between the context and the bag of words of a synset
	//Only used on the Word Embeddigs technique
	private List<Double> listMedia;
	
	private LinkedHashMap<ISynset, LinkedHashMap<String, LinkedHashMap<String, Double> > > mapPairSim;

//Constructor	
	
	public Utilities() {
		this.synsetCntxt = null;
		this.numSy = 0;
		this.listMedia = null;
		this.mapPairSim = null;
	}
	
//Getters and setters
	
	public void set_synsetCntx(HashMap<ISynset, List<String>> _synsetCntxt) {
		this.synsetCntxt = _synsetCntxt;
	}
	
	public Map<ISynset, List<String>> get_synsetCntx() {
		return synsetCntxt;
	}
	
	public void set_numSy(int num) {
		this.numSy = num;
	}
	
	public int get_numSy() {
		return numSy;
	}
	
	public void set_synsetMedia(List<Double> _synsetMedia) {
		this.listMedia = _synsetMedia;
	}
	
	public List<Double> get_synsetMedia() {
		return listMedia;
	}
	
	public void set_pairSim(LinkedHashMap<ISynset, LinkedHashMap<String, LinkedHashMap<String, Double> > > _mapPairSim) {
		this.mapPairSim = _mapPairSim;
	}
	
	public LinkedHashMap<ISynset, LinkedHashMap<String, LinkedHashMap<String, Double> > > get_pairSim() {
		return mapPairSim;
	}

}

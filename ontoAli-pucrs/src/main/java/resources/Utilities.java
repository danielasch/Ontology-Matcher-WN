package resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.jwi.item.ISynset;

public class Utilities {
	
	private Map<ISynset, List<String>> synsetCntxt;
	private int numSy;
	private List<Double> listMedia;
	
	public Utilities() {
		this.synsetCntxt = null;
		this.numSy = 0;
		this.listMedia = null;
	}
	
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

}

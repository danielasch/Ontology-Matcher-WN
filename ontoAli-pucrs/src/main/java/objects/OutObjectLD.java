package objects;

import java.util.List;
import java.util.Map;

import edu.mit.jwi.item.ISynset;

public class OutObjectLD {
	public class parLD {
		private String elementCTX;
		private Map<String, Float> par;
		private float totalPar;
		
		public void set_element_ctx(String _element) {
			this.elementCTX = _element;
		}
		
		public String get_element_ctx() {
			return this.elementCTX;
		}
		
		public void set_par(Map<String, Float> _map) {
			this.par = _map;
		}
		
		public Map<String, Float> get_par() {
			return this.par;
		}
		
		public void set_total(float _totalPar) {
			this.totalPar = _totalPar;
		}
		
		public float get_total() {
			return this.totalPar;
		}
	}
	
	private ISynset synset;
	private List<parLD> ctxList;
	private float total;
	
	public void set_synset(ISynset _synset) {
		this.synset = _synset;
	}
	
	public ISynset get_synset() {
		return this.synset;
	}
	
	public void set_ctxList(List<parLD> _ctxList) {
		this.ctxList = _ctxList;
	}
	
	public List<parLD> get_ctxList() {
		return this.ctxList;
	}
	
	public void set_valor_total(float _total) {
		this.total = _total;
	}
	
	public float get_valor_total() {
		return this.total;
	}
	
	public parLD instance_parLD() {
		parLD pld = new parLD();
		return pld;
	}
}

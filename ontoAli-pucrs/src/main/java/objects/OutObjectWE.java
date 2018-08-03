package objects;

import java.util.HashMap;
import java.util.Map.Entry;

public class OutObjectWE implements Comparable<OutObjectWE> {
	
	private Concept cnpUp;
	private HashMap<String, Object> map;
	private Double[] vec;
	private double mediaT;
	
	public OutObjectWE(int size) {
		cnpUp = null;
		map = new HashMap<String, Object>();
		vec = new Double[size];
		mediaT = 0;
	}
	
	public Concept get_topConcept() {
		return this.cnpUp;
	}
	
	public HashMap<String, Object> get_map() {
		return this.map;
	}
	
	public Double[] get_vec() {
		return this.vec;
	}
	
	public double get_mediaTotal() {
		return this.mediaT;
	}
	
	public void set_topConcept(Concept cnp) {
		this.cnpUp = cnp;
	}
	
	public void set_map(HashMap<String, Object> _map) {
		this.map = _map;
	}
	
	public void set_vec(Double[] _vec) {
		this.vec = _vec;
	}
	
	public void set_mediaTotal(double media) {
		this.mediaT = media;
	}
	
	@SuppressWarnings("unchecked")
	public void info() {
		System.out.println("NAME: " + cnpUp.get_className());
		System.out.println("SIMILARIDADE:");
		int aux = 0;
		for(Entry<String, Object> entry: map.entrySet()) {
			String key = entry.getKey();
			HashMap<String, Double> value = (HashMap<String, Double>) entry.getValue();
			System.out.print("\t" + key + "=" + vec[aux] + " | ");
			
			for(Entry<String, Double> entr: value.entrySet()) {
				String ky = entr.getKey();
				double val = entr.getValue();
				System.out.print(ky + "=" + val + "; ");
			}
			aux++;
			System.out.println("\n");
		}
		System.out.println("TOTAL AVERAGE: " + mediaT);
	}

	@Override
	public int compareTo(OutObjectWE o) {
		if(this.mediaT > o.get_mediaTotal()) {
			return -1;
		} else if(this.mediaT < o.get_mediaTotal()) {
			return 1;
		}
		return 0;
	}
	

}

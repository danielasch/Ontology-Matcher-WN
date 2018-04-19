package matchingProcess;

public class Mapping {
	
	private String source;
	private String target;
	
	private String relation;
	private String measure;
	
	/*Mapping(String _source, String _target, String _relation, boolean _measure) {
		source = _source;
		target = _target;
		relation = _relation;
		measure = _measure; 
	}*/
	
	void set_source(String _source) {
		source = _source;
	}
	
	String get_source(){
		return source;
	}
	
	void set_target(String _target) {
		target = _target;
	}
	
	String get_target() {
		return target;
	}
	
	void set_relation(String _relation) {
		relation = _relation;
	}
	
	String get_relation() {
		return relation;
	}
	
	void set_measure(String _measure) {
		measure = _measure;
	}
	
	String get_measure() {
		return measure;
	}
}

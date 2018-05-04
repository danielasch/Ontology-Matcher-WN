package matchingProcess;

/*
 * This class maps the alignment between domain Ont. classes and top Ont. classes
 */
public class Mapping {
	
//Attributes
	
	//source entity
	private String source;
	//target entity
	private String target;
	//relation between source and target
	private String relation;
	//trust measure
	private String measure;

//Getters and setters 
	
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

package objects;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import edu.mit.jwi.item.ISynset;
import resources.Utilities;

/*
 * Concept class contains all the information about a OWLClass
 */
public class Concept {

//Attributes 	
	
	//OWLOntology
	private OWLOntology ontology;
	//OWLClass
	private OWLClass owlClass;
	//OWLOntology ID
	private String ontologyID;
	//OWLClass ID
	private String classID;
	//Ontology name
	private String ontologyName;
	//Class name
	private String className;
	//Concept annotation
	private String desc;
	//Concept context
	private Set<String> context;					
	// Auxiliary class to save the information about this concept 
	//to generate the text out file.
	private Utilities ut;
	//Super concepts list
	private List<OWLClassExpression> supers;
	//Sub concepts list
	private List<OWLClassExpression> subs;
	//Synset disambiguated
	private ISynset goodSynset;
	//OWLClass aligned to this concept
	private OWLClass aliClass;

//Constructor
	
	public Concept() {
		this.context = new HashSet<String>();
		this.ut = null;
	}

//Getters and setters	
	
	protected void set_owlOntology(OWLOntology onto) {
		this.ontology = onto; 
	}
	public OWLOntology get_owlOntology() {
		return this.ontology; 
	}
	
	protected void set_owlClass(OWLClass owlclass) {
		owlClass = owlclass; 
	}
	
	public OWLClass get_owlClass() {
		return owlClass;
	}
	
	void set_ontologyID(String _ontologyID) {
		ontologyID = _ontologyID;
	}
	
	String get_ontologyID() {
		return ontologyID;
	}

	void set_classID(String _classID) {
		classID = _classID;
	}
	
	public String get_classID() {
		return classID;
	}
	
	void set_ontologyName(String _ontologyName) {
		ontologyName = _ontologyName;
	}
	
	public String get_ontologyName() {
		return ontologyName;
	}
	
	void set_className(String _className) {
		className = _className;
	}
	
	public String get_className() {
		return className;
	}
	
	protected void set_desc(String _desc) {
		desc = _desc; 
	}
	
	public String get_desc() {
		return desc;
	}
	
	protected void set_context(Set<String> set) {
		context = set;
	}
	
	public Set<String> get_context() {
		return context;
	}
	
	protected void set_utilities(Utilities ut) {
		this.ut = ut; 
	}
	public Utilities get_utilities() {
		return this.ut; 
	}
	
	protected void set_supers(List<OWLClassExpression> _supers) {
		supers = _supers;
	}
	
	public List<OWLClassExpression> get_supers() {
		return supers;
	}
	
	protected void set_subs(List<OWLClassExpression> _subs) {
		subs = _subs;
	}
	
	public List<OWLClassExpression> get_subs() {
		return subs;
	}
	
	void set_goodSynset(ISynset _goodSynset) {
		goodSynset = _goodSynset;
	}
	
	public ISynset get_goodSynset() {
		return goodSynset;
	}
	
	void set_aliClass(OWLClass _aliclass) {
		aliClass = _aliclass; 
	}
	
	public OWLClass get_aliClass() {
		return aliClass;
	}

//Print the concept information method
	
	public void print_info() {
		
		System.out.println("Concept: " + this.className);
		System.out.println("Description: " + this.desc);

		if(this.context != null) {
			String out = "Context: ";
			Iterator<String> iterator = this.context.iterator();
			while(iterator.hasNext()) {
				String a = iterator.next();
				if(!iterator.hasNext()) {
					out = out + a + ".";
				} else {
					out = out + a + ", "; 
				}
			}
			System.out.println(out + "\n");
		} else {
			System.out.println("Context: null" + "\n");
		}
		
		if(this.goodSynset != null) {
			System.out.println("Synset: " + this.goodSynset.toString());
			System.out.println("Gloss: " + this.goodSynset.getGloss().toString() + "\n");
		} else {
			System.out.println("Synset: null" + "\n");
		}
	}

//Methods
	
	/*
	 * This method identify if the concept name is separated by under line, or hyphen, or UpperCase or 
	 *if the concept name is simple.
	 * Then separate the concept name, returning the last token or the simple term.
	 */
	protected String sp_conceptName() {
		String name = null;
		String cnpName = this.className;
		if(cnpName.contains("_")) {
			String words[];
			words = cnpName.split("_");
			int i = words.length;
			name = words[i - 1];	
		} else if(cnpName.contains("-")) {
			String words[];
			words = cnpName.split("-");
			int i = words.length;
			name = words[i - 1];
		} else if(hasUpperCase(cnpName)) {
			int x = cnpName.length();
			int up = 0;
			for(int y = 1; y < x; y++) {
				if(Character.isUpperCase(cnpName.charAt(y)) && y > up) {
					up = y;	
				}	
			}
			if(up != 0) {
				name = cnpName.substring(up);
			}
		} else {
			name = cnpName;
		}
		return name;
	}	
	
	/*
	 * This methods test if a string has UpperCase in its middle.
	 */
	private boolean hasUpperCase(String word) {
		
		int x = word.length();
		
		for(int y = 1; y < x; y++) {
			if(Character.isUpperCase(word.charAt(y))) {
				return true;
			}	
		}
		return false;	
	}
	
}

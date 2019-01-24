package conceptExtraction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import objects.Concept;
import objects.ConceptManager;
/*
 * This class extract the information about a concept from the ontology
 */
public class ContextExtraction {
	private int fullcontext;
	
//Constructor
	
	ContextExtraction(int fullcntx) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        this.fullcontext = fullcntx;
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Context method selected!" );
	}
	
	ContextExtraction() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        this.fullcontext = 0;
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Context method selected!" );
	}
	
//Log methods
	
	private void init_log() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Extracting domain ontology classes..." );
	}
	
	private void final_log() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Domain ontology classes Extracted!" );
	}
	
	private void init_log_upper() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Extracting upper ontology classes..." );
	}
	
	private void final_log_upper() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Upper ontology classes Extracted!" );
	}
	
//Methods
	
	protected List<Concept> extract_init(OWLOntology onto) {
		if(this.fullcontext == 0) {
			return extract(onto);
		} else if(this.fullcontext == 1) {
			return extract_full(onto);
		} else {
			System.out.println("**ERROR**");
			return null;
		}
	}
	
	/*
	 * Extracts the concept and its information from domain ontology
	 */
	private List<Concept> extract(OWLOntology onto) {
		init_log();
		//the list with all concepts extracted 
		List<Concept> listCon = new ArrayList<Concept>();
		ConceptManager man = new ConceptManager();
		
		for(OWLClass owlClass: onto.getClassesInSignature()) {
			if(!owlClass.isTopEntity()) {

				//instantiate the Concept class
				Concept concept = new Concept();
				Set<String> context = new HashSet<String>();
				String desc = null;
            	List<OWLClassExpression> listSup = new ArrayList<OWLClassExpression>();
            	List<OWLClassExpression> listSub = new ArrayList<OWLClassExpression>();
				
            	//extracts the super-owlclasses
				extract_superClass(onto, owlClass, context, listSup);

            	if(listSup.isEmpty() || verifyTHING(listSup)) { 
            		//sets the ontology into the concept class
            		man.config_owlOntology(concept, onto);
            		//sets the owlclass into the concept class
					man.config_owlClass(concept, owlClass);
					//sets the owlclassID into the concept class
					man.config_classId(concept, owlClass.toString());
					//sets the owlclass name into the concept class
					man.config_className(concept, owlClass.getIRI().getFragment());
            		//adds the concept name into the context
            		context.add(owlClass.getIRI().getFragment());
					//desc receive the annotation of a concept
					desc = extract_annotation(onto, owlClass, context);
					//sets the description into the concept class
					man.config_description(concept, desc);
						//extracts the super-owlclasses
						//extract_superClass(onto, owlClass, context, listSup);
					//extracts the sub-owlclasses
					extract_subClass(onto, owlClass, context, listSub);
					//sets the context list into a cocnept class
					man.config_context(concept, context);
					//sets the super-owlclasses list into a concept class
					man.config_supers(concept, listSup);
					//sets the sub-owlclasses list into a concept class
					man.config_subs(concept, listSub);
					//adds the Concept into a list
					listCon.add(concept);
            	}
			}
		}
		final_log();
		return listCon;
	}
	
	/*
	 * Extracts the concept and its information from domain ontology and sets the context of a concept as the full ontology
	 */
	private List<Concept> extract_full(OWLOntology onto) {
		init_log();
		//the list with all concepts extracted 
		List<Concept> listCon = new ArrayList<Concept>();
		ConceptManager man = new ConceptManager();
		Set<String> full = new HashSet<String>();
		
		for(OWLClass owlClass: onto.getClassesInSignature()) {
			full.add(owlClass.getIRI().getFragment());
			if(!owlClass.isTopEntity()) {

				//instantiate the Concept class
				Concept concept = new Concept();
				Set<String> context = new HashSet<String>();
				String desc = null;
            	List<OWLClassExpression> listSup = new ArrayList<OWLClassExpression>();
            	List<OWLClassExpression> listSub = new ArrayList<OWLClassExpression>();
				
            	//extracts the super-owlclasses
				extract_superClass(onto, owlClass, null, listSup);

            	if(listSup.isEmpty() || verifyTHING(listSup)) { 
            		//sets the ontology into the concept class
            		man.config_owlOntology(concept, onto);
            		//sets the owlclass into the concept class
					man.config_owlClass(concept, owlClass);
					//sets the owlclassID into the concept class
					man.config_classId(concept, owlClass.toString());
					//sets the owlclass name into the concept class
					man.config_className(concept, owlClass.getIRI().getFragment());
            		
					//desc receive the annotation of a concept
					desc = extract_annotation(onto, owlClass, context);
					//sets the description into the concept class
					man.config_description(concept, desc);
					//extracts the sub-owlclasses
					extract_subClass(onto, owlClass, null, listSub);
					//sets the context list into a cocnept class
					man.config_context(concept, context);
					//sets the super-owlclasses list into a concept class
					man.config_supers(concept, listSup);
					//sets the sub-owlclasses list into a concept class
					man.config_subs(concept, listSub);
					//adds the Concept into a list
					listCon.add(concept);
            	}
			}
		}
		setter_full_context(listCon, full);
		final_log();
		return listCon;
	}
	
	private void setter_full_context(List<Concept> listCon, Set<String> full) {
		for(Concept cnp: listCon) {
			cnp.get_context().addAll(full);
		}
	}
	
	/*
	 * Extracts the concept and its information from top ontology
	 */
	protected List<Concept> extract_upper(OWLOntology onto) {
		init_log_upper();
		List<Concept> listCon = new ArrayList<Concept>();
		ConceptManager man = new ConceptManager();
		
		for(OWLClass owlClass: onto.getClassesInSignature()) {
			//instantiate the Concept class
			Concept concept = new Concept();
			String desc = null;
			//sets the ontology into the concept class
            man.config_owlOntology(concept, onto);
            //sets the owlclass into the concept class
			man.config_owlClass(concept, owlClass);
			//sets the owlclassID into the concept class
			man.config_classId(concept, owlClass.toString());
			//sets the owlclass name into the concept class
			man.config_className(concept, owlClass.getIRI().getFragment());
			//desc receive the annotation of a concept
			desc = extract_annotation_upper(onto, owlClass);
			//sets the description into the concept class
			man.config_description(concept, desc);
			//adds the concept into a list
			listCon.add(concept);
		}
		final_log_upper();
		return listCon;
	}
	
	protected List<Concept> extract_upperWE(OWLOntology onto) {
		init_log_upper();
		List<Concept> listCon = new ArrayList<Concept>();
		ConceptManager man = new ConceptManager();
		
		for(OWLClass owlClass: onto.getClassesInSignature()) {
		
			if(!owlClass.isOWLThing()) { //rodando com toda ontologia
				// instantiate the Concept class
				Concept concept = new Concept();
				Set<String> context = new HashSet<String>();
				String desc = null;

				// sets the ontology into the concept class
				man.config_owlOntology(concept, onto);
				// sets the owlclass into the concept class
				man.config_owlClass(concept, owlClass);
				// sets the owlclassID into the concept class
				man.config_classId(concept, owlClass.toString());
				// sets the owlclass name into the concept class
				man.config_className(concept, owlClass.getIRI().getFragment());
				// desc receive the annotation of a concept
				desc = extract_annotation_upperWE(onto, owlClass, context);
				// sets the description into the concept class
				man.config_description(concept, desc);

				context.add(owlClass.getIRI().getFragment());
				man.config_context(concept, context);

				// adds the concept into a list
				listCon.add(concept);
			}
		}
		final_log_upper();
		return listCon;
	}
	
	/*
	 * Extracts the annotation of the top onto. concept
	 */
	private String extract_annotation_upperWE(OWLOntology onto, OWLClass cls, Set<String> context) {
		String desc = null;
		for(OWLAnnotation anno: cls.getAnnotations(onto)) {
			//get the annotation at comment label
			if(anno.getProperty().getIRI().getFragment().equals("comment") && anno != null && context != null) { 
				String aux = anno.getValue().toString();
				if(aux != null) {
					desc = rm_suffix(aux);
					context.add(desc);			
				}
			} 
		}	
		return desc;
	}
	
	private String extract_annotation_upper(OWLOntology onto, OWLClass cls) {
		String aux = null;
		for(OWLAnnotation anno: cls.getAnnotations(onto)) {
		aux = anno.getValue().toString();
		}
		return aux;
	}
	/*
	 * Extracts the annotation of the domain ont. concept
	 */
	private String extract_annotation(OWLOntology onto, OWLClass cls, Set<String> cntxt) {
		String desc = null;
		for(OWLAnnotation anno: cls.getAnnotations(onto)) {
			String aux2 = null;
			//get the annotation at comment label
			if(anno.getProperty().getIRI().getFragment().equals("comment") && anno != null) { 
				String aux = anno.getValue().toString();
				aux = rm_suffix(aux);
				aux2 = aux;
        	
				aux = remove_specialChar(aux);
				cntxt.add(aux);
			//get the annotation at definition label
			} else if(anno.getProperty().getIRI().getFragment().equals("definition") && anno != null) {
				String aux = anno.getValue().toString();
				aux = rm_suffix(aux);
				aux2 = aux;
        	
				aux = remove_specialChar(aux);
				cntxt.add(aux);
			//get the annotation at example label
			} else if(anno.getProperty().getIRI().getFragment().equals("example") && anno != null) {
				String aux = anno.getValue().toString();
				aux = rm_suffix(aux);
				aux2 = aux;
        	
				aux = remove_specialChar(aux);
				cntxt.add(aux);
			}
			//condition that avoid the apparition of null and repeated elements into the concept description 
			if(desc == null && aux2 != null) {
				desc = aux2;
			} else if((desc != null && aux2 != null) && !desc.contains(aux2)) {
				desc = desc + aux2;
			}
			
        }
		return desc;
	}
	
	/*
	 * Extracts the super classes of a owlClass
	 */
	protected void extract_superClass(OWLOntology onto, OWLClass cls, Set<String> cntxt, List<OWLClassExpression> list) {
		for(OWLClassExpression sup: cls.getSuperClasses(onto)) {
        	if(!sup.isAnonymous()) {
        		if(cntxt != null) {
        			cntxt.add(sup.asOWLClass().getIRI().getFragment().toString());
        		}
        		list.add(sup);
        		
        		extract_superRecurClass(onto, sup, cntxt, list);	 	
        	}	
		}
	}
	
	/*
	 * Recursive call of extract_superClass
	 */
	protected void extract_superRecurClass(OWLOntology onto, OWLClassExpression su, Set<String> cntxt, List<OWLClassExpression> list) {	
		if(su != null) {
			for(OWLClassExpression sup: su.asOWLClass().getSuperClasses(onto)) {
        	
				if(!sup.isAnonymous()) {					
					list.add(sup);
					if(cntxt != null) {
	        			cntxt.add(sup.asOWLClass().getIRI().getFragment().toString());
	        		}
					
					extract_superRecurClass(onto, sup, cntxt, list); 	
				}	
			}
		}		
	}
	
	/*
	 * Extracts the sub classes of a owlClass
	 */
	protected void extract_subClass(OWLOntology onto, OWLClass cls, Set<String> cntxt, List<OWLClassExpression> list) {
		for(OWLClassExpression sub: cls.getSubClasses(onto)) {
        	if(!sub.isAnonymous()) {
        		if(cntxt != null) {
        			cntxt.add(sub.asOWLClass().getIRI().getFragment().toString());
        		}
        		list.add(sub);
        		
        		extract_subRecurClass(onto, sub, cntxt, list);
        		
        	}
        }
	}
	
	/*
	 * Recursive call of extract_subClass
	 */
	protected void extract_subRecurClass(OWLOntology onto, OWLClassExpression su, Set<String> cntxt, List<OWLClassExpression> list) {
		if(su != null) {
			for(OWLClassExpression sub: su.asOWLClass().getSubClasses(onto)) {				
				if(!sub.isAnonymous()) {
					if(cntxt != null) {
	        			cntxt.add(sub.asOWLClass().getIRI().getFragment().toString());
	        		}
	        		list.add(sub);
	        		
	        		extract_subRecurClass(onto, sub, cntxt, list);
				}
			}
		}
	}
	
//Auxiliary methods 
	
	/*
	 * Removes the suffix of the annotation 
	 */
	private String rm_suffix(String aux) {
		if(aux.endsWith("^^xsd:string")) {
			aux = aux.replace("^^xsd:string", "");
		}
		if(aux.endsWith("@en")) {
			aux = aux.replace("@en", "");
		}
		
		return aux;
	}
	
	/*
	 * Verifies if the list contains owl:Thing
	 */
	private boolean verifyTHING(List<OWLClassExpression> list) {
		if(list.get(0).asOWLClass().isTopEntity()) {
			return true;
		}
		return false;
	}
	
	/*
	 * Removes some chars of a string
	 */
	private String remove_specialChar(String word) {
		
		if(!isSite(word)) {
			String aux = word;
			char x = '"';
			String z = String.valueOf(x);
    	
			if(aux.contains("  ")) {
    		aux = aux.replaceAll("  ", " ");
			}

			if(aux.contains(z)) {
    		aux = aux.replace(z, "");
			}
    	
			if(aux.contains(".")) {
    		aux = aux.replace(".", " ");
			}
    	
			if(aux.contains(",")) {
    		aux = aux.replace(",", "");
			}
    	
			if(aux.contains("?")) {		
    		aux = aux.replace("?", " ");
			}
    	
			if(aux.contains(":")) {
    		aux = aux.replace(":", " ");
			}
    	
			if(aux.contains("!")) {
    		aux = aux.replace("!", " ");
			}
    	
			if(aux.contains("  ")) {
    		aux = aux.replaceAll("  ", " ");
			}
			return aux;
		}
		return word;
	}
	
	private boolean isSite(String word) {
		if(word.contains("http:")) {
			return true;
		}
		return false;
	}
}

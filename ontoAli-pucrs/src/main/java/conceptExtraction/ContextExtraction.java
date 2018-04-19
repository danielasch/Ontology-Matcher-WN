package conceptExtraction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import objects.Concept;
import objects.ConceptManager;

public class ContextExtraction {
	
	ContextExtraction() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Context method selected!" );
	}
	//*info logs*//
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
	
	//*process*//
	protected List<Concept> extract(OWLOntology onto) {
		init_log();
		List<Concept> listCon = new ArrayList<Concept>();
		ConceptManager man = new ConceptManager();
		
		for(OWLClass owlClass: onto.getClassesInSignature()) {
			Concept concept = new Concept();
			Set<String> context = new HashSet<String>();
			String desc = null;
            TreeMap<String, Integer> distance = new TreeMap<String, Integer>();
            List<OWLClassExpression> listSup = new ArrayList<OWLClassExpression>();
            List<OWLClassExpression> listSub = new ArrayList<OWLClassExpression>();
			
            man.config_owlOntology(concept, onto);
			man.config_owlClass(concept, owlClass);
			man.config_classId(concept, owlClass.toString());
			man.config_className(concept, owlClass.getIRI().getFragment());
			
            distance.put(owlClass.getIRI().getFragment(), 0);
            context.add(owlClass.getIRI().getFragment());
			
			desc = extract_annotation(onto, owlClass, context);
			man.config_description(concept, desc);
			
			extract_superClass(onto, owlClass, context, distance, listSup);
			extract_subClass(onto, owlClass, context, distance, listSub);
			man.config_context(concept, context);
			man.config_supers(concept, listSup);
			man.config_subs(concept, listSub);
			man.config_distance(concept, distance);
			
			listCon.add(concept);
		}
		final_log();
		return listCon;
	}
	
	protected List<Concept> extract_upper(OWLOntology onto) {
		init_log_upper();
		List<Concept> listCon = new ArrayList<Concept>();
		ConceptManager man = new ConceptManager();
		
		for(OWLClass owlClass: onto.getClassesInSignature()) {
			Concept concept = new Concept();
			String desc = null;
			
            man.config_owlOntology(concept, onto);
			man.config_owlClass(concept, owlClass);
			man.config_classId(concept, owlClass.toString());
			man.config_className(concept, owlClass.getIRI().getFragment());
			
			desc = extract_annotation_upper(onto, owlClass);
			man.config_description(concept, desc);
			listCon.add(concept);
		}
		final_log_upper();
		return listCon;
	}
	
	private String extract_annotation_upper(OWLOntology onto, OWLClass cls) {
		String aux = null;
		for(OWLAnnotation anno: cls.getAnnotations(onto)) {
        	aux = anno.getValue().toString();
        }
		return aux;
	}
	
	private String extract_annotation(OWLOntology onto, OWLClass cls, Set<String> cntxt) {
		String desc = null;
		for(OWLAnnotation anno: cls.getAnnotations(onto)) {
			String aux2 = null;
			if(anno.getProperty().getIRI().getFragment().equals("comment") && anno != null) { 
				String aux = anno.getValue().toString();
				aux = rm_suffix(aux);
				aux2 = aux;
        	
				aux = remove_specialChar(aux);
				cntxt.add(aux);
			} else if(anno.getProperty().getIRI().getFragment().equals("definition") && anno != null) {
				String aux = anno.getValue().toString();
				aux = rm_suffix(aux);
				//cnp.set_desc(aux);
				aux2 = aux;
        	
				aux = remove_specialChar(aux);
				cntxt.add(aux);
			} else if(anno.getProperty().getIRI().getFragment().equals("example") && anno != null) {
				String aux = anno.getValue().toString();
				aux = rm_suffix(aux);
				//cnp.set_desc(aux);
				aux2 = aux;
        	
				aux = remove_specialChar(aux);
				cntxt.add(aux);
			}
			//condicao que previne que apareca null, elementos repetidos na descricao do conceito//
			if(desc == null && aux2 != null) {
				desc = aux2;
			} else if ((desc != null && aux2 != null) && !desc.contains(aux2)) {
				desc = desc + aux2;
			}
			
        }
		return desc;
	}
	
	protected void extract_superClass(OWLOntology onto, OWLClass cls, Set<String> cntxt, Map<String, Integer> dis, List<OWLClassExpression> list) {
		int cont = 0;
		for(OWLClassExpression sup: cls.getSuperClasses(onto)) {
        	if(!sup.isAnonymous()) {					   		
        		cont++;
        		cntxt.add(sup.asOWLClass().getIRI().getFragment().toString());
        		dis.put(sup.asOWLClass().getIRI().getFragment(), cont);
        		list.add(sup);
        		
        		extract_superRecurClass(onto, sup, cntxt, dis, cont, list);	
                	
        	}	
		}
		
	}
	
	
	protected void extract_superRecurClass(OWLOntology onto, OWLClassExpression su, Set<String> cntxt, Map<String, Integer> dis, int cont, List<OWLClassExpression> list) {	
		if(su != null) {
			cont++;
			for(OWLClassExpression sup: su.asOWLClass().getSuperClasses(onto)) {
        	
				if(!sup.isAnonymous()) {					
					list.add(sup);
					cntxt.add(sup.asOWLClass().getIRI().getFragment().toString());
					dis.put(sup.asOWLClass().getIRI().getFragment(), cont);
					
					extract_superRecurClass(onto, sup, cntxt, dis, cont, list); 	
				}	
			}
		}		
	}
	
	protected void extract_subClass(OWLOntology onto, OWLClass cls, Set<String> cntxt, Map<String, Integer> dis, List<OWLClassExpression> list) {
		int cont = 0;
		cont--;
		for(OWLClassExpression sub: cls.getSubClasses(onto)) {
        	if(!sub.isAnonymous()) {
        		cntxt.add(sub.asOWLClass().getIRI().getFragment().toString());
        		dis.put(sub.asOWLClass().getIRI().getFragment(), cont);
        		list.add(sub);
        		
        		extract_subRecurClass(onto, sub, cntxt, dis, cont, list);
        		
        	}
        }
	}
	
	protected void extract_subRecurClass(OWLOntology onto, OWLClassExpression su, Set<String> cntxt, Map<String, Integer> dis, int cont, List<OWLClassExpression> list) {
		if(su != null) {
			cont--;
			for(OWLClassExpression sub: su.asOWLClass().getSubClasses(onto)) {
				
				if(!sub.isAnonymous()) {
	        		list.add(sub);
	        		cntxt.add(sub.asOWLClass().getIRI().getFragment().toString());
	        		dis.put(sub.asOWLClass().getIRI().getFragment(), cont);
	        		
	        		extract_subRecurClass(onto, sub, cntxt, dis, cont, list);
				}
			}
		}
	}
	
	//* Funções auxiliares *//
	private String rm_suffix(String aux) {
		if(aux.endsWith("^^xsd:string")) {
			aux = aux.replace("^^xsd:string", "");
		}
		if(aux.endsWith("@en")) {
			aux = aux.replace("@en", "");
		}
		
		return aux;
	}
	
	private String remove_specialChar(String word) {
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
	
	
}

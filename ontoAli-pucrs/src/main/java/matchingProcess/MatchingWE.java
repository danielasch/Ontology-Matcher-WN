package matchingProcess;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import objects.Concept;
import objects.ConceptManager;
import objects.Ontology;
import resources.BaseResource;

	
/*
 * This class matches Domain Ont. classes with Top Ont. classes
 */
public class MatchingWE {

	//Attributes
		
		//Map list
		private List<Mapping> listMap;
		//path to write the rdf file
		private String localfile;
		
		private BaseResource baseresource;
	//Constructor	
		
		public MatchingWE(String _file, BaseResource br) {
			log();
			listMap = new ArrayList<Mapping>();
			localfile = _file;
			baseresource = br;
		}

	//Log methods	
		
		private void log() {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Word Embeddings Matcher selected!" );
		}
		
		private void init_log() {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Matching ontologies..." );
		}
		
		private void final_log() {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Ontologies matched!" );
		}
		
		private void out_log() {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - RDF file generated!" );
		}

	//Methods
		
		/*
		 * Turn the mapping class into a string
		 * to write the rdf file
		 */
		private String toRDF(Mapping m) {
			
			String out = "\t<map>\n" +
					"\t\t<Cell>\n" +
					"\t\t\t<entity1 rdf:resource='"+ m.get_target() +"'/>\n" +
					"\t\t\t<entity2 rdf:resource='"+ m.get_source() +"'/>\n" +
					"\t\t\t<relation>" + m.get_relation().toString() + "</relation>\n" +
					"\t\t\t<measure rdf:datatype='http://www.w3.org/2001/XMLSchema#float'>"+ m.get_measure() +"</measure>\n" +
					"\t\t</Cell>\n" + "\t</map>\n";
			return out;		
		}
		
		/*
		 * Writes the rdf file
		 */
		public void out_rdf(Ontology onto1, Ontology onto2) {
			
			try {
				FileWriter arq = new FileWriter(localfile);
				PrintWriter print = new PrintWriter(arq);
			
				print.print("<?xml version='1.0' encoding='utf-8' standalone='no'?>\n" + 
							"<rdf:RDF xmlns='http://knowledgeweb.semanticweb.org/heterogeneity/alignment#'\n" +
							"\t\t xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'\n" +
							"\t\t xmlns:xsd='http://www.w3.org/2001/XMLSchema#'\n" + 
							"\t\t xmlns:align='http://knowledgeweb.semanticweb.org/heterogeneity/alignment#'>\n");
			
				print.print("<Alignment>\n" + 
							"\t<xml>yes</xml>\n" + 
							"\t<level>0</level>\n" + 
							"\t<type>11</type>\n");
			
				print.print("\t<onto1>\n" + "\t\t<Ontology rdf:about=" + '"' + onto2.get_ontologyID().getOntologyIRI().toString() + '"' + ">\n" + 
							"\t\t\t<location>file:" + onto2.get_fileName() + "</location>\n" + 
								"\t\t</Ontology>\n" + "\t</onto1>\n");
			
				print.print("\t<onto2>\n" + "\t\t<Ontology rdf:about=" + '"' + onto1.get_ontologyID().getOntologyIRI().toString() + '"' + ">\n" + 
					"\t\t\t<location>file:" + onto1.get_fileName() + "</location>\n" + 
						"\t\t</Ontology>\n" + "\t</onto2>\n");
			
				for(Mapping m: listMap) {
					if(!m.get_measure().equals("false")) {
						print.print(toRDF(m));
					}
				}
			
				print.print("</Alignment>\n" + "</rdf:RDF>");
			
				arq.close();
				out_log();
			} catch(IOException e) {
				System.out.println("Operacão I/O interrompida, no arquivo de saída .RDF!");
		    	System.out.println("erro: " + e);
				
			}
		}

	//Methods
		
		/*
		 * extract the supper class of a OWLClass
		 */
		private OWLClassExpression extract_superClass(OWLClass cls, OWLOntology onto) {
			
			OWLClassExpression last = null;
			for(OWLClassExpression sup: cls.getSuperClasses(onto)) {
	        	
	        	if(!sup.isAnonymous()) {					
	        		last = extract_superRecurClass(sup, onto);
	        		if(last == null) {
	        			last = sup;
	        		}
	        	}	
			}
			return last;
		}
			
		private OWLClassExpression extract_superRecurClass(OWLClassExpression su, OWLOntology onto) {	
			OWLClassExpression last = null;
			if(su != null) {

				for(OWLClassExpression sup: ((OWLClass) su).getSuperClasses(onto)) {
	        	
					if(!sup.isAnonymous()) {					
						last = extract_superRecurClass(sup, onto);
						if(last == null) {
							last = sup;
						}
					}	
				}
			}
			return last;
		}
		
		public void match(List<Concept> listDom, List<Concept> listUp) {
			
			for(Concept cnpDom: listDom) {
				//System.out.println("============CNPDOM==============");
				//System.out.println(cnpDom.get_className());
				Set<String> contextDom = cnpDom.get_context();
				double max = 0;
				Concept align = null;
				ConceptManager man = new ConceptManager();
			
				for(Concept cnpUp: listUp) {
					//System.out.println("============CNPUP==============");
					//System.out.println(cnpUp.get_className());
					 Set<String> contextUp = cnpUp.get_context();
					 double mediaT = 0;
					 int sizeDom = contextDom.size();
					 
					 for(String elDom: contextDom) {
						 double media = 0;
						 int sizeUp = contextUp.size();
						 
						 for(String elUp:contextUp) {
							 double sim = this.baseresource.get_word2vec().get_word2Vec().similarity(elDom, elUp);
							 //System.out.println(elDom + " | " + elUp + "=" + sim);
							 if(!(Double.isNaN(sim))) {
					    			//increment the media and adds the similarity
				                    media = media + (sim * 10);
				                } else {
				                	//case similarity is null
				                    media = media + 0;
				                }
						 }
						media = media / sizeUp;
						mediaT = mediaT + media;
					 }
					 mediaT = mediaT / sizeDom;
					 if(mediaT > max) {
						 max = mediaT;
						 align = cnpUp;
					 }
				}
				Mapping map = new Mapping();
				man.config_aliClass(cnpDom, align.get_owlClass());
				map.set_source(cnpDom.get_classID());
				map.set_target(align.get_classID());
				map.set_measure("1.0");
				map.set_relation("&lt;");
				this.listMap.add(map);
				System.out.println("==========ALIGN==========");
				System.out.println(cnpDom.get_classID());
				System.out.println(align.get_classID());
				
			}
			
		}

}

package matchingProcess;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import edu.mit.jwi.item.ISynset;
import objects.Concept;
import objects.ConceptManager;
import objects.Ontology;


/*
 * This class matches Domain Ont. classes with Top Ont. classes
 */
public class Matching {

//Attributes
	
	//Map list
	private List<Mapping> listMap;
	//path to write the rdf file
	private String localfile;

//Constructor	
	
	public Matching(String _file) {
		log();
		listMap = null;
		localfile = _file;
	}

//Log methods	
	
	private void log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Matcher selected!" );
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
	
	/*
	 * Search for the alignment in DOLCE and DUL and generates the mapping	
	 */
	public void compare_dolce(List<Concept> domain, List<Concept> upper) {
		//creates a local list of mapping class
		List<Mapping> listM = new ArrayList<Mapping>();
		ConceptManager man = new ConceptManager();
		//last will reaceive the concept of higher level of DOLCE
		OWLClassExpression last = null;
		//owlUpper receive the OWLOntology of DOLCE
		OWLOntology owlUpper = upper.get(0).get_owlOntology();
		
		init_log();
		
		for(Concept cnp_1: domain) {
			//instantiate the mapping class
			Mapping map = new Mapping();
			//synset receive the disambiguated synset of cnp_1
			ISynset synset = cnp_1.get_goodSynset();
			
			if(synset != null) {
				//cls will receive the OWLClass aligned to cnp_1
				OWLClass cls = new_search(cnp_1, upper);
				
				if(cls != null) {
					//last receive the higher level concept of the top onto. concept
					last = extract_superClass(cls, owlUpper);
					
					if(last != null) {
						//sets the alignment of cnp_1
						man.config_aliClass(cnp_1, last.asOWLClass());
					} else {
						//sets the alignment of cnp_1
						man.config_aliClass(cnp_1, cls);
					}
					//sets the mapping source
					map.set_source(cnp_1.get_owlClass().getIRI().toString());
					//sets the mapping target
					map.set_target(cnp_1.get_aliClass().getIRI().toString());
					//sets the mapping realtion
					map.set_relation("&lt;");
					//sets the mapping measure
					map.set_measure("1.0");
					//add the mapping into a list
					listM.add(map);
				} else {
					//sets the alignment of cnp_1 as null
					man.config_aliClass(cnp_1, null);

					map.set_source(cnp_1.get_classID());
					map.set_target("null");
					map.set_relation("null");
					map.set_measure("false");
					//add the mapping into a list
					listM.add(map);
				}
			} else {

				map.set_source(cnp_1.get_classID());
				map.set_target("null");
				map.set_relation("null");
				map.set_measure("false");
				//add the mapping into a list
				listM.add(map);
			}
		}
		//listMap receive the local list of mappings
		this.listMap = listM;
		final_log();
	}
	
	/*
	 * Search for the Top ontology class that matches the synset selected,
	 * comparing the description of the Top Ont. concept and the gloss of the synset. 
	 */
	private OWLClass new_search(Concept cnp_1, List<Concept> topConceptList) {			
		OWLClass clss = null;
		Set<String> glossList = new HashSet<String>();
		//gloss receive the gloss of the synset of cnp_1
		String gloss = cnp_1.get_goodSynset().getGloss();

		int max = 0;
		//glossList contains all tokens of gloss
		glossList = sp_string(gloss);
		//removes some chars of glossList
		glossList = rm_specialChar(glossList);
		
		for(Concept cnp_2: topConceptList) {
			if(cnp_2.get_desc() != null) {
				
				Set<String> descList = new HashSet<String>();
				int size = 0;
				//desc receive the description of cnp_2 (top onto. concept)
				String desc = cnp_2.get_desc();
				//descList contains all tokens of desc
				descList = sp_string(desc); 
				//removes some chars of DescList
				descList = rm_specialChar(descList);
				//size will receive the numbers of overlaps between two list
				size = intersection(glossList, descList);
				//size must be different than 1, to avoid intersection with one overlap 
				if(size != 1) {	
					if(size > max) {
						max = size;
						clss = cnp_2.get_owlClass();
					}
				}			 
			}
			
		}
		return clss;
	}
	
	/*
	 * removes some characters of a set of strings
	 */
	private Set<String> rm_specialChar(Set<String> list) {
		
		Set<String> temp = new LinkedHashSet<String>();
		char x = '"';
		String z = String.valueOf(x);
		for(String word: list) {
			
			if(word.contains(z)) {
				word = word.replace(z, "");
			}
			
			if(word.endsWith("-")) {
				word = word.replace("-", "");
			}
		
			if(word.contains("(")) {
				word = word.replace("(", "");
			}
        
			if(word.contains(")")) {
				word = word.replace(")", "");
			}
        
			if(word.contains(",")) {
				word = word.replace(",", "");
			}
        
			if(word.contains(":")) {
				word = word.replace(":", "");
			}        
        
			if(word.contains("'s")) {
				word = word.replace("'s", "");
			}
        
			if(word.contains("'")) {
				word = word.replace("'", "");
			}
        
			if(word.contains("?")) {
				word = word.replace("?", "");
			}
        
			if(word.contains("!")) {
				word = word.replace("!", "");
			}
        
			if(word.contains(".")) {
				word = word.replace(".", "");
			}
        
			if(word.contains(";")) {
				word = word.replace(";", "");
			}
			
			if(word.contains("\\")) {
				word = word.replace("\\", "");
			}
			temp.add(word);
		}
		list.clear();
		list = temp;
        return list;	
	}
	
	/*
	 * Split strings separated by space or �
	 */
	private Set<String> sp_string(String str) {
		Set<String> temp= new HashSet<String>();

			String[] split = str.split(" |�");	
			
			for(String strSplit: split) {
				temp.add(strSplit.toLowerCase());
			}
			return temp;
		}
	
	/*
	 * Overlapping between two lists
	 */
	private int intersection(Set<String> list_1, Set<String> list_2) {
		int inter = 0;
		
		for(String word_1: list_1) {
			
			word_1 = word_1.toLowerCase();
			for(String word_2: list_2) {
				
				if(word_1.equals(word_2)) {
					inter++;
					break;
				}
			}	
		}
		return inter;
	}
	
	/*
	 * Search for the alignment in SUMO
	 */
	public void compare_sumo(List<Concept> domain, List<Concept> upper) {
		//creates a local list of mapping class
		List<Mapping> listM = new ArrayList<Mapping>();
		ConceptManager man = new ConceptManager();
		//last will receive the concept of higher level of SUMO
		OWLClass last = null;
		init_log();
		for(Concept cnp_1: domain) {
			//instantiate the mapping class
			Mapping map = new Mapping();
			//synset receive the disambiguated synset of cnp_1
			ISynset synset = cnp_1.get_goodSynset();

			if(synset != null) {
				//code receive the synset code
				String code = "" + synset.getOffset();
			
				//synset code fixation
				code = code_fixation(code);
				//reads the WordNet mapping to SUMO
				try {
					BufferedReader br = new BufferedReader(new FileReader("resources/WordNetMappings30-noun.txt"));
					String line = "init";

					while ((line = br.readLine()) != null) {
						
						if(line.startsWith(code)) {
							String aux = null;
							//aux receive the name and the relation of the concept aligned to the synset of cnp_1
							aux = rc_alignment(line);
							//set mapping source
							map.set_source(cnp_1.get_owlClass().getIRI().toString());
							//aux receive the name of the concept aligned to the synset
							last = rc_upperConcept(aux, upper);
							
							if(last != null) {
								//sets Top Ont. concept align class of cnp_1 as last
								man.config_aliClass(cnp_1, last);
								//sets the mapping target
								map.set_target(cnp_1.get_aliClass().getIRI().toString());
								//sets the relation
								if(aux.endsWith("=")) {
									map.set_relation("&lt");
								} else if(aux.endsWith("+")) {
									map.set_relation("&gt;");
								}
								map.set_measure("1.0");
								//add the mapping into a list
								listM.add(map);
								break;
							//sets the mapping for last == null	
							} else {
								man.config_aliClass(cnp_1, null);
								map.set_source(cnp_1.get_owlClass().getIRI().toString());
								map.set_target("null");
								map.set_relation("null");
								map.set_measure("false");
								//add the mapping into a list
								listM.add(map);
							}
						}
					}
					br.close();
				} catch(FileNotFoundException e) {
			    	System.out.println("WordNetMappings30-ouns.txt file not found!");
			    	System.out.println("erro: " + e);
			    } catch (IOException e) {
			    	System.out.println("I/O operation failed on WordNetMappings30-ouns.txt file!");
			    	System.out.println("erro: " + e);
			    }
			//set the mapping for synset == null		
			} else {
			//sets the align Class of cnp_1 as null	
			man.config_aliClass(cnp_1, null);
			
			map.set_source(cnp_1.get_owlClass().getIRI().toString());
			map.set_target("null");
			map.set_relation("null");
			map.set_measure("false");
			//add the mapping into a list
			listM.add(map);
			}
		}
		//listMap receive the local list of mappings
		this.listMap = listM;
		final_log();
	}
	
	/*
	 * Recover OWLClass that has the same name as the line
	 */
	private OWLClass rc_upperConcept(String line, List<Concept> upper) {
		if(line.endsWith("=")) {
			line = line.replace("=", "");
		} else if(line.endsWith("+")) {
			line = line.replace("+", "");
		}
		for(Concept cnp_2: upper) {
			if(cnp_2.get_className().toLowerCase().equals(line.toLowerCase())) {
				return cnp_2.get_owlClass();
			}
		}
		return null;
	}
	
	/*
	 * Fix the synset code, turning the code into 8 digits.
	 */
	private String code_fixation(String code) {
		if(code.length() == 7) {
			code = "0" + code;
		} else if(code.length() == 6) {
			code = "00" + code;
		} else if(code.length() == 5) {
			code = "000" + code;
		} else if(code.length() == 4) {
			code = "0000" + code;
		} else if(code.length() == 3) {
			code = "00000" + code;
		} else if(code.length() == 2) {
			code = "000000" + code;
		} else if(code.length() == 1) {
			code = "0000000" + code;
		}
		return code;
	}
	
	/*
	 * Recover the name of the alignment class in the text file
	 */
	private String rc_alignment(String line) {
		String aux;
		int i = line.indexOf("&%");
		aux = line.substring(i);
		aux = aux.replaceFirst("&%", "");
		return aux;
	}
	
}


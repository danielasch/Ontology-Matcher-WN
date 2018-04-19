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

public class Matching {

	private List<Mapping> listMap;
	private String localfile;
	
	public Matching(String _file) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Matcher selected!" );
		listMap = null;
		localfile = _file;
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
					print.print(toRDF(m));				//adc if
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
	//funções para DOLCE//
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
		
	public void compare_dolce(List<Concept> domain, List<Concept> upper) {
		List<Mapping> listM = new ArrayList<Mapping>();
		ConceptManager man = new ConceptManager();
		OWLClassExpression last = null;
		OWLOntology owlUpper = upper.get(0).get_owlOntology();
		init_log();
		for(Concept cnp_1: domain) {
		
			Mapping map = new Mapping();
			ISynset synset = cnp_1.get_goodSynset();
			//cnp_1.print_info();

			if(synset != null) {
				OWLClass cls = new_search(cnp_1, upper);
			
				if(cls != null) {
					//System.out.println("Ali Concept: " + cls.getIRI().getFragment().toString());
					last = extract_superClass(cls, owlUpper);
				
					if(last != null) {
						//System.out.println("Ali Concept Top: " + last.asOWLClass().getIRI().getFragment().toString());
						man.config_aliClass(cnp_1, last.asOWLClass());
						//cnp_1.set_aliClass(last.asOWLClass());
					} else {
						man.config_aliClass(cnp_1, cls);
						//cnp_1.set_aliClass(cls);
					}
			
					map.set_source(cnp_1.get_owlClass().getIRI().toString());
					map.set_target(cnp_1.get_aliClass().getIRI().toString());		//alterado de ali.toString || alterado de get_classID
					map.set_relation("&lt;");
					//map.set_relation("=");
					//map.set_relation("<");
					//map.set_measure("true");
					map.set_measure("1.0");
			
					//System.out.println("-----------");
					listM.add(map);
				} else {
					//System.out.println("Ali Concept: " + "null");
					man.config_aliClass(cnp_1, null);
					//cnp_1.set_aliClass(null);
					map.set_source(cnp_1.get_classID());
					map.set_target("null");
					map.set_relation("null");
					map.set_measure("false");
				
					//System.out.println("-----------");
					listM.add(map);
				}
			} else {

				map.set_source(cnp_1.get_classID());
				map.set_target("null");
				map.set_relation("null");
				map.set_measure("false");
			
				//System.out.println("-----------");
				listM.add(map);
			}
		}
		this.listMap = listM;
		final_log();
	}
		
	private OWLClass new_search(Concept cnp_1, List<Concept> topConceptList) {			
		OWLClass clss = null;
		Set<String> glossList = new HashSet<String>();
		String gloss = cnp_1.get_goodSynset().getGloss();

		int max = 0;
		glossList = sp_string(gloss);
		glossList = rm_specialChar(glossList);
		//System.out.println(glossList);
		for(Concept cnp_2: topConceptList) {
			if(cnp_2.get_desc() != null) {
				
				Set<String> descList = new HashSet<String>();
				int size = 0;
				
				String desc = cnp_2.get_desc();
				descList = sp_string(desc); 
			
				descList = rm_specialChar(descList);
				size = intersection(glossList, descList);
				if(size != 1) {	//para evitar matching por uma palavra relacionada na intersecção 
					if(size > max) {
						max = size;
						clss = cnp_2.get_owlClass();
						//System.out.println(descList + " | " + size);
					}
				}			 
			}
			
		}
		return clss;
	}
	
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
				word = word.replace("'s", "");		//adc remo��o " 's "
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
	
	private Set<String> sp_string(String str) {
		Set<String> temp= new HashSet<String>();

			String[] split = str.split(" |�");	
			
			for(String strSplit: split) {
				temp.add(strSplit.toLowerCase());
			}
			return temp;
		}
	
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
	
	//funções para SUMO//
	public void compare_sumo(List<Concept> domain, List<Concept> upper) {
		List<Mapping> listM = new ArrayList<Mapping>();
		ConceptManager man = new ConceptManager();
		OWLClass last = null;
		init_log();
		for(Concept cnp_1: domain) {
			Mapping map = new Mapping();
			ISynset synset = cnp_1.get_goodSynset();

			if(synset != null) {
				String code = "" + synset.getOffset();
				code = code_fixation(code);
				try(BufferedReader br = new BufferedReader(new FileReader("resources/WordNetMappings30-noun.txt"))) {
					String line = "FIRST READ";			// = br.readLine();
					while ((line = br.readLine()) != null) {
						if(line.startsWith(code)) {
							String aux = null;
							//System.out.println(line);
							aux = rc_alignment(line);
							
							map.set_source(cnp_1.get_owlClass().getIRI().toString());	//*
							last = rc_upperConcept(aux, upper);
							if(last != null) {
								man.config_aliClass(cnp_1, last);
								
								map.set_target(cnp_1.get_aliClass().getIRI().toString());
							
								if(aux.endsWith("=")) {
									map.set_relation("&lt");
								} else if(aux.endsWith("+")) {
									map.set_relation("&gt;");
								}
								map.set_measure("1.0");
								listM.add(map);
								break;
							} else {
								man.config_aliClass(cnp_1, null);
								map.set_source(cnp_1.get_owlClass().getIRI().toString());
								map.set_target("null");
								map.set_relation("null");
								map.set_measure("false");
								listM.add(map);
							}
						}
					}
					br.close();
				} catch(FileNotFoundException e) {
			    	System.out.println("Arquivo da SUMO não encontrado!");
			    	System.out.println("erro: " + e);
			    } catch (IOException e) {
			    	System.out.println("Operação I/O interrompida, no arquivo da SUMO!");
			    	System.out.println("erro: " + e);
			    }
					
			} else {
			man.config_aliClass(cnp_1, null);
			
			map.set_source(cnp_1.get_owlClass().getIRI().toString());
			map.set_target("null");
			map.set_relation("null");
			map.set_measure("false");
			listM.add(map);
			}
		}
		this.listMap = listM;
		final_log();
	}
	
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
	
	private String rc_alignment(String line) {
		String aux;
		int i = line.indexOf("&%");
		aux = line.substring(i);
		aux = aux.replaceFirst("&%", "");
		return aux;
	}
	
}


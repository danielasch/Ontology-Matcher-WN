package matchingProcess;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import objects.Concept;
import objects.ConceptManager;
import objects.Ontology;
import objects.OutObjectWE;
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
		
		public void match(List<Concept> listDom, List<Concept> listUp) {
			init_log();
			for(Concept cnpDom: listDom) {
				Set<String> contextDom = cnpDom.get_context();
				double max = 0;
				Concept align = null;
				ConceptManager man = new ConceptManager();
				int sizeDom = contextDom.size();
				
				List<OutObjectWE> ooList = new ArrayList<>();
				int aux = 0;

				for(Concept cnpUp: listUp) {
					OutObjectWE oo = new OutObjectWE(sizeDom);
					Set<String> contextUp = cnpUp.get_context();
					double mediaT = 0;
					int sizeUp = contextUp.size();
					
					HashMap<String, Object> map = new HashMap<>();
					Double[] vec = new Double[sizeDom];
					int aux_1 = 0;
					for(String elDom: contextDom) {
						double media = 0;
						
						HashMap<String, Double> map_1 = new HashMap<>();
						
						for(String elUp: contextUp) {
							double sim = similarity(elDom, elUp);
							media = media + sim;
							
							map_1.put(elUp, sim);	
						}
						
						media = media / sizeUp;
						mediaT = mediaT + media;
						vec[aux_1] = media;
						map.put(elDom, map_1);
						aux_1++;
					}
					
					mediaT = mediaT / sizeDom;
					
					if(mediaT > max) {
						max = mediaT;
						align = cnpUp;
					}
					
					//System.out.println("============OO==============");
					//System.out.println(cnpUp);
					oo.set_topConcept(cnpUp);
					//System.out.println(map);
					oo.set_map(map);
					//System.out.println(vec);
					oo.set_vec(vec);
					//System.out.println(mediaT);
					oo.set_mediaTotal(mediaT);
					
					if(aux < 5) {
						ooList.add(oo);
						Collections.sort(ooList);
						aux++;
					} else {
						ooList.add(oo);
						Collections.sort(ooList);
						ooList.remove(4);
						aux++;
					}
				}
				
				Mapping map = new Mapping();
				man.config_aliClass(cnpDom, align.get_owlClass());
				man.config_object(cnpDom, ooList);
				map.set_source(cnpDom.get_classID());
				map.set_target(align.get_classID());
				map.set_measure("1.0");
				map.set_relation("&lt;");
				this.listMap.add(map);			
			}
			final_log();	
		}
		
		public void matchInv(List<Concept> listDom, List<Concept> listUp) {
			init_log();
			for(Concept cnpDom: listDom) {
				Set<String> contextDom = cnpDom.get_context();
				double max = 0;
				Concept align = null;
				ConceptManager man = new ConceptManager();
				int sizeDom = contextDom.size();
				
				List<OutObjectWE> ooList = new ArrayList<>();
				int aux = 0;

				for(Concept cnpUp: listUp) {
					OutObjectWE oo = new OutObjectWE(sizeDom);
					Set<String> contextUp = cnpUp.get_context();
					double mediaT = 0;
					int sizeUp = contextUp.size();
					
					HashMap<String, Object> map = new HashMap<>();
					Double[] vec = new Double[sizeUp];
					int aux_1 = 0;
					for(String elUp: contextUp) {
						double media = 0;
						
						HashMap<String, Double> map_1 = new HashMap<>();
						
						for(String elDom: contextDom) {
							double sim = similarity(elDom, elUp);
							media = media + sim;
							
							map_1.put(elDom, sim);	
						}
						
						media = media / sizeDom;
						mediaT = mediaT + media;
						vec[aux_1] = media;
						map.put(elUp, map_1);
						aux_1++;
					}
					
					mediaT = mediaT / sizeUp;
					
					if(mediaT > max) {
						max = mediaT;
						align = cnpUp;
					}
					
					//System.out.println("============OO==============");
					//System.out.println(cnpUp);
					oo.set_topConcept(cnpUp);
					//System.out.println(map);
					oo.set_map(map);
					//System.out.println(vec);
					oo.set_vec(vec);
					//System.out.println(mediaT);
					oo.set_mediaTotal(mediaT);
					
					if(aux < 5) {
						ooList.add(oo);
						Collections.sort(ooList);
						aux++;
					} else {
						ooList.add(oo);
						Collections.sort(ooList);
						ooList.remove(4);
						aux++;
					}
				}
				
				Mapping map = new Mapping();
				man.config_aliClass(cnpDom, align.get_owlClass());
				man.config_object(cnpDom, ooList);
				map.set_source(cnpDom.get_classID());
				map.set_target(align.get_classID());
				map.set_measure("1.0");
				map.set_relation("&lt;");
				this.listMap.add(map);			
			}
			final_log();	
		}
		
		private double similarity(String elDom, String elUp) {
			double sim = this.baseresource.get_word2vec().get_word2Vec().similarity(elDom, elUp);
			if(!(Double.isNaN(sim))) {
				//increment the media and adds the similarity
				return (sim * 10);
            } else {
            	//case similarity is null
            	return 0;
            }
		}

}

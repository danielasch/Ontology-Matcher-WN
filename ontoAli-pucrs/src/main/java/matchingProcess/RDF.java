package matchingProcess;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import objects.Ontology;

public class RDF {
	
	//Attributes
	
		//Map list
		private List<Mapping> listMap;
		//path to write the rdf file
		private String localfile;
	
		private void out_log() {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - RDF file generated!" );
		}
		
	//Setters and getters
		public void set_listMap(List<Mapping> _listMap) {
			this.listMap = _listMap;
		}
		
		public List<Mapping> get_listMap() {
			return listMap;
		}
		
		public void set_file(String _file) {
			this.localfile = _file;
		}
		
		public String get_file() {
			return localfile;
		}
	
	//Methods
		
	public void addMap(Mapping map) {
		this.listMap.add(map);
	}
	
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
		} catch(IOException e) {
			System.out.println("Operacão I/O interrompida, no arquivo de saída .RDF!");
	    	System.out.println("erro: " + e);
			
		}
		this.listMap.clear();
		out_log();
	}

}

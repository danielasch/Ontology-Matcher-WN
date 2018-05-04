package resources;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import edu.mit.jwi.item.ISynset;
import objects.Concept;

/*
 * This class generates the text files
 */
public class OutFiles {
	
//Attributes
	
	//Contains path to write the text files
	private String outPath;

//Constructor	
	
	/*
	 * This constructor receive the path to the alignment and
	 * change .rdf by _1.text, this way, the text file will be
	 * generated at the save folder as the alignment
	 */
	public OutFiles(String path) {
		String aux = path.replace(".rdf", "_1.txt");
		this.outPath = aux;
	}
	
//Methods
	
	/*
	 * Generates the text file for the overlapping technique
	 */
	public void out_file(List<Concept> listDomain) {
		try {
			FileWriter arq = new FileWriter(this.outPath);
			PrintWriter printer = new PrintWriter(arq);
			List<String> bgwSelect  = new ArrayList<String>();
			
			for(Concept cnp: listDomain) {
				printer.print("NOME: " + cnp.get_className() + "\n");
				printer.print("Desc: " + cnp.get_desc() + "\n");
				printer.print("Supers: " + cnp.get_supers() + "\n");
				printer.print("Subs: " + cnp.get_subs() + "\n");
				printer.print("Contexto: " + cnp.get_context() + "\n");
				printer.print("Conceito Topo alinhado: " + cnp.get_aliClass() + "\n");
				printer.print("Synset selecionado: " + cnp.get_goodSynset() + "\n");
				printer.print("Número de Synsets recuperados: " + cnp.get_utilities().get_numSy() + "\n\n");
				printer.print("Conjunto de synsets recuperados:\n");
				for(Entry<ISynset, List<String>> entry : cnp.get_utilities().get_synsetCntx().entrySet()) {
					List<String> value = entry.getValue();
					
					if(cnp.get_goodSynset() != null && cnp.get_goodSynset().equals(entry.getKey())) {
						bgwSelect = entry.getValue();
					}
					printer.print(entry.getKey() + " | " + entry.getKey().getGloss() + "\n");
					printer.print("BOW: " + value.toString());
					printer.print("\n\n");
				}
				
				printer.print("Intersecção de palavras:");
				for(String a: bgwSelect) {
					if(cnp.get_context().contains(a)) {
						printer.print(" " + a + ",");
					}					
				}
				printer.print("\n----------\n");
				
			}
		arq.close();
		} catch(IOException e) {
			System.out.println("Operação I/O interrompida, no arquivo de saída syCNTXT!");
	    	System.out.println("erro: " + e);
		}
	}

	/*
	 * Generates the text file for the Word Embeddings technique
	 */	
	public void out_file_we(List<Concept> listDomain) {
		try {
			FileWriter arq = new FileWriter(this.outPath);
			PrintWriter printer = new PrintWriter(arq);
			
			for(Concept cnp: listDomain) {
				printer.print("NOME: " + cnp.get_className() + "\n");
				printer.print("Desc: " + cnp.get_desc() + "\n");
				printer.print("Supers: " + cnp.get_supers() + "\n");
				printer.print("Subs: " + cnp.get_subs() + "\n");
				printer.print("Contexto: " + cnp.get_context() + "\n");
				printer.print("Conceito Topo alinhado: " + cnp.get_aliClass() + "\n");
				printer.print("Synset selecionado: " + cnp.get_goodSynset() + "\n");
				printer.print("Conjunto de synsets recuperados:\n");
				
				int index = 0;
				for(Entry<ISynset, List<String>> entry : cnp.get_utilities().get_synsetCntx().entrySet()) {
					List<String> value = entry.getValue();
					
					printer.print(entry.getKey() + " | " + entry.getKey().getGloss() + "\n");
					printer.print("BOW: " + value.toString() + "\n");
					printer.print("MEDIA: " + cnp.get_utilities().get_synsetMedia().get(index).toString());
					index++;	
					printer.print("\n\n");
				}
				printer.print("\n----------\n");
				
			}
		arq.close();
		} catch(IOException e) {
			System.out.println("Operação I/O interrompida, no arquivo de saída syCNTXT!");
	    	System.out.println("erro: " + e);
		}
	}

}

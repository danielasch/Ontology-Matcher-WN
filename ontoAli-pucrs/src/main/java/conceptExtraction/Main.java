/****************************************************
*	top-match is a tool to align top and domain     *
* ontologies.                                       *
*****************************************************
* 	This is the main class that calls specialized   *
* classes to generate the alignment.                *
*                                                   *
*                                                   *
* @author Rafael Basso                              *
****************************************************/
package conceptExtraction;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.output.TeeOutputStream;

import matchingProcess.Matching;
import matchingProcess.MatchingWE;
import matchingProcess.MatchingWN;
import objects.Concept;
import objects.Ontology;
import resources.BaseResource;
import resources.Evaluator;
import resources.OutFiles;
import synsetSelection.SynsetDisambiguation;
import synsetSelection.SynsetDisambiguationLD;
import synsetSelection.SynsetDisambiguationWE;

/*
 * main class, instantiate and calls the necessary classes to execute the process
 */
public class Main {

//Main method
	//arg 0 = domain onto
	//arg 1 = rdf out
	//arg 2 = dolce/sumo
	//arg 3 = tec - se 1:ativa termo composto [0 - termo simples ou 1 - termo composto] - se 2:modelo [modelo = google, glove, 0 - xero para nenhum] -
	//arg 4 = ref align 
	public static void main(String[] args) {
		long start = sTime();
		verify(args);
		String[] op = sp_options(args);
		int tec = Integer.parseInt(op[0]);
		int compound = Integer.parseInt(op[1]);
		int fullcntx = Integer.parseInt(op[2]);
		String model = op[3];
		
		switch(tec) {
			case 1:
				lesk(args, compound, fullcntx);
				break;
			case 2:
				wordEmbedding(args, compound, model, fullcntx);
				break;
			case 3:
				directWE(args, model, fullcntx);//modelo word embedings
				break;
			case 4:
				directWN(args, compound, fullcntx);
				break;
			case 5:
				levenshteinDistance(args, compound, fullcntx);
				break;
			default:
				System.out.println("Invalid arguments order, please try:\n" + 
						"1º) domain ontology path\n" +
						"2º) out file path\n" + 
						"3º) top ontology selection [sumo or dolce]\n" +
						"4º) technic selection [technic:compound:model]\n" +
						"TECHNIC --> 1, 2, 3, 4 or 5 - select a certain technic\n" +
						"\t 1 - Lesk\n" +
						"\t 2 - Word Embeddings\n" +
						"\t 3 - Direct Word Embedding\n" +
						"\t 4 - WordNet Hierarchical Structure\n" +
						"\t 5 - Levenshtein Distance\n\n" +
						"COMPOUND --> 0 or 1 - activate searching for compounds terms on WORDNET\n" +
						"\t 0 - off\n" +
						"\t 1 - on\n\n" +
						"MODEL --> 1 or 2 - select the Word Embedding model\n" + 
						"\t glove - GloVe\n" +
						"\t google - GoogleNewsModel\n" +
						"5º) reference alignment path [optional]");
				break;
		}
		fTime(start);
	}
	
	private static void levenshteinDistance(String[] args, int single, int fullcntx) {
		String topOnto = args[2].toLowerCase();
		List<Concept> listDom = null;
		List<Concept> listUp = null;
		
		Ontology domain = new Ontology(args[0]);
		switch(topOnto) {
			case "dolce":
				Ontology upperD = new Ontology("resources/wnNounsyn_v7.owl");

				listDom = domain(domain, fullcntx);
				listUp = dolce(upperD);

				disambLD(listDom, single);
				matchDolce(domain, upperD, args[1], listDom, listUp);
				outLD(args[1], listDom);
				evaluate(args);
				break;
			case "sumo":
				Ontology upperS = new Ontology("resources/SUMO.owl");
				listDom = domain(domain, fullcntx);
				listUp = sumo(upperS);
				disambLD(listDom, single);
				matchSumo(domain, upperS, args[1], listDom, listUp);
				outLD(args[1], listDom);
				evaluate(args);
				break;
			default:
				System.out.println("Invalid Upper Ontology selection! Choose SUMO, or DOLCE!");
				break;
		}
	}
	
	private static void directWN(String[] args, int single, int fullcntx) {
		String topOnto = args[2].toLowerCase();
		List<Concept> listDom = null;
		List<Concept> listUp = null;
		
		Ontology domain = new Ontology(args[0]);
		switch(topOnto) {
			case "dolce":
				Ontology upperD = new Ontology("resources/DOLCE-Lite.owl");

				listDom = domain(domain, fullcntx);
				listUp = dolce(upperD);
				disamb(listDom, single);
				matchWN(domain, upperD, args[1], listDom, listUp);
				outWNH(args[1], listDom);
				evaluate(args);
				break;
			case "sumo":
				Ontology upperS = new Ontology("resources/SUMO.owl");
				listDom = domain(domain, fullcntx);
				listUp = sumo(upperS);
				disamb(listDom, single);
				matchWN(domain, upperS, args[1], listDom, listUp);
				outWNH(args[1], listDom);
				evaluate(args);
				break;
			default:
				System.out.println("Invalid Upper Ontology selection! Choose SUMO, or DOLCE!");
				break;
		}
	}
	
	private static void directWE(String[] args, String model, int fullcntx) {
		String topOnto = args[2].toLowerCase();
		List<Concept> listDom = null;
		List<Concept> listUp = null;
		
		Ontology domain = new Ontology(args[0]);
		switch(topOnto) {
			case "dolce":
				Ontology upperD = new Ontology("resources/DOLCE-Lite.owl");

				listDom = domain(domain, fullcntx);
				listUp = dolceT(upperD);
				matchWE(domain, upperD, args[1], listDom, listUp, model);
				outWE(args[1], listDom);
				evaluate(args);
				break;
			case "sumo":
				Ontology upperS = new Ontology("resources/SUMO.owl");
				listDom = domain(domain, fullcntx);
				listUp = sumoT(upperS);
				matchWE(domain, upperS, args[1], listDom, listUp, model);
				outWE(args[1], listDom);
				evaluate(args);
				break;
			default:
				System.out.println("Invalid Upper Ontology selection! Choose SUMO, or DOLCE!");
				break;
		}
	}
	
	private static void lesk(String[] args, int single, int fullcntx) {
		String topOnto = args[2].toLowerCase();
		List<Concept> listDom = null;
		List<Concept> listUp = null;
		
		Ontology domain = new Ontology(args[0]);
		switch(topOnto) {
			case "dolce":
				Ontology upperD = new Ontology("resources/wnNounsyn_v7.owl");

				listDom = domain(domain, fullcntx);
				listUp = dolce(upperD);
				disamb(listDom, single);
				matchDolce(domain, upperD, args[1], listDom, listUp);
				out(args[1], listDom);
				evaluate(args);
				break;
			case "sumo":
				Ontology upperS = new Ontology("resources/SUMO.owl");
				listDom = domain(domain, fullcntx);
				listUp = sumo(upperS);
				disamb(listDom, single);
				matchSumo(domain, upperS, args[1], listDom, listUp);
				out(args[1], listDom);
				evaluate(args);
				break;
			default:
				System.out.println("Invalid Upper Ontology selection! Choose SUMO, or DOLCE!");
				break;
		}
	}
	
	private static void wordEmbedding(String[] args, int compound, String model, int fullcntx) {
		String topOnto = args[2].toLowerCase();
		List<Concept> listDom = null;
		List<Concept> listUp = null;
		
		Ontology domain = new Ontology(args[0]);
		switch(topOnto) {
			case "dolce":
				Ontology upperD = new Ontology("resources/wnNounsyn_v7.owl");

				listDom = domain(domain, fullcntx);
				listUp = dolce(upperD);
				disambWE(listDom, model, compound);
				matchDolce(domain, upperD, args[1], listDom, listUp);
				outWNWE(args[1], listDom);
				evaluate(args);
				break;
			case "sumo":
				Ontology upperS = new Ontology("resources/SUMO.owl");
				listDom = domain(domain, fullcntx);
				listUp = sumo(upperS);
				disambWE(listDom, model, compound);
				matchSumo(domain, upperS, args[1], listDom, listUp);
				outWNWE(args[1], listDom);
				evaluate(args);
				break;
			default:
				System.out.println("Invalid Upper Ontology selection! Choose SUMO, or DOLCE!");
				break;
		}
	}
	
	private static List<Concept> dolce(Ontology upper) {
		List<Concept> listUp = new ArrayList<Concept>();
		
		ContextExtraction exct = new ContextExtraction();
		listUp = exct.extract_upper(upper.get_ontology());
		return listUp;
	}
	
	private static List<Concept> dolceT(Ontology upperS) {
		List<Concept> listUp = new ArrayList<Concept>();
		
		ContextExtraction exct = new ContextExtraction();
		listUp = exct.extract_upperWE(upperS.get_ontology());
		return listUp;
	}
	
	
	private static List<Concept> sumo(Ontology upperS) {
		List<Concept> listUp = new ArrayList<Concept>();
		
		ContextExtraction exct = new ContextExtraction();
		listUp = exct.extract_upper(upperS.get_ontology());
		return listUp;
	}
	
	private static List<Concept> sumoT(Ontology upperS) {
		List<Concept> listUp = new ArrayList<Concept>();
		
		ContextExtraction exct = new ContextExtraction();
		listUp = exct.extract_upperWE(upperS.get_ontology());
		return listUp;
	}
	
	private static List<Concept> domain(Ontology domain, int fullcntx) {
		List<Concept> listDom = new ArrayList<Concept>();
		
		ContextExtraction exct = new ContextExtraction(fullcntx);
		listDom = exct.extract_init(domain.get_ontology());
		return listDom;
	}
	
	private static void disamb(List<Concept> listDom, int single) {
		BaseResource base = new BaseResource(1, null);
		ContextProcessing proc = new ContextProcessing(base);
		proc.process(listDom);
		
		SynsetDisambiguation disam = new SynsetDisambiguation(base, single);
		disam.disambiguation(listDom);
	}
	
	private static void disambLD(List<Concept> listDom, int single) {
		BaseResource base = new BaseResource(1, null);
		ContextProcessing proc = new ContextProcessing(base);
		proc.process(listDom);
		
		SynsetDisambiguationLD disam = new SynsetDisambiguationLD(base, single);
		disam.disambiguation(listDom);
	}
	
	
	private static void disambWE(List<Concept> listDom, String model, int compound) {
		BaseResource base = new BaseResource(2, model);
		ContextProcessing proc = new ContextProcessing(base);
		proc.process(listDom);
		
		SynsetDisambiguationWE disam = new SynsetDisambiguationWE(base,compound);
		disam.disambiguation(listDom);
	}
	
	private static void matchWE(Ontology domain, Ontology upper, String outPath, List<Concept> listDom, List<Concept> listUp, String model) {
		BaseResource base = new BaseResource(2, model);
		ContextProcessing proc = new ContextProcessing(base);
		proc.procWE(listDom);

		proc.procWE(listUp);

		MatchingWE match = new MatchingWE(outPath, base);
		match.matchInv(listDom, listUp);
		match.out_rdf(domain, upper);
	}
	
	private static void matchWN(Ontology domain, Ontology upper, String outPath, List<Concept> listDom, List<Concept> listUp) {
		BaseResource base = new BaseResource(1, null);
		MatchingWN mat = new MatchingWN(base, outPath);
		mat.match(listDom,listUp);
		mat.out(domain, upper);
	}
	
	private static void matchDolce(Ontology domain, Ontology upper, String outPath, List<Concept> listDom, List<Concept> listUp) {
		Matching mat = new Matching(outPath);
		mat.compare_dolce(listDom, listUp);
		mat.out_rdf(domain, upper);
	}
	
	private static void matchSumo(Ontology domain, Ontology upper, String outPath, List<Concept> listDom, List<Concept> listUp) {
		Matching mat = new Matching(outPath);
		mat.compare_sumo(listDom, listUp);
		mat.out_rdf(domain, upper);
	}
	
	private static void out(String outPath, List<Concept> listDom) {
		OutFiles out = new OutFiles(outPath);
		out.out_file(listDom);
	}
	
	private static void outLD(String outPath, List<Concept> listDom) {
		OutFiles out = new OutFiles(outPath);
		out.out_file_LD(listDom);
	}
	
	private static void outWNWE(String outPath, List<Concept> listDom) {
		OutFiles out = new OutFiles(outPath);
		out.out_file_we_wn_pair(listDom);
	}
	
	private static void outWE(String outPath, List<Concept> listDom) {
		OutFiles out = new OutFiles(outPath);
		out.out_file_we(listDom);
	}
	
	private static void outWNH(String outPath, List<Concept> listDom) {
		OutFiles out = new OutFiles(outPath);
		out.out_file_wn_h(listDom);
	}
	
	private static void evaluate(String[] args) {
		if(args.length == 5) {
			Evaluator eva = new Evaluator(args[4], args[1]); //REF, ALI
			eva.evaluate();
		}	
	}
	
//Execution time methods
	
	private static long sTime() {
		long start = System.nanoTime();
		return start;
	}
	
	private static void fTime(long start) {
		long end = System.nanoTime();
		end = end - start;
		end = end / 1000000000;
		
		minute(end);
	}
	private static void minute(long time) {
		int aux = 0;
		long sec = 0;
		boolean test = true;
		while(test) {
			time = time - 60;
			aux++;
			if(time < 0) {
				sec = time + 60;
				test = false;
				aux--;
			}
		}
		System.out.println("Execution time: " + aux + ":" + sec);
	}
//Verify arguments method
	
	/*
	 * Verifies if the arguments passed are in the right condition to execute the program
	 */
	private static void verify(String[] args) {
		
		if(args[0].contains("\\")) {
			args[0] = args[0].replaceAll("\\", "/");
		}
		if(!args[0].endsWith(".owl")) {
			args[0] = args[0].concat(".owl");
		}
		
		if(args[1].contains("\\")) {
			args[1] = args[1].replaceAll("\\", "/");
		}
		
		if(!args[1].endsWith("/")) {
			args[1] = args[1].concat("/");
		}
		args[1] = verifyRDF(args);
	}
	
	private static String verifyRDF(String[] args) {
		String outFile = args[1];
		String outFileLog = args[1];
		
		int sIndex = args[0].lastIndexOf("/");
		int eIndex = args[0].lastIndexOf(".");
		String aux = args[0].substring(sIndex + 1, eIndex);

		outFile = outFile.concat(aux + "-" + args[2]);
		outFileLog = outFileLog.concat("out-" + aux + "-" + args[2]);
		
		if(args[3].substring(0, 1).contains("1")) {
			outFile = outFile.concat("-lesk.rdf");
			outFileLog = outFileLog.concat("-lesk.txt");
			
		} else if(args[3].substring(0, 1).contains("2")) {
			outFile = outFile.concat("-WE.rdf");
			outFileLog = outFileLog.concat("-WE.txt");
			
		} else if(args[3].substring(0, 1).contains("3")) {
			outFile = outFile.concat("-dWE.rdf");
			outFileLog = outFileLog.concat("-dWE.txt");
			
		} else if(args[3].substring(0, 1).contains("4")) {
			outFile = outFile.concat("-dWN.rdf");
			outFileLog = outFileLog.concat("-WN.txt");	
			
		} else if(args[3].substring(0, 1).contains("5")) {
			outFile = outFile.concat("-LD.rdf");
			outFileLog = outFileLog.concat("-LD.txt");	
		} else {
			outFile = outFile.concat(".rdf");
			outFileLog = outFileLog.concat(".txt");
		}
		
		outputStream(outFileLog);	//LOG PATH MAIS FILE NAME
		return outFile; 	//RDF PATH MAIS FILE NAME
	}
	
	private static void outputStream(String outFileLog) {
		try {
			FileOutputStream fos = new FileOutputStream(outFileLog);
			try {
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			TeeOutputStream tos = new TeeOutputStream(System.out, fos);
			PrintStream ps = new PrintStream(tos);
			System.setOut(ps);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static String[] sp_options(String[] args) {
		String[] op = {"","","",""}; 
		if(args[3].length() > 6) {
			op[0] = args[3].substring(0,1);
			op[1] = args[3].substring(2,3);
			op[2] = args[3].substring(4,5);
			op[3] = args[3].substring(5);
			return op;
		} else if(args[3].substring(1,2).equals(":") && args[3].substring(3,4).equals(":")) {
			op[0] = args[3].substring(0,1);
			op[1] = args[3].substring(2,3);
			op[2] = args[3].substring(4,5);
			return op;
		} else {	
			System.out.println("**ERROR**");
			System.out.println("4th argument is invalid! Please try something like:\n");
			return null;			
		}
	}
	
}

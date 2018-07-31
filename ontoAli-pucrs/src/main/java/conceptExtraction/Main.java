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
import objects.Concept;
import objects.Ontology;
import resources.BaseResource;
import resources.Evaluator;
import resources.OutFiles;
import synsetSelection.SynsetDisambiguation;
import synsetSelection.SynsetDisambiguationWE;

/*
 * main class, instantiate and calls the necessary classes to execute the process
 */
public class Main {

//Main method
	//arg 0 = domain onto
	//arg 1 = rdf out
	//arg 2 = dolce/sumo
	//arg 3 = tec - se 2:modelo [modelo = google ou glove]
	//arg 4 = ref align
	public static void main(String[] args) {
		long start = sTime();
		verify(args);
		String model = sp_model(args);
		int tec = Integer.parseInt(args[3]);
		switch(tec) {
			case 1:
				context(args);
				break;
			case 2:
				wordEmbedding(args, model);
				break;
			case 3:
				resnik(args);
				break;
			case 4:
				lin(args);
				break;
			case 5:
				wup(args);
				break;
			case 6:
				noWN(args, model);
				break;
			default:
				System.out.println("Invalid arguments order, please try:\n" + 
						"1º) domain ontology path\n" +
						"2º) out file path\n" + 
						"3º) top ontology selection [sumo or dolce]\n" +
						"4º) technic selection [1, 2, 3, 4 or 5 - the numbers correspond to a certain technic]\n" + 
						"\t 1 - Overlapping\n" +
						"\t 2 - WordEmbeddings\n" +
						"\t 3 - Resnik\n" +
						"\t 4 - Lin\n" +
						"\t 5 - Wup\n" +
						"5º) reference alignment path [optional]");
				break;
		}
		fTime(start);
	}
	
	private static void noWN(String[] args, String model) {
		String topOnto = args[2].toLowerCase();
		List<Concept> listDom = null;
		List<Concept> listUp = null;
		
		Ontology domain = new Ontology(args[0]);
		switch(topOnto) {
			case "dolce":
				Ontology upperD = new Ontology("resources/DOLCE-Lite.owl");

				listDom = domain(domain);
				listUp = dolceT(upperD);
				//disamb(listDom);
				matchWE(domain, upperD, args[1], listDom, listUp, model);
				//out(args[1], listDom);
				evaluate(args);
				break;
			case "sumo":
				Ontology upperS = new Ontology("resources/SUMO.owl");
				listDom = domain(domain);
				listUp = sumoT(upperS);
				//disamb(listDom);
				matchWE(domain, upperS, args[1], listDom, listUp, model);
				//out(args[1], listDom);
				evaluate(args);
				break;
			default:
				System.out.println("Invalid Upper Ontology selection! Choose SUMO, or DOLCE!");
				break;
		}
	}
	
	private static void wup(String[] args) {
		String topOnto = args[2].toLowerCase();
		List<Concept> listDom = null;
		List<Concept> listUp = null;
		
		Ontology domain = new Ontology(args[0]);
		switch(topOnto) {
			case "dolce":
				Ontology upperD = new Ontology("resources/wnNounsyn_v7.owl");

				listDom = domain(domain);
				listUp = dolce(upperD);
				//disamb(listDom);
				matchDolce(domain, upperD, args[1], listDom, listUp);
				//out(args[1], listDom);
				evaluate(args);
				break;
			case "sumo":
				Ontology upperS = new Ontology("resources/SUMO.owl");
				listDom = domain(domain);
				listUp = sumo(upperS);
				//disamb(listDom);
				matchSumo(domain, upperS, args[1], listDom, listUp);
				//out(args[1], listDom);
				evaluate(args);
				break;
			default:
				System.out.println("Invalid Upper Ontology selection! Choose SUMO, or DOLCE!");
				break;
		}
	}
	
	private static void lin(String[] args) {
		String topOnto = args[2].toLowerCase();
		List<Concept> listDom = null;
		List<Concept> listUp = null;
		
		Ontology domain = new Ontology(args[0]);
		switch(topOnto) {
			case "dolce":
				Ontology upperD = new Ontology("resources/wnNounsyn_v7.owl");

				listDom = domain(domain);
				listUp = dolce(upperD);
				//disamb(listDom);
				matchDolce(domain, upperD, args[1], listDom, listUp);
				//out(args[1], listDom);
				evaluate(args);
				break;
			case "sumo":
				Ontology upperS = new Ontology("resources/SUMO.owl");
				listDom = domain(domain);
				listUp = sumo(upperS);
				//disamb(listDom);
				matchSumo(domain, upperS, args[1], listDom, listUp);
				//out(args[1], listDom);
				evaluate(args);
				break;
			default:
				System.out.println("Invalid Upper Ontology selection! Choose SUMO, or DOLCE!");
				break;
		}
	}
	
	private static void resnik(String[] args) {
		String topOnto = args[2].toLowerCase();
		List<Concept> listDom = null;
		List<Concept> listUp = null;
		
		Ontology domain = new Ontology(args[0]);
		switch(topOnto) {
			case "dolce":
				Ontology upperD = new Ontology("resources/wnNounsyn_v7.owl");

				listDom = domain(domain);
				listUp = dolce(upperD);
				//disamb(listDom);
				matchDolce(domain, upperD, args[1], listDom, listUp);
				//out(args[1], listDom);
				evaluate(args);
				break;
			case "sumo":
				Ontology upperS = new Ontology("resources/SUMO.owl");
				listDom = domain(domain);
				listUp = sumo(upperS);
				//disamb(listDom);
				matchSumo(domain, upperS, args[1], listDom, listUp);
				//out(args[1], listDom);
				evaluate(args);
				break;
			default:
				System.out.println("Invalid Upper Ontology selection! Choose SUMO, or DOLCE!");
				break;
		}
	}
	
	private static void context(String[] args) {
		String topOnto = args[2].toLowerCase();
		List<Concept> listDom = null;
		List<Concept> listUp = null;
		
		Ontology domain = new Ontology(args[0]);
		switch(topOnto) {
			case "dolce":
				Ontology upperD = new Ontology("resources/wnNounsyn_v7.owl");

				listDom = domain(domain);
				listUp = dolce(upperD);
				disamb(listDom);
				matchDolce(domain, upperD, args[1], listDom, listUp);
				out(args[1], listDom);
				evaluate(args);
				break;
			case "sumo":
				Ontology upperS = new Ontology("resources/SUMO.owl");
				listDom = domain(domain);
				listUp = sumo(upperS);
				disamb(listDom);
				matchSumo(domain, upperS, args[1], listDom, listUp);
				out(args[1], listDom);
				evaluate(args);
				break;
			default:
				System.out.println("Invalid Upper Ontology selection! Choose SUMO, or DOLCE!");
				break;
		}
	}
	
	private static void wordEmbedding(String[] args, String model) {
		String topOnto = args[2].toLowerCase();
		List<Concept> listDom = null;
		List<Concept> listUp = null;
		
		Ontology domain = new Ontology(args[0]);
		switch(topOnto) {
			case "dolce":
				Ontology upperD = new Ontology("resources/wnNounsyn_v7.owl");

				listDom = domain(domain);
				listUp = dolce(upperD);
				disambWE(listDom, model);
				matchDolce(domain, upperD, args[1], listDom, listUp);
				outWE(args[1], listDom);
				evaluate(args);
				break;
			case "sumo":
				Ontology upperS = new Ontology("resources/SUMO.owl");
				listDom = domain(domain);
				listUp = sumo(upperS);
				disambWE(listDom, model);
				matchSumo(domain, upperS, args[1], listDom, listUp);
				outWE(args[1], listDom);
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
	
	private static List<Concept> domain(Ontology domain) {
		List<Concept> listDom = new ArrayList<Concept>();
		
		ContextExtraction exct = new ContextExtraction();
		listDom = exct.extract(domain.get_ontology());
		return listDom;
	}
	
	private static void disamb(List<Concept> listDom) {
		BaseResource base = new BaseResource(1, null);
		ContextProcessing proc = new ContextProcessing(base);
		proc.process(listDom);
		
		SynsetDisambiguation disam = new SynsetDisambiguation(base);
		disam.disambiguation(listDom);
	}
	
	
	private static void disambWE(List<Concept> listDom, String model) {
		BaseResource base = new BaseResource(2, model);
		ContextProcessing proc = new ContextProcessing(base);
		proc.process(listDom);
		
		SynsetDisambiguationWE disam = new SynsetDisambiguationWE(base);
		disam.disambiguation(listDom);
	}
	
	private static void matchWE(Ontology domain, Ontology upper, String outPath, List<Concept> listDom, List<Concept> listUp, String model) {
		BaseResource base = new BaseResource(2, model);
		ContextProcessing proc = new ContextProcessing(base);
		proc.procWE(listDom);

		proc.procWE(listUp);

		MatchingWE match = new MatchingWE(outPath, base);
		match.match(listDom, listUp);
		match.out_rdf(domain, upper);
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
	
	private static void outWE(String outPath, List<Concept> listDom) {
		OutFiles out = new OutFiles(outPath);
		out.out_file_we_pair(listDom);
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
		
		if(args[3].equals("2")) {
			outFile = outFile.concat("-WE.rdf");
			outFileLog = outFileLog.concat("-WE.txt");
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
	
	private static String sp_model(String[] args) {
		if(args[3].contains(":")) {
			int aux = args[3].indexOf(":");
			String model = args[3].substring(aux+1);
			args[3] = args[3].substring(0, aux);
			return model;
		} else {
			return "";
		}
	}
	
	
				
}

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
import objects.Concept;
import objects.Ontology;
import resources.BaseResource;
import resources.Evaluator;
import resources.OutFiles;
import synsetSelection.SynsetDisambiguation;
import synsetSelection.SynsetDisambiguationWE;
import synsetSelection.SynsetDisambiguationWE2;

/*
 * main class, instantiate and calls the necessary classes to execute the process
 */
public class Main {

//Main method
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		//this method verifies if the arguments are correct 
		verify(args);
		//if there is 4 arguments, then it means that no reference alignment was passed.
		//so, none evaluation will be done
		if(args.length == 4) {
			
			String domainPath = args[0];
			String outPath = args[1];
			String topOntoSelection = args[2];
			int technic = Integer.parseInt(args[3]);
			

			
			//condition that selects the Top Ontology, this case DOLCE
			if(topOntoSelection.toLowerCase().equals("dolce")) {
				//instantiate the domain ontology, this way we can extract its information
				Ontology domain = new Ontology(domainPath);//contrutor que receba FILE
				//instantiate the Upper ontology, this way we can estract its information
				Ontology upper = new Ontology("resources/wnNounsyn_v7.owl");
				//instantiate the class that will generate the text file
				OutFiles out = new OutFiles(outPath);
				
				//selects the disambiguation technique
				//number 1 corresponds to the context overlapping process
				if(technic == 1) {
					//instantiation of the Concept list, each Concept contains the information about a OWLClass
					//this list is for concepts of the domain ontology
					List<Concept> listCon = new ArrayList<Concept>();
					//instantiation of the Concept list, each Concept contains the information about a OWLClass
					//this list is for concepts of the top ontology
					List<Concept> listUp = new ArrayList<Concept>();
					//instantiation of resources class, it receives the technique number,
					//so it can initiate the right resources for the process selected
					BaseResource base = new BaseResource(technic);
					//instantiation of the class that will extract all information about the OWLClass
					ContextExtraction exct = new ContextExtraction();
					//instantiation of the class that will process the context of a Concept
					ContextProcessing proc = new ContextProcessing(base);
					//instantiation of the class that will disambiguate the synset
					SynsetDisambiguation disam = new SynsetDisambiguation(base);
					//instantiation of the matching class, this class generates the rdf
					Matching mat = new Matching(outPath);
					//listCon receives the list of domain Concepts
					listCon = exct.extract(domain.get_ontology());
					//listUp receives the list of top onto. concepts
					listUp = exct.extract_upper(upper.get_ontology());
					//calls the process of the context
					proc.process(listCon);
					//calls the disambiguation of the process, this case the disambiguation 
					//by context overlapping
					disam.disambiguation(listCon);
					//calls the class that searches for the synset correspondent to the top onto. concept
					mat.compare_dolce(listCon, listUp);
					//generates the rdf file
					mat.out_rdf(domain, upper);
					//generates the text file referent to the technique selected
					out.out_file(listCon);
				
	 //OBS: in the code below the calls repeat until the context processing, some changes occurs because of 
	 //technique selected, the top ontology selected and evaluation process 				
					
				//number 2 corresponds to the word embedding process	
				} else if(technic == 2) {
					
					List<Concept> listCon = new ArrayList<Concept>();
					List<Concept> listUp = new ArrayList<Concept>();
					BaseResource base = new BaseResource(technic);
					ContextExtraction exct = new ContextExtraction();
					ContextProcessing proc = new ContextProcessing(base);
					SynsetDisambiguationWE disamWE = new SynsetDisambiguationWE(base);
					Matching mat = new Matching(outPath);
					listCon = exct.extract(domain.get_ontology());
					listUp = exct.extract_upper(upper.get_ontology());
					proc.process(listCon);
					//calls the disambiguation of the process, this case the disambiguation 
					//using the word embeddings model
					disamWE.disambiguation(listCon);
					mat.compare_dolce(listCon, listUp);
					mat.out_rdf(domain, upper);
					//generates the text file referent to the technique selected
					out.out_file_we_pair(listCon);
				
				//number 3 - developing 	
				} else if(technic == 3) {
					
					List<Concept> listCon = new ArrayList<Concept>();
					List<Concept> listUp = new ArrayList<Concept>();
					BaseResource base = new BaseResource(2);
					ContextExtraction exct = new ContextExtraction();
					ContextProcessing proc = new ContextProcessing(base);
					SynsetDisambiguationWE2 disamWE2 = new SynsetDisambiguationWE2(base);
					Matching mat = new Matching(outPath);
					listCon = exct.extract(domain.get_ontology());
					listUp = exct.extract_upper(upper.get_ontology());
					proc.process(listCon);
					disamWE2.disambiguation(listCon, listUp);
					mat.compare_dolce(listCon, listUp);
					mat.out_rdf(domain, upper);	
				
				//condition that happens when the technique number isn't 0, 1, 2
				} else {
					System.out.println("Invalid technic selected!");
					System.out.println("0 -> No context technic;\n" + 
										"1 -> Context technic;\n" +
										"2 -> Context + Word Embedding technic;");
				}
				
			//condition that selects the Top Ontology, this case SUMO	
			} else if(topOntoSelection.toLowerCase().equals("sumo")) {
				Ontology domain = new Ontology(domainPath);
				Ontology upper = new Ontology("resources/SUMO.owl");
				OutFiles out = new OutFiles(outPath);
				if(technic == 0) {
					
				} else if(technic == 1) {
					List<Concept> listCon = new ArrayList<Concept>();
					List<Concept> listUp = new ArrayList<Concept>();
					BaseResource base = new BaseResource(technic);
					ContextExtraction exct = new ContextExtraction();
					ContextProcessing proc = new ContextProcessing(base);
					SynsetDisambiguation disam = new SynsetDisambiguation(base);
					Matching mat = new Matching(outPath);
					listCon = exct.extract(domain.get_ontology());
					listUp = exct.extract_upper(upper.get_ontology());
					proc.process(listCon);
					disam.disambiguation(listCon);
					mat.compare_sumo(listCon, listUp);
					mat.out_rdf(domain, upper);
					
				} else if(technic == 2) {
					List<Concept> listCon = new ArrayList<Concept>();
					List<Concept> listUp = new ArrayList<Concept>();
					BaseResource base = new BaseResource(technic);
					ContextExtraction exct = new ContextExtraction();
					ContextProcessing proc = new ContextProcessing(base);
					SynsetDisambiguationWE disamWE = new SynsetDisambiguationWE(base);
					Matching mat = new Matching(outPath);
					listCon = exct.extract(domain.get_ontology());
					listUp = exct.extract_upper(upper.get_ontology());
					proc.process(listCon);
					disamWE.disambiguation(listCon);
					mat.compare_sumo(listCon, listUp);
					mat.out_rdf(domain, upper);
					out.out_file_we_pair(listCon);
					
				} else if(technic == 3) {
					List<Concept> listCon = new ArrayList<Concept>();
					List<Concept> listUp = new ArrayList<Concept>();
					BaseResource base = new BaseResource(2);
					ContextExtraction exct = new ContextExtraction();
					ContextProcessing proc = new ContextProcessing(base);
					SynsetDisambiguationWE2 disamWE2 = new SynsetDisambiguationWE2(base);
					Matching mat = new Matching(outPath);
					listCon = exct.extract(domain.get_ontology());
					listUp = exct.extract_upper(upper.get_ontology());
					proc.process(listCon);
					disamWE2.disambiguation(listCon, listUp);
					mat.compare_sumo(listCon, listUp);
					mat.out_rdf(domain, upper);
					
				} else {
					System.out.println("Invalid technic selected!");
					System.out.println("0 -> No context technic;\n" + 
										"1 -> Context technic;\n" +
										"2 -> Context + Word Embedding technic;" +
										"3 -> Context + WEmb and Hypernyms technic");
				}
			} else if(topOntoSelection.toLowerCase().equals("dul")) {	
				Ontology domain = new Ontology(domainPath);
				Ontology upper = new Ontology("resources/DUL.owl");
				OutFiles out = new OutFiles(outPath);
				if(technic == 0) {
					
				} else if(technic == 1) {
					List<Concept> listCon = new ArrayList<Concept>();
					List<Concept> listUp = new ArrayList<Concept>();
					BaseResource base = new BaseResource(technic);
					ContextExtraction exct = new ContextExtraction();
					ContextProcessing proc = new ContextProcessing(base);
					SynsetDisambiguation disam = new SynsetDisambiguation(base);
					Matching mat = new Matching(outPath);
					listCon = exct.extract(domain.get_ontology());
					listUp = exct.extract_upper(upper.get_ontology());
					proc.process(listCon);
					disam.disambiguation(listCon);
					mat.compare_dolce(listCon, listUp);
					mat.out_rdf(domain, upper);
					out.out_file(listCon);
					
				} else if(technic == 2) {
					List<Concept> listCon = new ArrayList<Concept>();
					List<Concept> listUp = new ArrayList<Concept>();
					BaseResource base = new BaseResource(technic);
					ContextExtraction exct = new ContextExtraction();
					ContextProcessing proc = new ContextProcessing(base);
					SynsetDisambiguationWE disamWE = new SynsetDisambiguationWE(base);
					Matching mat = new Matching(outPath);
					listCon = exct.extract(domain.get_ontology());
					listUp = exct.extract_upper(upper.get_ontology());
					proc.process(listCon);
					disamWE.disambiguation(listCon);
					mat.compare_dolce(listCon, listUp);
					mat.out_rdf(domain, upper);	
					out.out_file_we_pair(listCon);
					
				} else if(technic == 3) {
					List<Concept> listCon = new ArrayList<Concept>();
					List<Concept> listUp = new ArrayList<Concept>();
					BaseResource base = new BaseResource(2);
					ContextExtraction exct = new ContextExtraction();
					ContextProcessing proc = new ContextProcessing(base);
					SynsetDisambiguationWE2 disamWE2 = new SynsetDisambiguationWE2(base);
					Matching mat = new Matching(outPath);
					listCon = exct.extract(domain.get_ontology());
					listUp = exct.extract_upper(upper.get_ontology());
					proc.process(listCon);
					disamWE2.disambiguation(listCon, listUp);
					mat.compare_dolce(listCon, listUp);
					mat.out_rdf(domain, upper);
					
				} else {
					System.out.println("Invalid technic selected!\n");
					System.out.println("0 -> No context technic;\n" + 
										"1 -> Context technic;\n" +
										"2 -> Context + Word Embedding technic;\n" +
										"3 -> Context + WEmb and Hypernyms technic");
				}
			} else {
				System.out.println("Invalid Upper Ontology selection! Choose SUMO, or DOLCE or DUL!");
			}
			
		//if there is 5 arguments, then it means that the reference alignment was passed.
		//so, none evaluation will be done	
		} else if(args.length == 5) {
			
			String domainPath = args[0];
			String outPath = args[1];
			String topOntoSelection = args[2];
			int technic = Integer.parseInt(args[3]);
			String refPath = args[4];
			
			//condition that selects the Top Ontology, this case DOLCE 
			if(topOntoSelection.toLowerCase().equals("dolce")) {
				Ontology domain = new Ontology(domainPath);
				Ontology upper = new Ontology("resources/wnNounsyn_v7.owl");
				OutFiles out = new OutFiles(outPath);
				
				if(technic == 1) {
					
					List<Concept> listCon = new ArrayList<Concept>();
					List<Concept> listUp = new ArrayList<Concept>();
					BaseResource base = new BaseResource(technic);
					ContextExtraction exct = new ContextExtraction();
					ContextProcessing proc = new ContextProcessing(base);
					SynsetDisambiguation disam = new SynsetDisambiguation(base);
					Matching mat = new Matching(outPath);
					//instantiate the evaluation class, passing the reference alignment path 
					//and the generated alignment
					Evaluator eva = new Evaluator(refPath, outPath);//****
					
					listCon = exct.extract(domain.get_ontology());
					listUp = exct.extract_upper(upper.get_ontology());
					proc.process(listCon);
					disam.disambiguation(listCon);
					mat.compare_dolce(listCon, listUp);
					mat.out_rdf(domain, upper);
					out.out_file(listCon);
					//calls the evaluation process, this process print in the screen
					//the F-Measure, Precision, Recall and Overall
					eva.evaluate();
					
				} else if(technic == 2) {
					List<Concept> listCon = new ArrayList<Concept>();
					List<Concept> listUp = new ArrayList<Concept>();
					BaseResource base = new BaseResource(technic);
					ContextExtraction exct = new ContextExtraction();
					ContextProcessing proc = new ContextProcessing(base);
					SynsetDisambiguationWE disamWE = new SynsetDisambiguationWE(base);
					Matching mat = new Matching(outPath);
					Evaluator eva = new Evaluator(refPath, outPath);
					
					listCon = exct.extract(domain.get_ontology());
					listUp = exct.extract_upper(upper.get_ontology());
					proc.process(listCon);
					disamWE.disambiguation(listCon);
					mat.compare_dolce(listCon, listUp);
					mat.out_rdf(domain, upper);
					out.out_file_we(listCon);
					eva.evaluate();
					
				} else {
					System.out.println("Invalid technic selected!\n");
					System.out.println("0 -> No context technic;\n" + 
										"1 -> Context technic;\n" +
										"2 -> Context + Word Embedding technic;");
				}
				
				
			} else if(topOntoSelection.toLowerCase().equals("sumo")) {
				Ontology domain = new Ontology(domainPath);
				Ontology upper = new Ontology("resources/sumo-2.owl");
				OutFiles out = new OutFiles(outPath);
				if(technic == 0) {
					
				} else if(technic == 1) {
					List<Concept> listCon = new ArrayList<Concept>();
					List<Concept> listUp = new ArrayList<Concept>();
					BaseResource base = new BaseResource(technic);
					ContextExtraction exct = new ContextExtraction();
					ContextProcessing proc = new ContextProcessing(base);
					SynsetDisambiguation disam = new SynsetDisambiguation(base);
					Matching mat = new Matching(outPath);
					Evaluator eva = new Evaluator(refPath, outPath);
					
					listCon = exct.extract(domain.get_ontology());
					listUp = exct.extract_upper(upper.get_ontology());
					proc.process(listCon);
					disam.disambiguation(listCon);
					mat.compare_sumo(listCon, listUp);
					mat.out_rdf(domain, upper);
					out.out_file(listCon);
					eva.evaluate();
					
				} else if(technic == 2) {
					List<Concept> listCon = new ArrayList<Concept>();
					List<Concept> listUp = new ArrayList<Concept>();
					BaseResource base = new BaseResource(technic);
					ContextExtraction exct = new ContextExtraction();
					ContextProcessing proc = new ContextProcessing(base);
					SynsetDisambiguationWE disamWE = new SynsetDisambiguationWE(base);
					Matching mat = new Matching(outPath);
					Evaluator eva = new Evaluator(refPath, outPath);
					
					listCon = exct.extract(domain.get_ontology());
					listUp = exct.extract_upper(upper.get_ontology());
					proc.process(listCon);
					disamWE.disambiguation(listCon);
					mat.compare_sumo(listCon, listUp);
					mat.out_rdf(domain, upper);
					out.out_file_we(listCon);
					eva.evaluate();
					
				} else {
					System.out.println("Invalid technic selected!\n");
					System.out.println("0 -> No context technic;\n" + 
										"1 -> Context technic;\n" +
										"2 -> Context + Word Embedding technic;");
				}
				
			} else {
				System.out.println("Invalid Upper Ontology selection! Choose SUMO, or DOLCE or DUL!");
			}
		} else {
			System.out.println("Invalid arguments order, please try:\n" + 
								"1º) domain ontology path\n" +
								"2º) out file path\n" + 
								"3º) top ontology selection [sumo or dolce]\n" +
								"4º) technic selection [0, 1, 2 - the numbers correspond to a certain technic]\n" + 
								"5º) reference alignment path [optional]");
		}		
	}

//Verify the arguments method
	
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
		
		outputStream(outFileLog);
		return outFile;
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
	
	
				
}

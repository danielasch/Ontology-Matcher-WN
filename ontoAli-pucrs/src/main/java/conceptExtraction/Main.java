package conceptExtraction;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyStorageException;

//import DomainOntology;
//import UpperOntology;
import matchingProcess.Matching;
import objects.Concept;
import objects.Ontology;
import resources.BaseResource;
import resources.Evaluator;
import resources.OutFiles;
import synsetSelection.SynsetDisambiguation;
import synsetSelection.SynsetDisambiguationWE;
import synsetSelection.SynsetDisambiguationWE2;

public class Main {
	
	public static void main(String[] args) throws OWLOntologyStorageException, FileNotFoundException, IOException {
		
		if(args.length == 4) {
			String domainPath = args[0];
			String outPath = args[1];
			String topOntoSelection = args[2];
			int technic = Integer.parseInt(args[3]);
			
			if(topOntoSelection.toLowerCase().equals("dolce")) {
				Ontology domain = new Ontology(domainPath);
				Ontology upper = new Ontology("resources/wnNounsyn_v7.owl");
				OutFiles out = new OutFiles(outPath);
				if(technic == 0) {
					
				/*técnica padrão com contexto*/	
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
				/*técnica Word embedding*/	
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
					out.out_file_we(listCon);
				/*técnica Word embedding 2 */	
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
					System.out.println("Invalid technic selected!");
					System.out.println("0 -> No context technic;\n" + 
										"1 -> Context technic;\n" +
										"2 -> Context + Word Embedding technic;");
				}
				
				
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
					out.out_file_we(listCon);
					
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
					out.out_file_we(listCon);
					
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
			
		/*Evaluator selecionado*/	
		} else if(args.length == 5) {
			String domainPath = args[0];
			String outPath = args[1];
			String topOntoSelection = args[2];
			int technic = Integer.parseInt(args[3]);
			String refPath = args[4];
			
			if(topOntoSelection.toLowerCase().equals("dolce")) {
				Ontology domain = new Ontology(domainPath);
				Ontology upper = new Ontology("resources/wnNounsyn_v7.owl");
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
					mat.compare_dolce(listCon, listUp);
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
				Ontology upper = new Ontology("resources/sumo.owl");
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
								"1°) domain ontology path\n" +
								"2°) out file path\n" + 
								"3°) top ontology selection [sumo or dolce]\n" +
								"4º) technic selection [0, 1, 2 - the numbers correspond to a certain technic]\n" + 
								"5°) reference alignment path [optional]");
		}
		
		
		
	}
				
}

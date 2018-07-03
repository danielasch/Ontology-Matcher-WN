package conceptExtraction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import objects.Concept;
import objects.ConceptManager;
import resources.BaseResource;
import resources.StanfordLemmatizer;

public class DescriptionProcessing {
	
	//Attributes
	
		//BaseResource contains the necessary resources to execute the context process
		private BaseResource base;

	//Constructor	
		
		DescriptionProcessing(BaseResource _base) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Description process selected!" );
			this.base = _base;
		}

	//Log methods	
		
		private void init_log() {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Processing the Description..." );
		}
		private void final_log() {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Description processed!" );
		}

	//Methods
		
		/*
		 * This method process the context 
		 */
		protected void process(List<Concept> listCon) {
			ConceptManager man = new ConceptManager();
			init_log();
			
			for(Concept concept: listCon) {
				Set<String> context = new HashSet<String>();
				//desc will receive the processed description of a concept
				context = init(concept.get_desc());
				//sets the description of a concept as the context
				man.config_context(concept, context);
			}
			final_log();
		}
		
		/*
		 * Split process
		 */
		private Set<String> init(String desc) {
			
			String aux = rm_specialChar(desc);
			Set<String> temp = new HashSet<String>();
			//Lemmatize the context
			temp = lemmatizer(aux);
			//Remove the stop words
			temp = rm_stopWords(temp);
			return temp;
		}
		
		private Set<String> lemmatizer(String desc) {
			List<String> lemma = new ArrayList<String>();
			StanfordLemmatizer slem = this.base.get_lemmatizer();
			lemma = slem.lemmatize(desc);
			return slem.toSet(lemma);
		}
		
//Auxiliary methods
		
		/*
		 * Removes the stopwords of a set
		 */
		private Set<String> rm_stopWords(Set<String> set) {
		    Set<String> wordSet = new HashSet<String>();
		    List<String> stpWords = this.base.get_StpWords();
			for(String word: set) {            
				String wordLow = word.toLowerCase();
		        if(!(stpWords.contains(wordLow))) {  
		        	if( !(word.equals(" ") || word.equals("-") || word.equals("")) ) {
		                	wordSet.add(wordLow);
		            } 
		        }  
		    }
			return wordSet;
		}		
		
		private String rm_specialChar(String word) {
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
	        
	        return word;	
		}

}

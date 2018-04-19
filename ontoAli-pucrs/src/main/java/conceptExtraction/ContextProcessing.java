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

public class ContextProcessing {
	
	private BaseResource base;
	
	ContextProcessing(BaseResource _base) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Context process selected!" );
		this.base = _base;
	}
	//*info logs*//
	private void init_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Processing the context..." );
	}
	private void final_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Context processed!" );
	}
	//*process*//
	protected void process(List<Concept> listCon) {
		ConceptManager man = new ConceptManager();
		init_log();
		
		for(Concept concept: listCon) {
			Set<String> context = init(concept.get_context());
			man.config_context(concept, context);
		}
		final_log();
	}
	
	private Set<String> init(Set<String> context) {
		Set<String> aux = sp_string(context);
		Set<String> temp = new HashSet<String>();
		for(String word: aux) {
			if(hasUpperCase(word)) {
				String tempStr = rm_specialChar(word);
				temp.addAll(sp_upperCase(tempStr));
			} else {
				String tempStr = rm_specialChar(word);
				temp.add(tempStr.toLowerCase());
			}
		}
		temp = rm_stopWords(temp);
		temp = lemmatizer(temp);
		return temp;
	}
	
	private Set<String> lemmatizer(Set<String> context) {
		List<String> lemma = new ArrayList<String>();
		StanfordLemmatizer slem = this.base.get_lemmatizer();
		String toLemma = slem.toLemmatize(slem.toList(context));
		lemma = slem.lemmatize(toLemma);
		return slem.toSet(lemma);
	}
	
	private Set<String> sp_string(Set<String> context) {
		Set<String> temp= new HashSet<String>();
		
		for(String str: context) {
			String[] split = null;
			if(str.contains("_")) {
				split = str.split("_");
				for(String strSplit: split) {
					temp.add(strSplit.toLowerCase());
				}
			} else if(hasWhiteSpace(str)) {
				split = str.split(" |�");
				for(String strSplit: split) {
					temp.add(strSplit);
				}
			} else if(hasUpperCase(str)) {
				temp.addAll(sp_upperCase(str));
			} else {
				temp.add(str);
			}	
		}
		context.clear();
		context = temp;
		return context;
	}
	
	private Set<String> sp_upperCase(String wordComp) {
		Set<String> sep = new HashSet<String>();
		int x = wordComp.length();
		int up, aux = 0;
		
		for(int y = 1; y < x; y++) {
			if(Character.isUpperCase(wordComp.charAt(y))) {
				up = y;
				sep.add(wordComp.substring(aux, up).toLowerCase());
				aux = up;
			}	
		}
		sep.add(wordComp.substring(aux).toLowerCase());
		return sep;
	}
	
	//*métodos de remoção*//
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
        
        return word;	
	}
	
	//*métodos de teste*//
	private boolean hasWhiteSpace(String str) {
		int length = str.length();
		
		for(int y = 1; y < length; y++) {
			if(Character.isWhitespace(str.charAt(y))) {
				return true;
			}	
		}
		return false;	
	}
	
	private boolean hasUpperCase(String word) {
		int x = word.length();
		
		for(int y = 1; y < x; y++) {
			if(Character.isUpperCase(word.charAt(y))) {
				return true;
			}	
		}
		return false;	
	}
	

}

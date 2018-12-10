package synsetSelection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import edu.mit.jwi.item.IWord;
import objects.Concept;
import objects.ConceptManager;
import resources.BaseResource;
import resources.StanfordLemmatizer;

public class SynsetBasic {
	
	protected BaseResource base;
	protected int single;
	
	/*
	 * create the bag of words of a synset
	 */
	protected List<String> createBagWords(List<IWord> wordsSynset, String glossSynset) {
	    List<String> list = new ArrayList<String>();
	    Set<String> set = new HashSet<String>();
	    StanfordLemmatizer slem = this.base.get_lemmatizer();
	    for (IWord i : wordsSynset) {
	    	StringTokenizer st = new StringTokenizer(i.getLemma().toLowerCase().replace("_"," ")," ");
	    	while (st.hasMoreTokens()) {
	    		  String token = st.nextToken();
	    	 	  if (!list.contains(token)) {
	    	  	      list.add(token);
	    	      }
	    	}
	    }
	    glossSynset = glossSynset.replaceAll(";"," ").replaceAll("\"", " ").replaceAll("-"," ").toLowerCase();
	    StringTokenizer st = new StringTokenizer(glossSynset," ");
    	while (st.hasMoreTokens()) {
    		   String token = st.nextToken().toLowerCase();
    		   token = rm_specialChar(token);
    		   if (!this.base.get_StpWords().contains(token) && !list.contains(token)) {			
    			   list.add(token);
    		   }
    	}
    	//turn the list into a string to lemmatize the list
    	String toLemma = slem.toLemmatize(list);
    	//clears the list
		list.clear();
		//list receive the string lemmatized
		list = slem.lemmatize(toLemma); 
		//turns the list into a set, 
		//to avoid repeated lemmatized strings
		set =  slem.toSet(list);
		//turns back the set into a list
		list = slem.toList(set);
	   return list;
	}
	
	/*
	 * Remove some char of a string 
	 */
	protected String rm_specialChar(String word) {
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
        if(word.contains("'")) {
        	word = word.replace("'", "");
        }        
        if(word.contains(".")) {
        	word = word.replace(".", "");
        } 
        if(word.contains("?")) {
        	word = word.replace("?","");
        }
        if(word.contains("!")) {
        	word = word.replace("!","");
        }       
        return word;	
	}
	
	protected String concatList(List<String> list) {
		int i = list.size();
		String name = "";
		for(int j=0;j<i-1;j++) {
			name = name.concat(list.get(j) + " ");
		}
		name = name.concat(list.get(i-1));
		return name;
	}
	
	protected String single_compund(ConceptManager man, Concept concept, StanfordLemmatizer slem, int single) {
		String name = null;
		if(single == 0) {
			name = man.conceptName_wn(concept);
			List<String> cnpNameLemma = slem.lemmatize(name);
			int i = cnpNameLemma.size();
			name = cnpNameLemma.get(i - 1);
			
		} else if(single == 1) {
			name = man.conceptName(concept);
			List<String> cnpNameLemma2 = slem.lemmatize(name);
			name = concatList(cnpNameLemma2);
		}
		return name;
	}

}

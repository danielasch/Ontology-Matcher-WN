package synsetSelection;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import objects.Concept;
import objects.ConceptManager;
import resources.BaseResource;
import resources.StanfordLemmatizer;
import resources.Utilities;

public class SynsetDisambiguation {
	
	private BaseResource base;
	
	
	public SynsetDisambiguation(BaseResource _base) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Synset didambiguation selected!" );
		this.base = _base;
	}
	//*info logs*//
	private void init_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Disambiguating Synsets..." );
	}
	
	private void final_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Synsets disambiguated!" );
	}
	//*process*//
	public void disambiguation(List<Concept> listCon) {
		try {
			init_log();
			for(Concept concept: listCon) {
				rc_goodSynset(concept);
			}
			final_log();
		} catch(IOException e) {
			System.out.println("I/O operation failed - WN dictinoary!");
			System.out.println("error: " + e);
		}
	}
	
	private void rc_goodSynset(Concept concept) throws IOException {		
		IDictionary dict = this.base.get_dictionary();
		StanfordLemmatizer slem = this.base.get_lemmatizer();
		ConceptManager man = new ConceptManager();
		Utilities ut = new Utilities();
		
		LinkedHashMap<ISynset, List<String>> temp1 = new LinkedHashMap<ISynset, List<String>>();
		int numSy = 0;
		
		List<String> context = slem.toList(concept.get_context());
		String name = man.conceptName_wn(concept);
		List<String> cnpNameLemma = slem.lemmatize(name);
		int i = cnpNameLemma.size();
		name = cnpNameLemma.get(i - 1);

		dict.open();
		IIndexWord idxWord = dict.getIndexWord(name, POS.NOUN);
		
		if(idxWord != null) {
			int max = 0;
			List<IWordID> wordIds = idxWord.getWordIDs();
			int numSynset = wordIds.size();
			if(numSynset != 1) {
				for (IWordID wordId : wordIds) {
					IWord word = dict.getWord(wordId);
					ISynset synset = word.getSynset();
	
					/*Cria uma lista com as palavras do synset */
					List<IWord> wordsSynset = synset.getWords();
					String glossSynset = synset.getGloss();
			    		
					/*Retorna uma lista com o BagOfWords do synset*/
					List<String> bagSynset = createBagWords(wordsSynset, glossSynset);
					temp1.put(synset, bagSynset);
					/*Metodo que compara o Contexto com o BagOfWords do synset*/
					int size = intersection(context, bagSynset);
					if(size > max) {
						max = size;
						man.config_synset(concept, synset);
						//System.out.println(max);
					}
					numSy++;
				}
				ut.set_numSy(numSy);
			} else {
				IWordID wordId = wordIds.get(0);
				IWord word = dict.getWord(wordId);
				ISynset synset = word.getSynset();
				man.config_synset(concept, synset);
				ut.set_numSy(1);
			}
		}
		dict.close();
		ut.set_synsetCntx(temp1);
		man.config_utilities(concept, ut);
	}
	
	private List<String> createBagWords(List<IWord> wordsSynset, String glossSynset) {
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
	    //System.out.println(stopWords);
	    StringTokenizer st = new StringTokenizer(glossSynset," ");
    	while (st.hasMoreTokens()) {
    		   String token = st.nextToken().toLowerCase();
    		   token = rm_specialChar(token);
    		   if (!this.base.get_StpWords().contains(token) && !list.contains(token)) {			
    			   list.add(token);
    		   }
    	}
    	
    	String toLemma = slem.toLemmatize(list);
		list.clear();
		list = slem.lemmatize(toLemma); 	 	
		set =  slem.toSet(list); 
		list = slem.toList(set);
	   return list;
	}
	
	int intersection(List<String> context, List<String> bagSynset) {
		int inter = 0;
		for(String word: context) {
			//System.out.println(word);
			word = word.toLowerCase();
			for(String wordCompared: bagSynset) {
				if(word.equals(wordCompared)) {
					inter++;
				}
			}
		}
		return inter;
	}
	
	private String rm_specialChar(String word) {
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
	

}

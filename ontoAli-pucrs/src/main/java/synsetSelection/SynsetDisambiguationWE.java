package synsetSelection;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
/*
 * This class disambiguate the synset using the Word Embedding model from GloVe.
 * Model specifications - 6B tokens, 400K vocab, uncased, & 200d vectors.
 */
public class SynsetDisambiguationWE {

//Attributes
	
	//BaseResource contains the necessary resources to execute the disambiguation
	private BaseResource base;

//Constructor	
	
	public SynsetDisambiguationWE(BaseResource _base) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Synset didambiguation with Word embedding selected!" );
		this.base = _base;
	}

//Log methods
	
	private void init_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Disambiguating Synsets..." );
	}
	
	private void final_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Synsets disambiguated!" );
	}

//Methods	
	
	/*
	 * This method selects the right synset to a concept
	 */
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
		
	/*
	 * Disambiguation process 
	 */
	void rc_goodSynset(Concept concept) throws IOException {
		//Idictionary is used to access WordNet
		IDictionary dict = this.base.get_dictionary();
		//The lemmatizer
		StanfordLemmatizer slem = this.base.get_lemmatizer();
		ConceptManager man = new ConceptManager();
		//Utilities carries the temp1 list and the temp2 list of a concept
		Utilities ut = new Utilities();
		//temp1 saves the synset and its bag of words (OutFiles use only)
		LinkedHashMap<ISynset, List<String>> temp1 = new LinkedHashMap<ISynset, List<String>>();
		
		//temp2 saves the averages between the context and the bag of words of a concept (OutFiles use only)
		ArrayList<Double> temp2 = new ArrayList<Double>();
		
		LinkedHashMap<ISynset, LinkedHashMap<String, LinkedHashMap<String, Double> > > temp3 = new LinkedHashMap<ISynset, LinkedHashMap<String, LinkedHashMap<String, Double> > >();
				
		List<String> context = slem.toList(concept.get_context());
		//name receive the concept name
		String name = man.conceptName_wn(concept);
		//lemmatize the concept name
		List<String> cnpNameLemma = slem.lemmatize(name);
		int i = cnpNameLemma.size();
		//name receive the concept name lemmatized
		name = cnpNameLemma.get(i - 1);
		//open the dictionary 
		dict.open();
		//idxWord receive the IIndexWord of a noun word in WordNet
		//in this case the concept name was used as argument
		IIndexWord idxWord = dict.getIndexWord(name, POS.NOUN);

		if(idxWord != null) {
			double max = 0;
			//each IWordID has a synset
			List<IWordID> wordIds = idxWord.getWordIDs();
			
			for (IWordID wordId : wordIds) {
				IWord word = dict.getWord (wordId) ;
			    ISynset synset = word.getSynset();
			    LinkedHashMap<String, LinkedHashMap<String, Double> > temp4 = new LinkedHashMap<String, LinkedHashMap<String, Double> >();
			    
			    //wordsSynset receive the words that composes the synset
			    List<IWord> wordsSynset = synset.getWords();
			    //glossSynset receive the gloss of the synset
			    String glossSynset = synset.getGloss();
			    //bagSynset receive the bag of words of the synset
				//the bag of words is created with the gloss and the 
				//words that compose the synset
			    List<String> bagSynset = createBagWords(wordsSynset, glossSynset);
			    
			    //temporary map that saves the synset and its bag of words
			    temp1.put(synset, bagSynset);
			    double auxT = 0;
			    for(String cntxtEl: context) {
			    	
			    	LinkedHashMap<String, Double> temp5 = new LinkedHashMap<String, Double>();
			    	double aux1 = 0;
			    	//For each element of the bag of words
			    	for(String bgwEl: bagSynset) {
			    		//Verifies the similarity between the context element and the bag of words element
			    		double sim = this.base.get_word2vec().get_word2Vec().similarity(cntxtEl, bgwEl);
			    		//condition that verifies if the similarity recovered is not null,
			    		//if is null then the similarity receives 0
			    		if(!(Double.isNaN(sim))) {
			    			//increment the aux1 and adds the similarity
		                    aux1 = aux1 + sim * 10;
		                } else {
		                	//case similarity is null
		                    aux1 = aux1 + 0;
		                }
			    		temp5.put(bgwEl, sim * 10);
			    	}
			    	temp4.put(cntxtEl, temp5);
			    //makes the average between context element and all bag of words elements,
			    //dividing aux1 by the bag of words size
			    aux1 = aux1 / bagSynset.size();
			    //increment the auxT and adds the previous average
			    auxT = auxT + aux1;	
			    	
			    }
			    //makes the total average between the previous averages and the context size,
			    //dividing auxT by context size
			    auxT = auxT / context.size();
			    //adds the total average into a list
			    temp2.add(auxT);
			    //the synset with the higher auxT is selected as the right one
			    if(auxT > max) {
			    	max = auxT;
			    	//sets the synset of a concept
			    	man.config_synset(concept, synset);
			    }
			    temp3.put(synset, temp4);
			}
		}
		//closes the IDictionary
		dict.close();
		//utilities sets the synset and the bag of words map
		ut.set_synsetCntx(temp1);
		//utilities sets the total average list
		ut.set_synsetMedia(temp2);
		
		ut.set_pairSim(temp3);
		//sets the utilities of a concept
		man.config_utilities(concept, ut);
	}
	
	/*
	 * create the bag of words of a synset
	 */
	private List<String> createBagWords(List<IWord> wordsSynset, String glossSynset) {
	    List<String> list = new LinkedList<String>();
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

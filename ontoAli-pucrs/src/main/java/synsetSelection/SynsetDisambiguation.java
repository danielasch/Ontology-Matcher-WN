package synsetSelection;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;


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
 * This class disambiguate the recovered synsets for a concept
 */
public class SynsetDisambiguation extends SynsetBasic {
	
//Attributes
	
	//BaseResource contains the necessary resources to execute the disambiguation
	//private BaseResource base;
	
//Constructor
	
	public SynsetDisambiguation(BaseResource _base, int _single) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Synset didambiguation selected!" );
		this.base = _base;
		this.single = _single;
	}
	
//Log Methods
	
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
	private void rc_goodSynset(Concept concept) throws IOException {
		//Idictionary is used to access WordNet
		IDictionary dict = this.base.get_dictionary();
		//The lemmatizer
		StanfordLemmatizer slem = this.base.get_lemmatizer();
		ConceptManager man = new ConceptManager();
		//Utilities carries the temp1 list and the numSy of a concept
		Utilities ut = new Utilities();
		//temp1 saves the synset and its bag of words (OutFiles use only)
		LinkedHashMap<ISynset, List<String>> temp1 = new LinkedHashMap<ISynset, List<String>>();
		//numSy will receive the number of synsets recovered to a concept (OutFiles use only)
		int numSy = 0;
		
		List<String> context = slem.toList(concept.get_context());
		
		String name = single_compund(man, concept, slem, this.single);
		//open the Idictionary
		dict.open();
		IIndexWord idxWord = dict.getIndexWord(name, POS.NOUN);
		
		//idxWord receive the IIndexWord of a noun word in WordNet
		//in this case the concept name was used as argument
		
		if(idxWord != null) {
			int max = 0;
			//each IWordID has a synset
			List<IWordID> wordIds = idxWord.getWordIDs();
			//the numbers o synsets recovered
			int numSynset = wordIds.size();
			//if numSynset is different than 1, then the 
			//disambiguation process occurs
			if(numSynset != 1) {
				for (IWordID wordId : wordIds) {
					IWord word = dict.getWord(wordId);
					ISynset synset = word.getSynset();
					//System.out.println(synset);
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
					//size receive the number of overlaps between two lists
					int size = intersection(context, bagSynset);
					//System.out.println(size);
					if(size > max) {
						max = size;
						//sets the synset of a concept
						man.config_synset(concept, synset);
					}
					numSy++;
				}
				//utilities set the number os synsets
				ut.set_numSy(numSy);
			} else {
				IWordID wordId = wordIds.get(0);
				IWord word = dict.getWord(wordId);
				ISynset synset = word.getSynset();
				man.config_synset(concept, synset);
				//utilities set the number os synsets,
				//in this case 1
				ut.set_numSy(1);
			}
		}
		//closes the IDictionary
		dict.close();
		//utilities sets the synset and the bag of words map
		ut.set_synsetCntx(temp1);
		//sets the utilities of a concept
		man.config_utilities(concept, ut);
	}
	
	/*
	 * Overlapping between two lists
	 */
	int intersection(List<String> context, List<String> bagSynset) {
		int inter = 0;
		for(String word: context) {
			word = word.toLowerCase();
			for(String wordCompared: bagSynset) {
				if(word.equals(wordCompared)) {
					inter++;
					break;
				}
			}
		}
		return inter;
	}
	

}

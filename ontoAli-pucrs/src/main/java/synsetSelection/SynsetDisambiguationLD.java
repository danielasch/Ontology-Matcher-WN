package synsetSelection;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import objects.Concept;
import objects.ConceptManager;
import objects.OutObjectLD;
import objects.OutObjectLD.parLD;
import resources.BaseResource;
import resources.StanfordLemmatizer;

public class SynsetDisambiguationLD extends SynsetBasic{
		
	//Constructor
		
		public SynsetDisambiguationLD(BaseResource _base, int _single) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Levenshtein Distance - Synset didambiguation selected!" );
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
			
			List<String> context = slem.toList(concept.get_context());
			
			//function that chooses if the name from concept is a single term or compound term
			//if single returns the last term from name, if not returns all terms separated by BLANK_SPACES
			String name = single_compund(man, concept, slem, this.single);
			
			//open the Idictionary
			dict.open();
			IIndexWord idxWord = null;
			
			idxWord = dict.getIndexWord(name, POS.NOUN);
			
			//idxWord receive the IIndexWord of a noun word in WordNet
			//in this case the concept name was used as argument to be searched on WN
			
			if(idxWord != null) {
				float max = 0;
				//each IWordID has a synset
				List<IWordID> wordIds = idxWord.getWordIDs();
				//the numbers o synsets recovered
				int numSynset = wordIds.size();
				//if numSynset is different than 1, then the 
				//disambiguation process occurs
				
				List<OutObjectLD> listOOLD = new ArrayList<>(numSynset);
				if(numSynset != 1) {
					for (IWordID wordId : wordIds) {
						IWord word = dict.getWord(wordId);
						ISynset synset = word.getSynset();
						
						OutObjectLD oold = new OutObjectLD();
						oold.set_synset(synset);
						
						//wordsSynset receive the words that composes the synset
						List<IWord> wordsSynset = synset.getWords();
						//glossSynset receive the gloss of the synset
						String glossSynset = synset.getGloss();
				    	//bagSynset receive the bag of words of the synset
						//the bag of words is created with the gloss and the 
						//words that compose the synset
						List<String> bagSynset = createBagWords(wordsSynset, glossSynset);
						
						float value = calculate(context, bagSynset, oold);
						//System.out.println(size);
						
						
						if(value > max) {
							max = value;
							//sets the synset of a concept
							man.config_synset(concept, synset);
						}
						
						listOOLD.add(oold);
					}

				} else {
					IWordID wordId = wordIds.get(0);
					IWord word = dict.getWord(wordId);
					ISynset synset = word.getSynset();
					man.config_synset(concept, synset);
	
					OutObjectLD oold = new OutObjectLD();
					oold.set_synset(synset);
					
					listOOLD.add(oold);
				}
				man.config_object(concept,listOOLD);
			}
			//closes the IDictionary
			dict.close();
		}
		
		/*
		 * Levenshtein Distance calculation
		 */
		
		private float calculate(List<String> context, List<String> bagSynset, OutObjectLD oold) {
			float value = 0;
			
			List<parLD> listpld = new ArrayList<parLD>(context.size());
			
			for(String element_c: context) {
				float medium = 0;
				
				parLD pld = oold.instance_parLD();
				Map<String, Float> par = new HashMap<>();
				pld.set_element_ctx(element_c);
				
				for(String element_b: bagSynset) {
					float quick = computeLevenshteinDistance(element_c, element_b);
					medium = medium + quick;
					par.put(element_b, quick);
				}
				value = value + medium;
				
				pld.set_total(medium);
				pld.set_par(par);
				listpld.add(pld);
			}
			oold.set_ctxList(listpld);
			oold.set_valor_total(value);
			return value;
		}
		
		private int minimum(int a, int b, int c) {
			return Math.min(Math.min(a, b), c);
		}
		
		public float computeLevenshteinDistance(String str1,String str2) {
			int[][] distance = new int[str1.length() + 1][str2.length() + 1];
			for (int i = 0; i <= str1.length(); i++)
				distance[i][0] = i;
			for (int j = 1; j <= str2.length(); j++)
				distance[0][j] = j;
			for (int i = 1; i <= str1.length(); i++)
				for (int j = 1; j <= str2.length(); j++)
					distance[i][j] = minimum(
							distance[i - 1][j] + 1,
							distance[i][j - 1] + 1,
							distance[i - 1][j - 1]+ ((str1.charAt(i - 1) == str2.charAt(j - 1)) ?
							0 : 1));
			
			return (float) (1.0-((float)(distance[str1.length()][str2.length()])/Math.max(str1.length(),
			str2.length())));
		}
}

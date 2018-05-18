package synsetSelection;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import objects.Concept;
import objects.ConceptManager;
import resources.BaseResource;
import resources.StanfordLemmatizer;

public class SynsetDisambiguationWE2 {
	
private BaseResource base;
	
	public SynsetDisambiguationWE2(BaseResource _base) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Synset didambiguation with Word embedding selected!" );
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
	public void disambiguation(List<Concept> listCon, List<Concept> listUp) {
		try {
			init_log();
			for(Concept concept: listCon) {
				rc_goodSynset(concept, listUp);
			}
			final_log();
		} catch(IOException e) {
			System.out.println("I/O operation failed - WN dictinoary!");
			System.out.println("error: " + e);
		}
	}
	
	void rc_goodSynset(Concept concept, List<Concept> listUp) throws IOException {		
		IDictionary dict = this.base.get_dictionary();
		StanfordLemmatizer slem = this.base.get_lemmatizer();
		ConceptManager man = new ConceptManager();

		//List<String> context = slem.toList(concept.get_context());
		String name = man.conceptName_wn(concept);

		List<String> cnpNameLemma = slem.lemmatize(name);
		int i = cnpNameLemma.size();
		name = cnpNameLemma.get(i - 1);

		//dict.open();
		//IIndexWord idxWord = dict.getIndexWord(name, POS.NOUN);
		List<String> lister = (List<String>) this.base.get_word2vec().get_word2Vec().wordsNearest(name, 200);
		//System.out.println("NOME: " + name);
		//System.out.println("CLASS: " + man.conceptName_wn(concept));
		for(String vizinho: lister) {
			//System.out.println("-- " + vizinho);
			for(Concept cnp_2: listUp) {
				if(vizinho.toLowerCase().equals(cnp_2.get_className().toLowerCase())) {
					//System.out.println(vizinho + "|" + cnp_2.get_className());
					dict.open();

					IIndexWord idxWord = dict.getIndexWord(name, POS.NOUN);

					if(idxWord != null) {
						List<IWordID> wordIds = idxWord.getWordIDs();
						
						for (IWordID wordId : wordIds) {
							IWord word = dict.getWord (wordId) ;
						    ISynset synset = word.getSynset();
						    //System.out.println("SY:" + synset);	
						    List<ISynsetID> hypers = synset.getRelatedSynsets(); 
						    
						    IIndexWord idxWord_2 = dict.getIndexWord(cnp_2.get_className().toLowerCase(), POS.NOUN);
						    if(idxWord_2 != null) {
								List<IWordID> wordIds_2 = idxWord_2.getWordIDs();
								
								for (IWordID wordId_2 : wordIds_2) {
									IWord word_2 = dict.getWord (wordId_2) ;
								    ISynset synset_2 = word_2.getSynset();
								    //System.out.println("SY_2:" + synset_2);
								    
								    for(ISynsetID syID: hypers) {
								    	ISynset sy_2 = dict.getSynset(syID);
								    	//System.out.println("SYR: "+sy_2);
								    	if(synset_2.equals(sy_2)) {
								    		//System.out.println("TEM RELAÇÃO");
								    	}
								    }
								}
						    } 

						}
					}
					dict.close();
				}
			}
			
		}
	}

}

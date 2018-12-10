package matchingProcess;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.Pointer;
import objects.Concept;
import objects.ConceptManager;
import objects.Ontology;
import objects.outObjectWNH;
import resources.BaseResource;

public class MatchingWN extends RDF{
	
	private BaseResource base;
	
	
//Log Methods
	
	
	private void init_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Matching ontologies..." );
	}
	
	private void final_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Ontologies matched!" );
	}

	
//Constructor 
	
	
	public MatchingWN(BaseResource _base, String _localfile) {
		List<Mapping>lstmap = new LinkedList<>();
		set_listMap(lstmap);
		this.base = _base;
		set_file(_localfile);
	}
	
	
//Methods
	
	/*
	 * Start the matching process
	 */
	public void match(List<Concept> listDom, List<Concept> listUp) {
		init_log();
		for(Concept cnp: listDom) {
			if(cnp.get_goodSynset() != null) {
				matchHyp(cnp, listUp);
			}	
		}
		final_log();
	}
	
	public void out(Ontology onto1, Ontology onto2) {
		out_rdf(onto1,onto2);
	}
	
	private void matchHyp(Concept cnp, List<Concept> listUp) {
		IDictionary dict = this.base.get_dictionary();
		try {
			dict.open();
		} catch (IOException e) {
			System.out.println("ERROR - Could not open the WordNet Dictionary");
			e.printStackTrace();
		}
		Map<Concept, Integer> map = new HashMap<>();
		ISynset synset = cnp.get_goodSynset();
		int cont = 0;
		Concept align = null;
		
		ConceptManager man = new ConceptManager();
		outObjectWNH nc = new outObjectWNH();
		
		nc.set_synset(synset);
		nc.set_cont(cont);
		nc.create_list();
		findHypers(dict, synset, listUp, map, cont, nc);
		dict.close();
		align = rightCnp(map);
		
		if(align != null) {
			Mapping mappin = new Mapping();
			man.config_aliClass(cnp, align.get_owlClass());
			mappin.set_source(cnp.get_classID());
			mappin.set_target(align.get_classID());
			mappin.set_measure("1.0");
			mappin.set_relation("&lt;");
			this.listMap.add(mappin);
		}
		man.config_object(cnp, nc);
		map.clear();
	}
	
	
	/*
	 * Returns the Hypernym of a certain synset 
	 */
	private List<ISynsetID> getHyper(ISynset synset) {
		return synset.getRelatedSynsets(Pointer.HYPERNYM);
	}
	
	/*
	 * Searches for the synset words that matches a Top Ont. Concept
	 */
	private Concept search(ISynset synset, List<Concept> listUp) {
		List<IWord> listIW = synset.getWords();
		Concept align = null;
		for(IWord iw: listIW) {
			String word = iw.getLemma();
			align = searchTop(word, listUp);	
		}
		return align;
	}
		
	/*
	 * Searches for a Top Ont. Conept that matches a certain word (str that contains the synset of hypernym)
	 */
	private Concept searchTop(String word, List<Concept> listUp) {
		for(Concept up: listUp) {
			if(up.get_className().toLowerCase().equals(word.toLowerCase())) {
				return up;
			}
		}
		return null;
	}
	
	
	/*
	 * Runs the Word Net Hypernyms structure
	 */
	private void findHypers(IDictionary dict, ISynset synset, List<Concept> listUp, Map<Concept, Integer> map, int cont, outObjectWNH nc) {
		if(synset != null) {
			ISynset synsetAux = null;
			Concept align = null;
			
			List<ISynsetID> hyp = getHyper(synset);
			if(!hyp.isEmpty()) {
				cont++;
				for(ISynsetID synsetID: hyp) {
					synsetAux = dict.getSynset(synsetID);
					outObjectWNH nnc = new outObjectWNH(synsetAux, cont);
					nc.add_list(nnc);
					align = search(synsetAux, listUp);

					if(align != null) {
						map.put(align, cont);
					}
					findHypers(dict, synsetAux, listUp, map, cont, nnc);
				}
			}
		}
	}
	
	
	/*
	 *	Returns the lower level aligned hypernym 
	 */
	private Concept rightCnp(Map<Concept, Integer> map) {
		if(map.size() == 1) {
			return map.entrySet().iterator().next().getKey();
		} else {
			int max = -1;
			Concept right = null;
			for (Entry<Concept, Integer> entry : map.entrySet()) {
				if(max == -1) {
					max = entry.getValue();
					right = entry.getKey();
				} else if(max > entry.getValue()) {
					max = entry.getValue();
					right = entry.getKey();
				}
			}
			return right;
		}
	}
	
}

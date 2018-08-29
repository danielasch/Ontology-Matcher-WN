package matchingProcess;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.Pointer;
import objects.Concept;
import objects.ConceptManager;
import objects.Ontology;
import resources.BaseResource;

public class MatchingWN extends RDF{
	
	private BaseResource base;
	
	public MatchingWN(BaseResource _base, String _localfile) {
		List<Mapping>lstmap = new LinkedList<>();
		set_listMap(lstmap);
		this.base = _base;
		set_file(_localfile);
	}
	
	public void match(List<Concept> listDom, List<Concept> listUp) {
		for(Concept cnp: listDom) {
			System.out.println(cnp.get_className());
			matchHyp(cnp, listUp);
		}
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
		ISynset synset = cnp.get_goodSynset();
		Concept align = null;
		boolean aux = true;
		while(aux) {
			List<ISynsetID> hyp = getHyper(synset);
			for(ISynsetID synsetID: hyp) {
				synset = dict.getSynset(synsetID);
				align = search(synset, listUp);
			}
			if(align != null || hyp.isEmpty()) {
				aux = false;
			}
		}
		if(align != null) {
			System.out.println(cnp.get_className() + "||" + align.get_className().toString());
			System.out.println(synset);
			Mapping map = new Mapping();
			ConceptManager man = new ConceptManager();
			man.config_aliClass(cnp, align.get_owlClass());
			map.set_source(cnp.get_owlClass().getIRI().toString());
			map.set_target(align.get_owlClass().getIRI().toString());
			map.set_measure("1.0");
			map.set_relation("&lt;");
			addMap(map);
			
		} else {
			System.out.println(cnp.get_className() + "||" + "null");
			System.out.println(synset);
		}
		dict.close();
	}
	
	private List<ISynsetID> getHyper(ISynset synset) {
		return synset.getRelatedSynsets(Pointer.HYPERNYM);
	}
	
	private Concept search(ISynset synset, List<Concept> listUp) {
		List<IWord> listIW = synset.getWords();
		Concept align = null;
		for(IWord iw: listIW) {
			String word = iw.getLemma();
			align = searchTop(word, listUp);	
		}
		return align;
	}
	
	private Concept searchTop(String word, List<Concept> listUp) {
		for(Concept up: listUp) {
			if(up.get_className().toLowerCase().equals(word.toLowerCase())) {
				return up;
			}
		}
		return null;
	}
	
}

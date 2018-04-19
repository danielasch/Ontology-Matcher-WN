package resources;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class StanfordLemmatizer {
	protected StanfordCoreNLP pipeline;
	
	public StanfordLemmatizer() {

	    Properties props;
	    props = new Properties();
	    props.put("annotators", "tokenize, ssplit, pos, lemma");
	    
	    init_log();
	    pipeline = new StanfordCoreNLP(props);

	}
	
	private void init_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Initializing Lemmatizer..." );
	}
	
	public List<String> lemmatize(String documentText) {
		List<String> lemmas = new ArrayList<String>();
    	// Create an empty Annotation just with the given textd
    	Annotation document = new Annotation(documentText);
    	// run all Annotators on this text
    	pipeline.annotate(document);
    	// Iterate over all of the sentences found
    	List<CoreMap> sentences = document.get(SentencesAnnotation.class);
    	for(CoreMap sentence: sentences) {
        	// Iterate over all tokens in a sentence
        	for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
            	// Retrieve and add the lemma for each word into the
            	// list of lemmas
            	lemmas.add(token.get(LemmaAnnotation.class));
        	}
    	}
    	return lemmas;
	}
	//*métodos de conversão*//
	public String toLemmatize(List<String> list) {
		String full = "";
		for(String word: list) {
			full = full + word + " ";
		}
		return full;
	}
	
	public HashSet<String> toSet(List<String> list) {
		HashSet<String> set = new HashSet<String>();
		for(String word: list) {
			set.add(word);
		}
		return set;
	}
	
	public List<String> toList(Set<String> set) {
		List<String> list = new ArrayList<String>();
		for(String word: set) {
			list.add(word);
		}
		return list;
	}
	

}

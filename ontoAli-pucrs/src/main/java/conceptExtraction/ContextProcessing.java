package conceptExtraction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import objects.Concept;
import objects.ConceptManager;
import resources.BaseResource;
import resources.StanfordLemmatizer;

/*
 * This class process the context of a concept
 */
public class ContextProcessing {

	//Attributes

	//BaseResource contains the necessary resources to execute the context process
	private BaseResource base;

	//Constructor	

	ContextProcessing(BaseResource _base) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Context process selected!" );
		this.base = _base;
	}

	//Log methods	

	private void init_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Processing the context..." );
	}
	private void final_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Context processed!" );
	}

//Methods - second process
	
	protected void procWE(List<Concept> listUp) {
		init_log();
		
		for(Concept cnp: listUp) {
			start(cnp);
		}
		
		final_log();
	}
	
	private void start(Concept cnp) {
		ConceptManager cMan = new ConceptManager();
		List<String> list = null;
		Set<String> set = null;
		list = token(cnp.get_context());
		list = removal(list);
		list = separate(list);
		set = lemma(list);
		set = number(set);
		cMan.config_context(cnp, set);
	}
	
	private List<String> token(Set<String> ctx) {
		List<String> list = new ArrayList<>();
		List<String> aux = new ArrayList<>();
		for(String el: ctx) {
			tokenize(el, " ", aux);
		}
		
		for(String el: aux) {
			tokenize(el, "\n", list);
		}
		aux.clear();
		for(String el: list) {
			tokenize(el, "_", aux);
		}
		list.clear();
		return aux;
	}
	
	private void tokenize(String el, String sp, List<String> list) {

		StringTokenizer st = new StringTokenizer(el, sp);
		while (st.hasMoreTokens()) {
 		   	String token = st.nextToken();

 		   	if (!list.contains(token)) {			
 		   		list.add(token);
 		   	}
		}
	}
	
	private List<String> removal(List<String> aux) {
		List<String> list = new ArrayList<>();
		for (String word : aux) {
			String str = null;
			str = removeSpecialChar(word);
			tokenize(str, " ", list);
		}
		aux.clear();
		return list;
	}
	
	private String removeSpecialChar(String word) {
		char x = '"';
		String asp = String.valueOf(x);
		if(!isSite(word)) {
			word = word.replace(".", "").replace(",", "").replace("'s", "").replace(asp, "").replace(")", "").replace("(", " ").replace(";", "").replace("&", "").replace("'", "").replace("?", "").replace("!", "").replace("%", "").replace(":", "");
		} else {
			word = word.replace(",", "").replace("'s", "").replace(asp, "").replace(")", "").replace("(", "").replace("'", "").replace("?", "").replace("!", "");
		}
		return word;
	}
	
	private boolean isSite(String word) {
		if(word.contains("http:")) {
			return true;
		}
		return false;
	}
	
	private List<String> separate(List<String> aux) {
		List<String> list = new ArrayList<>();
		for (String word : aux) {
			String str = null;
			if(!isSite(word)) {
				str = putCharSeq(word);
				tokenize(str, "#", list);
			} else {
				str = word;
				tokenize(str, "#", list);
			}

		}
		aux.clear();
		return list;
	}
	
	private String putCharSeq(String word) {
		String str = "";
		int sIndex = 0;
		for(int i=0; i < word.length(); i++) { //length -1
			char ch = word.charAt(i);
			
			if(Character.isUpperCase(ch) && i != word.length()-1) {
				char next = word.charAt(i+1);
				
				if(i!=0 && Character.isLowerCase(next)) {
					char prev = word.charAt(i-1);
					if(prev != '#') {
						str = str + word.substring(sIndex, i) + "#";
						sIndex = i;
					}
				}
			} else if(!Character.isLetterOrDigit(ch)) {
				word = word.replace("-", "#").replace("/", "#");
			}
		}
		str = str + word.substring(sIndex);
		return str;
	}
	
	private Set<String> lemma(List<String> aux) {
		List<String> list = null;
		Set<String> set = null;
		list = stopWords(aux);
		set = lemmatize(list);
		aux.clear();
		return set;
	}
	
	private List<String> stopWords(List<String> aux) {
		List<String> stpWords = this.base.get_StpWords();
		List<String> list = new ArrayList<>();
		for(String word: aux) {            
			String wordLow = word.toLowerCase();
			if(!(stpWords.contains(wordLow))) {  
				list.add(wordLow);
			}  
		}
		return list;
	}
	
	private Set<String> lemmatize(List<String> aux) {
		List<String> lemma = new ArrayList<String>();
		StanfordLemmatizer slem = this.base.get_lemmatizer();
		String toLemma = slem.toLemmatize(aux);
		lemma = slem.lemmatize(toLemma);
		aux.clear();
		return slem.toSet(lemma);
	}
	
	private Set<String> number(Set<String> aux) {
		Set<String> set = new HashSet<>();
		for(String word: aux) {
			String str = removeNumbers(word);
			if(!str.equals("")) {
				set.add(str);
			}
		}
		aux.clear();
		return set;
	}
	
	private String removeNumbers(String word) {
		word = word.replaceAll("[*0-9]", "");
		return word;
	}
	
	//Methods

	/*
	 * This method process the context 
	 */
	protected void process(List<Concept> listCon) {
		ConceptManager man = new ConceptManager();
		init_log();

		for(Concept concept: listCon) {
			//System.out.println("@@" + concept.get_className());
			//System.out.println("@@" + concept.get_context());
			//context will receive the processed context of a concept
			Set<String> context = init(concept.get_context());
			//sets the context of a concept
			man.config_context(concept, context);
		}
		final_log();
	}

	/*
	 * Split process
	 */
	private Set<String> init(Set<String> context) {
		//Split strings, turning all elements of the context
		//into tokens
		Set<String> aux = sp_string(context);
		Set<String> temp = new HashSet<String>();
		//Split strings with upperCase.
		//This second split, avoid tokens separated by upperCase
		//that were in the description and were not separated. 
		for(String word: aux) {
			if(word.length() != 0) {	
				if(hasUpperCase(word)) {
					String tempStr = rm_specialChar(word);
					temp.addAll(sp_upperCase(tempStr));
				} else {
					String tempStr = rm_specialChar(word);
					temp.add(tempStr.toLowerCase());
				}
			}
		}
		//Remove the stop words
		//System.out.println("$$" + temp);
		temp = rm_stopWords(temp);
		//Lemmatize the context
		temp = lemmatizer(temp);
		//System.out.println("--" + temp);
		temp = rm_stopWords(temp);
		//System.out.println("---" + temp);
		return temp;
	}

	/*
	 * Lemmatizes all elements of a set
	 */
	private Set<String> lemmatizer(Set<String> context) {
		List<String> lemma = new ArrayList<String>();
		StanfordLemmatizer slem = this.base.get_lemmatizer();
		String toLemma = slem.toLemmatize(slem.toList(context));
		lemma = slem.lemmatize(toLemma);
		return slem.toSet(lemma);
	}

	/*
	 * Split strings separated with "_", " ", "�" and upperCase
	 */
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

	/*
	 * Splits strings separated by upperCase
	 */
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

	//Auxiliary methods

	/*
	 * Removes the stopwords of a set
	 */
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

	/*
	 * Removes some chars of a string
	 */
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
			word = word.replace("'s", "");
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

	/*
	 * Verifies if a string is separated by space
	 */
	private boolean hasWhiteSpace(String str) {
		int length = str.length();

		for(int y = 1; y < length; y++) {
			if(Character.isWhitespace(str.charAt(y))) {
				return true;
			}	
		}
		return false;	
	}

	/*
	 * Verifies if a string is separated by UpperCase
	 */
	private boolean hasUpperCase(String word) {
		int x = word.length();
		if(!hasConsecutiveUpperCase(word)) {
			for(int y = 1; y < x; y++) {
				if(Character.isUpperCase(word.charAt(y))) {
					return true;
				}	
			}
		}
		return false;	
	}

	private boolean hasConsecutiveUpperCase(String word) {
		int cont = 0;
		int size = word.length();

		if(size != 0) {
			for(int y = 0; y < word.length(); y++) {
				if(Character.isUpperCase(word.charAt(y))) {
					cont++;
				}	
			}
			float aux = (cont / size) * 10;
			if(aux > 6) {
				return true;
			} 
		}
		return false;
	}


}

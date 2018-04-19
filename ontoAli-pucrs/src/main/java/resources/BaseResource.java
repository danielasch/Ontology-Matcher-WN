package resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;

public class BaseResource {
	
	private List<String> stpWords = new ArrayList<String>();
	private String wnhome = "resources/WordNet-3.0"; //Local do WordNet
	private IDictionary dict;
	private StanfordLemmatizer slem;
	private Word2Vector w2v;
	
	public BaseResource() {
			init_log();
			rd_StpWords();
			rc_dictionary();
			this.slem = new StanfordLemmatizer();
			this.w2v = null;
	}
	public BaseResource(int x) {
		if(x == 1) {
			init_log();
			rd_StpWords();
			rc_dictionary();
			this.slem = new StanfordLemmatizer();
			this.w2v = null;	
		} else if(x == 2) {
			init_log();
			rd_StpWords();
			rc_dictionary();
			this.slem = new StanfordLemmatizer();
			this.w2v = new Word2Vector();
		} else {
			System.out.println("ERROR*****");
		}
	}
	
	//*info logs*//
	private void init_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Initializing resources!" );
	}
	
	private void stpWords_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Reading Stop Words..." );
	}
	
	private void dictionary_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Accessing WordNet..." );
	}
	//*readers*//
	private void rd_StpWords() {
		stpWords_log();
		try {
			BufferedReader br = new BufferedReader(new FileReader("resources/stopwords2.txt"));
			String line;
			
			while((line = br.readLine()) != null) {
				this.stpWords.add(line.toLowerCase());
			}
			br.close();
		} catch(FileNotFoundException e) {
	    	System.out.println("StopWords file was not found!");
	    	System.out.println("error: " + e);
	    } catch (IOException e) {
	    	System.out.println("I/O operation failed - StopWords file!");
	    	System.out.println("error: " + e);
	    }
	}
	
	private void rc_dictionary() {
		dictionary_log();
		try {
			String path = wnhome + File.separator + "dict";
			URL url = new URL("file",null,path);
			this.dict = new Dictionary(url);
		} catch(MalformedURLException e) {
			System.out.println("WordNet URL malformed!");
			System.out.println("error: " + e);
		}
	}
	
	//*getters*//
	public List<String> get_StpWords() {
		return this.stpWords;
	}
	
	public IDictionary get_dictionary() {
		return this.dict;
	}
	
	public StanfordLemmatizer get_lemmatizer() {
		return this.slem;
	}
	
	public Word2Vector get_word2vec() {
		return this.w2v;
	}
	

}

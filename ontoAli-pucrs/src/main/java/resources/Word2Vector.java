package resources;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

/*
 * This class is used to red the Word Embeddings model
 */
public class Word2Vector {
	
//Attributes
	
	//Word Embedding model File, this file is located at resources folder
	private File gModel = new File("resources/glove.6B.200d.txt");
	//Word2Vec contains the model read
	private Word2Vec w2V = null;
	
//Log method
	
	private void init_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Reading Word2Vec Model!" );
	}

//Constructor
	
	protected Word2Vector() {
		init_log();
		//read the w2v model from the gModel attribute (that contains the model file)
		this.w2V = WordVectorSerializer.readWord2VecModel(gModel);
	}

//Getter method
	public Word2Vec get_word2Vec() {
		return this.w2V;
	}
	
	
}

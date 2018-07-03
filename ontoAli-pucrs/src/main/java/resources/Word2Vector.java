package resources;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

/*
 * This class is used to read the Word Embeddings model
 */
public class Word2Vector {
	
//Attributes
	
	//Word Embedding model File, this file is located at resources folder
	private File gModel;
	//Word2Vec contains the model read
	private Word2Vec w2V;
	
//Log method
	
	private void init_log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Reading Word2Vec Model!" );
	}

//Constructor
	
	protected Word2Vector(String model) {
		init_log();
		verifyModel(model);
		//read the w2v model from the gModel attribute (that contains the model file)
		this.w2V = WordVectorSerializer.readWord2VecModel(gModel);
	}

//Getter method
	public Word2Vec get_word2Vec() {
		return this.w2V;
	}
	
	private void verifyModel(String model) {
		switch(model.toLowerCase()) {
			case "google": this.gModel = new File("resources/GoogleNews-vectors-negative300.bin.gz");
				break;
			case "glove": this.gModel = new File("resources/glove.6B.200d.txt");
				break;
			default: System.out.println("Word Embeddings model don't exist! Please choose [google or glove]");
				break;
		}
	}
}

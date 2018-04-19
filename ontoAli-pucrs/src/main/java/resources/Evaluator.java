package resources;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;

import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.eval.PRecEvaluator;
import fr.inrialpes.exmo.align.parser.AlignmentParser;

public class Evaluator {
	String ref;
	String alin;
	PRecEvaluator evaluator;
	
	public Evaluator(String ref, String alin) {
		set_alignment(alin);
		set_reference(ref);
	}
	
	private void log() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - Evaluating the matching..." );
	}
	
	void set_reference(String _ref) {
		this.ref = _ref;
	}
	
	String get_reference() {
		return this.ref;
	}
	
	void set_alignment(String _alin) {
		this.alin = _alin;
	}
	
	String get_alignment() {
		return this.alin;
	}
	
	private void set_evaluator(PRecEvaluator eva) {
		this.evaluator = eva;
	}
	
	protected PRecEvaluator get_evaluator() {
		return this.evaluator;
	}
	
	public void evaluate() {
		Alignment ref = new BasicAlignment();
		Alignment alin = new BasicAlignment();
		
		AlignmentParser ap1 = new AlignmentParser(0);
		AlignmentParser ap2 = new AlignmentParser(0);
		log();
		try {
			ref = ap1.parse(new File(get_reference()).toURI() );
			alin = ap2.parse(new File(get_alignment()).toURI());
			
			Properties p = new Properties();
			PRecEvaluator eva = new PRecEvaluator(ref, alin);
			eva.eval(p);
			set_evaluator(eva);
			System.out.println("REF - ALI:\n" + "F-Measure: " + eva.getFmeasure() + "\nPrecision:  " + eva.getPrecision() + "\nRecall: " + eva.getRecall() + "\nOverall: " + eva.getOverall());
		} catch (AlignmentException e) {
			System.out.println("error: Reference-Alignment or Alignment: format");
			e.printStackTrace();
		}
	}

}

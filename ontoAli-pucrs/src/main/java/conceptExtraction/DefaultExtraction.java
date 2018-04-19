package conceptExtraction;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DefaultExtraction {
	
	
	DefaultExtraction() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " - [log] - No-Context method selected!" );
	}

}

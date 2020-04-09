import java.text.SimpleDateFormat;
import java.util.Date;

import com.kumaran.tac.framework.selenium.frameworklayer.Utility;

public class Custom_Attributes extends Utility{
	
	
	public static String startTime() {

		Date date = new Date();
		System.out.println("Custom Method Running - Start Time");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startTime = formatter.format(date);

		return startTime;
	}

	// End Time
	public static String completedTime() {
		Date date = new Date();
		System.out.println("Custom Method Running - Completed Time");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String completedTime = formatter.format(date);
		/*
		 * if (startTime != null && endTime != null) { getRunDuration(startTime,
		 * endTime); }
		 */
		return completedTime;

	}

}

package kvaddakopter.utils;

public class SecToMinSec {

	public static String transform(long sec){
		long mins =   sec / 60;
		long newSec = sec % 60;
		String returnString = "";
		if (mins >= 1)  returnString += (mins + " min ");
		returnString += (newSec + " sek");
		return returnString;
	}
}

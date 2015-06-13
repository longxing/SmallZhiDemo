package com.iii360.sup.common.utl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsualUsedHelpMethod {
  private static String regex = null;
	public static boolean isMatchForDecimal(String inputString){
		regex = "[0-9]{1,}\\.[0-9]{2,}";
		return isMatchRegex(inputString,regex);
	}
	
	private static boolean isMatchRegex(String inputString, String regex){
		 Pattern p = Pattern.compile(regex);
		 Matcher m = p.matcher(inputString);
		 return m.matches();
	}
}

////ID20130109001 hujinrong begin
//package com.base.util;
//
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import com.base.log.LogManager;
//
//public class NumberStringConvertUtil {
////ID20120831002 hujinrong begin
//    private static String mNumberPatternNeeds = "[一二三四五六七八九壹贰叁肆伍陆柒扒玖零0123456789两]";
////ID20120831002 hujinrong end
//    private static String mNumberPatternNeedsForGewei = "[0123456789]+";// 遇到1万50这样的式子
//    private static Pattern mNumberPattern = Pattern.compile(mNumberPatternNeeds);
//    private static Pattern mNumberPatternForSpecial = Pattern.compile(mNumberPatternNeedsForGewei);
//
//    public static double getNumberFromString(String sourceNumber) throws Exception {
//        if (sourceNumber == null || sourceNumber.equals("")) {
//            return 0;
//        }
//        double ret = 0;
//        try {
//            ret = Double.parseDouble(sourceNumber);
//            return ret;
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            // LogManager.printStackTrace(e);
//        }
//
//        int numberBeforeWan = 0;
//        int numberAfterWan = 0;
//        double floatNumber = 0;
//        boolean isHaveZero = true;
//        String strNumberBeforWan = sourceNumber;
//        String strNumberAfterWan = null;
//        String strNumberFloat = null;
//        if (sourceNumber != null && !sourceNumber.equals("") && sourceNumber.contains("万")) {
//            String splits[] = sourceNumber.split("万");
//            strNumberBeforWan = splits[0];
//            sourceNumber = splits.length == 2 ? splits[1] : null;
//            if (sourceNumber != null && sourceNumber.startsWith("零")) {
//                isHaveZero = true;
//            } else {
//                isHaveZero = false;
//            }
//            numberBeforeWan = getNumberFromStringInTenThousand(strNumberBeforWan);
//        }
//
//        // 处理 2万5这种情况
//
//        if (sourceNumber != null && !sourceNumber.equals("") && sourceNumber.contains("点")) {
//            String splits[] = sourceNumber.split("点");
//            strNumberAfterWan = splits[0];
//            sourceNumber = splits.length == 2 ? splits[1] : null;
//            numberAfterWan = getNumberFromStringInTenThousand(strNumberAfterWan);
//            floatNumber = getFloatNumber(sourceNumber);
//        } else if (sourceNumber != null && !sourceNumber.equals("") && sourceNumber.contains(".")) {
//            String splits[] = sourceNumber.split("\\.");
//            strNumberAfterWan = splits[0];
//            sourceNumber = splits.length == 2 ? splits[1] : null;
//            numberAfterWan = getNumberFromStringInTenThousand(strNumberAfterWan);
//            floatNumber = getFloatNumber(sourceNumber);
//        } else if (sourceNumber != null && !sourceNumber.equals("")) {
//            numberAfterWan = getNumberFromStringInTenThousand(sourceNumber);
//        }
//
//        {
//            if (!isHaveZero && numberAfterWan < 10 && numberAfterWan > 0) {
//                numberAfterWan = numberAfterWan * 1000;
//            }
//        }
//        return numberBeforeWan * 10000 + numberAfterWan + floatNumber;
//
//    }
//
//    // 转换万以内的数到数字“五千八百四十三”
//    public static int getNumberFromStringInTenThousand(String sourceNumber) throws Exception {
//
//        if (sourceNumber == null || sourceNumber.equals("")) {
//            return 0;
//        }
//        int charAtQian = 0;
//        int charAtBai = 0;
//        int charAtShi = 0;
//
//        int numberForQian = 0;
//        int numberForBai = 0;
//        int numberForShi = 0;
//        int numberForGeWei = 0;
////处理类似五千五这种情况
//        boolean afterQian = true;
//        boolean afterBai = true;
//
//        String strNumberForQian = sourceNumber;
//        String strNumberForBai = null;
//        String strNumberForShi = null;
//        String strNumberForGeWei = null;
//
//        Matcher matcher = null;
//        if (sourceNumber != null && !sourceNumber.equals("") && sourceNumber.contains("千")) {
////            afterQian = true;
//            String splits[] = sourceNumber.split("千");
//            strNumberForQian = splits[0];
//            strNumberForQian = strNumberForQian.replace("零", "");
//            sourceNumber = splits.length == 2 ? splits[1] : null;
//
//            matcher = mNumberPattern.matcher(strNumberForQian);
//
//            if (matcher.matches()) {
//                if(sourceNumber != null) {
//                    if (sourceNumber.startsWith("零")) {
//                        afterQian = true;
//                    } else {
//                        afterQian = false;
//                    }
//                }
//                numberForQian = CharToNumber(matcher.group());
//            } else {
//                throw new Exception("Number Format Error For QIAN");
//            }
//        }
//
//        if (sourceNumber != null && !sourceNumber.equals("") && sourceNumber.contains("百")) {
//            afterQian = true;
////            afterBai = true;
//            String splits[] = sourceNumber.split("百");
//            strNumberForBai = splits[0];
//            strNumberForBai = strNumberForBai.replace("零", "");
//            sourceNumber = splits.length == 2 ? splits[1] : null;
//
//            matcher = mNumberPattern.matcher(strNumberForBai);
//            if (matcher.matches()) {
//                if(sourceNumber != null) {
//                    if (sourceNumber.startsWith("零")) {
//                        afterBai = true;
//                    } else {
//                        afterBai = false;
//                    }
//                }
//                numberForBai = CharToNumber(strNumberForBai);
//            } else {
//                throw new Exception("Number Format Error For BAI");
//            }
//        }
//
//        if (sourceNumber != null && !sourceNumber.equals("") && sourceNumber.contains("十")) {
//            afterQian = true;
//            afterBai = true;
//            String splits[] = sourceNumber.split("十");
//            // 防止出现这种情况---"十"
//            if (splits.length == 0) {
//                strNumberForShi = "一";
//                sourceNumber = "";
//            } else {
//                strNumberForShi = splits[0];
//                strNumberForShi = strNumberForShi.replace("零", "");
//                if (strNumberForShi == null || "".equals(strNumberForShi)) {
//                    strNumberForShi = "一";
//                }
//                sourceNumber = splits.length == 2 ? splits[1] : null;
//            }
//            matcher = mNumberPattern.matcher(strNumberForShi);
//            if (matcher.matches()) {
//                numberForShi = CharToNumber(strNumberForShi);
//            } else {
//                throw new Exception("Number Format Error For BAI");
//            }
//        }
//
//        if (sourceNumber != null && !sourceNumber.equals("")) {
//            strNumberForGeWei = sourceNumber;
//
//            strNumberForGeWei = strNumberForGeWei.replace("零", "");
//            matcher = mNumberPattern.matcher(strNumberForGeWei);
//            Matcher matcher1 = mNumberPatternForSpecial.matcher(strNumberForGeWei);
//            if (matcher.matches()) {
//                numberForGeWei = CharToNumber(strNumberForGeWei);
//            } else if (matcher1.matches()) {
//                // 遇到1万50这样的式子
//                numberForGeWei = Integer.parseInt(strNumberForGeWei);
//            } else {
//                throw new Exception("Number Fomat For GeWei");
//            }
//        }
//        if (numberForGeWei < 10 && numberForGeWei > 0) {
//            if (!afterQian) {
//                numberForGeWei = numberForGeWei * 100;
//            } 
//            if (!afterBai) {
//                numberForGeWei = numberForGeWei * 10;
//            }
//        }
//
//        return numberForQian * 1000 + numberForBai * 100 + numberForShi * 10 + numberForGeWei;
//
//    }
//
//    public static int CharToNumber(String sourceChar) {
//        char c = sourceChar.charAt(0);
////ID20120831002 hujinrong begin
//        String numberString = "一二三四五六七八九壹贰叁肆伍陆柒扒玖零0123456789两";
//        String numberMapping = "123456789123456789001234567892";
////ID20120831002 hujinrong end
//        char numberMappingChars[] = numberMapping.toCharArray();
//        char numberChars[] = numberString.toCharArray();
//
//        for (int i = 0; i < numberChars.length; i++) {
//            if (c == numberChars[i]) {
//                return Integer.parseInt(numberMappingChars[i] + "");
//            }
//        }
//        return 0;
//    }
//
//    // 处理小数点后面的情况
//    public static double getFloatNumber(String number) {
//        if (number == null || number.equals(""))
//            return 0;
//        Pattern pattern = Pattern.compile("[一二三四五六七八九壹贰叁肆伍陆柒扒玖零0123456789]+");
//        double f = 0.0;
//        Matcher matcher = pattern.matcher(number);
//        if (matcher.matches()) {
//            String matchGroup = matcher.group();
//            for (int i = matchGroup.length() - 1; i >= 0; i--) {
//                f = CharToNumber(matchGroup.charAt(i) + "") * 0.1 + 0.1 * f;
//            }
//        } else {
//            new Exception("Float Number Error!!!");
//        }
//        return f;
//    }
//    public static String stringToNumber(String content) {
//    	//ID201214001 hujinrong begin
//    	Pattern pattern = Pattern.compile(".*?([1234567890一二三四五六七八九十百千两万]+)(.*)");
//    	Matcher matcher = pattern.matcher(content);
//    	while(matcher.matches()){
//    		String numberStr = matcher.group(1);
//    		int number = 0;
//    		try {
//				number = (int)NumberStringConvertUtil.getNumberFromString(numberStr);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				LogManager.printStackTrace(e);
//			}
//			content = content.replace(numberStr, number+"");
//    		String content2 = matcher.group(2);
//    		if(content2 == null || "".equals(content2)){
//    			break;
//    		}
//    		matcher = pattern.matcher(content2);
//    	}
//    	return content;
//    	
//    	      
//    }
//}
////ID20130109001 hujinrong end
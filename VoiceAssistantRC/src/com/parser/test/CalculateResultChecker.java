package com.parser.test;

import com.iii360.sup.common.utl.LogManager;



public class CalculateResultChecker implements ResultChecker {

    private String[] source = new String[] { "+", "-", "*", "/", "鍔犱笂", "鍑忓幓",
            "涔樹笂", "闄や互" };
    private String[] des = new String[] { "+", "-", "*", "/", "+", "-", "*",
            "/" };

    @Override
    public String recovery(String input, String result) {
        // TODO Auto-generated method stub
        // Log.i("hujinrong",input);
        // Log.i("hujinrong",result);
        String inputs[] = input.split(" ");
        String results[] = result.split(" ");

        String arg0 = inputs[1].substring(5);
        String arg1 = inputs[2].substring(5);
        String arg2 = inputs[3].substring(5);

        double arg0d = Double.parseDouble(arg0) + 0.0;
        String operate = null;
        for (int i = 0; i < source.length; i++) {
            if (arg1.equals(source[i])) {
                operate = des[i];
            }
        }
        double arg2d = Double.parseDouble(arg2) + 0.0;

        String assembleInput = arg0d + operate + arg2d;
        String realResult = results[1].substring(5);
        LogManager.i("hujinrong test", "input-----" + assembleInput);
        // Log.i("hujinrong test","realresult----"+realResult);
        LogManager.i("hujinrong test", "result----" + realResult);
        if (assembleInput.equals(realResult)) {
            return null;
        } else
            return input;

    }

}

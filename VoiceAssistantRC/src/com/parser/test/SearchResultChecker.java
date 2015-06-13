package com.parser.test;


import com.iii360.sup.common.utl.LogManager;



public class SearchResultChecker implements ResultChecker {

    @Override
    public String recovery(String input, String result) {
        String rec = null;
        LogManager.i("hujirnong", "inputs==" + input);
        LogManager.i("hujirnong", "result==" + result);
        if (input != null && result != null && !input.equals(result)) {

            String[] inputs = input.split(" ");
            String[] results = result.split(" ");
            String inputSrc = inputs[0];

            String year = inputs[1].substring(4);
            String month = inputs[2].substring(4);
            String day = inputs[3].substring(4);
            String hour = inputs[4].substring(4);
            String time = inputs[5].substring(4);

            String arg1 = results[1].substring(4);
            String arg2 = results[2].substring(4);
            String arg3 = results[3].substring(4);

            if (!arg2.equals(year + month + day) || !arg1.equals(hour + time))
                rec = "something";
        }
        return rec;
    }

}

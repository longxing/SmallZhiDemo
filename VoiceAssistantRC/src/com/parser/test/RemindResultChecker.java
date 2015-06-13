package com.parser.test;


public class RemindResultChecker implements ResultChecker {

    @Override
    public String recovery(String input, String result) {
        // TODO Auto-generated method stub
        String rec = null;
        // Log.i("hujirnong","inputs=="+input);
        // Log.i("hujirnong","result=="+result);
        // if(input != null
        // && result != null
        // && !input.equals(result)) {
        //
        // String [] inputs = input.split(" ");
        // String [] results = result.split(" ");
        // String inputSrc = inputs[0];
        // for(int i = 1;i < inputs.length;i++) {
        // String[] values = inputs[i].split(":");
        // String argExp = "[" + values[1] + "," + values[0] + "]";
        // inputSrc = inputSrc.replace(values[1], argExp);
        // }
        //
        // rec = inputSrc;
        // for(int i = 1;i < results.length;i++) {
        // rec += " " + results[i];
        // }
        //
        //
        // }
        // return rec;
        if (input != null && result != null && !input.equals(result)) {

            String[] inputs = input.split(" ");
            String[] results = result.split(" ");
            String inputSrc = inputs[0];

            String year = inputs[1].substring(5);
            String month = inputs[2].substring(5);
            String day = inputs[3].substring(5);
            String hour = inputs[4].substring(5);
            String time = inputs[5].substring(5);

            String arg1 = results[1].substring(5);
            String arg2 = results[2].substring(5);
            String arg3 = results[3].substring(5);

            // Log.i("hujinrong2",year+month+day);
            // Log.i("hujinrong2",hour+time);
            // Log.i("hujinrong2","arg1----"+arg1);
            if (!arg3.equals(year + month + day) || !arg2.equals(hour + time))
                rec = input;
        }
        return rec;
    }

}

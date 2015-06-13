package com.parser.test;

public class DefaultResultChecker implements ResultChecker {

    @Override
    public String recovery(String input, String result) {
        String rec = null;
        
        if(input != null
        && result != null
        && !input.equals(result)) {
            
            String [] inputs = input.split(" ");
            String [] results = result.split(" ");
            String inputSrc = inputs[0];
            for(int i = 1;i < inputs.length;i++) {
                String[] values = inputs[i].split(":");
                String argExp = "[" + values[1] + "," + values[0] + "]";
                inputSrc = inputSrc.replace(values[1], argExp);
            }
            
            rec = inputSrc;
            for(int i = 1;i < results.length;i++) {
                rec += " " + results[i];
            }
            
            
        }
        return rec;
    }



}

package com.voice.common.util.nlp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.LogManager;

import com.voice.common.util.nlp.SplitDict;

public class WordSegment {

    private HashMap<String, WordObj> mWords;
    private SplitDict mSplitDict;

    public WordSegment(HashMap<String, WordObj> model) {
        this.mWords = model;
        mSplitDict = new SplitDict(null);
        for (String s : model.keySet()) {
             // System.out.println(s);
            mSplitDict.addWord(s);
        }
    }

    public ArrayList<String> getSegMent(String from) {

        ArrayList<ArrayList<String>> result = mSplitDict.getSplitStrings(from);
        if (result.size() > 1) {
            // write info to file
        }
        return result.get(0);
    }

}

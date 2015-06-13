package com.voice.common.util.nlp;

import java.util.ArrayList;

public class ParseResultList {
	private ArrayList<SentenceObject> resultList;
	private int resultSocre;

	public ParseResultList() {
		resultList = new ArrayList<SentenceObject>();
		resultSocre = Integer.MAX_VALUE;
	}

	public void setSocre(int score) {
		resultSocre = score;
	}
	
	public void addObject(SentenceObject object){
		resultList.add(object);
	}

	public ParseResultList(ArrayList<SentenceObject> list, int socre) {
		resultList = list;
		resultSocre = socre;
	}

	public int getScore() {
		return resultSocre;
	}

	public ArrayList<SentenceObject> getResultList() {
		return resultList;
	}

}
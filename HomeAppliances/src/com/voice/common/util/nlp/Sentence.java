package com.voice.common.util.nlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iii360.base.common.utl.LogManager;

public class Sentence {
	private static ArrayList<String> compats = new ArrayList<String>();
	private Pattern splitePatter;

	public static void main(String[] args) {
	}

	public void read(String filePath) {
		File f = new java.io.File(filePath);
		BufferedReader bf = null;
		FileInputStream fins = null;
		try {
			fins = new FileInputStream(f);
			Reader r = new InputStreamReader(fins);
			bf = new BufferedReader(r);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String patter = "";
		if (bf != null) {
			String s;
			try {
				while ((s = bf.readLine()) != null) {
					s = s.trim();
					if (!s.startsWith("#")) {
						compats.add(s);
						patter += s;
						patter += "|";
					}

				}
				bf.close();
				fins.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		splitePatter = Pattern.compile(patter);
		System.out.println(patter);
	}

	public boolean isFullOrder(ArrayList<WordObj> from) {
		StringBuffer sb = new StringBuffer();
		for (WordObj s : from) {
			sb.append(s.getType());
		}
		String content = sb.toString();
		LogManager.e(content);
		Matcher sents = splitePatter.matcher(content);
		while (sents.find()) {
			String currentOrderType = sents.group();
			if (currentOrderType.length() > 0) {
				return true;
			}
		}

		return false;
	}

}

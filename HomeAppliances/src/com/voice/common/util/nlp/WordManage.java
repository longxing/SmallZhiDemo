package com.voice.common.util.nlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class WordManage {
	private static final String TYPE = "type";
	private static final String SAME = "sa";
	private static final String BIND = "bd";
	private static final String SUB = "sub";

	public HashMap<String, WordObj> hashMap = new HashMap<String, WordObj>();
	private ArrayList<WordObj> mTypes = new ArrayList<WordObj>();
	private WordSegment ws;
	private WordSqueeCompare wsc;

	public static void main(String[] args) {
		WordManage wm = new WordManage();
		wm.readFile("HotText2");
		System.out.println(wm.comPair("唱歌", "关闭客厅灯"));
	}

	public int comPair(String s1, String s2) {
		ArrayList<WordObj> wlist1 = getSegList(s1);
		ArrayList<WordObj> wlist2 = getSegList(s2);
		return wsc.comPare(wlist1, wlist2);
	}

	public int comPair(ArrayList<WordObj> wlist1, ArrayList<WordObj> wlist2) {
		return wsc.comPare(wlist1, wlist2);
	}

	public int comPairWord(WordObj obj1, WordObj obj2) {
		return wsc.compairWord(obj1, obj2);
	}

	public WordObj getWordObj(String word) {
		return hashMap.get(word);
	}

	public ArrayList<WordObj> getSegList(String from) {
		if(null == ws ){
			return new ArrayList<WordObj>();
		}
		ArrayList<String> list1 = ws.getSegMent(from);
		ArrayList<WordObj> wlist1 = new ArrayList<WordObj>();
		for (String s : list1) {
			if (hashMap.containsKey(s)) {
				wlist1.add(hashMap.get(s));
			} 
//			else {
//				WordObj w = new WordObj(s);
//				w.setType("其他");
//				wlist1.add(w);
//			}
		}
		return wlist1;
	}

	public void readFile(String path) {
		File f = new File(path);
		try {
			FileInputStream fins = new FileInputStream(f);
			InputStreamReader in = new InputStreamReader(fins);
			BufferedReader br = new BufferedReader(in);

			String s;

			WordObj[] objs = new WordObj[20];
			String[] types = new String[20];
			while ((s = br.readLine()) != null) {
				if (!s.startsWith("#")) {
					int size = 0;// 层级，缩进的个数
					s = s.replaceAll("\t+$", "");
					s = s.replace(" +$", "");
					while (s.contains("\t") || s.contains("    ")) {
						if (s.contains("\t")) {
							s = s.replaceFirst("\t", "");
						} else if (s.contains("    ")) {
							s = s.replaceFirst("    ", "");
						}
						size++;
					}
					s = s.trim();// 当前行的字符
					if (s != null && s.length() == 0) {
						continue;
					}
					// System.out.println(s);
					if (size > 0 && size % 2 == 0) {
						types[size] = s;
					} else {
						String lastType = "";// 操作符
						if (size > 1) {
							lastType = types[size - 1];
						}
						WordObj wordObj = null;
						if (lastType.length() == 0 || lastType.equals(SUB) || size == 0) {
							if (!hashMap.containsKey(s)) {
								wordObj = new WordObj(s);
								if (size > 1) {
									wordObj.setParent(objs[size - 2]);
								} else if (size == 1) {
									wordObj.setParent(objs[0]);
								} else if (size == 0) {
									// System.out.println("size == 0" + s);
									wordObj.setType(s);
								}
								wordObj.setDepth(size);
								hashMap.put(s, wordObj);
								if (size == 0) {
									mTypes.add(wordObj);
								}
							} else {
								wordObj = hashMap.get(s);
								if (size > 1) {
									wordObj.setParent(objs[size - 2]);
								} else if (size == 1) {
									wordObj.setParent(objs[0]);
								}
							}
						} else if (lastType.equals(TYPE)) {
							// System.out.println(s);
							 if (s.equals(SAME)) {
                                 System.out.println("same" + objs[size - 4]);
                                for (WordObj w : objs[size - 4].getSons()) {
                                    objs[size - 4].addSame(w);
                                    w.setDepth(objs[size - 4].getDepth());
                                    System.out.println(w);
                                }
                            }
						} else if (lastType.equals(SAME)) {
							if (hashMap.containsKey(s)) {
								wordObj = hashMap.get(s);
							} else {
								try {
									wordObj = (WordObj) objs[size - 2].clone();
									wordObj.setWord(s);
									objs[size - 2].addSame(wordObj);
									hashMap.put(s, wordObj);
								} catch (CloneNotSupportedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							// System.out.println(wordObj + " same " + objs[size
							// - 2]);
							objs[size - 2].addSame(wordObj);
						} else if (lastType.equals(BIND)) {
							if (hashMap.containsKey(s)) {
								wordObj = hashMap.get(s);
							} else {
								wordObj = new WordObj(s);
								hashMap.put(s, wordObj);
								if (size > 1) {
									wordObj.setParent(objs[size - 2]);
								} else if (size == 1) {
									wordObj.setParent(objs[0]);
								}
							}
							objs[size - 2].SetBind(wordObj);
						}
						objs[size] = wordObj;
					}
					// System.out.println(s + "  " + size);
				}
			}
			br.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ws = new WordSegment(hashMap);
		wsc = new WordSqueeCompare(mTypes);
	}
}

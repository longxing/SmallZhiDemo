package com.voice.common.util.nlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.iii360.base.common.utl.LogManager;

import android.content.IntentSender.SendIntentException;

public class WordSqueeCompare {
	private ArrayList<WordObj> types = new ArrayList<WordObj>();
	public static final int SAME = 0;// 完全匹配
	public static final int SAMILAR = 2;// 同类中出现sa
	public static final int INCLUD = 4;// 父类中出现sub
	public static final int LOST = 12;// 缺少某个类型的匹配
	public static final int DIFF = 18;// 完全类型、或内容不匹配


	public WordSqueeCompare(ArrayList<WordObj> types) {
		this.types = types;
	}

	/**
	 * 
	 * @param src1
	 *            new
	 * @param src2
	 *            form source
	 * @return
	 */
	public int comPare(ArrayList<WordObj> src1, ArrayList<WordObj> src2) {
		if (src1.size() == 0 || src2.size() == 0) {
			return Integer.MAX_VALUE;
		}

		WordSqueen w1 = new WordSqueen(src1);
		WordSqueen w2 = new WordSqueen(src2);

		int result = 0;

		for (HashMap<String, WordObjPath> cHash : w1.mValues) {
			for (HashMap<String, WordObjPath> vHash : w2.mValues) {
				int tempResult = comPare(cHash, vHash);
				// 取得误差最大的最为结果
				if (tempResult > result) {
					result = tempResult;
				}
			}
		}

		return result;
	}

	/**
	 * @param h1
	 * @param h2
	 * @return
	 */
	public int comPare(HashMap<String, WordObjPath> h1, HashMap<String, WordObjPath> h2) {
		int result = 0;
		// System.out.println(types.size());

		for (WordObj w : types) {

			String type = w.getWord();

			if (h1.containsKey(type) && h2.containsKey(type)) {
				int subResult = compairList(h1.get(type), h2.get(type));
				result += subResult;
			} else if (h1.containsKey(type)) {
				result += LOST;
			} else if (h2.containsKey(type)) {
				result += LOST;
			}
			// System.out.println(type + "  " + result + "  " + h1.get(type) +
			// "  " + h2.get(type));
		}
		return result;
	}

	/**
	 * 如果含有相同的属性， 但是又冲突的话就把句子拆成两个句子去执行。
	 * 
	 * @author jushang
	 * 
	 */
	public class WordSqueen {

		public ArrayList<HashMap<String, WordObjPath>> mValues = new ArrayList<HashMap<String, WordObjPath>>();

		public WordSqueen(ArrayList<WordObj> objs) {
			HashMap<String, WordObjPath> ObjTypeHash = new HashMap<String, WordObjPath>();
			mValues.add(ObjTypeHash);

			for (WordObj w : objs) {
				if (w == null) {
					System.out.println("w == null");
				}
				String type = w.getType();

				ArrayList<HashMap<String, WordObjPath>> addList = new ArrayList<HashMap<String, WordObjPath>>();
				for (HashMap<String, WordObjPath> currentHash : mValues) {

					if (currentHash.containsKey(type)) {

						int result = compairSingleList(w, currentHash.get(type));
						switch (result) {
						case SAME:
							// do nothing

							break;
						case INCLUD:

							currentHash.get(type).add(w);
							break;
						case SAMILAR:
							// do nothing

							break;
						case DIFF:
							// do add new queen
							System.out.println("add once");
							HashMap<String, WordObjPath> newHash = (HashMap<String, WordObjPath>) currentHash.clone();
							newHash.remove(type);
							WordObjPath wordObjPath = new WordObjPath();
							wordObjPath.add(w);
							newHash.put(type, wordObjPath);
							addList.add(newHash);
							break;
						default:
							break;
						}

					} else {
						WordObjPath wordObjPath = new WordObjPath();
						wordObjPath.add(w);
						currentHash.put(type, wordObjPath);

					}
				}
				mValues.addAll(addList);

			}

		}

	}

	/**
	 * 比较两个词之间的相似度，根据类型，
	 * 
	 * @param w1
	 * @param w2
	 * @return
	 */
	public int compairWord(WordObj w1, WordObj w2) {
		if (w1.getDepth() > w2.getDepth()) {
			WordObj w3 = w2;
			w2 = w1;
			w1 = w3;
		}

		if (!w1.getType().equals(w2.getType())) {
			return DIFF;
		}
		if (w1.getDepth() == w2.getDepth()) {
			if (w1.getWord().equals(w2.getWord())) {
				return SAME;
			} else if (w1.getSameWords() != null && w1.getSameWords().containsKey(w2.getWord())) {
				return SAMILAR;
			}

		} else {
			int small = DIFF;

			if (w1.getDepth() < w2.getDepth()) {
				for (WordObj w3 : w2.getParent()) {
					int result = compairWord(w1, w3);
					if (result < small) {
						small = result;
					}
				}
				if (small < INCLUD) {
					small = INCLUD;
				}
				return small;
			}

		}

		return DIFF;
	}

	public int compairSingleList(WordObj w, WordObjPath path) {
		fillPath(path);
		// if(w.getDepth())
		// if()
		int wDepth = w.getDepth();
		WordObj pathObj = path.getByDepth(wDepth);
		if (pathObj != null) {
			return compairWord(w, pathObj);
		} else if (path.size() > 0) {
			if (wDepth < path.get(0).getDepth()) {
				WordObjPath Fillspath = fillDepth(w, path.get(0));
				if (Fillspath != null) {
					return INCLUD;
				}
			} else if (wDepth > path.getBigDepth()) {
				WordObjPath Fillspath = fillDepth(w, path.get(path.size() - 1));
				if (Fillspath != null) {
					return INCLUD;
				}
			}
		}

		return DIFF;
	}

	public int compairList(WordObjPath path1, WordObjPath path2) {
		fillPath(path2);
		fillPath(path1);

		WordObjPath testPath1 = fillDepth(path1.get(0), path2.get(path2.size() - 1));
		WordObjPath testPath2 = fillDepth(path2.get(0), path1.get(path1.size() - 1));

		if (testPath1 != null || testPath2 != null) {
			// 第一个单词
			int result1 = compairWord(path1.get(0), path2.get(0));
			// 最后一个单词
			int result2 = compairWord(path1.get(path1.size() - 1), path2.get(path2.size() - 1));
			// System.out.println("result 1 " + result1 + " result 2 " +
			// result2);
			return result1 > result2 ? result1 : result2;
		}
		return DIFF;
	}

	private void fillPath(WordObjPath path) {
		if (path.size() > 1) {
			int i = 0;
			for (; (i + 1) < path.size(); i++) {
				int bigDep = path.get(i).getDepth();
				int smallDep = path.get(i + 1).getDepth();
				if (bigDep - smallDep <= 1) {
					continue;
				} else {
					WordObjPath mPath = fillDepth(path.get(i), path.get(i + 1));
					if (mPath != null) {
						path.addAll(mPath);
					} else {
						System.err.println("can't not found right path");
					}
				}
			}

		}
	}

	/**
	 * 
	 * @param w1
	 *            samllDepth
	 * @param w2
	 *            bigDepth
	 * @return
	 */
	public WordObjPath fillDepth(WordObj w1, WordObj w2) {

		if (w1.getDepth() == w2.getDepth()) {
			WordObjPath path = new WordObjPath();
			if (compairWord(w1, w2) <= SAMILAR) {
				path.add(w1);
				return path;
			}
		}

		if (w1.getParent() != null) {
			// System.out.println(w1 + "  WordObjPath " +
			// w1.getParent().size());
			for (WordObj w3 : w1.getParent()) {
				// System.out.println("paring- " + w3 + "    " + w2);
				WordObjPath path = fillDepth(w3, w2);

				if (path != null) {
					path.add(w1);
					return path;
				}
			}
		}
		return null;
	}

	/**
	 * 普通的Wordobj的list，排列方式按照倒序排列，深度越大越靠前
	 * 
	 * @author jushang
	 * 
	 */
	public class WordObjPath extends ArrayList<WordObj> {

		/**
         * 
         */
		private static final long serialVersionUID = 1L;

		@Override
		public boolean add(WordObj e) {
			// TODO Auto-generated method stub
			// return super.add(e);
			if (contains(e)) {
				return true;
			}
			int i = 0;
			for (; i < size(); i++) {
				if (get(i).getDepth() < e.getDepth()) {
					break;
				}
			}
			add(i, e);
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends WordObj> c) {
			// TODO Auto-generated method stub
			for (WordObj w : c) {
				add(w);
			}
			return true;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			StringBuilder sb = new StringBuilder();
			for (WordObj w : this) {
				sb.append(w.getWord());
				sb.append(" ");
			}
			return sb.toString();
		}

		public int getSmallDepth() {
			if (size() > 0) {
				get(0).getDepth();
			}
			return 0;
		}

		public int getBigDepth() {
			if (size() > 0) {
				get(size() - 1).getDepth();
			}
			return 0;
		}

		public WordObj getByDepth(int depth) {
			for (WordObj w : this) {
				if (w.getDepth() == depth) {
					return w;
				}
			}
			return null;
		}

	}

}

package com.parser.iengine.crf;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.base.file.FileUtil;
import com.base.resource.IResourceManager;
import com.base.resource.ResourceManagerAndroid;
import com.iii360.sup.common.utl.LogManager;

public class CRFUtil {
	private static IResourceManager mIResourceManager = new ResourceManagerAndroid();
	private final static String LIB_FILE = "libcrfjni.so";
	static {

		// init();
	}

	public static void init(Object context, String path) {

		FileUtil.initWorkPath(path);
		initLibrary(context);

		initModelFiles(context);
		CRFExport.InitWorkSpace(FileUtil.PATH);
	}

	private static void initLibrary(Object context) {

		// String path = FileUtil.PATH_LIB + LIB_FILE;
		// InputStream input = mIResourceManager.getInputStream(context,
		// "libs/lib");
		// LogManager.i("Init library:" + path);
		// FileUtil.copyAssetsFile(input, FileUtil.PATH_LIB, LIB_FILE, false);
		System.loadLibrary("crfjni");
	}

	private static void initModelFiles(Object context) {
		String[] models = mIResourceManager.list(context, "model");
		for (String model : models) {
			String path = "model/" + model;
			LogManager.i("Init model:" + path);
			FileUtil.copyAssetsFile(mIResourceManager.getInputStream(context, path), FileUtil.PATH_MODEL, model, true);
		}
	}

	private static void train(String typeName) {

		CRFExport.Train(typeName);

	}

	public static void train() {
		CRFExport.TrainAll();
	}

	public final static String parser(String text) {
		// CommandInfo info = null;

		if (text != null && !text.trim().equals("")) {
			String format = CRFExport.Parser(text);
			LogManager.i(format);
			// String params = "";
			// if(format != null) {
			//
			// String[] temps = format.split("\n");
			// for(int i = 0;i < temps.length;i++) {
			// if(temps[i].contains("B")) {
			// String str = "";
			// while(!temps[i].contains("O")) {
			//
			// str += temps[i++].substring(0, 1);
			//
			// }
			// params += str + ",";
			// }
			// }
			//
			//
			// }
		}

		return null;
	}

	private static void addParamToMap(HashMap<String, String> paramsMap, String src) {
		int startIndex = src.indexOf("\t");
		int endIndex = src.length();
		boolean isBegin = src.substring(startIndex + 1, startIndex + 2).equals("B");
		String argName = src.substring(startIndex + 2, endIndex);
		String argValue = paramsMap.get(argName);
		if (argValue == null || isBegin) {
			argValue = "";
		}
		argValue += src.substring(0, startIndex);
		// LogManager.e(argName + "  " + argValue);
		paramsMap.put(argName, argValue);
	}

	private static boolean checkModelIsVaild(String commandName) {
		File file = new File(FileUtil.PATH_MODEL + commandName);
		return file.exists();
	}

	private static String preProcess(String text) {
		String ret = text;
		if (text != null) {
			ret = text.replaceAll("\\s*|\t|\r|\n", "");
		}
		return ret;
	}

	public final static HashMap<String, String> extractParams(String text, String model) {

		HashMap<String, String> paramsMap = new HashMap<String, String>();
		LogManager.e(text);
		if (checkModelIsVaild(model)) {
			String src = CRFExport.ParserBy(preProcess(text), model);
			// String src =
			// "今\tBarg1\n天\tEarg1\n浦\tEarg0\n东\tEarg0\n会\tO\n不\tO\n会\tO\n热\tO\n";
			LogManager.d(src);

			if (src != null) {
				String[] itemList = src.split("\n");
				for (String item : itemList) {
					// LogManager.e(item);
					if (!item.contains("O")) {
						addParamToMap(paramsMap, item);
					}

				}

			}
		} else {
			LogManager.e("model:" + model + " is not exists!");
		}

		return paramsMap;

	}

	private static String format(String src) {

		String ret = "";
		if (src != null && !src.trim().equals("")) {
			String text = src.trim();

			String mark = "";
			int curIndex = 0;
			while (curIndex < text.length()) {
				int indexStart = text.indexOf("[", curIndex);
				int indexEnd = text.indexOf("]", curIndex);
				int indexSplit = text.indexOf(",", curIndex);

				if (indexStart < indexEnd) {
					String head = text.substring(curIndex, indexStart);
					if (head != null && !head.equals("")) {
						ret += addTrim(text.substring(curIndex, indexStart), "    O\n");
					}

					int overIndex = indexEnd;
					int cur = indexStart + 1;
					if (indexSplit > 0) {
						overIndex = indexSplit;
						mark = text.substring(indexSplit + 1, indexEnd);
					}

					if (indexStart < overIndex - 2) {

						ret += text.substring(cur, cur + 1) + "    B" + mark + "\n";
						cur++;
						while (cur < overIndex - 1) {
							ret += text.substring(cur, cur + 1) + "    I" + mark + "\n";
							cur++;
						}

					}
					ret += text.substring(cur, cur + 1) + "    E" + mark + "\n";
					curIndex = indexEnd + 1;
				} else {
					ret += addTrim(text.substring(curIndex), "    O\n");
					curIndex = text.length();
				}

			}

			// ret += "\n";
		}

		return ret;
	}

	private static String addTrim(String src, String trim) {
		String ret = "";
		if (src != null) {
			for (int i = 0; i < src.length(); i++) {
				String temp = src.substring(i, i + 1);
				ret += temp + trim;
			}
		}
		return ret;
	}

	public static void addTrainData(String commandName, String text) {

		String data = format(text);
		ArrayList<String> srcArray = new ArrayList<String>();
		srcArray.add(data);

		ArrayList<String> trainArray = new ArrayList<String>();
		trainArray.add(text);

		FileUtil.writeArrayStringToFile(srcArray, FileUtil.PATH_TRAIN + commandName, true);
		FileUtil.writeArrayStringToFileByTrim(trainArray, FileUtil.PATH_TRAINNING + commandName
				+ FileUtil.TRIM_TRAINING, true);
	}

	public static void training(String fileName) {

		String trainingFileName = fileName + FileUtil.TRIM_TRAINING;

		String trainingFilePath = FileUtil.PATH_TRAINNING + trainingFileName;

		LogManager.d("=========Train data:" + trainingFileName + " Start!");

		ArrayList<String> srcArray = FileUtil.getFileContent(trainingFilePath);

		if (srcArray.size() < 1) {
			LogManager.e("no train data");
			return;
		}

		String commandName = fileName;
		if (commandName == null || commandName.trim().equals("") || !commandName.contains("Command")) {
			LogManager.e("invalid command name");
			return;
		}

		addOnlyTrainData(commandName, srcArray, false);
		train(commandName);

		LogManager.d("=========Train data:" + trainingFileName + " Done!");
	}

	public static void addTrainData(String commandName, ArrayList<String> corpus) {
		addTrainData(commandName, corpus, true, true);
	}

	public static void addTrainData(String commandName, String[] src) {
		ArrayList<String> corpus = new ArrayList<String>();
		for (String item : src) {
			corpus.add(item);
		}
		addTrainData(commandName, corpus);
	}

	private static void addOnlyTrainData(String commandName, ArrayList<String> corpus, boolean isAdd) {
		addTrainData(commandName, corpus, isAdd, false);
	}

	private static void addTrainData(String commandName, ArrayList<String> corpus, boolean isAdd,
			boolean isNeedAddTrainingFile) {
		ArrayList<String> srcArray = new ArrayList<String>();
		ArrayList<String> trainArray = new ArrayList<String>();

		for (String data : corpus) {

			if (isNeedAddTrainingFile) {
				trainArray.add(data);
			}
			srcArray.add(format(data));
		}

		FileUtil.writeArrayStringToFile(srcArray, FileUtil.PATH_TRAIN + commandName + FileUtil.TRIM_TRAINING, isAdd);
		if (isNeedAddTrainingFile) {
			FileUtil.writeArrayStringToFileByTrim(trainArray, FileUtil.PATH_TRAINNING + commandName
					+ FileUtil.TRIM_TRAINING, isAdd);
		}

	}

	private static ArrayList<String> getTrainCorpus() {

		ArrayList<String> corpus = null;

		return corpus;

	}
	/*
	 * public static void generationTrainData(String commandName) {
	 * 
	 * ArrayList<String> templateList = getTempList(commandName);
	 * HashMap<String, ArrayList<String>> argMap = new HashMap<String,
	 * ArrayList<String>>(); ArrayList<String> argList =
	 * getArgAndDicMap(commandName, argMap);
	 * 
	 * 
	 * for(String arg : argList) {
	 * 
	 * String argExp = "[" + arg + "]"; String argTempExp = "<" + arg + ">";
	 * ArrayList<String> tempList = new ArrayList<String>();
	 * 
	 * for(String temp : templateList) {
	 * 
	 * if(temp.contains(argExp)) { ArrayList<String> argDic = argMap.get(arg);
	 * for(String word : argDic) { tempList.add(temp.replace(arg, word + "," +
	 * arg)); } } else if(temp.contains(argTempExp)) { ArrayList<String> argDic
	 * = argMap.get(arg); for(String word : argDic) {
	 * tempList.add(temp.replace(argTempExp, word)); } } else {
	 * tempList.add(temp); } }
	 * 
	 * templateList = tempList; }
	 * 
	 * addTrainData(commandName, templateList, false);
	 * 
	 * }
	 */

}

package com.base.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;


import com.base.resource.IResourceManager;
import com.base.resource.ResourceManagerAndroid;
import com.iii360.sup.common.utl.LogManager;

public class FileUtil extends com.iii360.sup.common.utl.file.FileUtil {

	private static IResourceManager mIResourceManager = new ResourceManagerAndroid();
	public static String PATH = "";
//	private static String PATH_HEAD = "/data/data/";
	private static String PATH_HEAD = "/mnt/sdcard/";
	// public static String PATH = "";
	public static String PATH_TRAIN = PATH + "/train/";
	public static String PATH_TEST = PATH + "/test/";
	public static String PATH_TRAINNING = PATH + "/training/";
	public static String PATH_RESULT = PATH + "/result/";
	public static String PATH_MODEL = PATH + "/model/";
//	public static String PATH_LIB = "/data/data/com.voice.assistant.main" + "/lib/";
	public static String PATH_LIB = "/mnt/sdcard/com.voice.assistant.main" + "/lib/";
	public static String PATH_DATA = PATH + "/data/";
	public final static String TRIM_TEST = "_test.txt";
	public final static String TRIM_TRAINING = "_train.txt";
	public final static String TRIM_DIC = "_dic.txt";
	public final static String TRIM_TEMP = "_temp.txt";
	public final static String TRIM_MATCH = "_match.ret";
	public final static String TRIM_NOMATCH = "_nomatch.ret";
	public final static String TRIM_ERRORMATCH = "_errormatch.ret";

	private final static String BOM_HEAD = new String(new byte[] { -17, -69, -65 });

	private FileUtil() {
	};

	public static void copyAssetsFile(InputStream inStream, String destPath, String name, boolean isOverwrite) {
		writeFile(inStream, destPath, name, isOverwrite);
	}

	public static void initWorkPath(String packageName) {
		File dir;
		PATH = PATH_HEAD + packageName;
		PATH_TRAIN = PATH + "/train/";
		PATH_TEST = PATH + "/test/";
		PATH_TRAINNING = PATH + "/training/";
		PATH_RESULT = PATH + "/result/";
		PATH_MODEL = PATH + "/model/";
		// PATH_LIB = PATH +"/Ienginelib/";
		PATH_DATA = PATH + "/data/";

		String[] work_paths = new String[] { PATH, PATH_MODEL, PATH_LIB, PATH_DATA, PATH_TRAIN, PATH_TEST,
				PATH_TRAINNING, PATH_RESULT, };

		for (String path : work_paths) {

			LogManager.i("work path:" + path);
			dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		}
	}

	private static String replaceBom(String Src) {
		if (Src == null) {
			return Src;
		}

		return Src.replaceFirst(BOM_HEAD, "");
	}

	public static boolean checkStorageVaild() {
		return mIResourceManager.checkStorageVaild();
	}

	public static ArrayList<String> readFileToArrayByFlag(String path, String ingoreFlag) {
		ArrayList<String> outArray = new ArrayList<String>();
		ArrayList<String> readArray = getFileContent(path);
		if (readArray != null && readArray.size() > 0) {
			if (!TextUtils.isEmpty(ingoreFlag)) {
				for (String s : readArray) {
					if (!s.startsWith(ingoreFlag)) {
						outArray.add(s);
					}
				}

			} else {
				return readArray;
			}

		}

		return outArray;
	}

}

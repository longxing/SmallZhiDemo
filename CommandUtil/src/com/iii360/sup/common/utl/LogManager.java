package com.iii360.sup.common.utl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import android.os.Environment;
import android.util.Log;

/**
 * 
 * Log管理类 打印log建议都使用这个类。
 * 
 */
public class LogManager {
	private static final String TAG = "VoiceAssistant";
	private static final String TAG_CALLED = "has be called";
	private static final String TAG_EXCEPTION = "Exception==============";
	private static boolean DEBUG_MODE = true; // 此标志必须为true，否则程序报错
	private static boolean mIsOutToFile = false;
	private static boolean isNeedSendLogToServer = true;
	private static String mPath = "mnt/sdcard/voice360log.txt";
	public static final String toServerLogPath = "mnt/sdcard/LogmangerBox/AssistantBoxLog.txt";
	private static String dateFormate = "yyyy-MM-dd HH:mm:ss:sss";
	private static long limitLogFile = 10 * 1024 * 1024L;
	private static final long saveLogLargeTime = 30 * 24 * 3600 * 1000L;
	public static final String saveImportantLogDir = "mnt/sdcard/LogmangerBox/";

	private static boolean checkSdCardVaild() {

		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

	}

	/**
	 * 输出到文件
	 * 
	 * @param log
	 *            String log
	 */
	public static void outputToFile(String log) {
		outputToFile(new SimpleDateFormat(dateFormate).format(System.currentTimeMillis()) + "\t" + log, mPath);
	}

	// 保存重要的日志文件，并上传到服务器
	public static void outputToFileAndToSendServer(String log) {
		if (isNeedSendLogToServer) {
			File dirfile = new File(saveImportantLogDir);
			if (!dirfile.exists()) {
				dirfile.mkdirs();
			} else {
				File[] files = dirfile.listFiles();
				for (File f : files) {
					if (System.currentTimeMillis() - f.lastModified() > saveLogLargeTime) {
						f.delete();
					}
				}
			}
			File file = new File(toServerLogPath);
			if (file.exists() && file.length() > limitLogFile) {
				file.renameTo(new File(file.getName() + "_" + System.currentTimeMillis()));
			}
			forceOutput(new SimpleDateFormat(dateFormate).format(System.currentTimeMillis()) + "\t" + log, toServerLogPath);
		}
	}

	public static void forceOutput(String log, String filePath) {
		if (log == null) {
			return;
		}
		if (!checkSdCardVaild()) {
			Log.e(TAG, "No sdcard!");
			return;
		}
		final File saveFile = new File(filePath);
		try {
			final FileOutputStream outStream = new FileOutputStream(saveFile, true);
			outStream.write((log + "\n").getBytes());
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void outputToFile(String log, String filePath) {
		if (!mIsOutToFile) {
			return;
		}
		forceOutput(log, filePath);
	}

	/**
	 * 输出异常
	 * 
	 * @param exc
	 *            Exception
	 */
	public static void outputToFile(Exception exc) {
		if (!mIsOutToFile || exc == null) {
			return;
		}

		// Environment.getExternalStorageDirectory();
		final StackTraceElement[] stack = exc.getStackTrace();
		outputToFile(exc.toString());
		for (StackTraceElement item : stack) {
			outputToFile(item.toString());
		}
	}

	private static String bulidTag(String objectName, String methodName) {
		return "[" + objectName + "|" + methodName + "]";
	}

	public static boolean getDebugMode() {
		return DEBUG_MODE;
	}

	/**
	 * 
	 * @param e
	 *            Exception
	 * @param objectName
	 *            ObjectName
	 * @param methodName
	 *            MethodName
	 */
	public static void printStackTrace(Exception e, String objectName, String methodName) {
		e(objectName, methodName, TAG_EXCEPTION);
		e(objectName, methodName, e.toString());
		e.printStackTrace();
		outputToFile(e);
	}

	public static void printStackTrace() {
		for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
			Log.e(TAG, e.getFileName() + "|" + e.getMethodName() + "|" + e.getLineNumber());
		}
	}

	// 需要上传的日志文件
	public static void printExceptionToServer(String e, String objectName, String methodName) {
		final String log = bulidTag(objectName, methodName) + e;
		Log.e(TAG, log);
		outputToFileAndToSendServer(log);
	}

	/**
	 * 需要上传的消息日志文件
	 * 
	 * @param msg
	 */
	public static void printMessageToServer(String msg) {
		Log.e(TAG, msg);
		outputToFileAndToSendServer(msg);
	}

	// 需要上传异常日志文件
	public static void printStackTraceToServer() {
		for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
			outputToFileAndToSendServer(element.getFileName() + "|" + element.getMethodName() + "|" + element.getLineNumber());
		}
	}

	/**
	 * 
	 * @param objectName
	 *            ObjectName
	 * @param methodName
	 *            MethodName
	 */
	public static void e(String objectName, String methodName) {
		e(objectName, methodName, TAG_CALLED);

	}

	/**
	 * 
	 * @param objectName
	 *            ObjectName
	 * @param methodName
	 *            MethodName
	 */
	public static void w(String objectName, String methodName) {
		w(objectName, methodName, TAG_CALLED);

	}

	/**
	 * 
	 * @param objectName
	 *            ObjectName
	 * @param methodName
	 *            MethodName
	 */
	public static void d(String objectName, String methodName) {
		d(objectName, methodName, TAG_CALLED);

	}

	/**
	 * 
	 * @param objectName
	 *            ObjectName
	 * @param methodName
	 *            MethodName
	 */
	public static void v(String objectName, String methodName) {
		v(objectName, methodName, TAG_CALLED);

	}

	/**
	 * 
	 * @param objectName
	 *            ObjectName
	 * @param methodName
	 *            MethodName
	 */
	public static void i(String objectName, String methodName) {
		i(objectName, methodName, TAG_CALLED);
	}

	/**
	 * 
	 * @param objectName
	 *            ObjectName
	 * @param methodName
	 *            MethodName
	 * @param msg
	 *            Message
	 */
	public static void e(String objectName, String methodName, String msg) {
		if (DEBUG_MODE) {
			final String log = bulidTag(objectName, methodName) + msg;
			Log.e(TAG, log);
			outputToFile(log);
		}

	}

	/**
	 * 
	 * @param objectName
	 *            ObjectName
	 * @param methodName
	 *            MethodName
	 * @param msg
	 *            Message
	 */
	public static void w(String objectName, String methodName, String msg) {
		if (DEBUG_MODE) {
			final String log = bulidTag(objectName, methodName) + msg;
			Log.w(TAG, log);
			outputToFile(log);
		}
	}

	/**
	 * 
	 * @param objectName
	 *            ObjectName
	 * @param methodName
	 *            MethodName
	 * @param msg
	 *            Message
	 */
	public static void d(String objectName, String methodName, String msg) {

		if (DEBUG_MODE) {
			final String log = bulidTag(objectName, methodName) + msg;
			Log.d(TAG, log);
			outputToFile(log);
		}

	}

	/**
	 * 
	 * @param objectName
	 *            ObjectName
	 * @param methodName
	 *            MethodName
	 * @param msg
	 *            Message
	 */
	public static void v(String objectName, String methodName, String msg) {
		if (DEBUG_MODE) {
			final String log = bulidTag(objectName, methodName) + msg;
			Log.v(TAG, log);
			outputToFile(log);
		}

	}

	/**
	 * 
	 * @param objectName
	 *            ObjectName
	 * @param methodName
	 *            MethodName
	 * @param msg
	 *            Message
	 */
	public static void i(String objectName, String methodName, String msg) {
		if (DEBUG_MODE) {
			final String log = bulidTag(objectName, methodName) + msg;
			Log.i(TAG, log);
			outputToFile(log);
		}
	}

	/**
	 * 
	 * @param e
	 *            Exception
	 */

	public static void printStackTrace(Exception e) {
		final String objectName = Thread.currentThread().getStackTrace()[3].getFileName();
		final String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();

		e(objectName, methodName, TAG_EXCEPTION);
		e(objectName, methodName, e.toString());
		e.printStackTrace();
		outputToFile(e);

	}

	public static String bulidTag(String msg) {
		String objectName = Thread.currentThread().getStackTrace()[4].getFileName();
		String methodName = Thread.currentThread().getStackTrace()[4].getMethodName();

		return bulidTag(objectName, methodName) + msg;
	}

	/**
	 * @Title: e
	 * @Description: Print error log information
	 * @param: @param msg
	 * @return: void
	 * @Comment:
	 */
	public static void e(String msg) {
		if (DEBUG_MODE) {
			String log = bulidTag(msg);
			Log.e(TAG, log);
			outputToFile(log);
		}

	}

	/**
	 * @Title: w
	 * @Description: Print Warnning log information
	 * @param: @param msg
	 * @return: void
	 * @Comment:
	 */
	public static void w(String msg) {
		if (DEBUG_MODE) {
			String log = bulidTag(msg);
			Log.w(TAG, log);
			outputToFile(log);
		}
	}

	/**
	 * @Title: d
	 * @Description: Print debug log information
	 * @param: @param msg
	 * @return: void
	 * @Comment:
	 */
	public static void d(String msg) {

		if (DEBUG_MODE) {
			String log = bulidTag(msg);
			Log.d(TAG, log);
		}
	}

	/**
	 * @Title: v
	 * @Description: Print void log information
	 * @param: @param msg
	 * @return: void
	 * @Comment:
	 */
	public static void v(String msg) {
		if (DEBUG_MODE) {
			String log = bulidTag(msg);
			Log.v(TAG, log);
		}
	}

	/**
	 * @Title: i
	 * @Description: Print info log information
	 * @param: msg String message
	 * @return: void
	 * @Comment:
	 */
	public static void i(String msg) {
		if (DEBUG_MODE) {
			String log = bulidTag(msg);
			Log.i(TAG, log);
		}
	}

}

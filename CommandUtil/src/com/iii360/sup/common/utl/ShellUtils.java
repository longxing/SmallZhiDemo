package com.iii360.sup.common.utl;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.CharBuffer;
import android.R.anim;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import com.iii360.sup.common.utl.file.FileUtil;

public class ShellUtils {

	public static String execute(boolean isDestory, String... args) throws IOException, InterruptedException {
		StringBuffer cmd = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			cmd.append(args[i]).append(" ");
		}
		LogManager.e("shell execute:" + cmd);

		String result = "";
		InputStream inIs = null;
		Process process = null;
		InputStream inEr = null;
		try {
			process = Runtime.getRuntime().exec(args);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			baos.flush();
			byte[] data = baos.toByteArray();
			result = new String(data);
			if (result.endsWith("\n")) {
				result = result.substring(0, result.length() - 1);
			}
			//
			inEr = process.getErrorStream();
			while ((read = inEr.read()) != -1) {
				System.out.print(read);
			}
		} finally {
			try {
				if (inIs != null) {
					inIs.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (process != null && isDestory) {
				process.destroy();
			}
			if (inEr != null) {
				inEr.close();
			}
			// if (process.waitFor() != 0) {
			// return "";
			// }
		}
		LogManager.e("shell result:\n----------\n" + result + "\n----------\n");
		return result;
	}

	public static boolean slientInstall(File file) {
		boolean result = false;
		Process process = null;
		OutputStream out = null;
		try {

			process = Runtime.getRuntime().exec("su");
			out = process.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(out);
			dataOutputStream.writeBytes("chmod 777 " + file.getPath() + "\n");
			dataOutputStream.writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r " + file.getPath());
			// 提交命令
			dataOutputStream.flush();
			// 关闭流操作
			dataOutputStream.close();
			out.close();
			int value = process.waitFor();

			// 代表成功
			if (value == 0) {
				result = true;
			} else if (value == 1) { // 失败
				result = false;
			} else { // 未知情况
				result = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static int execRootCmdSilent(String paramString) {
		try {
			Process localProcess = Runtime.getRuntime().exec("su");
			Object localObject = localProcess.getOutputStream();
			DataOutputStream localDataOutputStream = new DataOutputStream((OutputStream) localObject);
			String str = String.valueOf(paramString);
			localObject = str + "\n";
			localDataOutputStream.writeBytes((String) localObject);
			localDataOutputStream.flush();
			localDataOutputStream.writeBytes("exit\n");
			localDataOutputStream.flush();
			localProcess.waitFor();
			localObject = localProcess.exitValue();
			return (Integer) localObject;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return 0;
	}

	public static String writeSerialNumber(Context context, String sn) {
		File file = new File("/sdcard/sn");
		FileWriter fw = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file);
			fw.write(sn);
			return sn;
		} catch (Exception e) {
			LogManager.e("write sn  to /sdcard/sn fail" + e.toString());
		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		SupKeyList.CURRENT_DEVICE_SN = "";
		return readSerialNumber();
	}

	@SuppressLint("NewApi")
	public static String readSerialNumber() {
		String sn = "000000000000";
		String aString = android.os.Build.SERIAL;
		LogManager.d("readSerialNumber cuurent sn:" + aString);
		if (!SupKeyList.CURRENT_DEVICE_SN.equals("")) {
			return SupKeyList.CURRENT_DEVICE_SN;
		}
		File file = new File("/sdcard/sn");
		File sysFile = new File("/system/usr/properties/sn");
		if (!file.exists() && !sysFile.exists()) {
			LogManager.e("sn is not exist!");
			return sn;
		} else if (sysFile.exists()) {
			FileReader fr = null;
			try {
				fr = new FileReader(sysFile);
				CharBuffer charBuffer = CharBuffer.allocate((int) sysFile.length());
				fr.read(charBuffer);
				sn = new String(charBuffer.array());
			} catch (Exception e) {
				LogManager.e("read sn  from /system/usr/properties/sn" + e.toString());
			} finally {
				try {
					if (fr != null) {
						fr.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (file.exists()) {
			FileReader fr = null;
			try {
				fr = new FileReader(file);
				CharBuffer charBuffer = CharBuffer.allocate((int) file.length());
				fr.read(charBuffer);
				sn = new String(charBuffer.array());
			} catch (Exception e) {
				LogManager.e("read sn  from /sdcard/sn fail fail" + e.toString());
			} finally {
				try {
					if (fr != null) {
						fr.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		SupKeyList.CURRENT_DEVICE_SN = sn;
		return sn;
	}

	/**
	 * 开机启动
	 * 
	 * @param context
	 * @return
	 */
	public static String readSerialNumber2(final Context context) {
		String sn = "000000000000";
		if (!SupKeyList.CURRENT_DEVICE_SN.equals("")) {
			LogManager.e("sn2 ::" + SupKeyList.CURRENT_DEVICE_SN);
			return SupKeyList.CURRENT_DEVICE_SN;
		}
		File file = new File("/sdcard/sn");
		File sysFile = new File("/system/usr/properties/sn");
		if (!file.exists() && !sysFile.exists()) {
			LogManager.e("sn is not exist!");
			return sn;
		} else if (sysFile.exists()) {
			FileReader fr = null;
			FileReader fr2 = null;
			try {
				fr = new FileReader(sysFile);
				CharBuffer charBuffer = CharBuffer.allocate((int) sysFile.length());
				fr.read(charBuffer);
				sn = new String(charBuffer.array());
				
				if(file.exists()){
					fr2 = new FileReader(file);
					CharBuffer charBuffer2 = CharBuffer.allocate((int) file.length());
					fr2.read(charBuffer2);
					String sn2 = new String(charBuffer2.array());
					
					if(!sn.equals(sn2)){
						sn=sn2;
						new Thread() {
							public void run() {
								try {
									String shName = "sn.sh";
									FileUtil.writeFile(context.getAssets().open(shName), Environment.getExternalStorageDirectory().getPath() + "/", shName, true);
									ShellUtils.execute(false, "su", "-c", "mount", "-o", "remount", "rw", "/mnt/sdcard");
									ShellUtils.execute(false, "su", "-c", Environment.getExternalStorageDirectory().getPath() + "/sn.sh");
								} catch (IOException e) {
									e.printStackTrace();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}.start();
					}
				}
			} catch (Exception e) {
				LogManager.e("read2 sn  /system/usr/properties/sn fail" + e.toString());
			} finally {
				try {
					if (fr != null) {
						fr.close();
					}
					
					if (fr2 != null) {
						fr2.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (file.exists()) {
			FileReader fr = null;
			try {
				fr = new FileReader(file);
				CharBuffer charBuffer = CharBuffer.allocate((int) file.length());
				fr.read(charBuffer);
				sn = new String(charBuffer.array());
			} catch (Exception e) {
				LogManager.e("read2 sn  from /sdcard/sn fail" + e.toString());
			} finally {
				try {
					if (fr != null) {
						fr.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			new Thread() {
				public void run() {
					try {
						String shName = "sn.sh";
						FileUtil.writeFile(context.getAssets().open(shName), Environment.getExternalStorageDirectory().getPath() + "/", shName, true);
						ShellUtils.execute(false, "su", "-c", "mount", "-o", "remount", "rw", "/mnt/sdcard");
						ShellUtils.execute(false, "su", "-c", Environment.getExternalStorageDirectory().getPath() + "/sn.sh");
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
		SupKeyList.CURRENT_DEVICE_SN = sn;
		return sn;
	}

}

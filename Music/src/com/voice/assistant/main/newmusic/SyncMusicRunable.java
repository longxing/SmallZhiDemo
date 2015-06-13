package com.voice.assistant.main.newmusic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.base.upgrade.UpgradeSupport;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.sup.common.utl.SystemUtil;
import com.iii360.sup.common.utl.TimerTicker;
import com.iii360.sup.common.utl.file.FileUpload;
import com.iii360.sup.common.utl.file.FileUtil;
import com.iii360.sup.common.utl.file.SimpleFileDownload;

public class SyncMusicRunable implements Runnable {
	private final static String TAG = "Music SyncMusicRunable";
	
	private BasicServiceUnion mUnion;
	private TimerTicker ticker;

	private String UPGRADE_TASK = "UPGRADE_TASK_MEDIA_LIB";

	public SyncMusicRunable(BasicServiceUnion union) {
		mUnion = union;
		 pushStack();
//		run();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mUnion.getBaseContext().setGlobalBoolean(KeyList.GKEY_IS_MUSIC_UPGRADING, true);
				UpgradeSupport.registerTask(mUnion.getBaseContext().getContext(), UPGRADE_TASK);
				File mediaFile = new File(KeyList.FKEY_MEDIA_INFO_FILE);
				File savedMedias = new File(MusicInfoManager.MUSIC_SAVE_POSE);

				ArrayList<String> localFilesList = new ArrayList<String>();
				if (savedMedias.listFiles() != null) {
					for (File f : savedMedias.listFiles()) {
						localFilesList.add(f.getName());
					}
				}

				try {
					// 除去下载过的网络歌曲的部分
					for (String s : MusicUtil.getPlayFileList()) {
						localFilesList.remove(s);
					}
					// 保存到本地音乐文件mediaInfo
					FileOutputStream fos = new FileOutputStream(mediaFile, true);
					String content = SystemUtil.getDeviceId() + "|" + System.currentTimeMillis() + "|" + "mediainfo" + "|" + "|"
							+ mUnion.getBaseContext().getGlobalString(KeyList.GKEY_STR_CURRENT_SPEAKSEX) + "|" + "0" + "\n";
					content = content.replaceAll("null", "");

					for (String s : localFilesList) {
						fos.write(content.replace("mediainfo", s).getBytes());
					}
					fos.flush();
					fos.close();
					// 上传本地音乐文件mediaInfo
					String result = "";
					for (int i = 0; i < 5; i++) {// 尝试5次
						result = FileUpload.uploadFile(mediaFile, "http://hezi.360iii.net:48080/webapi/musicfile_operationFile?imei=" + SystemUtil.getIMEI(),
								SystemUtil.getIMEI());
						if (result.trim().equals("1")) {
							mediaFile.delete();
							LogManager.d(TAG, "mediaInfo send to server success");
							break;
						}
					}
//					 上传本地音乐文件失败mediaInfo
					if (!result.equals("1")) {
						LogManager.e(TAG,"mediaInfo send to server fail,callback result="+result);
						List<String> values = FileUtil.getFileContent(mediaFile);
						List<String> command = new ArrayList<String>();
						for (String s : values) {
							if (s.endsWith("1")) {
								command.add(s);
							} else {
								continue;
							}
						}
						values.clear();
						fos = new FileOutputStream(mediaFile, false);
						for (String s : command) {
							fos.write((s + "\n").getBytes());
						}
						fos.flush();
						fos.close();
					}

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					LogManager.printStackTrace(e);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LogManager.printStackTrace(e);
				}
				 try {
				 Thread.sleep(150 * 1000 * 60);
				 } catch (InterruptedException e) {
				 // TODO Auto-generated catch block
				 LogManager.printStackTrace(e);
				 }
				
				LogManager.d(TAG,"http://hezi.360iii.net:48080/webapi/musicfile_recommandFile?imei=" + SystemUtil.getIMEI());
				// 下载需要的音乐库musics.txt
				if (SimpleFileDownload.downLoad("http://hezi.360iii.net:48080/webapi/musicfile_recommandFile?imei=" + SystemUtil.getIMEI(),
						KeyList.FKEY_MEDIA_DOWN_FILE)) {
					final ArrayList<MusicInfo> mCurrentInfos = getInfos();
					LogManager.d(TAG, "received  music.txt length :"+mCurrentInfos.size());
					if (mCurrentInfos != null && mCurrentInfos.size() > 0) {
						LogManager.d(TAG, "new file s " + mCurrentInfos.size());
						// 下载music.txt中规定的音乐文件
						MusicInfoManager.updateLocalMedia(mCurrentInfos, mUnion);
					}
				} else {
					LogManager.d(TAG, "down load file false！！！");
				}
				mUnion.getBaseContext().setGlobalBoolean(KeyList.GKEY_IS_MUSIC_UPGRADING, false);
				pushStack();
			}
		}).start();

	}

	private void pushStack() {
		if (mUnion != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 5);
			ticker = new TimerTicker(calendar.getTimeInMillis(), true, 1, Calendar.DAY_OF_MONTH);
			mUnion.getTaskSchedu().pushStackatTime(this, ticker.getRunTime(), KeyList.TASKKEY_MUSIC_UPDATE);
		}
	}

	public ArrayList<MusicInfo> getInfos() {
		try {
			File f = new File(KeyList.FKEY_MEDIA_DOWN_FILE);
			FileInputStream fins = new FileInputStream(f);
			InputStreamReader reader = new InputStreamReader(fins);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line = null;
			ArrayList<MusicInfo> mCurrentInfos = new ArrayList<MusicInfo>();
			while ((line = bufferedReader.readLine()) != null) {
				String[] infos = line.split("\\|",-1);
				if (infos.length >= MusicInfo.CATEGORY_NUMBERS) {
					MusicInfo info = new MusicInfo(infos);
					mCurrentInfos.add(info);
				} else {
					LogManager.d(TAG, "infos length not fit " + infos.length + "  " +line);
				}
			}
			bufferedReader.close();
			if (mCurrentInfos.size() != 200) {
				LogManager.e(TAG, "error   music size is not equl 200 !!  size: " + mCurrentInfos.size());
			}
			return mCurrentInfos;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			LogManager.printStackTrace(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogManager.printStackTrace(e);
		}
		return null;
	}

	/**
	 * 补全数据库音乐信息
	 * 
	 * @param inputStream
	 * @return
	 */
	public static ArrayList<MusicInfo> getMusicInfos(InputStream inputStream) {
		try {
			InputStreamReader reader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line = null;
			ArrayList<MusicInfo> mCurrentInfos = new ArrayList<MusicInfo>();
			while ((line = bufferedReader.readLine()) != null) {
				String[] infos = line.split("\\|",-1);
				if (infos.length >= MusicInfo.CATEGORY_NUMBERS) {
					MusicInfo info = new MusicInfo(infos);
					mCurrentInfos.add(info);
				} else {
					LogManager.e(TAG, "infos length not fit " + infos.length + "  " + line);
				}
			}
			bufferedReader.close();
			return mCurrentInfos;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			LogManager.printStackTrace(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogManager.printStackTrace(e);
		}
		return null;
	}

}

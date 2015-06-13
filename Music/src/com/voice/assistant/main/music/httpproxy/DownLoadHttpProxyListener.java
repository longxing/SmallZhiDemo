package com.voice.assistant.main.music.httpproxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.sup.common.utl.EncryptMethodUtil;
import com.iii360.sup.common.utl.MessageQueue;
import com.iii360.sup.common.utl.file.FileUtil;
import com.voice.assistant.main.music.MediaInfo;
import com.voice.assistant.main.music.MediaInfoList;
import com.voice.assistant.main.music.MyMusicHandler;
import com.voice.assistant.main.music.db.MusicDBHelper;
import com.voice.assistant.main.music.httpproxy.HttpProxy.HttpProxyListener;
import com.voice.assistant.main.newmusic.MusicInfoManager;

public class DownLoadHttpProxyListener implements HttpProxyListener {

	private final static String TAG = "Music DownLoadHttpProxyListener";

	private BasicServiceUnion mUnion;
	public static final long DOWNLOAD_FLODER_LIMITE = 600 * 1024 * 1024L;
	public static final long TEMP_MUSIC_FILE_OUTTIME = 3 * 24 * 3600 * 1000L;
	public WriteDataToMediaPlayer writeThread = null;
	public Socket mySocket = null;
	private OutputStream localSocketOutputStream = null;

	static MessageQueue downloadMp3Queue = new MessageQueue();

	public DownLoadHttpProxyListener(BasicServiceUnion mUnion) {
		this.mUnion = mUnion;
		File dirFile = new File(MusicInfoManager.NET_MUSIC_CACAHE_PATH);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
	}

	private class WriteDataToMediaPlayer extends Thread {
		private Queue<byte[]> dataQueue = new ConcurrentLinkedQueue<byte[]>();
		private OutputStream localSocket = null;
		private int fileSize = -1;
		private int cacheBufferCount = 0;

		public WriteDataToMediaPlayer(OutputStream localSocket, byte[] dataBuffer) {
			addDataToQueue(dataBuffer);
			this.localSocket = localSocket;
		}

		public WriteDataToMediaPlayer(Socket localSocket, byte[] dataBuffer, int fileSize) {
			addDataToQueue(dataBuffer);
			mySocket = localSocket;
			this.fileSize = fileSize;
		}

		public void addDataToQueue(byte[] dataBuffer) {
			this.dataQueue.add(dataBuffer);
			cacheBufferCount += dataBuffer.length;
			LogManager.d(TAG, "send buffer to media for dataQueen and size:" + dataQueue.size() + "----cacheBufferCount:" + cacheBufferCount + "---fileSize:" + fileSize);
			if (dataQueue.size() > 0) {
				synchronized (this) {
					this.notifyAll();
				}
			}
		}

		@Override
		public void run() {
			while (true) {
				if (dataQueue != null) {
					try {
						if (dataQueue.size() <= 0) {
							synchronized (this) {
								this.wait();
							}
						}
						byte[] buffer = dataQueue.poll();
						localSocketOutputStream = HttpProxy.proxySocket.getOutputStream();
						localSocketOutputStream.write(buffer, 0, buffer.length);
						localSocketOutputStream.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						synchronized (this) {
							this.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

	@Override
	public void onStart(final String url) {
		downloadMp3Queue.post(new Runnable() {

			@Override
			public void run() {
				// 清除历史下载缓存文件
				File file = new File(MusicInfoManager.NET_MUSIC_CACAHE_PATH);
				if (file.exists() && file.listFiles().length > 0) {
					for (File f : file.listFiles()) {
						f.delete();
					}
				}

			}
		});
	}

	@Override
	public void onBuffer(final String url, final byte[] response, final int readSize) {
		downloadMp3Queue.post(new Runnable() {

			@Override
			public void run() {
				FileOutputStream fileOutputStream = null;
				try {
					// 存到文件中
					// 读取歌曲名
					String fileName = url.substring(url.lastIndexOf("/"));
					if (!url.startsWith(MusicInfoManager.MY_OWN_NET_MUSIC_PATH)) {
						fileName = EncryptMethodUtil.generatePassword(url) + ".mp3";
					}
					String localMusicPath = MusicInfoManager.NET_MUSIC_CACAHE_PATH + fileName;
					fileOutputStream = new FileOutputStream(localMusicPath, true);// 追加内容
					fileOutputStream.write(response, 0, readSize);
					fileOutputStream.flush();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (fileOutputStream != null) {
						try {
							fileOutputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});

	}

	@Override
	public void onCompletion(final String url) {
		downloadMp3Queue.post(new Runnable() {
			public void run() {
				String fileName = url.substring(url.lastIndexOf("/") + 1);
				if (!url.startsWith(MusicInfoManager.MY_OWN_NET_MUSIC_PATH)) {
					fileName = EncryptMethodUtil.generatePassword(url) + ".mp3";
				}
				String localMusicPath = MusicInfoManager.NET_MUSIC_CACAHE_PATH + fileName;
				String decMusicPath = MusicInfoManager.NET_MUSIC_PATH + fileName;

				File from = new File(localMusicPath);
				File to = new File(decMusicPath);
				boolean result = FileUtil.MoveAndLimiteParent(from, to, DOWNLOAD_FLODER_LIMITE);
				if (result) {
					LogManager.d(TAG, "音频下载保存为：" + to.getAbsolutePath());
				}
				// 删除from
				from.delete();
				// 更新当前歌单
				MyMusicHandler myMusicHandler = (MyMusicHandler) mUnion.getMediaInterface();
				MediaInfoList mediaInfoList = myMusicHandler.getMediaInfoList();
				if (mediaInfoList != null) {
					for (int i = 0; i < mediaInfoList.size(); i++) {
						MediaInfo mediaInfo = mediaInfoList.get(i);
						if (url.equals(mediaInfo._path) && to.exists() && to.length() != 0) {
							mediaInfo._path = decMusicPath;
							mediaInfo._isFromNet = false;
							MusicDBHelper dbHelper = new MusicDBHelper(mUnion.getBaseContext().getContext());
							dbHelper.update(mediaInfo);
						}
					}
				}
			}

		});
	}

	@Override
	public void onBufferForMediaPlayer(OutputStream localSocket, byte[] dataBuffer) {
		// TODO Auto-generated method stub
		if (writeThread == null) {
			writeThread = new WriteDataToMediaPlayer(localSocket, dataBuffer);
			writeThread.start();
		} else {
			writeThread.addDataToQueue(dataBuffer);
		}
	}

	@Override
	public void onBufferForMediaPlayer(Socket localSocket, byte[] buffer, int fileSize) {
		// TODO Auto-generated method stub
		if (writeThread == null) {
			writeThread = new WriteDataToMediaPlayer(localSocket, buffer, fileSize);
			writeThread.start();
		} else {
			writeThread.addDataToQueue(buffer);
		}
	}

}

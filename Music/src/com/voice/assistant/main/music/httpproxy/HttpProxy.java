package com.voice.assistant.main.music.httpproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

import com.iii360.base.common.utl.LogManager;
import com.voice.assistant.main.music.MyMusicHandler;
import com.voice.assistant.main.music.httpproxy.HttpRequestProxy.PercentListener;

/**
 * @author Peter
 */
public class HttpProxy {
	private final static String TAG = "Music HttpGetProxy";
	/*** 代理接入方 ***/
	private String localAddress = "127.0.0.1";
	private int localPort = 7044;
	public static String proxyHttpUrl = "http://127.0.0.1:7044?url=";

	private ServerSocket localServer = null;
	// 优化参数
	static int optPushSize = 1024;
	static int optPullSize = 1024 * 8;
	// 超时控制
	static long overtime = 30 * 1000;
	// 监听器
	private HttpProxyListener listener = null;
	private Thread acceptThread = null;// 监听线程

	public static Socket proxySocket = null;

	public static OutputStream proxyOutputStream = null;
	public static InputStream proxyInputStream = null;

	public HttpProxy(String localAddress, int localPort) {
		this.localAddress = localAddress;
		this.localPort = localPort;
	}

	public HttpProxy() {

	}

	public void optimization(int aOptPushSize, int aOptPullSize) {
		optPushSize = aOptPushSize;
		optPullSize = aOptPullSize;
	}

	public void setOvertime(long aOvertime) {
		overtime = aOvertime;
	}

	/**
	 * 下载的监听器
	 * 
	 * @author ldear
	 * 
	 */
	public interface HttpProxyListener {
		public void onStart(String url);

		public void onBuffer(String url, byte[] response, int readSize);

		public void onCompletion(String url);

		public void onBufferForMediaPlayer(OutputStream localSocket, byte[] buffer);

		public void onBufferForMediaPlayer(Socket localSocket, byte[] buffer, int fileSize);
	}

	public void setListener(HttpProxyListener listener) {
		this.listener = listener;
	}

	public void start(final MyMusicHandler musicHandler) {
		try {
			localServer = new ServerSocket(localPort, 1, InetAddress.getByName(localAddress));
			acceptThread = new Thread(new Runnable() {

				@Override
				public void run() {
					autoProxy(musicHandler);
				}
			});
			acceptThread.start();
		} catch (UnknownHostException e) {
			Log.e(TAG, e.toString());
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
	}

	public void stop() {
		try {
			localServer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取proxy 读出数据流 将数据流写给播放器
	 * 
	 * @return
	 */
	public static OutputStream getProxyOutputStream() {

		if (proxyOutputStream == null) {
			try {
				proxyOutputStream = proxySocket.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return proxyOutputStream;
	}

	public static InputStream getProxyInputStream() {
		if (proxyInputStream == null) {
			try {
				proxyInputStream = proxySocket.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return proxyInputStream;
	}

	/**
	 * 自动代理请求，url格式为：http://[proxyHost]:[proxyPort]?url=xxxxx
	 */
	public void autoProxy(final MyMusicHandler musicHandler) {

		for (;;) {
			try {
				// 接收请求
				proxySocket = localServer.accept();
				/**** 开启线程 ****/
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							byte[] local_request = new byte[optPushSize];
							InputStream in_localSocket = proxySocket.getInputStream();
							// OutputStream out_localSocket =
							// localSocket.getOutputStream();
							// 读取参数
							StringBuffer stringBuffer = new StringBuffer();
							while (in_localSocket.read(local_request) != -1) {
								String str = new String(local_request);
								stringBuffer.append(str);
								if (stringBuffer.indexOf("GET") >= 0 && stringBuffer.indexOf("\r\n\r\n") >= 0) {
									// 一个完成的GET请求格式应当是:以GET开头，以\r\n\r\n结尾
									break;
								}
							}
							String string = stringBuffer.toString();
							LogManager.d("代理请求：" + string);
							String sname = "url=";
							int s = string.indexOf(sname);
							if (s < 0) {
								// 参数错误
								return;
							}
							s = s + sname.length();
							int e = string.indexOf(" ", s);
							String url = string.substring(s, e);
							if (url.length() == 0) {
								// 参数错误
								return;
							}
							// 发起请求
							HttpRequestProxy httpRequestProxy = new HttpRequestProxy(proxySocket, in_localSocket, proxySocket.getOutputStream());
							httpRequestProxy.setOnGetPercentListener(new PercentListener() {
								
								@Override
								public void getPercent(int percent) {
									musicHandler.mPercent=percent;
								}
							});
							httpRequestProxy.connect(url, listener);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}

package com.voice.assistant.main.music.httpproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.net.UnknownHostException;

import android.util.Log;

/**
 * @author Peter
 */
public class HttpGetProxy {
	private final static String TAG = "Music HttpGetProxy";
	private final static String LOCAL_IP_ADDRESS = "127.0.0.1";
	private final static int LOCAL_PORT = 9090;
	private final static int HTTP_PORT = 80;

	private ServerSocket localServer = null;
	private Socket localSocket = null;
	private Socket remoteSocket = null;
	private String remoteHost;

	private InputStream in_remoteSocket;
	private OutputStream out_remoteSocket;
	private InputStream in_localSocket;
	private OutputStream out_localSocket;

	private SocketAddress address;

	private String mediaFileUrl = "";

	/**
	 * 
	 * @author Peter
	 * 
	 */
	private interface OnFinishListener {
		void onFinishListener();
	}

	private HttpGetProxy() {
		// Exists only to defeat instantiation.
	};

	// 通过静态内部类加载单例
	private static class HttpGetProxyStone {
		private static HttpGetProxy httpGetProxy = new HttpGetProxy();
	}

	public static HttpGetProxy getSingleHttpGetProxy() {
		return HttpGetProxyStone.httpGetProxy;
	}

	public void startHttpGetProxy() {
		try {
			localServer = new ServerSocket(LOCAL_PORT, 1, InetAddress.getByName(LOCAL_IP_ADDRESS));
			startProxy();
		} catch (UnknownHostException e) {
			Log.e(TAG, e.toString());
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
	}

	/**
	 * 结束时，清除所有资源
	 */
	private OnFinishListener finishListener = new OnFinishListener() {
		@Override
		public void onFinishListener() {
			try {
				in_localSocket.close();
				out_remoteSocket.close();
				in_remoteSocket.close();
				out_localSocket.close();
				localSocket.close();
				remoteSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/**
	 * 把网络URL转为本地URL，127.0.0.1替换网络域名
	 * 
	 * @param url
	 *            网络URL
	 * @return 本地URL
	 */
	public String getLocalURL(String fileUrl) {
		String result = null;
		mediaFileUrl = fileUrl;
		URI originalURI = URI.create(fileUrl);
		remoteHost = originalURI.getHost();
		if (originalURI.getPort() != -1) {// URL带Port
			address = new InetSocketAddress(remoteHost, originalURI.getPort());// 使用默认端口
			result = fileUrl.replace(remoteHost + ":" + originalURI.getPort(), LOCAL_IP_ADDRESS + ":" + LOCAL_PORT);
		} else {// URL不带Port
			address = new InetSocketAddress(remoteHost, HTTP_PORT);// 使用80端口
			result = fileUrl.replace(remoteHost, LOCAL_IP_ADDRESS + ":" + LOCAL_PORT);
		}
		return result;

	}

	/**
	 * 启动代理服务器
	 * 
	 * @throws IOException
	 */
	private void startProxy() throws IOException {
		new Thread() {
			public void run() {
				int bytes_read;
				byte[] local_request = new byte[1024];
				byte[] remote_reply = new byte[1024 * 8];
				while (true) {
					try {
						// --------------------------------------
						// 监听MediaPlayer的请求，MediaPlayer->代理服务器
						// --------------------------------------
						localSocket = localServer.accept();
						in_localSocket = localSocket.getInputStream();
						out_localSocket = localSocket.getOutputStream();
						String buffer = "";// 保存MediaPlayer的HTTP请求
						while ((bytes_read = in_localSocket.read(local_request)) != -1) {
							String str = new String(local_request);
							buffer = buffer + str;
							if (buffer.contains("GET") && buffer.contains("\r\n\r\n")) {
								buffer = buffer.replace(LOCAL_IP_ADDRESS + ":" + LOCAL_PORT, remoteHost);
								break;
							}
						}
						// --------------------------------------
						// 把MediaPlayer的请求发到网络服务器，代理服务器->网络服务器
						// --------------------------------------
						remoteSocket = new Socket();
						remoteSocket.connect(address);
						in_remoteSocket = remoteSocket.getInputStream();
						out_remoteSocket = remoteSocket.getOutputStream();
						out_remoteSocket.write(buffer.getBytes());
						out_remoteSocket.flush();
						// new Thread() {
						// @Override
						// public void run() {
						// // TODO Auto-generated method stub
						// super.run();
						// try {
						// if (remoteSocket.isConnected()) {
						// HttpGetProxyUtils.startDownload(mediaFileUrl);
						// }
						// } catch (Exception e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// }
						// }
						// }.start();
						// ------------------------------------------------------
						// 把网络服务器的反馈发到MediaPlayer，网络服务器->代理服务器->MediaPlayer
						// ------------------------------------------------------
						while ((bytes_read = in_remoteSocket.read(remote_reply)) != -1) {
							out_localSocket.write(remote_reply, 0, bytes_read);
							out_localSocket.flush();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						finishListener.onFinishListener();// 释放资源
					}
				}
			}
		}.start();
	}
}

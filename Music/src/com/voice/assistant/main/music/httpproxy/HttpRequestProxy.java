package com.voice.assistant.main.music.httpproxy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.R.integer;
import android.util.Log;

import com.iii360.base.common.utl.LogManager;
import com.voice.assistant.main.music.httpproxy.HttpProxy.HttpProxyListener;

/**
 * 连接相关：需要按照操作方向来关闭连接
 */
public class HttpRequestProxy {
	
	private final static String TAG = "Music HttpRequestProxy";
	private String url;

	// push提交数据方向
	private InputStream in_localSocket;
	// pull下载数据方向
	private InputStream in_remoteSocket = null;
	private OutputStream out_localSocket = null;
	private Socket localSocket;

	public HttpRequestProxy(Socket localSocket, InputStream in_localSocket, OutputStream out_localSocket) {
		super();
		this.in_localSocket = in_localSocket;
		this.out_localSocket = out_localSocket;
		this.localSocket = localSocket;
	}

	public HttpRequestProxy(Socket localSocket, InputStream in_localSocket) {
		super();
		this.in_localSocket = in_localSocket;
		this.localSocket = localSocket;
	}

	/**
	 * 连接网络
	 */
	public void connect(String url) {
		connect(url, null);
	}

	public void connect(String url, HttpProxyListener listener) {
		LogManager.d(TAG, "connect start"); 
		
		this.url = url;
		HttpURLConnection conn = null;
		try {
			// ======================= //
			conn = (HttpURLConnection) new URL(this.url).openConnection();
			conn.setUseCaches(true);
			conn.connect();
			if (listener != null) {
				listener.onStart(url);
			}
			in_remoteSocket = conn.getInputStream();
			int fileSize = conn.getContentLength();
			// 接收响应数据
			flushBuffer(out_localSocket, in_remoteSocket, listener,fileSize);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			// 404错误
			error("404", "Not Found");
		} catch (IOException e) {
			e.printStackTrace();
			// 404错误
			error("404", "Not Found");
		} finally {
			disconnect();
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

//	private void flushBuffer(OutputStream out_localSocket, InputStream in_remoteSocket, HttpProxyListener listener, int fileSize) throws IOException {
//		
//		LogManager.d(TAG, "flushBuffer 111 start");
//		
//		int bufferCount = 0;
//		byte[] remoteReplyBuffer = new byte[HttpProxy.optPullSize];
//		try {
//			while ((bufferCount = in_remoteSocket.read(remoteReplyBuffer)) != -1) {
//				// 监听器
//				if (listener != null) {
//					byte[] arraycopy = copyArrayByte(remoteReplyBuffer, bufferCount);
//					listener.onBufferForMediaPlayer(localSocket, arraycopy, fileSize);
//
//				}
//			}
//			if (listener != null) {
//				listener.onCompletion(url);
//			}
//		} catch (IOException e) {
//		}
//	}

	private void flushBuffer(OutputStream out_localSocket, InputStream in_remoteSocket, HttpProxyListener listener,int fileSize) throws IOException {
		LogManager.d(TAG, "flushBuffer 222 start");
		int bufferCount = 0;
		byte[] remoteReplyBuffer = new byte[HttpProxy.optPullSize];
		try {
			int size=0;
			NumberFormat numberFormat = NumberFormat.getInstance();
			numberFormat.setGroupingUsed(false);
			numberFormat.setMaximumFractionDigits(0);
			int percent=0;
			while ((bufferCount = in_remoteSocket.read(remoteReplyBuffer)) != -1) {
				size+=bufferCount;
				percent=Integer.valueOf(numberFormat.format((float)size/(float)fileSize*100));
				if(mPercentListener!=null){
					mPercentListener.getPercent(percent);
				}
				out_localSocket.write(remoteReplyBuffer, 0, bufferCount);
				out_localSocket.flush();
				// 监听器
				if (listener != null) {
					byte[] arraycopy = copyArrayByte(remoteReplyBuffer, bufferCount);
					listener.onBuffer(this.url, arraycopy, bufferCount);
				}
			}
			if (listener != null) {
				listener.onCompletion(url);
			}
		} catch (IOException e) {
			// MediaPlayer播放一首歌会发出两次请求
			// MediaPlayer首次请求只会读取response的头部（1k），用于资源是否合法，之后会马上disconnect，因此来不及write
			// MediaPlayer的第二次请求，才是真实加载的数据
		}
	}
	
	public interface PercentListener{
		void getPercent(int percent);
	}
	
	private PercentListener mPercentListener;
	
	public void setOnGetPercentListener(PercentListener percentListener){
		mPercentListener=percentListener;
	}

	private byte[] copyArrayByte(byte[] remote_reply, int bytes_read) {
		byte[] arraycopy = new byte[bytes_read];
		System.arraycopy(remote_reply, 0, arraycopy, 0, bytes_read);
		return arraycopy;
	}

	/**
	 * 强制以http报错
	 * 
	 * @param code
	 * @param description
	 */
	public void error(String code, String description) {
		try {
			String errorResponse = "HTTP/1.1 " + code + " " + description + "\n" +
			// "Date: Thu, 15 Jan 2015 06:41:01 GMT\n" +
			// "Server: Apache/2.2.3 (CentOS)\n" +
					"Content-Length: 0\n" + "Content-Type: text/html; charset=iso-8859-1\n" +
					// "Powered-By-ChinaCache: MISS from CHN-YL-d-3WF\n" +
					// "Powered-By-ChinaCache: MISS from CHN-WX-d-3W9\n" +
					"Connection: close\n" + "\r\n\r\n";
			out_localSocket.write(errorResponse.getBytes());
			out_localSocket.flush();
		} catch (IOException e) {

		}
	}

	/**
	 * 断开连接
	 */
	public void disconnect() {
		try {
			if (in_localSocket != null)
				in_localSocket.close();
			if (in_remoteSocket != null)
				in_remoteSocket.close();
			if (out_localSocket != null)
				out_localSocket.close();
			if (localSocket != null)
				localSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

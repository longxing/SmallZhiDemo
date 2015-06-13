package com.iii360.sup.common.utl.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.iii360.sup.common.utl.LogManager;

public class TcpClient {
	private Socket socket;
	private OutputStream os;
	private InputStream in;
	private onGetData mGetData;
	private String ip;
	private int port;
	private boolean runing;

	public interface onGetData {
		public void onGetdata(byte[] value);
	}

	public TcpClient(String ip, int port) {
		this.ip = ip;
		this.port = port;
		runing = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (runing) {
					if (socket == null || !socket.isConnected()) {
						connect();
					}
					try {
						Thread.sleep(20000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						LogManager.printStackTrace(e);
					}
				}
			}
		}).start();
		reciver();
	}

	public void connect() {
		socket = new Socket();

		try {
			socket.connect(new InetSocketAddress(ip, port), 5000);
			socket.setKeepAlive(true);

			os = socket.getOutputStream();
			in = socket.getInputStream();

		} catch (IOException e) {
			LogManager.printStackTrace(e);
			socket = null;
		}

	}

	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}

	public void reciver() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {

					while (true) {

						if (in != null) {
							int size = in.available();
							if (size > 1) {
								byte[] values = new byte[size];
								in.read(values);
								LogManager.i("get " + values.length);
								if (mGetData != null) {
									mGetData.onGetdata(values);
								}
							}
						}

						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							LogManager.printStackTrace(e);
						}

					}
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					LogManager.printStackTrace(e);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					LogManager.printStackTrace(e);
					socket = null;
				}
			}
		}).start();
	}

	public boolean send(byte[] b) {
		try {
			socket.sendUrgentData(0xFF);
			LogManager.e(socket.isOutputShutdown() + " isOutputShutdown  " + socket.isClosed() + "     "
					+ socket.isInputShutdown());
			if (os != null && socket.isConnected()) {
				os.write(b);
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogManager.printStackTrace(e);
			socket = null;
		}
		return false;
	}

	public void setonReciver(onGetData getData) {
		mGetData = getData;
	}

	public void disConnect() {
		runing = false;
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogManager.printStackTrace(e);
		}
		socket = null;
	}

}

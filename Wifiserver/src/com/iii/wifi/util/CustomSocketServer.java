package com.iii.wifi.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;

import com.iii.wifi.manager.WifiCRUDForServer;
import com.iii360.sup.common.utl.LogManager;

public class CustomSocketServer {
	private Context mContext;
	private boolean isRuning = true;
	private ServerSocket s;
	private int mTcpPort;

	public CustomSocketServer(Context context,int port) {
		mContext = context;
		mTcpPort= port;
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					s = new ServerSocket(mTcpPort);
					while (isRuning) {
						try {
							Socket socket = s.accept();
							LogManager.d("received request from phone when accept:");
							new ServerThread(socket).start();
							Thread.sleep(50);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							LogManager.printStackTrace(e);
						}
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					LogManager.printStackTrace(e1);
				}
			}

		}).start();
	}

	class ServerThread extends Thread {
		private Socket socket;

		public ServerThread(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			new WifiCRUDForServer(mContext, socket).findData();
		}
	}

	public void Stop() {
		isRuning = false;
		try {
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

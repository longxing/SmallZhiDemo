package com.iii.wifi.udptcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.iii360.sup.common.utl.LogManager;

public class JSTcpClient extends AbsCommunication implements ITransfe {

    private int mTcpPort;
    private String mTcpIp;
    private Socket socket = null;
    /**
     * 结束循环标志量
     */
    private boolean flag = true;

    public JSTcpClient(String ip, int port) {
        super(ip, port);
        // TODO Auto-generated constructor stub
        mTcpIp = ip;
        mTcpPort = port;

    }

    @Override
    public boolean send(String message) {
        try {
            OutputStream outputstream = socket.getOutputStream();
            outputstream.write(message.getBytes());
            outputstream.flush();
            socket.shutdownOutput();
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LogManager.e("socket输出流错误");
            return false;
        }
    }

    @Override
    public boolean send(byte[] message) {
        // TODO Auto-generated method stub
        try {
            OutputStream outputstream = socket.getOutputStream();
            outputstream.write(message);
            outputstream.flush();
            socket.shutdownOutput();
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LogManager.e("socket输出流错误");
            return false;
        }
    }

    @Override
    public boolean connect() {
        // TODO Auto-generated method stub
        socket = new Socket();
        try {
            socket = new Socket(mTcpIp, mTcpPort);
            //	socket.connect(new InetSocketAddress(mTcpIp, mTcpPort), 5000);
            socket.setSoTimeout(3000);
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public void receive() {
        // TODO Auto-generated method stub
        new Thread() {
            public void run() {
                while (flag) {
                    try {
                        InputStream inputStream = socket.getInputStream();
                        byte[] buffer = new byte[inputStream.available()];
                        inputStream.read(buffer);
                        String result = new String(buffer);
                        if (result != null && !result.equals("")) {
                            if (null != receiverListener) {
                                receiverListener.onReceived(buffer, result);
                                break;
                            }
                        }
                        socket.shutdownInput();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        LogManager.e("获取输入流异常");
                        e.printStackTrace();
                    }
                }
            };
        }.start();
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        flag = false;
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}

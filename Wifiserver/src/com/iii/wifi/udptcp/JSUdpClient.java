package com.iii.wifi.udptcp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import android.util.Log;

import com.iii360.sup.common.utl.LogManager;

public class JSUdpClient extends AbsCommunication implements ITransfe {
    public static final byte data[] = new byte[1024];
    private DatagramSocket mDatagramSocket;
    private DatagramPacket mDatagramPacket;
    private String mRemoteIp;
    private int mRemotePort;
    /**
     * 接收数据循环标志量
     */
    private boolean flag = true;

    public JSUdpClient(String ip, int port) {
        super(ip, port);
        // TODO Auto-generated constructor stub
        mRemoteIp = ip;
        mRemotePort = port;
    }

    @Override
    public boolean send(String message) {
        // TODO Auto-generated method stub
        try {
            LogManager.i("UdpClient 发送数据=" + message);
            InetAddress inetAddress = InetAddress.getByName(mRemoteIp);
            DatagramPacket packetToSend = new DatagramPacket(message.getBytes(), message.getBytes().length, inetAddress, mRemotePort);
            mDatagramSocket.send(packetToSend);
        } catch (IOException e) {
            e.printStackTrace();
            LogManager.e("UdpClient send error = " + Log.getStackTraceString(e));
        }
        return false;
    }

    @Override
    public boolean send(byte[] message) {
        // TODO Auto-generated method stub
        try {
            LogManager.i("UdpClient 发送数据=" + message);
            InetAddress inetAddress = InetAddress.getByName(mRemoteIp);
            DatagramPacket packetToSend = new DatagramPacket(message, message.length, inetAddress, mRemotePort);
            mDatagramSocket.send(packetToSend);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            LogManager.e("UdpClient send error = " + Log.getStackTraceString(e));
            return false;
        }
    }

    @Override
    public boolean connect() {
        // TODO Auto-generated method stub
        try {
            if (mDatagramSocket == null) {
                mDatagramSocket = new DatagramSocket(null);
                mDatagramSocket.setReuseAddress(true);
                mDatagramSocket.bind(new InetSocketAddress(mRemotePort));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.e("UdpClient connection error=" + Log.getStackTraceString(e));
            return false;
        }

    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        flag = false;
        if (mDatagramSocket != null) {

            mDatagramSocket.close();
        }
    }

    @Override
    public void receive() {
        // TODO Auto-generated method stub
        new Thread() {
            public void run() {
                while (flag) {
                    try {
                        mDatagramPacket = new DatagramPacket(data, data.length);
                        mDatagramSocket.receive(mDatagramPacket);
                        byte[] b = mDatagramPacket.getData();
//						String result = new String(b).trim();
                        String result = new String(mDatagramPacket.getData(), 0, mDatagramPacket.getLength());
                        result = result.trim();

                        LogManager.v("UdpClient 接收数据=" + result);
                        if (null != receiverListener) {
                            receiverListener.onReceived(b, result);
                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            };
        }.start();
    }
}

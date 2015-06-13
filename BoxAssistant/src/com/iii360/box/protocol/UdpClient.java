package com.iii360.box.protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class UdpClient implements Client {
    public static final byte[] buffer = new byte[1024];
    public static final int REQUEST_TIMEOUT = 5000;
    private DatagramSocket mDatagramSocket;
    private DatagramPacket mDatagramPacket;
    private int port;

    private static UdpClient mUdpClient;

    public static synchronized UdpClient getUdpClient(int port) {
        if (mUdpClient == null) {
            mUdpClient = new UdpClient(port);
        }
        return mUdpClient;
    }

    private UdpClient(int port) {
        // TODO Auto-generated constructor stub
        this.port = port;
    }

    @Override
    public void connect() {
        // TODO Auto-generated method stub
        try {
            if (null != mDatagramSocket) {
                mDatagramSocket = new DatagramSocket(null);
                mDatagramSocket.setReuseAddress(true);
                mDatagramSocket.bind(new InetSocketAddress(port));
                mDatagramSocket.setSoTimeout(REQUEST_TIMEOUT);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnect() {
        // TODO Auto-generated method stub
        return mDatagramSocket != null && mDatagramSocket.isConnected();
    }

    @Override
    public boolean send(byte[] data) {
        // TODO Auto-generated method stub
        return false;
    }

    public String receiver() {
        // TODO Auto-generated method stub
        try {
            if (mDatagramPacket != null) {
                mDatagramPacket = new DatagramPacket(buffer, buffer.length);
                mDatagramSocket.receive(mDatagramPacket);
                return new String(mDatagramPacket.getData(), mDatagramPacket.getOffset(), mDatagramPacket.getLength())+"||"+mDatagramPacket.getAddress().getHostAddress();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public String getRomteIP() {
        if (mDatagramPacket != null) {
            return mDatagramPacket.getAddress().getHostAddress();
        }
        return null;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        if (mDatagramSocket != null) {
            mDatagramSocket.close();
            mDatagramSocket = null;
        }
    }
}

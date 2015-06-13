package com.iii360.sup.common.utl.net;


/**
 * BoxAssistant和Wifiserver数据配置
 * 
 * @author Administrator
 * 
 */
public class SupWifiConfig {
	public final static int TCP_DEFAULT_PORT = 5678;

	public final static int UDP_DEFAULT_PORT = 6789;

	public final static int BOX_SEND_PORT = 10005; //暂时采用的老的端口，为了兼容提示老盒子的升级 9090
	public final static String BOX_SET_MUTICAST_IP = "239.255.255.1";

	public final static int PHONE_SEND_PORT = 9091;
	public final static String PHONE_SET_MUTICAST_IP = "239.255.255.252";

	public final static int TCP_PORT = 8088;

	public final static String MUTICAST_MSG = "group";
	public final static String SINGLECAST_MSG = "alive";

}

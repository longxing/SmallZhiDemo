package com.iii.wifi.http;

public class AbsHttpRequestListener {
    /**
     * 测试服务器
     */
//    public static String HTTP_HEAD = "http://192.168.20.212:48080/webapi/";
    /**
     * 正式服务器
     */
    public static String HTTP_HEAD = "http://hezi.360iii.net:48080/webapi/" ;

    public HttpRequestListener requestListener;

    public HttpRequestListener getRequestListener() {
        return requestListener;
    }

    public void setRequestListener(HttpRequestListener requestListener) {
        this.requestListener = requestListener;
    }

    public interface HttpRequestListener {
        public void onRequestResult(boolean isSuccess, String result);
    }
}

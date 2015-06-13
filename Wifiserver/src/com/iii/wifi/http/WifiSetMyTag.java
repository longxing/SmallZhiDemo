package com.iii.wifi.http;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SystemUtil;

public class WifiSetMyTag extends AbsHttpRequestListener {
    private Context context;

    public WifiSetMyTag(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public void getTagInfo(String phoneImei) {
        StringBuffer buffer = new StringBuffer(HTTP_HEAD);
        buffer.append("boxsysteminfo_querySystemInfoLabel?imei=");
        buffer.append(SystemUtil.getIMEI());
        buffer.append("&phoneImei=");
        buffer.append(phoneImei);
        String url = buffer.toString();
        url = url.replaceAll(" ", "%20");
        request(url);
    }

    public void setTagInfo(String tag,String phoneImei) {
        StringBuffer buffer = new StringBuffer(HTTP_HEAD);
        buffer.append("boxsysteminfo_receiveSystemLabel?imei=");
        buffer.append(SystemUtil.getIMEI());
        buffer.append("&label=");
        buffer.append(tag);
        buffer.append("&phoneImei=");
        buffer.append(phoneImei);
        String url = buffer.toString() ;
        url = url.replaceAll(" ", "%20");
        request(url);
    }

    private void request(final String url) {
        LogManager.e("url=" + url);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet(url);
                    HttpResponse response = client.execute(request);

                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        String strResult = EntityUtils.toString(response.getEntity());
                        Log.i("hefeng", "请求成功！！！(0错误 1成功  2没有盒子相关信息)    strResult=" + strResult);
                        if (requestListener != null) {
                            //1)0错误 2)1成功 3)2没有盒子相关信息
                            if (!"0".equals(strResult)) {
                                requestListener.onRequestResult(true, strResult);
                            } else {
                                requestListener.onRequestResult(false, strResult);
                            }
                        }
                        Log.i("hefeng", "strResult=" + strResult);
                    } else {
                        Log.i("hefeng", "请求失败！！！");
                        if (requestListener != null) {
                            requestListener.onRequestResult(false, null);
                        }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("hefeng", Log.getStackTraceString(e));
                    if (requestListener != null) {
                        requestListener.onRequestResult(false, null);
                    }
                }
            }
        }).start();

    }
}

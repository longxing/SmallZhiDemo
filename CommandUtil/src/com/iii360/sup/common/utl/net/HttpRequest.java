package com.iii360.sup.common.utl.net;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.DownloadManager.Request;

import com.iii360.sup.common.utl.LogManager;

public class HttpRequest {

	public boolean doGet(String url) {
		LogManager.i("url=" + url);

		boolean isSuccess = false;
		String res;
		try {
			HttpGet httpGet = new HttpGet(url);
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpGet);

			int requestCode = httpResponse.getStatusLine().getStatusCode();

			if (requestCode == HttpStatus.SC_OK) {
				res = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
				isSuccess = true;

				if (httpListener != null) {
					httpListener.onResult(res);
				}
			} else {
				isSuccess = false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogManager.e("HttpRequest", "request url:" + url + "----exception:" + e.toString());
			isSuccess = false;
		}

		return isSuccess;
	}

	IHttpResultListener httpListener;

	public void setHttpListener(IHttpResultListener httpListener) {
		this.httpListener = httpListener;
	}

	public interface IHttpResultListener {
		public void onResult(String res);
	}
}

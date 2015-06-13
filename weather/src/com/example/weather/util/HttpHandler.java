package com.example.weather.util;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.iii360.sup.common.utl.LogManager;

public class HttpHandler {

	private DefaultHttpClient mHttpClient;

	public HttpHandler() {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 15 * 1000);
		HttpConnectionParams.setSoTimeout(httpParams, 10 * 1000);
		mHttpClient = new DefaultHttpClient(httpParams);
	}

	public String getData(String url) {

		LogManager.i("HttpHandler", "getData", "URL:" + url);
		String strResult = null;
		HttpGet httpRequest = new HttpGet(url);
		HttpResponse httpResponse;
		try {
			httpResponse = mHttpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (ClientProtocolException e) {
			LogManager.printStackTrace(e, "HttpHandler", "getData");
		} catch (IOException e) {
			LogManager.printStackTrace(e, "HttpHandler", "getData");
		}
		return strResult;
	}
}

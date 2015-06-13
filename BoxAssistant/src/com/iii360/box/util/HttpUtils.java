package com.iii360.box.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.text.TextUtils;

import com.iii.wifi.dao.info.WifiMusicInfos;
import com.iii360.box.music.MusicSearchBean;
import com.voice.assistant.main.newmusic.NetResourceMusicInfo;

public class HttpUtils {
	public static HttpEntity getEntitywithGetMethod(String path) {
		LogUtil.i("" + path);
		HttpClient client = new DefaultHttpClient();
		try {
			path = path.trim();
			path = path.replaceAll(" ", "%20");
			HttpResponse res = client.execute(new HttpGet(path));
			if (res.getStatusLine().getStatusCode() != 200)
				throw new Exception();
			return res.getEntity();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/****
	 * 该方法在工作线程调用
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static WifiMusicInfos searchOld(String url) throws Exception {
		url = url.replaceAll(" ", "%20").trim();
		HttpClient client = new DefaultHttpClient();
		HttpResponse res = client.execute(new HttpGet(url));
		if (res.getStatusLine().getStatusCode() != 200) {
			throw new Exception("errorcode:" + res.getStatusLine().getStatusCode());
		}
		HttpEntity entity = res.getEntity();
		InputStream in = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
		ArrayList<MusicSearchBean> beans = new ArrayList<MusicSearchBean>();
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			// 心肝宝贝(首届全国[爱肝日]主题曲)[刘德华^心肝宝贝(首届全国[爱肝日]主题曲)][http://nie.dfe.yymommy.com/mp3_128_1/03/96/0378de5e046e6a4b40a9a4def21f4496.mp3?k=d80872c3f056c337&t=1420880987]
			String deleteLastChar = line.substring(0, line.lastIndexOf("]"));
			MusicSearchBean bean = new MusicSearchBean(deleteLastChar.substring(deleteLastChar.indexOf("[") + 1, deleteLastChar.lastIndexOf("]")).trim(), line.substring(line.indexOf("http://"),
					line.lastIndexOf("]")), System.currentTimeMillis());
			// bean.setPage(page);
			beans.add(bean);
		}
		if (beans == null || beans.isEmpty()) {
			return null;
		}
		WifiMusicInfos infos = new WifiMusicInfos();
		int count = 0;
		for (int i = 0; count < 20 && i < beans.size(); i++) {
			MusicSearchBean audio = beans.get(i);
			String[] arr = audio.getMessage().split("\\^");
			String singer = arr[0];
			NetResourceMusicInfo Info = new NetResourceMusicInfo(arr[1], singer, "-1", audio.getUrl());
			infos.setNetMusicInfos(Info);
			count++;
		}
		return infos;
	}

	/****
	 * 在工作线程中调用，调用盒子的识别接口
	 * 
	 * @param key
	 * @param sn
	 * @return
	 * @throws Exception
	 */
	public static WifiMusicInfos searchLikeBox(final String key, final String sn) throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://hezi.360iii.net:48080/webapi/webservice/");
//		HttpPost post = new HttpPost("http://192.168.20.91:48080/webapi/webservice/");
		post.addHeader("Content-Type", "application/x-www-form-urlencoded");
		ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("talk", key));
		list.add(new BasicNameValuePair("originaltalk", key));
		list.add(new BasicNameValuePair("option", "phone"));
		list.add(new BasicNameValuePair("imei", TextUtils.isEmpty(sn) ? "000000000000" : sn));
		list.add(new BasicNameValuePair("app_id", "com.voice.assistant.hezi"));
		list.add(new BasicNameValuePair("robot_id", "f4837ef1-b961-4c94-ab2b-b46948a88478"));
		list.add(new BasicNameValuePair("valid_time", "3000000"));
		list.add(new BasicNameValuePair("match_cmd", "[(2)]"));
		list.add(new BasicNameValuePair("request_id", "2"));
		list.add(new BasicNameValuePair("communicationType", "0"));
		list.add(new BasicNameValuePair("voicemode", "2"));
		post.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
		HttpResponse res = client.execute(post);
		// String str = EntityUtils.toString(res.getEntity());
		WifiMusicInfos infos = parseXml(res.getEntity().getContent());
		return infos;
	}

	/****
	 * 解析盒子网络请求的xml
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static WifiMusicInfos parseXml(InputStream in) throws Exception {
		XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
		parser.setInput(in, "utf-8");
		int eventType = parser.getEventType();
		WifiMusicInfos info = null;
		out: while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("arg")) {
					String text = parser.nextText();
					LogUtil.e("voice--arg==" + text);
					String[] args = text.split("\\|\\|");
					if (args == null || args.length == 0)
						break out;
					if ("CommandPlayMedia".equals(args[0]) && !text.contains("http://")) {
						// 播放或下一首
						throw new Exception("CommandPlayMedia");
					}
					if ("CommandMediaControl".equals(args[0]) && args[1] != null && args[1].length() != 0) {
						// 其它控制
						throw new Exception("CommandMediaControl:" + args[1]);
					}
					LogUtil.e("命令==" + args[0]);
					if ("CommandPlayMedia".equals(args[0]) || "CommandStoryHezi".equals(args[0]) || "CommandJoke".equals(args[0])) {
						for (int i = 0; i < args.length; i++) {
							String arg = args[i];
							if (arg.contains("http://")) {
								info = new WifiMusicInfos();
								String[] arr = arg.split(",");
								for (int j = 0; j < arr.length; j++) {
									String s = arr[j];
									if (s.contains("http://")) {
										String nameAndAutor = s.substring(s.indexOf("[") + 1, s.indexOf("]"));
										NetResourceMusicInfo Info = null;
										if (nameAndAutor.contains("^")) {
											String[] a = nameAndAutor.split("\\^");
											Info = new NetResourceMusicInfo(a[1], a[0], "-1", s.substring(s.indexOf("http://"), s.length() - 1));
										} else {
											Info = new NetResourceMusicInfo(s.substring(0, s.indexOf("[")), nameAndAutor, "-1", s.substring(s.indexOf("http://"), s.length() - 1));
										}

										info.setNetMusicInfos(Info);
									}
								}
							}
						}
					}
				}

				break;
			}
			eventType = parser.next();
		}
		return info;
	}

}

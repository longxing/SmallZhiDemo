package com.voice.assistant.utl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.iii360.base.common.utl.LogManager;

public class HotelDetailInfoParser {
	public static interface IDataReceiveredListener {
		void onDataReceivered(String data) ;
	}
	public static void parserHotelInfo(final String urlStr,final IDataReceiveredListener dataReceivered ) {
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					LogManager.e("url is "+urlStr);
					URL url = new URL(urlStr);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(10000);
					conn.setReadTimeout(10000);
					InputStream in = conn.getInputStream();
					

					XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
					XmlPullParser parser=factory.newPullParser();
					parser.setInput(new InputStreamReader(in));
					
					int eventType=parser.getEventType();
					 
					while (eventType != XmlPullParser.END_DOCUMENT) {
						 if (eventType == XmlPullParser.START_DOCUMENT) {
							
						 }else if (eventType== XmlPullParser.START_TAG) {
							 String tagName = parser.getName();
							 if("info".equals(tagName)) {
								 if( dataReceivered != null ) {
									 dataReceivered.onDataReceivered(parser.nextText());
									 return ;
								 }
							 }
						 }else if(eventType== XmlPullParser.END_TAG) {
							
						 }
						 eventType = parser.next() ;
					}
					
				}catch(Exception e) {
					LogManager.printStackTrace(e);
				}
			}
		};
		new Thread(runnable).start();
	}
}

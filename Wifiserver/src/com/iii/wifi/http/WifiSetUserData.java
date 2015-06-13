package com.iii.wifi.http;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.text.TextUtils;

import com.iii.wifi.dao.info.WifiUserData;
import com.iii360.sup.common.utl.LocUtil;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.StringCallback;
import com.iii360.sup.common.utl.SystemUtil;

public class WifiSetUserData extends AbsHttpRequestListener {
	private StringBuffer urlBuffer;
	private LocUtil mLocUtil;
	private WifiUserData data;
	private String mCity;
	Context context;

	public WifiSetUserData(WifiUserData data, Context context) {
		// TODO Auto-generated constructor stub
		this.data = data;
		this.context = context;
		mLocUtil = new LocUtil();
		mLocUtil.getLocationInfo(new StringCallback() {
			@Override
			public void back(String paramString) {
				// TODO Auto-generated method stub
				mCity = paramString;
			}
		});
		// this.setUserData();
	}

	public String compsiteGetUserDataUrl(String phoneImei) {
		urlBuffer = new StringBuffer(HTTP_HEAD);
		urlBuffer.append("boxsysteminfo_querySystemInfo?imei=");
		urlBuffer.append(SystemUtil.getIMEI());
		urlBuffer.append("&phoneImei=");
		urlBuffer.append(phoneImei);

		return urlBuffer.toString().trim().replace("\n", "");
	}

	/**
	 * 查询数据的url格式：http://hezi.360iii.net:48080/webapi/
	 * boxsysteminfo_querySystemInfo
	 * ?imei=867936010109999&phoneImei=867936010109999
	 * 
	 * @param url
	 */
	public void getUserData(final String phoneImei) {
		// 查询数据的url格式：http://hezi.360iii.net:48080/webapi/boxsysteminfo_querySystemInfo?imei=867936010109999&phoneImei=867936010109999
		LogManager.e("============" + compsiteGetUserDataUrl(phoneImei));

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {

					HttpClient client = new DefaultHttpClient();
					HttpGet request = new HttpGet(compsiteGetUserDataUrl(phoneImei));
					HttpResponse response = client.execute(request);

					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						String strResult = EntityUtils.toString(response.getEntity());
						LogManager.i("请求成功！！！ (0错误 2没有此信息)===strResult：" + strResult);
						if (requestListener != null) {
							// 0错误 2没有此信息
							if (!"0".equals(strResult)) {
								requestListener.onRequestResult(true, strResult);
							} else {
								requestListener.onRequestResult(false, strResult);
							}
						}

						LogManager.i("strResult=" + strResult);
					} else {
						LogManager.i("请求失败！！！");
						if (requestListener != null) {
							requestListener.onRequestResult(false, null);
						}

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if (requestListener != null) {
						requestListener.onRequestResult(false, null);
					}
				}
			}
		}).start();
	}

	public void setUserData() {
		if (TextUtils.isEmpty(mCity)) {
			mLocUtil.getLocationInfo(new StringCallback() {
				@Override
				public void back(String paramString) {
					// TODO Auto-generated method stub
					mCity = paramString;
					request(compsiteUrl());
				}
			});
		} else {
			request(compsiteUrl());
		}
	}

	/**
	 * 设置数据的url格式：http://hezi.360iii.net:48080/webapi/
	 * boxsysteminfo_receiveSystemInfo
	 * ?gender=男&birth=1985-01-01&bloodType=N&marriage
	 * =N&child=无&eduBackGround=大专
	 * &city=上海市&brand=Huawei%20HUAWEI%20C8812&mac=c46ab7be07c4
	 * &imei=869630015356656
	 * &phoneImei=a0000043ee36a1&version=1&boxId=869630015356656
	 * 
	 * @return
	 */
	private String compsiteUrl() {
		// 设置数据的url格式：http://hezi.360iii.net:48080/webapi/boxsysteminfo_receiveSystemInfo?gender=男&birth=1985-01-01&bloodType=N&marriage=N&child=无&eduBackGround=大专&city=上海市&brand=Huawei%20HUAWEI%20C8812&mac=c46ab7be07c4&imei=869630015356656&phoneImei=a0000043ee36a1&version=1&boxId=869630015356656

		urlBuffer = new StringBuffer(HTTP_HEAD);
		urlBuffer.append("boxsysteminfo_receiveSystemInfo?");

		// 性别
		urlBuffer.append("gender=");
		urlBuffer.append(data.getSex());

		// 出身年月：1985-01-01
		if (!isEmpty(data.getBirth())) {
			urlBuffer.append("&birth=");
			urlBuffer.append(data.getBirth());
		}
		// 血型：默认为未知,则设置为N
		urlBuffer.append("&bloodType=");
		urlBuffer.append(data.getBoold());

		urlBuffer.append("&marriage=");

		String m = data.getMarriage();
		if (m != null) {
			// 婚姻：默认为保密,则设置N
			if (m.equals("已婚")) {
				urlBuffer.append("是");
			} else if (m.equals("未婚")) {
				urlBuffer.append("否");
			} else {
				urlBuffer.append(data.getMarriage());
			}
		} else {
			urlBuffer.append("N");
		}

		// 子女
		urlBuffer.append("&child=");
		urlBuffer.append(data.getChildren());

		// 教育程度
		urlBuffer.append("&eduBackGround=");
		urlBuffer.append(data.getEducation());

		// 所在的城市
		urlBuffer.append("&city=");
		urlBuffer.append(mCity);

		// 手机品牌
		urlBuffer.append("&brand=");
		urlBuffer.append(data.getBrand());

		// 盒子MAC
		urlBuffer.append("&mac=");
		urlBuffer.append(SystemUtil.getLocalMacAddress(context));

		// 盒子IMEI
		urlBuffer.append("&imei=");
		urlBuffer.append(SystemUtil.getIMEI());

		// 手机IMEI
		urlBuffer.append("&phoneImei=");
		urlBuffer.append(data.getImei());
		// 年龄
		if (!isEmpty(data.getAge())) {
			urlBuffer.append("&age=");
			urlBuffer.append(data.getAge());
		}
		// 星座
		if (!isEmpty(data.getConstellation())) {
			urlBuffer.append("&constellation=");
			urlBuffer.append(data.getConstellation());
		}
		urlBuffer.append("&version=");
		urlBuffer.append(SystemUtil.getVersionCode(context));

		urlBuffer.append("&boxId=");
		urlBuffer.append(SystemUtil.getIMEI());

		String url = urlBuffer.toString();
		url = url.replaceAll(" ", "%20");

		return url;
	}

	public boolean isEmpty(String str) {
		if (str == null || "".equals(str.trim()) || "null".equals(str.trim()))
			return true;
		return false;
	}

	private void request(final String urls) {
		LogManager.e("============" + urls);

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {

					HttpClient client = new DefaultHttpClient();
					HttpGet request = new HttpGet(urls);
					HttpResponse response = client.execute(request);

					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						String strResult = EntityUtils.toString(response.getEntity());
						LogManager.i("请求成功！！！ (0错误 1成功 3重复)===strResult：" + strResult);
						if (requestListener != null) {
							// 1)0错误 2)1成功 3)3重复
							if (!"0".equals(strResult)) {
								requestListener.onRequestResult(true, strResult);
							} else {
								requestListener.onRequestResult(false, strResult);
							}
						}

						LogManager.i("strResult=" + strResult);
					} else {
						LogManager.i("请求失败！！！");
						if (requestListener != null) {
							requestListener.onRequestResult(false, null);
						}

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if (requestListener != null) {
						requestListener.onRequestResult(false, null);
					}
				}
			}
		}).start();
	}
}

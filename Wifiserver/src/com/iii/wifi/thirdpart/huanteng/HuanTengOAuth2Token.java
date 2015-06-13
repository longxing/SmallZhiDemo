package com.iii.wifi.thirdpart.huanteng;

/***
 * 幻腾oauth2Token的json信息
 * 
 * @author Peter
 * @data 2015年5月12日下午2:44:45
 */
public class HuanTengOAuth2Token {
	private String access_token = null; // 访问令牌
	private String token_type = null; // 设备名称
	private String refresh_token = null; // 刷新令牌
	private String expires_in = null; // 几秒后过期
	private String timestamp = null; // 时间戳

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getToken_type() {
		return token_type;
	}

	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public String getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}

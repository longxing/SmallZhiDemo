package com.iii.wifi.dao.info;

public class WifiUserInfo{
	private String mName;
	private String mPassWord;
	private String encrypt;
	
	public String getmName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}
	public String getPassWord() {
		return mPassWord;
	}
	public void setPassWord(String passWord) {
		this.mPassWord = passWord;
	}
    public String getEncrypt() {
        return encrypt;
    }
    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
    }

}

package com.voice.assistant.main.newmusic;

public class WifiUserData {
	private String type = "";
	private String sex = "";
	private String birth = "";
	private String education = "";
	private String marriage = "";
	private String children = "";
	private String boold = "";
	private String imei = "";// 手机IMI码
	private String brand = "";// 手机品牌

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getMarriage() {
		return marriage;
	}

	public void setMarriage(String marriage) {
		this.marriage = marriage;
	}

	public String getChildren() {
		return children;
	}

	public void setChildren(String children) {
		this.children = children;
	}

	public String getBoold() {
		return boold;
	}

	public void setBoold(String boold) {
		this.boold = boold;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return type + "|" + sex + "|" + birth + "|" + education + "|" + marriage + "|" + children + "|" + boold + "|"
				+ imei + "|" + brand;
	}

	public void setFromString(String content) {
		if (content != null) {
			String[] values = content.split("\\|");
			if (values.length == 9) {
				type = values[0];
				sex = values[1];
				birth = values[2];
				education = values[3];
				marriage = values[4];
				children = values[5];
				boold = values[6];
				imei = values[7];
				brand = values[8];
			}
		}
	}

}

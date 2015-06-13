package com.iii.wifi.dao.info;

import java.util.ArrayList;
import java.util.List;

import com.voice.assistant.main.newmusic.NetResourceMusicInfo;

public class WifiMusicInfos {
    private String type;//操作类型，如设置，查询等
    private List<WifiMusicInfo> wifiInfos;
    private List<NetResourceMusicInfo> netMusicInfos;
    private int page;
    private int position;

    public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<WifiMusicInfo> getWifiInfos() {
        return wifiInfos;
    }

    public void setWifiInfos(List<WifiMusicInfo> wifiInfos) {
        this.wifiInfos = wifiInfos;
    }

    
    public void setWifiInfo(WifiMusicInfo wifiInfo) {
        if (this.wifiInfos == null) {
            this.wifiInfos = new ArrayList<WifiMusicInfo>();
        }
        this.wifiInfos.add(wifiInfo);
    }
    
    /**
     * 设置网络资源信息
     * @param netMusicInfos
     */
    public void setNetMusicInfos(NetResourceMusicInfo netMusicInfo){
        if (this.netMusicInfos == null) {
            this.netMusicInfos = new ArrayList<NetResourceMusicInfo>();
        }
        this.netMusicInfos.add(netMusicInfo);
    }
    
    /**
     * 获取网络资源信息
     * @param netMusicInfos
     */
    public List<NetResourceMusicInfo> getNetMusicInfos(){
       return this.netMusicInfos;
    }
    
    
}

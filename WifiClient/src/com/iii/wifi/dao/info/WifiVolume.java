package com.iii.wifi.dao.info;

public class WifiVolume {
    /**
     * 设置的音量
     */
    private int volume;
    /**
     * 盒子硬件当前的音量
     */
    private int currentVolume;
    /**
     * 盒子最大音量
     */
    private int maxVolume;
    private String type;

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getCurrentVolume() {
        return currentVolume;
    }

    public void setCurrentVolume(int currentVolume) {
        this.currentVolume = currentVolume;
    }

    public int getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(int maxVolume) {
        this.maxVolume = maxVolume;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}

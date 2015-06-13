package com.iii.wifi.manager.impl;

public abstract class AbsWifiResponseFactory {
    /**
     * 创建响应对象
     * @param cls
     * @return
     */
    public abstract <T extends AbsWifiResponse> T createResponse(String className);
}

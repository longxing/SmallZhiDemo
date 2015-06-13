package com.iii.wifi.http.parsrer;

public interface ParserData {
    /**
     * 解析json对象
     * @param json
     * @return
     * @throws Exception
     */
    public Object getParserData(String json) throws Exception;
}

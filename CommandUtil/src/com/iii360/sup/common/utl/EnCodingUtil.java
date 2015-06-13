package com.iii360.sup.common.utl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
/**
 * 
 * @author Jerome.Hu。
 *         EnCoding相关的工具类。
 *
 */
public class EnCodingUtil {
	/**
	 * 中文UTF转码
	 * @param src 原始中文。 
	 * @return UTF8转码后的中文。
	 */
	public static String getUtfString(String src) {
        String ret = src;
        if (src != null && !"".equals(src)) {
            try {
                ret = URLEncoder.encode(src, "utf-8");
            } catch (UnsupportedEncodingException e) {
                LogManager.printStackTrace(e);
            }
        }
        return ret;
    }
}

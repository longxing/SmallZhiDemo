package com.iii.wifi.util;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.iii360.sup.common.utl.ShellUtils;
import com.iii360.sup.common.utl.file.FileUtil;

public class SerialNumberUitls {

    /**
     * 读取序列号
     * 
     * @return
     * @throws IOException
     * @throws InterruptedException 
     */
    public static String readSerialNumber() throws IOException, InterruptedException {

        return ShellUtils.readSerialNumber();
    }

    /**
     * 写入序列号
     * 
     * @param sn
     * @return
     * @throws IOException
     * @throws InterruptedException 
     */
    public static String writeSerialNumber(Context context,String sn) throws IOException, InterruptedException {

        return ShellUtils.writeSerialNumber(context,sn);
    }

}

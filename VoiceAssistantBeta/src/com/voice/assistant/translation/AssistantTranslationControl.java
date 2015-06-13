//ID20120813001 zhanglin begin
package com.voice.assistant.translation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.iii360.base.common.utl.LogManager;
import com.voice.common.util.ScreenManager;

public class AssistantTranslationControl {
    private ScreenManager screenManager;

    public String getResult(String content, String lang) {

        String utfString = "";
        try {
            utfString = URLEncoder.encode(content, "utf-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
        	LogManager.printStackTrace(e);
        }

        String langText = getLangText(utfString, lang);
        return langText;
        // }

        /*
         * Intent it = new Intent(context,AssistantTranslationActivity.class );
         * Bundle b=new Bundle(); b.putString("china",content);
         * b.putString("other", langText); b.putString("langName", langName);
         * it.putExtras(b);
         */

    }

    private String getLangText(String utfString, String lang) {
        String text = "";
        String langText = "";
        try {
            URL theURL = new URL("http://api.microsofttranslator.com/v2/Http.svc/Translate?appId=3DFB58FA8FCBA68CDBDB3AC334519A862D993E5A&text="
                    + utfString + "&from=zh-CHS&to=" + lang);
            // url是字符串数组，如“http:/202.202.50.2/test.txt"
            URLConnection theUC = theURL.openConnection();
            theUC.connect();
            // 这语句很重要，决定与网络文件的连接是否成功，如不成功则语句会
            // 跳到异常处理处

            InputStreamReader _Input = new InputStreamReader(theUC.getInputStream(), "UTF-8");
            BufferedReader br = new BufferedReader(_Input);
            String line = "";

            while ((line = br.readLine()) != null) {
                text = text + line;
            }

            /*
             * InputStream in=theURL.openStream();//定义读入流in DataInputStream
             * data=new DataInputStream(new BufferedInputStream(in));
             * //定义数据流data String line; while((line=data.readLine())!=null) {
             * //当数据不为null时把一行数据赋值给字符串line text=text+line; }
             */

            int i = text.indexOf("/\">");
            int j = text.indexOf("</string>");
            langText = text.substring(i + 3, j);
        } catch (Exception ex) {
            langText = "翻译异常，请重新翻译";
        }

        return langText;
    }
}
// ID20120813001 zhanglin end
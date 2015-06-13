package com.iii360.external.recognise.engine;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.iii360.base.common.utl.LogManager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

/**
 * 辅助生成ebnf语法文件类 <br>
 * 需要权限: {@link android.Manifest.permission#READ_CONTACTS
 * android.permission.READ_CONTACTS}
 * {@link android.Manifest.permission#WRITE_EXTERNAL_STORAGE
 * android.permission.WRITE_EXTERNAL_STORAGE}
 */
public class GrammarHelper {

    public static final String notCnAndNumPattern = "[^\u4e00-\u9fa5^\u0030-\u0039]";

    public static final char[] NUMBER_CN = { '一', '二', '三', '四', '五', '六', '七', '八', '九', '零', '幺' };
    public static final int[] CN_NUMBER = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1 };

    private Context context;

    public GrammarHelper(Context context) {
        this.context = context;
    }

    /**
     * 获取联系人列表 返回格式：AA | BB | CC | DD
     */
    public String getConatcts() {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        StringBuilder contactSb = new StringBuilder();
        Set<String> strSet = new HashSet<String>();
        while (cursor.moveToNext()) {
            int nameIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
            String contact = cursor.getString(nameIndex);
            if (contact != null) {
                contact = contact.replaceAll(notCnAndNumPattern, "");

                // Log.i("contact", contact);

                if (contact != null && !contact.trim().equals("")) {
                	LogManager.e("contact is "+contact);
                    strSet.add(contact.toString());
                }
            }
        }
        Iterator<String> itr = strSet.iterator();
        while (itr.hasNext()) {
            contactSb.append(itr.next() + "\n");

            if (itr.hasNext())
                contactSb.append("|");

            itr.remove();
        }

        cursor.close();
        return contactSb.toString();
    }

    /**
     * 利用模板文件，生成ebnf格式grammar
     * 
     * @param contacts
     *            联系人列表 格式： AA | BB | CC
     * @param filename
     *            assets目录下模板文件名
     * @return
     */
    public String importContacts(String contacts, String filename) {
        BufferedReader br;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open(filename), Charset.forName("UTF-8")));
            String line = "";
            while ((line = br.readLine()) != null) {

                if (line.contains("#PERSON#")) {
                    line = line.replaceAll(" ", "");
                    line = line.replaceAll("#PERSON#;", "");
                    sb.append(line);
                    sb.append(contacts);
                    sb.append(";\n");
                    // Log.i("gh",sb.toString());

                } else {
                    sb.append(line + "\n");
                }

            }
            br.close();
        } catch (IOException e) {
//            e.printStackTrace();
            LogManager.printStackTrace(e);
        }

        return sb.toString();
    }

    /**
     * 将大写数字字串转换为阿拉伯数字字串 若输入字串中包含非大写数字字母则返回原串，不进行转换 eg:一二三四五六七八九零幺 -->
     * 12345678901
     * 
     * @param input
     *            汉字字串
     * @return 对应的阿拉伯数字字串或原串
     */
    public static String cnToUnmber(String input) {
        String ret;
        if (isNumberCnString(input)) {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < input.length(); i++) {
                s.append(numberCharCN2Arab(input.charAt(i)));
            }
            ret = s.toString();
        } else {
            ret = input;
        }
        return ret;

    }

    /**
     * 校验输入字串是否全为大写数字
     * 
     * @param input
     * @return
     */
    private static boolean isNumberCnString(String input) {
        boolean flag = true;
        for (int i = 0; i < input.length(); i++) {
            flag &= isNumberCnChar(input.charAt(i));
        }
        return flag;
    }

    /**
     * 校验输入字符是否为大写数字
     * 
     * @param charAt
     * @return
     */
    private static boolean isNumberCnChar(char charAt) {

        for (int i = 0; i < NUMBER_CN.length; i++) {
            if (NUMBER_CN[i] == charAt) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将一位中文数字转换为一位数字; eg: 一 返回 1;
     * 
     * @param onlyCNNumber
     * @return
     */
    public static int numberCharCN2Arab(char onlyCNNumber) {
        for (int i = 0; i < NUMBER_CN.length; i++) {
            if (onlyCNNumber == NUMBER_CN[i]) {
                return CN_NUMBER[i];
            }
        }
        return 0;
    }

}

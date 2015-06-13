package com.iii360.box.set;

import com.iii360.box.util.KeyList;

public class TTSVoice {

    public static String nameToIndex(String name) {
        for (int i = 0; i < KeyList.GKEY_VOICE_MAN_ARRAY.length; i++) {
            if (KeyList.GKEY_VOICE_MAN_ARRAY[i].equals(name)) {
                return i + "";
            }
        }

        return "0";
    }

    public static String indexToName(String index) {
        int id = Integer.parseInt(index);
        return KeyList.GKEY_VOICE_MAN_ARRAY[id];
    }
}

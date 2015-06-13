package com.voice.assistant.utl;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.sup.common.utl.SystemUtil;
import com.voice.assistant.main.KeyList;

public class TTStype {
   
    public static void checkTTSAPK(BaseContext mBaseContext) {

        mBaseContext.setPrefBoolean(KeyList.PKEY_TTS_IS_INSTALL_SHENGDA,
                SystemUtil.isInstallPackage(mBaseContext.getContext(), KeyList.PKEY_TTS_SHENGDATINGTING_PACKAGENAME));
        mBaseContext.setPrefBoolean(KeyList.PKEY_TTS_IS_INSTALL_XUNFEI,
                SystemUtil.isInstallPackage(mBaseContext.getContext(), KeyList.PKEY_TTS_XUNFEI_PACKAGENAME));

    }
}

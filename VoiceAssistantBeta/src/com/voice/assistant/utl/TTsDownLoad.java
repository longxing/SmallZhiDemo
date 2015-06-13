package com.voice.assistant.utl;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
//ID20121224001 zhanglin begin
import java.util.Random;

//ID20121224001 zhanglin end
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
//ID20130711001 hefeng begin
import android.view.KeyEvent;
import android.view.MotionEvent;
//ID20130711001 hefeng end
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.iii360.base.common.utl.BaseContext;
//import com.iii360.base.upgrade.DownloadManager;
import com.voice.assistant.main.KeyList;
import com.voice.assistant.main.R;

public class TTsDownLoad extends Dialog {
    public static String DEF_DOWNLOAD_SAMSUNG = "http://down.360iii.com/download/tts/samsung_tts.apk";
    public static String DEF_DOWNLOAD_JIETONG = "http://down.360iii.com/download/tts/jietong_tts.apk";
    public static String DEF_DOWNLOAD_TINGTING = "http://down.360iii.com/download/tts/shengda_tts.apk";
    public static String DEF_DOWNLOAD_IFLYTEK = "http://down.360iii.com/download/tts/xunfei_tts.apk";
    private static String FLODRE_PATH = "/sdcard/Download";
    private static String FLODER_PATH1 = "/sdcard/UCDownloads";
    private static String FLODER_PATH2 = "/sdcard/MxBrowser/Downloads";
    private static String FLODER_PATH3 = "/sdcard/QQBrowser/安装包";
    private static String FLODER_PATH4 = "/sdcard/baidu/flyflow/downloads";
    private static String FLODER_PATH5 = "/sdcard/VoiceAssistant";
    private static String DOWN_FLODERS[] = { FLODRE_PATH, FLODER_PATH1, FLODER_PATH2, FLODER_PATH3, FLODER_PATH4, FLODER_PATH5 };
    private Button mDownButton;
    private Button mCancelButton;
    private TextView mTVTitle;
    private TextView mTVContent;

    private onConfirmClick mClick;
    private String mTitle;
    private String mContent;

    private BaseContext mBaseContext;
    private static String[] mTingtingvalues = { "盛大播报引擎", "亲，检测到您已经下载了听听播报引擎,请点击安装", "听听中心可以让小智不需要网络也可以发出声音，节省流量，请下载并安装听听中心播报引擎.",
            KeyList.PKEY_TTS_SHENGDATINGTING_PACKAGENAME, KeyList.UMKEY_DOWNLOAD_TINGTING, DEF_DOWNLOAD_TINGTING };
    private static String[] mXunFeiValues = { "讯飞+语音引擎", "亲，为了节省流量和更好的用户体验,请点击安装后使用", "亲，为了节省流量和更好的用户体验,请在点击下载并安装语音包", KeyList.PKEY_TTS_XUNFEI_PACKAGENAME,
            KeyList.UMKEY_DOWNLOAD_XUNFEI, DEF_DOWNLOAD_IFLYTEK };

    public interface onConfirmClick {
        public void onConfirmClickLister();

        public void onDismissClickLister();
    }

    private Button.OnClickListener CancleClick = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mClick != null) {
                mClick.onDismissClickLister();
            }
            dismiss();
        }
    };

    public TTsDownLoad(Context context, onConfirmClick mConfirmClick, String title, String content) {
        super(context, R.style.dialog);
        mClick = mConfirmClick;
        mTitle = title;
        mContent = content;
        mBaseContext = new BaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.widget_downlocaltts);
//
//        mDownButton = (Button) findViewById(R.id.btnttsConfirm);
//        mCancelButton = (Button) findViewById(R.id.btnttsCancel);
//        mTVContent = (TextView) findViewById(R.id.tvttsshow);
//        mTVTitle = (TextView) findViewById(R.id.tvttstitle);
        if (mTitle != null) {
            mTVTitle.setText(Html.fromHtml(mTitle));
            mTVContent.setText(Html.fromHtml(mContent));
        }

        mDownButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mClick != null) {
                    mClick.onConfirmClickLister();
                }
                dismiss();
            }
        });
        mCancelButton.setOnClickListener(CancleClick);

    }

    @Override
    public void onBackPressed() {
        CancleClick.onClick(null);
    }

    public void setConfirmText(String text) {
        if (mDownButton != null) {
            mDownButton.setText(text);
        }
    }

    public void setCancelText(String text) {
        if (mCancelButton != null) {
            mCancelButton.setText(text);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public static TTsDownLoad getDowndia(final Context context, final String[] args) {
        TTsDownLoad tTsDownLoad;
        final String apkfile = checkAPK(args[3], context);
        if (apkfile == null) {
            tTsDownLoad = new TTsDownLoad(context, new onConfirmClick() {

                @Override
                public void onDismissClickLister() {

                }

                @Override
                public void onConfirmClickLister() {
                    String value = new BaseContext(context).getGlobalString(args[4], args[5]);
                    if (value == null || value.equals("")) {
                        return;
                    }
                    if (value.endsWith("apk")) {
                        // Intent intent = new Intent(context,
                        // UpgradeService.class);
                        // intent.putExtra("title", args[0]);
                        // intent.putExtra("url", value);
                        // intent.putExtra("new", true);
                        // intent.putExtra("id", new
                        // Random().nextInt(100000000));
                        // context.startService(intent);
//                        DownloadManager downloadManager = new DownloadManager(context, value.substring(value.lastIndexOf("/") + 1), value);// 第二个参数是文件名称
                                                                                                                                           // 第三个是下载路径
//                        downloadManager.downLoad();
//
                    } else {
                        Uri uri = Uri.parse(value);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }

                }

                // 亲，您当前正在使用的是在线语音播报，为了节省流量，请在下面点击下载听听中心
                // ID20120815002 zhanglin begin
            }, args[0], args[2]);
        } else {
            tTsDownLoad = new TTsDownLoad(context, new onConfirmClick() {

                @Override
                public void onDismissClickLister() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onConfirmClickLister() {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.fromFile(new File(apkfile));
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    context.startActivity(intent);

                }
            }, args[0], args[1]);
        }

        return tTsDownLoad;
    }

    public static TTsDownLoad getTingTingDownDia(final Context context) {
        return getDowndia(context, mTingtingvalues);
    }

    public static TTsDownLoad getXunFeiDownDia(final Context context) {
        return getDowndia(context, mXunFeiValues);
    }

    public static String checkAPK(String pacagename, Context context) {
        FilenameFilter mFilenameFilter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                // TODO Auto-generated method stub
                if (filename.endsWith("apk")) {
                    return true;
                }
                return false;
            }
        };
        PackageManager pm = context.getPackageManager();
        for (String floder : DOWN_FLODERS) {
            File f = new File(floder);
            if (f.exists() && f.isDirectory()) {

                File files[] = f.listFiles(mFilenameFilter);
                if (files != null) {
                    for (File apkfile : files) {

                        PackageInfo packageInfo = pm.getPackageArchiveInfo(apkfile.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
                        if (packageInfo != null) {
                            String apkname = packageInfo.packageName;
                            if (apkname.equals(pacagename)) {
                                return apkfile.getAbsolutePath();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}

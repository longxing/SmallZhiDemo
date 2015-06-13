package com.iii360.box;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.iii.wifi.dao.info.BoxModeEnum;
import com.iii.wifi.dao.info.WifiBoxModeInfo;
import com.iii.wifi.dao.manager.WifiCRUDForBoxMode;
import com.iii.wifi.dao.manager.WifiCRUDForBoxMode.ResultForBoxModeListener;
import com.iii360.box.adpter.ModeDetailListApdater;
import com.iii360.box.adpter.ModeDetailListApdater.SelectListener;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.IView;

public class MainModeDetailActivity extends BaseActivity implements IView {
    private TextView mBackIb;
    private BoxModeEnum mBoxModeEnum;
    /**
     * 
     * [{1=开/关客厅空调}, {2=开/关客厅电视机}] id为控制的ID
     * 
     */
    private ArrayList<Map<Integer, String>> mDetailList;//详细数据：id+打开客厅空调
    private TextView mBoxModeTitleTv;
    private ListView mModeDetailLv;
    private ModeDetailListApdater mModeDetailListApdater;
    private WifiCRUDForBoxMode mWifiCRUDForBoxMode;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mode_detail);
        initViews();
        initDatas();
    }

    @Override
    public void initViews() {
        // TODO Auto-generated method stub
        mBackIb = (TextView) findViewById(R.id.head_left_textview);
        mBoxModeTitleTv = (TextView) findViewById(R.id.head_title_tv);
        mModeDetailLv = (ListView) findViewById(R.id.main_mode_detail_list);
    }

    @Override
    public void initDatas() {
        // TODO Auto-generated method stub
        Intent intent = getIntent();
        if (intent != null) {
            mBoxModeEnum = (BoxModeEnum) intent.getSerializableExtra(KeyList.IKEY_BOXMODE_ENUM);
            mDetailList = (ArrayList<Map<Integer, String>>) intent.getSerializableExtra(KeyList.IKEY_BOXMODE_DETAIL_DATA);
        }

        getModeData();

        mModeDetailListApdater = new ModeDetailListApdater(context, mDetailList, mSelectList);
        mModeDetailListApdater.setSelectListener(new SelectListener() {
            @Override
            public void onResult(String result) {
                // TODO Auto-generated method stub
                //存储选中的拼接id字符串
                setPrefString(mBoxModeEnum.toString(), result);
            }
        });
        mModeDetailLv.setAdapter(mModeDetailListApdater);

        mBoxModeTitleTv.setText(mBoxModeEnum.getValue());
        mBackIb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                sendToServer();
                finish();
            }
        });
    }

    /**
     * 保存发送设置的数据
     */
    private synchronized void sendToServer() {
        mSelectStr = getPrefString(mBoxModeEnum.toString());
        mWifiCRUDForBoxMode = new WifiCRUDForBoxMode(getBoxIp(), getBoxTcpPort());
        mWifiCRUDForBoxMode.setBoxMode(mBoxModeEnum.getValue(), mSelectStr, new ResultForBoxModeListener() {
            @Override
            public void onResult(String type, String errorCode, List<WifiBoxModeInfo> infos) {
                // TODO Auto-generated method stub
                if (WifiCRUDUtil.isSuccessAll(errorCode)) {
                    LogManager.i("send box mode data ok");
                } else {
                    LogManager.i("send box mode data error");
                }
            }
        });
    }

    /**
     * 选中的数据id
     */
    private Set<Integer> mSelectList;
    /**
     * 选中的id拼接字符串
     */
    private String mSelectStr;

    private void getModeData() {
        //存储模式的格式为：MODE_GO_HOME 1||2||3   模式名称+控制设备ID

        //存储设备的ID:1||2||3
        mSelectStr = getPrefString(mBoxModeEnum.toString());
        //配置的设备ID
        mSelectList = new HashSet<Integer>();
        if (!TextUtils.isEmpty(mSelectStr)) {
            String[] selects = mSelectStr.split(KeyList.SEPARATOR_ACTION_SUBLIT);
            for (String s : selects) {
                mSelectList.add(Integer.parseInt(s));
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            sendToServer();
            return true;
        }
        return false;
    }

}

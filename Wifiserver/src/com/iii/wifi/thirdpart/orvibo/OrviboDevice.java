package com.iii.wifi.thirdpart.orvibo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.iii.wifi.thirdpart.inter.HFResultListener;
import com.iii.wifi.util.BasePreferences;
import com.iii.wifi.util.HexUtils;
import com.iii.wifi.util.KeyList;
import com.iii.wifi.util.WaitUtil;
import com.iii360.sup.common.utl.LogManager;
import com.orvibo.lib.wiwo.bo.Device;
import com.orvibo.lib.wiwo.core.WiwoService;
import com.orvibo.lib.wiwo.data.DBHelper;
import com.orvibo.lib.wiwo.i.AlloneControlResult;
import com.orvibo.lib.wiwo.i.WifiSocketControlResult;
import com.orvibo.lib.wiwo.model.AlloneControl;
import com.orvibo.lib.wiwo.model.DeviceManage;
import com.orvibo.lib.wiwo.model.InfraredLearn;
import com.orvibo.lib.wiwo.model.InfraredLearn.InfraredLearnResult;
import com.orvibo.lib.wiwo.model.Login;
import com.orvibo.lib.wiwo.model.Login.LoginListener;
import com.orvibo.lib.wiwo.model.SearchDevice;
import com.orvibo.lib.wiwo.model.SearchDevice.SearchDeviceListener;
import com.orvibo.lib.wiwo.model.SocketControl;
import com.orvibo.lib.wiwo.util.LibLog;

/**
 * wiwo_lib_v28.jar/lsd-lib-onekey-smartlink.jar
 * 
 * @author Administrator
 * 
 */
public class OrviboDevice {
    private static final String TAG = "Orvibo ";

    private Context context;
    private SocketControl mSocketControl;
    private List<Device> mDevices;
    private SearchDevice mSearchDevice = new SearchDevice();
    private OrviboDao mOrviboDao;
    private boolean enable = false;
    private static OrviboDevice orviboDevice;
    private BasePreferences mPreferences;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public static OrviboDevice getInstance(Context context) {
        if (orviboDevice == null) {
            synchronized (OrviboDevice.class) {
                if (orviboDevice == null) {
                    orviboDevice = new OrviboDevice(context);
                }
            }
        }

        return orviboDevice;
    }

    private OrviboDevice() {
        // TODO Auto-generated constructor stub
    }

    private OrviboDevice(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.mOrviboDao = new OrviboDao(context);
        this.mPreferences = new BasePreferences(context);
        init();
    }

    public void init() {
        try {
            LibLog.showLog = true;// true显示log
           
            context.startService(new Intent(context, WiwoService.class));
            // login
            Login login = new Login(context);
            login.setLoginDataListener(new LoginListener() {

                @Override
                public void onLoginResult(String uid, int result) {
                    LogManager.i(TAG + "onLoginResult()-uid:" + uid + ",result:" + result);
                    setEnable(true);
                }

                @Override
                public void onLoginFinish() {
                    LogManager.i(TAG + "onLoginFinish()");
                    setEnable(true);
                }
            });
            login.login();

            mSocketControl = new SocketControl(context);
            mSocketControl.setOnControlResultListener(new WifiSocketControlResult() {

                @Override
                public void onFailure(String uid, int result) {
                    // TODO Auto-generated method stub
                    LogManager.e(TAG + "onFailure()-uid:" + uid + ",result:" + result);
                }

                @Override
                public void onSuccess(String uid, int onOff) {
                    // TODO Auto-generated method stub
                    LogManager.i(TAG + "onSuccess()-uid:" + uid + ",onOff:" + onOff);
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LogManager.e(TAG + " error=" + Log.getStackTraceString(e));
        }
    }

    private long start;
    private boolean mIsSearch = false;

    /**
     * search devices
     * 
     */
    public void search() {
        try {
            if (mIsSearch) {

                //搜索超过12s
                long start = mPreferences.getPrefLong(KeyList.PKEY_ORVIBO_START_TIME, System.currentTimeMillis());
                if (System.currentTimeMillis() - start > 12000) {
                    mIsSearch = false;
                }

                return;
            }

            mPreferences.setPrefLong(KeyList.PKEY_ORVIBO_START_TIME, System.currentTimeMillis());
            mIsSearch = true;

            DBHelper.getInstance(context).clear(context);
//            DBHelper.GetInstance(context).clear(context);
            start = System.currentTimeMillis();

            LogManager.d(TAG + "search()");
            mSearchDevice.setOnSearchDeviceListener(new SearchDeviceListener() {

                @Override
                public void onSearchFinish(List<Device> devices) {
                    LogManager.i(TAG + "onSearchFinish()-devices:" + devices);
                    mDevices = new DeviceManage(context).getAllDevices();
                    mIsSearch = false;
                    
                    LogManager.i(TAG + "onSearchFinish()-devices:" + mDevices);
                    mIsSearch = false;

                    if (mDevices != null && !mDevices.isEmpty()) {
                        addToDB(mDevices);
                        LogManager.e(TAG + "search devices size:" + mDevices.size());
                    } else {
                        LogManager.e(TAG + "search devices size null");
                    }
                    LogManager.e(TAG + "searchDevice Time = " + (System.currentTimeMillis() - start) / 1000.0);
                    
//                    dealThread();
                }

                @Override
                public void onSearchError(int errorCode) {
                    LogManager.e(TAG + "onSearchError()-errorCode:" + errorCode);
                }

                @Override
                public void onSearch(String uid) {
                    // TODO Auto-generated method stub
                    LogManager.e(TAG + "onSearch()-uid:" + uid);
                }
            });
            
            mSearchDevice.search(context);
//            mSearchDevice.search(context, true);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mIsSearch = false;
            LogManager.e(TAG + " error=" + Log.getStackTraceString(e));
        }
    }

    private void dealThread() {
        try {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mDevices = new DeviceManage(context).getAllDevices();

                    WaitUtil.sleep(1000);

                    LogManager.i(TAG + "onSearchFinish()-devices:" + mDevices);
                    mIsSearch = false;

                    if (mDevices != null && !mDevices.isEmpty()) {
                        addToDB(mDevices);
                        LogManager.e(TAG + "search devices size:" + mDevices.size());
                    } else {
                        LogManager.e(TAG + "search devices size null");
                    }
                    LogManager.e(TAG + "searchDevice Time = " + (System.currentTimeMillis() - start) / 1000.0);
                }
            }).start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LogManager.e(TAG + " error=" + Log.getStackTraceString(e));
        }
    }

    private void addToDB(List<Device> devices) {
        for (Device device : devices) {
            List<Device> d = mOrviboDao.selectByUid(device.getUid());
            if (d == null || d.isEmpty()) {
                mOrviboDao.add(device);
            }
        }
    }

    public List<Device> getDeviceList() {
        if (mDevices == null) {
            mDevices = new ArrayList<Device>();
        }
        return mDevices;
    }

    /**
     * 控制开关
     * 
     * @param uid
     * @param open
     */
    public void controlOnOff(final String uid, final boolean open) {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (open) {
                        mSocketControl.on(uid);
                    } else {
                        mSocketControl.off(uid);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    LogManager.e(TAG + " error=" + Log.getStackTraceString(e));
                }
            }
        }.start();
    }

    /**
     * 控制红外指令
     * 
     * @param uid
     * @param command
     */
    public void sendHF(String uid, String command, final AlloneControlResult listener) {
        LogManager.i(TAG + "uid=" + uid + "||command=" + command);

        try {
            byte[] cmd = HexUtils.hexStr2Bytes(command);

            AlloneControl alloneControl = new AlloneControl(context);
            alloneControl.setOnAlloneControlResult(new AlloneControlResult() {

                @Override
                public void onFailure(String arg0, int arg1) {
                    // TODO Auto-generated method stub
                    if (listener != null) {
                        listener.onFailure(arg0, arg1);
                    }
                    LogManager.e(TAG + "send learn onFailure " + arg0 + "||" + arg1);
                }

                @Override
                public void onSuccess(String arg0) {
                    // TODO Auto-generated method stub
                    if (listener != null) {
                        listener.onSuccess(arg0);
                    }
                    LogManager.e(TAG + "send learn onSuccess " + arg0);
                }
            });
            alloneControl.infraredControl(uid, cmd);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            if (listener != null) {
                listener.onFailure(null, -1);
            }
            LogManager.e(TAG + " error=" + Log.getStackTraceString(e));
        }
    }

    public void learnHF(String uid, int deviceIndex, final HFResultListener listener) {
        LogManager.i(TAG + "uid=" + uid + "||deviceIndex=" + deviceIndex);
        try {
            InfraredLearn learn = new InfraredLearn(context, uid, deviceIndex);
            learn.setOnInfraredLearnResult(new InfraredLearnResult() {
                @Override
                public void onSuccess(String arg0, byte[] ir) {
                    // TODO Auto-generated method stub
                    LogManager.e(TAG + "learn onSuccess");
                    if (listener != null) {
                        String cmd = HexUtils.byte2HexStr(ir);
                        if (!TextUtils.isEmpty(cmd)) {
                            listener.onResult(true, cmd);
                        } else {
                            listener.onResult(true, "-1");
                        }
                    }
                }

                @Override
                public void onLearning() {
                    // TODO Auto-generated method stub
                    LogManager.e(TAG + "learn onLearning");
                }

                @Override
                public void onFailure(int arg0) {
                    // TODO Auto-generated method stub
                    LogManager.e(TAG + "learn onFailure arg0=" + arg0);
                    if (listener != null) {
                        listener.onResult(false, arg0 + "");
                    }
                }

                @Override
                public void onExitLearn() {
                    // TODO Auto-generated method stub
                    LogManager.e(TAG + "learn onExitLearn");
                }

            });
            learn.learn(null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LogManager.e(TAG + " error=" + Log.getStackTraceString(e));
        }

    }

}

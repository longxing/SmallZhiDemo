package com.iii.wifi.dao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iii.wifi.dao.imf.BroadlinkDeviceDao;
import com.iii.wifi.dao.imf.WifiControlDao;
import com.iii.wifi.dao.imf.WifiDeviceDao;
import com.iii.wifi.dao.imf.WifiModeDao;
import com.iii.wifi.dao.imf.WifiRoomDao;
import com.iii.wifi.thirdpart.orvibo.OrviboDao;
import com.iii360.sup.common.utl.LogManager;

public class DBHelper extends SQLiteOpenHelper {
	private static String mName = "VR";
	private static int mVersion = 3;
	private Context mContext;

	public DBHelper(Context context) {
		super(context, mName, null, mVersion);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(WifiControlDao.SQL_CREATE);
		db.execSQL(WifiRoomDao.SQL_CREATE);
		db.execSQL(WifiDeviceDao.SQL_CREATE);
		db.execSQL(WifiModeDao.SQL_CREATE);
		//liuwen begin
		db.execSQL(BroadlinkDeviceDao.SQL_CREATE);
		//liuwen end
		db.execSQL(OrviboDao.SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	    LogManager.i("oldVersion="+oldVersion+"||newVersion="+newVersion) ;
	    
        if (oldVersion == 1) {
            
            db.execSQL("ALTER TABLE wifidevice ADD devicetype varchar(50) DEFAULT('1')");
            db.execSQL("ALTER TABLE wifidevice ADD deviceModel varchar(50) DEFAULT('DEVICE_MODEL_WIFI_DOG')");
            
            db.execSQL("ALTER TABLE wificontrol ADD deviceModel varchar(50) DEFAULT('DEVICE_MODEL_WIFI_DOG')");
            
            db.execSQL(WifiModeDao.SQL_CREATE);
            db.execSQL(BroadlinkDeviceDao.SQL_CREATE);
            db.execSQL(OrviboDao.SQL_CREATE);
          
        }else if(oldVersion == 2){
            db.execSQL(OrviboDao.SQL_CREATE);
            
        }
	}

}

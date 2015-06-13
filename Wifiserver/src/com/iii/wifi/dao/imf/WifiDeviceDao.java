package com.iii.wifi.dao.imf;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.iii.wifi.dao.db.DBOperation;
import com.iii.wifi.dao.db.DBOperation.MappingCursor;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.inter.IWifiDeviceDao;
import com.iii.wifi.util.KeyList;
import com.iii.wifiserver.DogControllerService;

public class WifiDeviceDao implements IWifiDeviceDao {
	public static final String SQL_CREATE = "create table if not exists wifidevice (id integer primary key autoincrement,macadd varchar(200),deviceid varchar(200),roomid varchar(200),deviceName varchar(200),fitting varchar(200),devicetype varchar(50),deviceModel varchar(50))";
	private static final String SQL_INSERT = "insert into wifidevice (macadd,deviceid,roomid,deviceName,fitting,devicetype,deviceModel) values(?,?,?,?,?,?,?)";
	private static final String SQL_UPDATA = "update wifidevice set macadd = ?,deviceid = ?,roomid = ?,deviceName = ?,fitting = ?,devicetype= ?,deviceModel= ? where id=?";
	private static final String SQL_DELETE_BY_DEVICE_ID = "delete from wifidevice where id=? ";
	private static final String SQL_SELECT_BY_DEVICE_ID = "select * from wifidevice where id = ?";
	private static final String SQL_SELECT_ALL = "select * from wifidevice";
	private static final String SQL_SELECT_BY_ROOM_ID = "select * from wifidevice where roomid = ?";
	private static final String SQL_SELECT_BY_MAC_ADD = "select * from wifidevice where macadd = ?";
	private static final String SQL_SELECT_BY_ROOM_NAME_AND_ROOM_ID = "select * from wifidevice where macadd = ? and roomId = ?";
	private static final String SQL_SELECT_BY_ALL = "select * from wifidevice where macadd = ? and id = ? and roomid = ? and deviceName = ?";
	private MappingCursor<WifiDeviceInfo> mapping = new MappingCursor<WifiDeviceInfo>() {

		@Override
		public List<WifiDeviceInfo> mappingCursorToList(Cursor cursor) {
			// TODO Auto-generated method stub
			if (cursor == null) {
				return null;
			}
			List<WifiDeviceInfo> list = new ArrayList<WifiDeviceInfo>();
			while (cursor.moveToNext()) {
				WifiDeviceInfo info = new WifiDeviceInfo();
				info.setId(cursor.getInt(0));
				info.setMacadd(cursor.getString(1));
				info.setDeviceid(cursor.getString(0));
				info.setRoomid(cursor.getString(3));
				info.setDeviceName(cursor.getString(4));
				info.setFitting(cursor.getString(5));
				info.setDeviceType(Integer.parseInt(cursor.getString(6)));
				info.setDeviceModel(cursor.getString(7));
				list.add(info);
			}
			return list;
		}
	};
	private DBOperation db;
	private Context context;

	public WifiDeviceDao(Context context) {
		db = DBOperation.getIntence(context);
		this.context = context;
	}

	@Override
	public void add(WifiDeviceInfo info) {
		// TODO Auto-generated method stub
		db.add(SQL_INSERT, new Object[] { info.getMacadd(), info.getDeviceid(), info.getRoomid(), info.getDeviceName(), info.getFitting(), info.getDeviceType(), info.getDeviceModel() });
		DogControllerService.setHouseDevice(context, true);
		context.sendBroadcast(new Intent(KeyList.AKEY_COMMAND_CHANGE));
	}

	@Override
	public void deleteByDeviceId(String deviceId) {
		// TODO Auto-generated method stub
		db.delete(SQL_DELETE_BY_DEVICE_ID, new Object[] { deviceId });
		WifiControlDao control = new WifiControlDao(context);
		control.deleteByDeviceId(deviceId);
		DogControllerService.setHouseDevice(context, false);
		context.sendBroadcast(new Intent(KeyList.AKEY_COMMAND_CHANGE));
	}

	public void deleteByDeviceId2(String deviceId) {
		// TODO Auto-generated method stub
		db.delete(SQL_DELETE_BY_DEVICE_ID, new Object[] { deviceId });
		context.sendBroadcast(new Intent(KeyList.AKEY_COMMAND_CHANGE));
	}

	@Override
	public void updata(WifiDeviceInfo info) {
		// TODO Auto-generated method stub
		db.updata(SQL_UPDATA, new Object[] { info.getMacadd(), info.getId(), info.getRoomid(), info.getDeviceName(), info.getFitting(), info.getDeviceType(), info.getDeviceModel(), info.getId() });
		WifiControlDao control = new WifiControlDao(context);
		control.updateByDeviceId(String.valueOf(info.getRoomid()), String.valueOf(info.getId()));
		context.sendBroadcast(new Intent(KeyList.AKEY_COMMAND_CHANGE));
	}

	@Override
	public List<WifiDeviceInfo> selectByAll(WifiDeviceInfo info) {
		// TODO Auto-generated method stub
		return db.select(SQL_SELECT_BY_ALL, new String[] { info.getMacadd(), info.getId() + "", info.getRoomid(), info.getDeviceName() }, mapping);
	}

	@Override
	public List<WifiDeviceInfo> selectByAll() {
		// TODO Auto-generated method stub
		return db.select(SQL_SELECT_ALL, null, mapping);
	}

	@Override
	public List<WifiDeviceInfo> selectByDeviceId(String deviceId) {
		// TODO Auto-generated method stub
		return db.select(SQL_SELECT_BY_DEVICE_ID, new String[] { deviceId }, mapping);
	}

	@Override
	public List<WifiDeviceInfo> selectByRoomId(String roomId) {
		// TODO Auto-generated method stub
		return db.select(SQL_SELECT_BY_ROOM_ID, new String[] { roomId }, mapping);
	}

	@Override
	public List<WifiDeviceInfo> selectByDeviceName(String macadd, String roomId) {
		// TODO Auto-generated method stub
		return db.select(SQL_SELECT_BY_ROOM_NAME_AND_ROOM_ID, new String[] { macadd, roomId }, mapping);
	}

	@Override
	public List<WifiDeviceInfo> selectByMacAdd(String macAdd) {
		// TODO Auto-generated method stub
		return db.select(SQL_SELECT_BY_MAC_ADD, new String[] { macAdd }, mapping);
	}

}

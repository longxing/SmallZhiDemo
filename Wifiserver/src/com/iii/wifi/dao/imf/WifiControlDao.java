package com.iii.wifi.dao.imf;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.RemoteException;

import com.iii.wifi.dao.db.DBOperation;
import com.iii.wifi.dao.db.DBOperation.MappingCursor;
import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.inter.IWifiControlDao;
import com.iii.wifiserver.DogControllerService;
import com.iii360.sup.common.utl.LogManager;

public class WifiControlDao implements IWifiControlDao {
	
	private Context context = null;
	
	//liuwen begin
	public static final String SQL_CREATE = "create table if not exists wificontrol (id integer primary key autoincrement,corder varchar(200),dorder varchar(1000),roomId varchar(200),deviceid varchar(500),action varchar(50),frequency Integer,deviceModel varchar(50))";
	//end end
	private static final String SQL_INSERT = "insert into wificontrol (corder,dorder,roomId,deviceid,action,frequency,deviceModel) values(?,?,?,?,?,?,?)";
	private static final String SQL_UPDATA = "update wificontrol set corder = ?,dorder = ?,roomId = ?,deviceid = ?,action = ?,frequency = ? ,deviceModel = ? where id=?";
	private static final String SQL_UPDATA_BY_DEVICEID = "update wificontrol set roomId = ? where deviceid = ?";
	private static final String SQL_DELETE_BY_ID = "delete from wificontrol where id=?";
	private static final String SQL_DELETE_BY_DEVICE_ID = "delete from wificontrol where deviceid=?";
	private static final String SQL_SELECT_BY_ID = "select * from wificontrol where id=?  order by frequency desc";
	private static final String SQL_SELECT_BY_ROOM_ID = "select * from wificontrol where roomId = ? order by frequency desc";
	private static final String SQL_SELECT_BY_DEVICE_ID = "select * from wificontrol where deviceid = ? order by frequency desc";
	private static final String SQL_SELECT_ALL = "select * from wificontrol  order by frequency desc";
	private static final String SQL_SELECT_BY_ALL = "select * from wificontrol where corder = ? and dorder = ? and roomId = ? and deviceid = ? and action = ?";
	private static final String SQL_SELECT_BY_ROOM_DEVICE_ID_AND_ACTION = "select * from wificontrol where roomId = ? and deviceid= ? and action = ?";
	private static final String SQL_SELECT_BY_ROOM_DEVICE_ID = "select * from wificontrol where roomId = ? and deviceid= ?";
	
	private MappingCursor<WifiControlInfo> mapping = new MappingCursor<WifiControlInfo>() {

		@Override
		public List<WifiControlInfo> mappingCursorToList(Cursor cursor) {
			// TODO Auto-generated method stub
			if (cursor == null) {
				return null;
			}
			List<WifiControlInfo> list = new ArrayList<WifiControlInfo>();
			while (cursor.moveToNext()) {
				WifiControlInfo info = new WifiControlInfo();
				info.setId(cursor.getInt(0));
				info.setCorder(cursor.getString(1));
				info.setDorder(cursor.getString(2));
				info.setRoomId(cursor.getString(3));
				info.setDeviceid(cursor.getString(4));
				info.setAction(cursor.getString(5));
				info.setFrequency(cursor.getInt(6));
				//liuwen begin
				info.setDeviceModel(cursor.getString(7));
				//liuwen end
				list.add(info);
			}
			return list;
		}
	};
	private DBOperation db;
	private WifiRoomDao wifiRoomDao;

	public WifiControlDao(Context context) {
		this.context = context;
		db = DBOperation.getIntence(context);
		wifiRoomDao = new WifiRoomDao(context);
	}

	@Override
	public void add(WifiControlInfo info) {
		// TODO Auto-generated method stub
		info.setCorder(info.getAction());
		db.add(SQL_INSERT, new Object[] { info.getCorder(), info.getDorder(), info.getRoomId(), info.getDeviceid(),
				info.getAction(), info.getFrequency(),info.getDeviceModel() });
		try {
			DogControllerService.getCommandListen().onCommandChange();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void delete(int id) {
		// TODO Auto-generated method stub
		db.delete(SQL_DELETE_BY_ID, new Object[] { id });
		try {
			DogControllerService.getCommandListen().onCommandChange();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteByDeviceId(String id) {
		// TODO Auto-generated method stub
		db.delete(SQL_DELETE_BY_DEVICE_ID, new Object[] { id });
		try {
			DogControllerService.getCommandListen().onCommandChange();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void updata(WifiControlInfo info) {
		// TODO Auto-generated method stub
		info.setCorder(info.getAction());
		db.updata(SQL_UPDATA, new Object[] { info.getCorder(), info.getDorder(), info.getRoomId(), info.getDeviceid(),
				info.getAction(), info.getFrequency(),info.getDeviceModel(), info.getId() });
		try {

			DogControllerService.getCommandListen().onCommandChange();

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateByDeviceId(String roomid, String deviceid) {
		LogManager.e("roomid " + roomid + " deviceid " + deviceid);
		db.updata(SQL_UPDATA_BY_DEVICEID, new Object[] { roomid, deviceid });
		try {
			DogControllerService.getCommandListen().onCommandChange();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<WifiControlInfo> selectById(int id) {
		// TODO Auto-generated method stub
		return db.select(SQL_SELECT_BY_ID, new String[] { id + "" }, mapping);
	}

	@Override
	public List<WifiControlInfo> selectAll() {
		// TODO Auto-generated method stub
		return db.select(SQL_SELECT_ALL, null, mapping);
	}

	@Override
	public List<WifiControlInfo> selectByRoomId(String roomId) {
		// TODO Auto-generated method stub
		return db.select(SQL_SELECT_BY_ROOM_ID, new String[] { roomId }, mapping);
	}

	@Override
	public List<WifiControlInfo> selectByDeviceId(String deviceid) {
		// TODO Auto-generated method stub
		return db.select(SQL_SELECT_BY_DEVICE_ID, new String[] { deviceid }, mapping);
	}

	@Override
	public List<WifiControlInfo> selectByAll(WifiControlInfo info) {
		// TODO Auto-generated method stub
		return db.select(
				SQL_SELECT_BY_ALL,
				new String[] { info.getCorder(), info.getDorder(), info.getRoomId(), info.getDeviceid(),
						info.getAction() }, mapping);
	}

	@Override
	public List<WifiControlInfo> selectByRoomIdAndDeviceIdAndAction(WifiControlInfo info) {
		// TODO Auto-generated method stub
		return db.select(SQL_SELECT_BY_ROOM_DEVICE_ID_AND_ACTION, new String[] { info.getRoomId(), info.getDeviceid(),
				info.getAction() }, mapping);
	}
	
	@Override
	public List<WifiControlInfo> selectByRoomIdAndDeviceId(WifiControlInfo info) {
	    // TODO Auto-generated method stub
	    return db.select(SQL_SELECT_BY_ROOM_DEVICE_ID, new String[] { info.getRoomId(), info.getDeviceid(),
	            info.getAction() }, mapping);
	}

}

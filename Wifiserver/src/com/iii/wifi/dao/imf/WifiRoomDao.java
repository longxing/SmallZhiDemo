package com.iii.wifi.dao.imf;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.iii.wifi.dao.db.DBOperation;
import com.iii.wifi.dao.db.DBOperation.MappingCursor;
import com.iii.wifi.dao.info.WifiRoomInfo;
import com.iii.wifi.dao.inter.IWifiRoomDao;

public class WifiRoomDao implements IWifiRoomDao {
	public static final String SQL_CREATE = "create table if not exists wifiroom (id integer primary key autoincrement,roomname varchar(200),roomid varchar(200))";
	private static final String SQL_INSERT = "insert into wifiroom (roomname,roomid) values(?,?)";
	private static final String SQL_UPDATA = "update wifiroom set roomname = ?,roomid = ? where id=?";
	private static final String SQL_DELETE_BY_ROOM_ID = "delete from wifiroom where id=? ";
	private static final String SQL_SELECT_BY_ID = "select * from wifiroom where id=?";
	private static final String SQL_SELECT_BY_ROOM_ID = "select * from wifiroom where id = ?";
	private static final String SQL_SELECT_BY_ROOM_NAME = "select * from wifiroom where roomname = ?";
	private static final String SQL_SELECT_ALL = "select * from wifiroom";
	private static final String SQL_SELECT_BY_ALL = "select * from wifiroom where roomname = ? and id = ?";
	
	private MappingCursor<WifiRoomInfo> mapping = new MappingCursor<WifiRoomInfo>() {

		@Override
		public List<WifiRoomInfo> mappingCursorToList(Cursor cursor) {
			// TODO Auto-generated method stub
			if (cursor == null) {
				return null;
			}
			List<WifiRoomInfo> list = new ArrayList<WifiRoomInfo>();
			while (cursor.moveToNext()) {
				WifiRoomInfo info = new WifiRoomInfo();
				info.setId(cursor.getInt(0));
				info.setRoomName(cursor.getString(1));
				info.setRoomId(cursor.getString(0));
				list.add(info);
			}
			return list;
		}
	};
	private DBOperation db;

	public WifiRoomDao(Context context) {
		db = DBOperation.getIntence(context);
	}

	@Override
	public void add(WifiRoomInfo info) {
		// TODO Auto-generated method stub
		db.add(SQL_INSERT,
				new Object[] { info.getRoomName(), info.getRoomId() });
	}

	@Override
	public void deleteByRoomId(String roomId) {
		// TODO Auto-generated method stub
		db.delete(SQL_DELETE_BY_ROOM_ID, new Object[] { roomId });
	}

	@Override
	public void updata(WifiRoomInfo info) {
		// TODO Auto-generated method stub
		db.updata(
				SQL_UPDATA,
				new Object[] { info.getRoomName(), info.getId(),
						info.getId() });
	}

	@Override
	public List<WifiRoomInfo> selectByAll() {
		// TODO Auto-generated method stub
		return db.select(SQL_SELECT_ALL, null, mapping);
	}

	@Override
	public List<WifiRoomInfo> selectByAll(WifiRoomInfo info) {
		// TODO Auto-generated method stub
		return db.select(SQL_SELECT_BY_ALL, new String[]{info.getRoomName(),info.getRoomId()}, mapping);
	}

	@Override
	public List<WifiRoomInfo> selectByRoomId(String roomId) {
		// TODO Auto-generated method stub
		return db.select(SQL_SELECT_BY_ROOM_ID, new String[]{roomId}, mapping);
	}

	@Override
	public List<WifiRoomInfo> selectByRoomName(String roomName) {
		// TODO Auto-generated method stub
		return db.select(SQL_SELECT_BY_ROOM_NAME, new String[]{roomName}, mapping);
	}

}

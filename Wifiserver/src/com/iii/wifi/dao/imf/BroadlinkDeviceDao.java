package com.iii.wifi.dao.imf;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.iii.wifi.dao.db.DBOperation;
import com.iii.wifi.dao.db.DBOperation.MappingCursor;
import com.iii.wifi.thirdpart.broadlink.DeviceInfo;

public class BroadlinkDeviceDao {
	/*
	 *  mac: 设备的mac地址.
		type: 设备类型,目前支持的设备类型,SP1/SP2/RM1/RM2。(注:必须大写) 
		name: 设备名称,UTF8编码。
		lock: 设备当前锁定状态
		password: SP1/RM1认证所需密码,该值由设备生成,不能修改。
		id: SP2/RM2通信所需,该值由设备生成,不能修改。
		subdevice: 保留字段。置0
		key: SP2/RM2通信密钥,由设备产生,不能修改。
	 */
	public static final String SQL_CREATE = "create table if not exists broadlink (device_id integer primary key autoincrement,mac varchar(30),type varchar(30),name varchar(50),lock integer,password integer,id integer,subdevice integer,key varchar(50))";
	public static final String SQL_INSERT = "insert into broadlink(mac,type,name,lock,password,id,subdevice,key) values(?,?,?,?,?,?,?,?)";
	public static final String SQL_ALL_BY_MAC = "select * from broadlink where mac = ?";
	public static final String SQL_MAC = "select distinct * from broadlink";
	public static final String SQL_SELECT_BY_MAC = "select * from broadlink where mac = ?";
	
	private DBOperation db;

    public BroadlinkDeviceDao(Context context) {
        // TODO Auto-generated constructor stub
        db = DBOperation.getIntence(context);
    }
    
    public void add(DeviceInfo info) {
        db.add(SQL_INSERT, new Object[] { info.getMac() ,info.getType(), info.getName(),info.getLock(),info.getPassword(),info.getId(),info.getSubdevice(),info.getKey() });
    }
    
    public List<DeviceInfo> selectMac(){
    	return db.select(SQL_MAC, null, mapping);
    }
    
    public List<DeviceInfo> selectByMac(String mac){
        return db.select(SQL_SELECT_BY_MAC, new String[] { mac }, mapping);
    }
    
    public List<DeviceInfo> selectAll(ArrayList<String> macList){
    	List<DeviceInfo> deviceList = new ArrayList<DeviceInfo>();
    	for(int i = 0;i<macList.size();i++){
    		deviceList.add(db.select(SQL_ALL_BY_MAC,new String[] { macList.get(i)}));
    	}
    	return deviceList;
    }
    
    private MappingCursor<DeviceInfo> mapping = new MappingCursor<DeviceInfo>() {
        @Override
        public List<DeviceInfo> mappingCursorToList(Cursor cursor) {
            // TODO Auto-generated method stub
            if (cursor == null) {
                return null;
            }
            List<DeviceInfo> list = new ArrayList<DeviceInfo>();
            while (cursor.moveToNext()) {
            	DeviceInfo info = new DeviceInfo();
            	info.setMac(cursor.getString(1));
            	info.setType(cursor.getString(2));
            	info.setName(cursor.getString(3));
            	info.setLock(cursor.getInt(4));
            	info.setPassword(cursor.getInt(5));
            	info.setId(cursor.getInt(6));
            	info.setSubdevice(cursor.getInt(7));
            	info.setKey(cursor.getString(8));
                list.add(info);
            }
            return list;
        }
    };
}
	
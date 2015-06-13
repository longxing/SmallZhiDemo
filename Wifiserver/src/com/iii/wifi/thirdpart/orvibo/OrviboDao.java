package com.iii.wifi.thirdpart.orvibo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.iii.wifi.dao.db.DBOperation;
import com.iii.wifi.dao.db.DBOperation.MappingCursor;
import com.orvibo.lib.wiwo.bo.Device;

public class OrviboDao {
    public static final String SQL_CREATE = "create table if not exists orvibo (id integer primary key autoincrement,deviceIndex integer,name text,deviceType integer,value integer, rfKey integer,uid text,model text,lock integer,rfid integer,standardIRFlag integer,filename text)";
    public static final String SQL_INSERT = "insert into orvibo(deviceIndex ,name ,deviceType ,value , rfKey ,uid ,model ,lock ,rfid ,standardIRFlag ,filename ) values(?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_SELECT_ALL = "select * from orvibo";
    public static final String SQL_SELECT_BY_UID = "select * from orvibo where uid = ?";

    private DBOperation db;

    public OrviboDao(Context context) {
        db = DBOperation.getIntence(context);
    }

    public void add(Device device) {
        // TODO Auto-generated method stub
        db.add(SQL_INSERT, new Object[] { device.getDeviceIndex(), device.getName(), device.getDeviceType(),device.getValue(), device.getRfKey(), device.getUid(),
                device.getModel(), device.getLock(), device.getRfid(), device.getStandardIRFlag(), device.getFilename() });
    }

    public void update(Device device) {
        // TODO Auto-generated method stub
        db.updata(SQL_INSERT, new Object[] { device.getDeviceIndex(), device.getName(), device.getDeviceType(),device.getValue(), device.getRfKey(), device.getUid(),
                device.getModel(), device.getLock(), device.getRfid(), device.getStandardIRFlag(), device.getFilename() });
    }

    public List<Device> selectAll() {
        // TODO Auto-generated method stub
        return db.select(SQL_SELECT_ALL, null, mapping);
    }

    public List<Device> selectByUid(String uid) {
        return db.select(SQL_SELECT_BY_UID, new String[] { uid }, mapping);
    }

    private MappingCursor<Device> mapping = new MappingCursor<Device>() {

        @Override
        public List<Device> mappingCursorToList(Cursor cursor) {
            // TODO Auto-generated method stub
            if (cursor == null) {
                return null;
            }
            List<Device> list = new ArrayList<Device>();
            while (cursor.moveToNext()) {
                Device info = new Device();
                info.setDeviceIndex(cursor.getInt(1));
                info.setName(cursor.getString(2));
                info.setDeviceType(cursor.getInt(3));
                info.setValue(cursor.getInt(4));
                info.setRfKey(cursor.getInt(5));
                info.setUid(cursor.getString(6));
                info.setModel(cursor.getString(7));
                info.setLock(cursor.getInt(8));
                info.setRfid(cursor.getInt(9));
                info.setStandardIRFlag(cursor.getInt(10));
                info.setFilename(cursor.getString(11));

                list.add(info);
            }
            return list;
        }
    };
}

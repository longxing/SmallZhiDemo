package com.iii.wifi.dao.imf;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.iii.wifi.dao.db.DBOperation;
import com.iii.wifi.dao.db.DBOperation.MappingCursor;
import com.iii.wifi.dao.info.WifiBoxModeInfo;
import com.iii.wifi.util.KeyList;

public class WifiModeDao {
    public static final String SQL_CREATE = "create table if not exists wifimode (id integer primary key autoincrement,name varchar(200),controlid varchar(1000),action varchar(200))";
    private static final String SQL_INSERT = "insert into wifimode (id,name,controlid,action) values(?,?,?,?)";
    private static final String SQL_UPDATA = "update wifimode set controlid = ? where id=?";
    private static final String SQL_DELETE_BY_ID = "delete from wifimode where id=? ";
    private static final String SQL_SELECT_ALL = "select * from wifimode";
    private static final String SQL_SELECT_BY_NAME = "select * from wifimode where name = ?";
    private static final String SQL_SELECT_BY_ID = "select * from wifimode where id = ?";
    private static final String SQL_SELECT_BY_NAME_AND_ACTION = "select * from wifimode where name = ? and action = ?";
    private static final String SQL_SELECT_BY_BETWEEN_ID = "select * from wifimode where id between ?  and ?";
//    private static final String SQL_SELECT_BY_BETWEEN_ID = "select * from wifimode where id>=? and id<=?";
    //select * from wificontrol where id>=1 and id<=2;

    private DBOperation db;
    private List<WifiBoxModeInfo> list = new ArrayList<WifiBoxModeInfo>();
    private Context context;

    public WifiModeDao(Context context) {
        // TODO Auto-generated constructor stub
        db = DBOperation.getIntence(context);
        this.context = context;
    }

    public void add(WifiBoxModeInfo info) {
        db.add(SQL_INSERT, new Object[] { info.getId() ,info.getModeName(), info.getControlIDs(),info.getAction() });
        context.sendBroadcast(new Intent(KeyList.AKEY_COMMAND_CHANGE));
    }

    public void update(WifiBoxModeInfo info) {
        db.updata(SQL_UPDATA, new Object[] { info.getControlIDs(), info.getId() });
        context.sendBroadcast(new Intent(KeyList.AKEY_COMMAND_CHANGE));
    }

    public void delete(int modeId) {
        db.delete(SQL_DELETE_BY_ID, new Object[] { modeId });
        context.sendBroadcast(new Intent(KeyList.AKEY_COMMAND_CHANGE));
    }

    /**
     * @return ���д򿪹ر�ģʽ�������
     */
    public List<WifiBoxModeInfo> selectAll() {
        return db.select(SQL_SELECT_ALL, null, mapping);
    }

    public List<WifiBoxModeInfo> selectByName(String name) {
        return db.select(SQL_SELECT_BY_NAME, new String[] { name }, mapping);
    }
    
    public List<WifiBoxModeInfo> selectById(String id) {
        return db.select(SQL_SELECT_BY_ID, new String[] { id }, mapping);
    }
    
    public List<WifiBoxModeInfo> selectByNameAndAction(String name , String action) {
        return db.select(SQL_SELECT_BY_NAME_AND_ACTION, new String[] { name , action }, mapping);
    }
    
//    public List<WifiBoxModeInfo> selectByBetweenId(final int startId ,final int endId) {
//        return db.select(SQL_SELECT_ALL, null, new MappingCursor<WifiBoxModeInfo>() {
//            @Override
//            public List<WifiBoxModeInfo> mappingCursorToList(Cursor cursor) {
//                // TODO Auto-generated method stub
//                if (cursor == null) {
//                    return null;
//                }
//                list.clear();
//                while (cursor.moveToNext()) {
//                    if (cursor.getInt(0) >= startId && cursor.getInt(0) <= endId) {
//                        WifiBoxModeInfo info = new WifiBoxModeInfo();
//                        info.setId(cursor.getInt(0));
//                        info.setModeName(cursor.getString(1));
//                        info.setControlIDs(cursor.getString(2));
//                        info.setAction(cursor.getString(3));
//                        list.add(info);
//                    }
//                }
//                return list;
//            }
//        });
//    }
    
    /**
     * @return ��**ģʽ�������
     */
    public List<WifiBoxModeInfo> selectOpenModeData() {
        return db.select(SQL_SELECT_BY_BETWEEN_ID, new String[] { "100000" , "100006" }, mapping);
    }
    
    /**
     * @return �ر�**ģʽ�������
     */
    public List<WifiBoxModeInfo> selectCloseModeData() {
        return db.select(SQL_SELECT_BY_BETWEEN_ID, new String[] { "100007" , "100013" }, mapping);
    }
    
    private MappingCursor<WifiBoxModeInfo> mapping = new MappingCursor<WifiBoxModeInfo>() {
        @Override
        public List<WifiBoxModeInfo> mappingCursorToList(Cursor cursor) {
            // TODO Auto-generated method stub
            if (cursor == null) {
                return null;
            }
            list.clear();
            while (cursor.moveToNext()) {
                WifiBoxModeInfo info = new WifiBoxModeInfo();
                info.setId(cursor.getInt(0));
                info.setModeName(cursor.getString(1));
                info.setControlIDs(cursor.getString(2));
                info.setAction(cursor.getString(3));
                list.add(info);
            }
            return list;
        }
    };
}

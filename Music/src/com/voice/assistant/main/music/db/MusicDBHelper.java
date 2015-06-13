package com.voice.assistant.main.music.db;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iii360.base.common.utl.LogManager;
import com.iii360.sup.common.base.ErrCode;
import com.voice.assistant.main.music.MediaInfo;
import com.voice.assistant.main.newmusic.MusicInfo;
import com.voice.assistant.main.newmusic.MusicUtil;

public class MusicDBHelper extends SQLiteOpenHelper {
	private static final String TAG = "Music MusicDBHelper";

	public static final String SQL_CREATE = "create table if not exists mediaInfo (id integer primary key autoincrement,_isPlaying text,"
			+ "_isVideo text,_curPos integer,_size integer,_name text,_path text,_path2 text,_type text,_isFromNet text,_singerName text,_Id text not null unique,_updateTime long,_collect_state text)";
	private static final int pageSize = 10;
	public static final String SQL_SELECT_BY_ID = "select * from mediaInfo where _Id = ?";
	public static final String SQL_SELECT_BY_LOCAL = "select distinct _Id,_name,_singerName,_updateTime,_collect_state,_path from mediaInfo where _isFromNet=0 order by _updateTime desc limit "
			+ pageSize + " offset ?";

	public static final String SQL_INSERT = "insert into mediaInfo(_isPlaying, _isVideo ,_curPos ,_size ,_name ,_path ,_path2 ,_type ,_isFromNet,_singerName,_Id,_updateTime,_collect_state) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String SQL_UPDATA = "update mediaInfo set _isPlaying = ?,_isVideo = ?,_curPos = ?,_size = ?,_name = ?,_path= ?,_path2= ?,_type= ?,_isFromNet= ?,_singerName= ?,_Id= ?,_updateTime = ?, _collect_state= ? where _Id=?";

	private static String DB_NAME = "MD";
	private static int mVersion = 1;
	protected static String DB_PATH = "/mnt/sdcard/databases/";
	private SQLiteDatabase dbDatabase = null;

	public MusicDBHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context, DB_NAME, null, mVersion);
		if(getWritableDB()!=ErrCode.XOK){
			return;
		}
		if (tabbleIsExist("mediaInfo") && dbDatabase.getVersion() != mVersion) {
			updateSqlLite(dbDatabase);
		} else {
			dbDatabase.execSQL(SQL_CREATE);
			dbDatabase.setVersion(mVersion);
		}
	}

	private void updateSqlLite(SQLiteDatabase sldb) {
		try {
			if (!checkColumnExists(sldb, "mediaInfo", "_updateTime")) {
				sldb.execSQL("ALTER TABLE mediaInfo ADD COLUMN _updateTime long");
			} else {
				return;
			}
			if (!checkColumnExists(sldb, "mediaInfo", "_collect_state")) {
				sldb.execSQL("ALTER TABLE mediaInfo ADD COLUMN _collect_state text");
			} else {
				return;
			}
			sldb.setVersion(mVersion);
		} catch (Exception e) {
			LogManager.e(e.toString());
		}
	}

	private boolean checkColumnExists(SQLiteDatabase db, String tableName, String columnName) {
		boolean result = false;
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select * from sqlite_master where name = ? and sql like ?", new String[] { tableName, "%" + columnName + "%" });
			result = null != cursor && cursor.moveToFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private boolean tabbleIsExist(String tableName) {
		boolean result = false;
		if (tableName == null) {
			return false;
		}
		Cursor cursor = null;
		try {
			String sql = "select count(*) as c from  sqlite_master where type ='table' and name ='" + tableName.trim() + "' ";
			if (getWritableDB()!= ErrCode.XOK) {
				return false;
			}
			cursor = dbDatabase.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			return false;
		}
		return result;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		if (getWritableDB() !=ErrCode.XOK) {
			return;
		}
		dbDatabase.execSQL(SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public void add(MediaInfo info) {
		try {
			if (getWritableDB() !=ErrCode.XOK) {
				return;
			}
			dbDatabase.execSQL(
					SQL_INSERT,
					new Object[] { info._isPlaying, info._isVideo, info._curPos, info._size, info._name, info._path, info._path2, info._type, info._isFromNet, info._singerName, info._Id,
							info._updateTime, info._collect_state });
			dbDatabase.close();
		} catch (Exception e) {
			LogManager.e(TAG, e.toString() + "this music info exist already:_id=" + info._Id);
		}

	}

	public void addCheck(MediaInfo info) {
		if (null == info._Id) {
			LogManager.e(TAG, "null==info._Id....cant't add to db");
			return;
		}
		List<MediaInfo> list = selectById(info._Id);

		if (list == null || list.isEmpty()) {
			add(info);
		}
	}

	public void update(MediaInfo info) {
		if (getWritableDB() !=ErrCode.XOK) {
			return;
		}
		dbDatabase.execSQL(
				SQL_UPDATA,
				new Object[] { info._isPlaying, info._isVideo, info._curPos, info._size, info._name, info._path, info._path2, info._type, info._isFromNet, info._singerName, info._Id,
						info._updateTime, info._collect_state,info._Id });
		dbDatabase.close();
	}

	private void execSqlString(String sqlString) {
		if (getWritableDB() !=ErrCode.XOK) {
			return;
		}
		dbDatabase.execSQL(sqlString);
		dbDatabase.close();
	}

	public void deleteMusicInfoById(String id) {
		String SQL_DELETE_BY_ID = "delete from mediaInfo where _Id='" + id + "'";
		execSqlString(SQL_DELETE_BY_ID);
	}

	public void deleteMusicInfoByIds(String... ids) {
		String SQL_DELETE_BY_IDS = "delete from mediaInfo where _Id in (" + ids + ")";
		execSqlString(SQL_DELETE_BY_IDS);
	}

	private List<MediaInfo> list = new ArrayList<MediaInfo>();

	public List<MediaInfo> selectById(String _id) {
		if (getWritableDB() !=ErrCode.XOK) {
			list.clear();
			return list;
		}
		Cursor cursor = dbDatabase.rawQuery(SQL_SELECT_BY_ID, new String[] { _id });
		list.clear();

		while (cursor.moveToNext()) {

			MediaInfo info = new MediaInfo(null, null);

			info._isPlaying = strToBool(cursor.getString(1));
			info._isVideo = strToBool(cursor.getString(2));

			info._curPos = cursor.getInt(3);
			info._size = cursor.getInt(4);

			info._name = cursor.getString(5);
			info._path = cursor.getString(6);
			info._path2 = cursor.getString(7);
			info._type = cursor.getString(8);

			info._isFromNet = strToBool(cursor.getString(9));

			info._singerName = cursor.getString(10);
			info._Id = cursor.getString(11);

			list.add(info);
		}
		cursor.close();
		dbDatabase.close();
		return list;
	}

	public List<MediaInfo> selectByLocal(int page) {
		if (getWritableDB() !=ErrCode.XOK) {
			list.clear();
			return list;
		}
		Cursor cursor = dbDatabase.rawQuery(SQL_SELECT_BY_LOCAL, new String[] { (page - 1) * pageSize + "" });
		list.clear();
		HashMap<String, MusicInfo> goodMusic = MusicUtil.goodMusic;

		while (cursor.moveToNext()) {
			MediaInfo info = new MediaInfo(null, null);
			info._Id = cursor.getString(cursor.getColumnIndex("_Id"));
			info._name = cursor.getString(cursor.getColumnIndex("_name"));
			info._singerName = cursor.getString(cursor.getColumnIndex("_singerName"));
			info._path = "/" + cursor.getString(cursor.getColumnIndex("_path"));
			info._isFromNet = false;
			info._duration = "--:--";
			MusicInfo musicInfo = new MusicInfo(info._Id);
			musicInfo.mBaseNum = -100;
			info._musicInfo = musicInfo;
			if (goodMusic.containsKey(info._Id)) {
				info._isCollected = true;
			} else {
				info._isCollected = false;
			}
			list.add(info);
		}
		cursor.close();
		dbDatabase.close();

		return list;
	}

	private boolean strToBool(String param) {
		if (param.equals("true")) {
			return true;
		}
		return false;
	}

	public int getWritableDB() {
		if (dbDatabase == null || !dbDatabase.isOpen()) {
			dbDatabase = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
			if(dbDatabase==null){
				return ErrCode.XERR_DB_OPEN;
			}
		}
		return ErrCode.XOK;
	}

}

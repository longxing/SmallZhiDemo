package com.example.weather.db;

// ID20130724001 chenyuming	begin

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iii360.base.common.utl.LogManager;
import com.iii360.sup.common.base.ErrCode;

public class DBHelper extends SQLiteOpenHelper {

	protected static int Version = 5;
	protected static String DB_NAME = "VA";

	protected static String DB_PATH = "/mnt/sdcard/databases/";

	private SQLiteDatabase dbDatabase = null;

	public DBHelper(Context context) {

		super(context, DB_NAME, null, Version);
		if (getWritableDB() != ErrCode.XOK) {
			return;
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (getWritableDB() !=ErrCode.XOK) {
			return;
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	/**
	 * select count(*) direct return the Number of Values you want.
	 * 
	 * select * return the values you want select,so you need use getCount() to get Number.
	 */
	public int getCount(String countCurrentKnowledge) {
		int i = 0;
		try {
			Cursor cursor = dbDatabase.rawQuery(countCurrentKnowledge, null);
			cursor.moveToFirst();
			if (countCurrentKnowledge.contains("count(*)")) {
				i = cursor.getInt(0);
			} else {
				i = cursor.getCount();
			}
			cursor.close();
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}
		return i;
	}

	public Cursor rawQuery(String sql) {
		return dbDatabase.rawQuery(sql, null);
	}

	public Cursor rawQuery(String sql, String[] selectionArgs) {
		return dbDatabase.rawQuery(sql, selectionArgs);
	}

	public void execSQL(String sql) {
		dbDatabase.execSQL(sql);
	}

	public void execSQL(String sql, Object[] objects) {
		dbDatabase.execSQL(sql, objects);
	}

	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		return dbDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	}

	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
		return dbDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

	}

	public void start() {
		try {
			dbDatabase.beginTransaction();
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}
	}

	public void setFlag() {
		try {
			dbDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			LogManager.e("DBOperationKnowledge", "setFlag", e.toString());
		}
	}

	public void beginTransaction() {
		dbDatabase.beginTransaction();
	}

	public void setTransactionSuccessful() {
		dbDatabase.setTransactionSuccessful();
	}

	public void endTransaction() {
		dbDatabase.endTransaction();
	}

	public void stop() {
		try {
			dbDatabase.endTransaction();
		} catch (Exception e) {
			LogManager.e("DBOperationKnowledge", "stop", e.toString());
		}
	}

	public void update(String sql, Object[] objects) {
		try {
			dbDatabase.execSQL(sql, objects);
		} catch (Exception e) {
			LogManager.e("DBOperationKnowledge", "update", e.toString());
		}
	}

	public void insert(ContentValues cv, String table) {
		try {
			dbDatabase.insert(table, null, cv);
		} catch (Exception e) {
			LogManager.e("DBOperationKnowledge", "insert", e.toString());
		}

	}

	public void insert(String table, String nullColumnHack, ContentValues values) {
		dbDatabase.insert(table, nullColumnHack, values);
	}

	public void update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		dbDatabase.update(table, values, whereClause, whereArgs);
	}

	public void clear(String sql, String countCurrentKnowledge) {
		int maxId = getCount(countCurrentKnowledge);
		if (maxId > 0) {
			try {
				dbDatabase.execSQL(sql);
			} catch (Exception e) {
				LogManager.e("DBOperationKnowledge", "clear", e.toString());
			}
		}
	}

	public void doexec(String order) {
		try {

			dbDatabase.execSQL(order);

		} catch (Exception e) {
			// TODO: handle exception
			LogManager.printStackTrace(e);
		}
	}

	public int getWritableDB() {
		if (dbDatabase == null || !dbDatabase.isOpen()) {
			dbDatabase = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
			if (dbDatabase == null) {
				return ErrCode.XERR_DB_OPEN;
			}
		}
		return ErrCode.XOK;
	}

}

package com.iii360.box.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iii360.box.music.MusicSearchBean;

public class MusicSearchDao {
	private DBHelper dbHelper;

	public MusicSearchDao(Context context) {
		dbHelper = new DBHelper(context, DBHelper.curVersion);
	}

	public boolean isExistsKey(String key, SQLiteDatabase db) {
		Cursor c = null;
		try {
			db = dbHelper.getReadableDatabase();
			c = db.query(MusicSearchBean.TABLE_NAME, null, MusicSearchBean.MESSAGE_COLUMN_NAME + "=?", new String[] { key + "" }, null, null, null);
			if (c != null) {
				return c.moveToNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				c.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public long add(MusicSearchBean bean) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db == null) {
			return -1;
		}
		try {
			boolean exists = isExistsKey(bean.getMessage(), db);
			if (exists) {
				if (updateWithTime(bean, db) > 0) {
					return 0;
				}
				return -1;
			}
			ContentValues values = new ContentValues();
			values.put(MusicSearchBean.MESSAGE_COLUMN_NAME, bean.getMessage());
			values.put(MusicSearchBean.URL_COLUMN_NAME, bean.getUrl());
			values.put(MusicSearchBean.CREATE_TIME_COLUMN_NAME, bean.getCreateTime());

			return db.insert(MusicSearchBean.TABLE_NAME, null, values);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				db.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private int updateWithTime(MusicSearchBean bean, SQLiteDatabase db) {
		try {
			ContentValues values = new ContentValues();
			values.put(MusicSearchBean.CREATE_TIME_COLUMN_NAME, bean.getCreateTime());
			return db.update(MusicSearchBean.TABLE_NAME, values, MusicSearchBean.MESSAGE_COLUMN_NAME + "=?", new String[] { bean.getMessage() });
		} catch (Exception e) {
		}
		return 0;

	}

	public ArrayList<MusicSearchBean> getAllHistory() {
		ArrayList<MusicSearchBean> beans = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		if (db == null) {
			return null;
		}
		try {
			c = db.query(MusicSearchBean.TABLE_NAME, null, null, null, null, null, MusicSearchBean.CREATE_TIME_COLUMN_NAME + " desc");
			// c =
			// db.rawQuery("select * from "+MusicSearchBean.TABLE_NAME+" order by ",
			// null);
			if (c != null) {
				beans = new ArrayList<MusicSearchBean>();
				while (c.moveToNext()) {
					MusicSearchBean bean = new MusicSearchBean(c.getInt(c.getColumnIndex(MusicSearchBean.ID_COLUMN_NAME)), c.getString(c
							.getColumnIndex(MusicSearchBean.MESSAGE_COLUMN_NAME)), c.getString(c.getColumnIndex(MusicSearchBean.URL_COLUMN_NAME)),
							c.getLong(c.getColumnIndex(MusicSearchBean.CREATE_TIME_COLUMN_NAME)));
					beans.add(bean);
				}
				return beans;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				c.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				db.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean delete(int id) {
		SQLiteDatabase db = null;
		db = dbHelper.getWritableDatabase();
		int count = db.delete(MusicSearchBean.TABLE_NAME, MusicSearchBean.ID_COLUMN_NAME + "=?", new String[] { id + "" });
		if (count > 0) {
			return true;
		}
		try {
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean deleteAll() {
		SQLiteDatabase db = null;
		db = dbHelper.getWritableDatabase();
		int count = db.delete(MusicSearchBean.TABLE_NAME, null, null);
		if (count > 0) {
			return true;
		}
		try {
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public MusicSearchBean getHistoryById(int id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db == null) {
			return null;
		}
		Cursor c = db.query(MusicSearchBean.TABLE_NAME, null, MusicSearchBean.ID_COLUMN_NAME + "=?", new String[] { id + "" }, null, null, null);
		try {
			if (c != null) {
				if (c.moveToFirst()) {
					return new MusicSearchBean(c.getInt(c.getColumnIndex(MusicSearchBean.ID_COLUMN_NAME)), c.getString(c
							.getColumnIndex(MusicSearchBean.MESSAGE_COLUMN_NAME)), c.getString(c.getColumnIndex(MusicSearchBean.URL_COLUMN_NAME)),
							c.getLong(c.getColumnIndex(MusicSearchBean.CREATE_TIME_COLUMN_NAME)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				c.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				db.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}

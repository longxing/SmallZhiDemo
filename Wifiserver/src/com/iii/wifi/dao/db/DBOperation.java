/******************************************************************************************
 * @file DBOperation.java
 *
 * @brief  operation the data base
 *
 * Code History:
 *      [2015-04-07] xiaohua lu, code refactoring first version
 *
 * Code Review:
 *
 *********************************************************************************************/

package com.iii.wifi.dao.db;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.iii.wifi.thirdpart.broadlink.DeviceInfo;
import com.iii360.sup.common.base.ErrCode;
import com.iii360.sup.common.utl.LogManager;

public class DBOperation {
	// ///////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////// Member Variables
	// /////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////

	public static final String Tag = "[DBOperation]";

	public static DBOperation mDBOperation = null;
	public DBHelper mDBHelper = null;
	private SQLiteDatabase mDB = null;
	private static AtomicBoolean mAtomicBoolean = new AtomicBoolean(false);

	// ///////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////// Public Functions
	// /////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////

	public static DBOperation getIntence(Context context) {
		if (mDBOperation == null) {
			mDBOperation = new DBOperation(context);
		}
		return mDBOperation;
	}

	private DBOperation(Context context) {
		mDBHelper = new DBHelper(context);
	}

	/**
	 * @brief execute the SQLite opeartion
	 * @return: XOK--successful; esle is failure
	 * 
	 */

	public int execute(String sql, Object[] bindArgs) {
		int nRes = ErrCode.XOK;

		nRes = initWritableDatabase();
		if (ErrCode.XOK != nRes) {
			LogManager.e(Tag, "==>DBOperation::execute(): [ERROR] fail to get DB, nRes = " + nRes);
			return nRes;
		}

		try {
			mDB.execSQL(sql, bindArgs);
		} catch (SQLiteException ex) {
			LogManager.e(Tag, "==>DBOperation::execute(): [ERROR] fail to execSQL!");
			mDB.close();
			return nRes;
		}

		mDB.close();
		mAtomicBoolean.compareAndSet(true, false);
		return ErrCode.XOK;
	}

	public void add(String sql, Object[] bindArgs) {
		int mCode = execute(sql, bindArgs);
		if (mCode == ErrCode.XOK) {
			LogManager.i(Tag, "==>DBOperation::add(): success to add!");
		} else {
			LogManager.e(Tag, "==>DBOperation::add(): [ERROR] fail to add!");
		}
	}

	public void updata(String sql, Object[] bindArgs) {
		int mCode = execute(sql, bindArgs);
		if (mCode == ErrCode.XOK) {
			LogManager.i(Tag, "==>DBOperation::updata(): success to updata!");
		} else {
			LogManager.e(Tag, "==>DBOperation::updata(): [ERROR] fail to updata!");
		}
	}

	public void delete(String sql, Object[] bindArgs) {
		int mCode = execute(sql, bindArgs);
		if (mCode == ErrCode.XOK) {
			LogManager.i(Tag, "==>DBOperation::delete(): success to delete!");
		} else {
			LogManager.e(Tag, "==>DBOperation::delete(): [ERROR] fail to delete!");
		}
	}

	public <T> List<T> select(String sql, String[] values, MappingCursor<T> mapping) {
		List<T> list;
		Cursor cursor = null;
		int mCode = initReadableDatabase();
		if (mCode != ErrCode.XOK) {
			LogManager.e(Tag, "DBOperation::select(): [ERROR] fail to select DB !");
			return null;
		}
		try {
			cursor = mDB.rawQuery(sql, values);
			list = mapping.mappingCursorToList(cursor);
		} catch (Exception e) {
			// TODO jiangshenglan‘s bug
			list = new ArrayList<T>();
		} finally {
			try {
				cursor.close();
			} catch (Exception e) {

			}
			mDB.close();
			mAtomicBoolean.compareAndSet(true, false);
		}
		return list;
	}

	// liuwen begin
	public DeviceInfo select(String sql, String[] values) {
		DeviceInfo info = new DeviceInfo();
		Cursor cursor = null;
		int mCode = initReadableDatabase();
		if (mCode != ErrCode.XOK) {
			LogManager.e(Tag, "DBOperation::select(): [ERROR] fail to select DB !");
			return null;
		}

		try {
			cursor = mDB.rawQuery(sql, values);
			cursor.moveToNext();
			info.setMac(cursor.getString(1));
			info.setType(cursor.getString(2));
			info.setName(cursor.getString(3));
			info.setLock(cursor.getInt(4));
			info.setPassword(cursor.getInt(5));
			info.setId(cursor.getInt(6));
			info.setSubdevice(cursor.getInt(7));
			info.setKey(cursor.getString(8));
		} catch (Exception e) {
			// TODO jiangshenglan‘s bug
			info = new DeviceInfo();
		} finally {
			try {
				cursor.close();
			} catch (Exception e) {

			}
			mDB.close();
			mAtomicBoolean.compareAndSet(true, false);
		}
		return info;
	}

	// liuwen end

	public static interface MappingCursor<T> {
		public List<T> mappingCursorToList(Cursor cursor);
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////// Member Functions
	// /////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @brief open the writable database
	 * @return: XOK--successful; esle is failure
	 * 
	 */
	private int initWritableDatabase() {
		while (!mAtomicBoolean.compareAndSet(false, true)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LogManager.e(Tag, "==>DBOperation::initWritableDatabase(): [ERROR] fail to sleep thread!");
				return ErrCode.XERR_THREAD_SLEEP;
			}
		}

		try {
			mDB = mDBHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			LogManager.e(Tag, "==>DBOperation::initWritableDatabase(): [ERROR] throw exception of getWritableDatabase!");
			mDB = null;
		}
		if (null == mDB) {
			LogManager.e(Tag, "==>DBOperation::initWritableDatabase(): [ERROR] fail to getWritableDatabase!");
			return ErrCode.XERR_DB_OPEN;
		}

		while (mDB.isDbLockedByCurrentThread() && mDB.isDbLockedByOtherThreads()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LogManager.e(Tag, "==>DBOperation::initWritableDatabase(): [ERROR] fail to get DB right!");
				mDB.close();
				return ErrCode.XERR_THREAD_SLEEP;
			}
		}

		return ErrCode.XOK;
	}

	private int initReadableDatabase() {
		while (!mAtomicBoolean.compareAndSet(false, true)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LogManager.e(Tag, "==>DBOperation::initReadableDatabase(): [ERROR] fail to sleep thread!");
				return ErrCode.XERR_THREAD_SLEEP;
			}
		}
		try {
			mDB = mDBHelper.getReadableDatabase();
		} catch (SQLiteException ex) {
			LogManager.e(Tag, "==>DBOperation::initReadableDatabase(): [ERROR] throw exception of getReadableDatabase!");
			mDB = null;
		}
		if (null == mDB) {
			LogManager.e(Tag, "==>DBOperation::initReadableDatabase(): [ERROR] fail to getReadableDatabase!");
			return ErrCode.XERR_DB_OPEN;
		}
		while (mDB.isDbLockedByCurrentThread() && mDB.isDbLockedByOtherThreads()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LogManager.e(Tag, "DBOperation::initReadableDatabase(): [ERROR] fail to get DB right!");
				mDB.close();
				return ErrCode.XERR_THREAD_SLEEP;
			}
		}

		return ErrCode.XOK;
	}

}

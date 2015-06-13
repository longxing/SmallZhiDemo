package com.iii360.base.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	/**
	 * 
	 * @author Jerome.Hu
	 *
	 */
	public interface IDBHelperListener {
		public void onCreate(SQLiteDatabase db);
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) ;
	}
	
	private IDBHelperListener mListener ;
	
	public DBHelper(Context context,String name,int versionName,IDBHelperListener listener) {
	      super(context, name, null, versionName); 
	      mListener = listener ;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		mListener.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		mListener.onUpgrade(db, oldVersion, newVersion);
	}
}

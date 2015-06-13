package com.iii360.box.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static String name = "boxassistant.db";
	private static String CREATE_TABLE_SEARCH_HISTORY_SQL = "create table if not exists music_search_history_table(_id integer primary key autoincrement, msg text not null,msg_url text not null,create_time long not null)";
//	private static final String CREATE_TABLE_PIC_CACHE_SQL = "create table if not exists pic_cache_table(_id integer primary key autoincrement,pic_url text not null ,local_path text not null,constraint pic_cache_url_path_uk unique(pic_url) )";
	public static int curVersion = 1;

	public DBHelper(Context context, int version) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_SEARCH_HISTORY_SQL);
//		db.execSQL(CREATE_TABLE_PIC_CACHE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}

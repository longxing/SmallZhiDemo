package com.example.weather.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.example.weather.model.CityInfo;
import com.example.weather.model.ProvinceInfo;
import com.iii360.base.common.utl.LogManager;

public class CityDBHelper extends DBHelper {
	protected static String TABLE_PROVINCE = "provinces";
	protected static String TABLE_CITY = "citys";
	protected static String[] ALL_QUERY_CITY_INFO_FIELDS = new String[] { "_id", "city_num", "province_id", "name", };
	protected static String[] QUERY_PROVINCE_INFO_FIELDS = new String[] { "_id", "name", };

	public CityDBHelper(Context context) {
		super(context);
	}

	public ArrayList<CityInfo> queryAllCity(int provinceId) {
		ArrayList<CityInfo> citys = new ArrayList<CityInfo>();
		Cursor cursor = query(TABLE_CITY, ALL_QUERY_CITY_INFO_FIELDS, "province_id=?", new String[] { String.valueOf(provinceId) }, null, null, "_id asc");
		cursor.moveToFirst();

		if (cursor.getCount() > 0) {
			do {
				CityInfo info = new CityInfo();
				int idx = 0;
				info._id = cursor.getInt(idx++);
				info._cityCode = cursor.getString(idx++);
				info._provinceId = cursor.getInt(idx++);
				info._name = cursor.getString(idx++);
				int startInx = info._name.indexOf(".");
				if (startInx > 0) {
					info._name = info._name.substring(startInx + 1);
				}
				citys.add(info);
			} while (cursor.moveToNext());
		}

		cursor.close();
		return citys;
	}

	public ArrayList<ProvinceInfo> queryAllProvinces() {
		ArrayList<ProvinceInfo> provinces = new ArrayList<ProvinceInfo>();
		Cursor cursor = null;
		try {

			cursor = query(TABLE_PROVINCE, QUERY_PROVINCE_INFO_FIELDS, null, null, null, null, "_id asc");
			cursor.moveToFirst();

			if (cursor.getCount() > 0) {
				do {
					ProvinceInfo info = new ProvinceInfo();
					int idx = 0;
					info._id = cursor.getInt(idx++) - 1;
					info._name = cursor.getString(idx++);
					provinces.add(info);
				} while (cursor.moveToNext());
			}

		} catch (SQLiteException e) {

			LogManager.printStackTrace(e, "DBHelper", "createDataBase");
		} finally {
			if (cursor != null) {
				cursor.close();
			}

		}

		return provinces;
	}

	public CityInfo getCityInfoByName(String name) {
		CityInfo info = null;
		Cursor cursor = null;

		try {
			cursor = query(TABLE_CITY, ALL_QUERY_CITY_INFO_FIELDS, "(name=?) or (name like ?)", new String[] { name, "%." + name }, null, null, "_id ASC");
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				info = new CityInfo();
				int idx = 0;
				info._id = cursor.getInt(idx++);
				info._cityCode = cursor.getString(idx++);
				info._provinceId = cursor.getInt(idx++);
				info._name = cursor.getString(idx++);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();

		} finally {
			if (cursor != null) {
				cursor.close();
			}

		}
		return info;
	}

}

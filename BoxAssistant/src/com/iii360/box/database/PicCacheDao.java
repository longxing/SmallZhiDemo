package com.iii360.box.database;


public class PicCacheDao {
//	private DBHelper dbHelper;
//
//	public PicCacheDao(Context context) {
//		dbHelper = new DBHelper(context, DBHelper.curVersion);
//	}
//
//	public CacheBean getByURL(String url) {
//		synchronized (PicCacheDao.class) {
//			SQLiteDatabase db = null;
//			Cursor c = null;
//			try {
//				db = dbHelper.getReadableDatabase();
//				c = db.query(CacheBean.TABLE_NAME, null, CacheBean.PIC_URL_COLUMN_NAME + "=?", new String[] { url }, null, null, null);
//				if (c != null) {
//					if (c.moveToFirst()) {
//						CacheBean bean = new CacheBean();
//						bean.setId(c.getInt(c.getColumnIndex(CacheBean.ID_COLUMN_NAME)));
//						bean.setLocalPath(c.getString(c.getColumnIndex(CacheBean.LOCAL_PATH_COLUMN_NAME)));
//						bean.setPicUrl(c.getString(c.getColumnIndex(CacheBean.PIC_URL_COLUMN_NAME)));
//						return bean;
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					c.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				try {
//					db.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			return null;
//		}
//
//	}
//
//	public boolean isExists(String url) {
//		SQLiteDatabase db = null;
//		Cursor c = null;
//		try {
//			db = dbHelper.getReadableDatabase();
//			c = db.query(CacheBean.TABLE_NAME, null, CacheBean.PIC_URL_COLUMN_NAME + "=?", new String[] { url }, null, null, null);
//			if (c != null) {
//				return c.moveToFirst();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				c.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			try {
//				db.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return true;
//	}
//
//	public static final String PIC_IS_EXISTS = "PIC_IS_EXISTS";
//
//	public void add(CacheBean bean) throws Exception {
//		synchronized (PicCacheDao.class) {
//			if (isExists(bean.getPicUrl()))
//				throw new Exception(PIC_IS_EXISTS);
//			if (bean.getLocalPath() == null)
//				return;
//			SQLiteDatabase db = null;
//			try {
//				db = dbHelper.getWritableDatabase();
//				ContentValues values = new ContentValues();
//				values.put(CacheBean.LOCAL_PATH_COLUMN_NAME, bean.getLocalPath());
//				values.put(CacheBean.PIC_URL_COLUMN_NAME, bean.getPicUrl());
//				db.insert(CacheBean.TABLE_NAME, null, values);
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					db.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//
//		}
//	}
//
//	public void deleteByUrl(String url) {
//		synchronized (PicCacheDao.class) {
//			SQLiteDatabase db = null;
//			try {
//				db = dbHelper.getWritableDatabase();
//				db.delete(CacheBean.TABLE_NAME, CacheBean.PIC_URL_COLUMN_NAME + "=?", new String[] { url });
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					db.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
}

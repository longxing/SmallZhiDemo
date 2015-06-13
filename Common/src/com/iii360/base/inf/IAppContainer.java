package com.iii360.base.inf;

import java.util.HashMap;
import java.util.List;

import android.graphics.drawable.Drawable;

/**
 * 应用程序列表容器。Application对象需实现之。
 * @author Jerome.Hu
 *
 */
public interface IAppContainer {
		/**
		 * 
		 * @param list 应用程序列表
		 */
		public void setAppList(List<Object> list);
		/**
		 * 
		 * @return 应用程序列表
		 */
		public List<Object> getAppList();
		
		/**
		 * 
		 * @param list 应用标
		 */
		public void setAppDrawable(HashMap<String, Drawable> map);
		/**
		 * 
		 * @return 应用程序列表
		 */
		public HashMap<String, Drawable> getAppDrawable();
}

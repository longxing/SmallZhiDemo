package com.iii360.sup.inf;

public interface IGlobalValueOperation {
	/**
	 * 
	 * @param key 全局对象的key值
	 * @param value 全局对象key对应的value值
	 */
	 public void setGlobalBoolean(String key, boolean value) ;
	 /**
	  * 
	  * @param key 全局对象对应的key值
	  * @param value 全局对象对应的value值
	  */
	 public void setGlobalString(String key, String value) ;
	 /**
	  * 
	  * @param key 全局对象的key值
	  * @param value 全局对象key对应的value值
	  */
	 public void setGlobalInteger(String key, Integer value);
	 /**
		 * 
		 * @param key 全局对象的key值
		 * @param value 全局对象key对应的value值
		 */
	 public void setGlobalLong(String key, Long value);
	 /**
		 * 
		 * @param key 全局对象的key值
		 * @param value 全局对象key对应的value值
		 */
	 public void setGlobalObject(String key, Object value);
	 /**
		 * 
		 * @param key 全局对象的key值
		 * @param defVal 全局对象key对应的默认value值
		 * @return 对应的value值
		 */
	 public boolean getGlobalBoolean(String key, boolean defVal);
	 /**
	   	 * 
	   	 * @param key 全局对象的key值
	   	 * @param defVal 全局对象key对应的默认value值
	   	 * @return 对应的value值
	   	 */
	 public String getGlobalString(String key, String defVal) ;
	 /**
	   	 * 
	   	 * @param key 全局对象的key值
	   	 * @param defVal 全局对象key对应的默认value值
	   	 * @return 对应的value值
	   	 */
	 public int getGlobalInteger(String key, Integer defVal) ;
	 /**
	   	 * 
	   	 * @param key 全局对象的key值
	   	 * @param defVal 全局对象key对应的默认value值
	   	 * @return 对应的value值
	   	 */
	 public long getGlobalLong(String key, Long defVal) ;
	 /**
	   	 * 
	   	 * @param key 全局对象的key值
	   	 * @param defVal 全局对象key对应的默认value值
	   	 * @return 对应的value值
	   	 */
	 public Object getGlobalObject(String key, Object defVal) ;
	 /**
	   	 * 
	   	 * @param key 全局对象的key值
	   	 * @return key对应的value值
	   	 */
	 public boolean getGlobalBoolean(String key);
	 /**
	   	 * 
	   	 * @param key 全局对象的key值
	   	 * @return key对应的value值
	   	 */
	 public float getGlobalFloat( String key ) ;
	 /**
	   	 * 
	   	 * @param key 全局对象的key值
	   	 * @return key对应的value值
	   	 */
	 public String getGlobalString(String key) ;
	 /**
	   	 * 
	   	 * @param key 全局对象的key值
	   	 * @return key对应的value值
	   	 */
	 public int getGlobalInteger(String key) ;
	 /**
	   	 * 
	   	 * @param key 全局对象的key值
	   	 * @return key对应的value值
	   	 */
	 public long getGlobalLong(String key) ;
	 /**
	   	 * 
	   	 * @param key 全局对象的key值
	   	 * @return key对应的value值
	   	 */
	 public Object getGlobalObject(String key);
}

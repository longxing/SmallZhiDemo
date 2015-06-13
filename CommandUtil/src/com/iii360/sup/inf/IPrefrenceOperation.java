package com.iii360.sup.inf;

public interface IPrefrenceOperation {
	 /**
     * 
     * @param key Preference key值
     * @return key对应的value值
     */
	  public boolean getPrefBoolean(String key) ;
	  /**
	     * 
	     * @param key Preference key值
	     * @param defVal 默认的value值
	     * @return key对应的value值
	     */
	  public boolean getPrefBoolean(String key, boolean defVal);
	  /**
	     * 
	     * @param key Preference key值
	     * @param value 设置的value值
	     */
	  public void setPrefBoolean(String key, boolean value);
	  /**
	     * 
	     * @param key Preference key值
	     * @return key对应的value值
	  */
	  public String getPrefString(String key) ;
	  /**
	     * 
	     * @param key Preference key值
	     * @param defVal 默认的value值
	     * @return key对应的value值
	     */
	  public String getPrefString(String key, String defVal);
	  /**
	   * 
	   * @param key Preference key值
	   * @param value 设置的value值
	   */
	  public void setPrefInteger(String key, int value) ;
	  /**
	   * 
	   * @param key Preference key值
	   * @return key对应的value值
	   */
	  public int getPrefInteger(String key) ;
	  /**
	   * 
	   * @param key Preference key值
	   * @param defVal 默认的value值
	   * @return key对应的value值
	   */
	  public int getPrefInteger(String key, int defVal) ;
	  /**
	   * 
	   * @param key Preference key值
	   * @param value 设置的value值
	   */
	  public void setPrefString(String key, String value) ;
	  /**
	   * 
	   * @param key Preference key值
	   * @return key对应的value值
	   */
	  public long getPrefLong(String key) ;
	  /**
	   * 
	   * @param key Preference key值
	   * @param defVal 默认的value值
	   * @return key对应的value值
	   */
	  public long getPrefLong(String key, long defVal) ;
	  /**
	   * 
	   * @param key Preference key值
	   * @param value 设置的value值
	   */
	  public void setPrefLong(String key, long value) ;
	  /**
	   * 
	   * @param key Preference key值
	   * @return key对应的value值
	   */
	  public float getPrefFloat(String key) ;
	  /**
	   * 
	   * @param key Preference key值
	   * @param defVal 默认的value值
	   * @return key对应的value值
	   */
	  public float getPrefFloat(String key, float defVal);
	  /**
	   * 
	   * @param key Preference key值
	   * @param value 设置的value值
	   */
	  public void setPrefFloat(String key, float value);
}

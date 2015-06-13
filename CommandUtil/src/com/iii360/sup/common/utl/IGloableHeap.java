package com.iii360.sup.common.utl;

import java.util.Map;
/**
 * <pre>
 * 全局的程序空间。程序的Application 要实现该接口。
 * 并对所有方法进行响应。
 * 且方法的返回值不能为 null.
 * </pre>
 * @author Jerome.Hu
 * 
 */
public interface IGloableHeap {
	/**
	 * 
	 * @return 全局存放String类型对象的Map
	 */
	public Map<String,String> getGloabalString();
	/**
	 * 
	 * @return 全局存放Object类型对象的Map
	 */
	public Map<String,Object> getGlobalObjectMap();
	/**
	 * 
	 * @return 全局存放Integer类型对象的Map
	 */
	public Map<String,Integer> getGlobalIntegerMap(); 
	/**
	 * 
	 * @return 全局存放Float类型对象的Map
	 */
	public Map<String,Float> getGlobalFloatMap();
	/**
	 * 
	 * @return 全局存放Long类型对象的Map
	 */
	public Map<String,Long> getGlobalLongMap();
	/**
	 * 
	 * @return 全局存放Boolean类型对象的Map
	 */
	public Map<String,Boolean> getGlobalBooleanMap();
}

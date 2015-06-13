package com.iii360.base.inf;

import java.util.List;
import java.util.Map;

/**
 * 联系人列表容器。
 * 全局的Application 如果用到联系人jar包contact.必须实现该接口
 * @author Jerome.Hu.
 *
 */
public interface IContactsContainer {
	/**
	 * 
	 * @return 联系人列表
	 */
	public List<Object> getContactsNameList();
	/**
	 * 设置联系人列表
	 * @param contactsList 联系人列表
	 */
	public void setContactNameList(List<Object> contactsList);
	/**
	 * 
	 * @return 保存了联系人姓名，电话的Map
	 */
	public Map<String,Object> getContactMap();
	/**
	 * 设置 联系人,电话map.
	 * @param contactMap 联系人电话列表map.
	 */
	public void setContactMap(Map<String,Object> contactMap);
}

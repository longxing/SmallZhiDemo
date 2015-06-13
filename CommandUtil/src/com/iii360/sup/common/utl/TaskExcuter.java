package com.iii360.sup.common.utl;

import java.io.Serializable;

public class TaskExcuter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String needHandle;
	public TimerTicker timeUnit;
	public int id;
	public boolean isSystemCommand;
	public long creatTime;
	public boolean isExcuted = false;
}
/******************************************************************************************
* @file ErrCode.java
*
* @brief define all the global error code
*
* Code History:
*      [2015-04-07] xiaohua lu, initial version, define all the error code
*
* Code Review:
*
*********************************************************************************************/

package com.iii360.sup.common.base;



public class ErrCode 
{

	public static final int XOK = 0x0000;					// No error
	public static final int XERR_NONE = 0x0000;		// No error
	
	public static final int XERR_BASE = 0x0001;
	public static final int XERR_UNKNOWN = 0x0001;
	public static final int XERR_INVALID_PARAM = 0x0002;
	public static final int XERR_UNSUPPORTED = 0x0003;	
	public static final int XERR_NO_MEMORY = 0x0004;
	public static final int XERR_BAD_STATE = 0x0005;
	public static final int XERR_USER_CANCEL = 0x0005;
	public static final int XERR_EXPIRED = 0x0006;
	public static final int XERR_USER_PAUSE = 0x0007;
	public static final int XERR_BUFFER_OVERFLOW = 0x0008;
	public static final int XERR_BUFFER_UNDERFLOW = 0x0009;
	public static final int XERR_NO_DISKSPACE = 0x000A;
	public static final int	XERR_COMPONENT_NOT_EXIST = 0x000B;
	public static final int	XERR_GLOBAL_DATA_NOT_EXIST = 0x000C;
	
	
	public static final int XERR_FILE_BASE	= 0x1000;	
	public static final int XERR_FILE_GENERAL = 0x1000;
	public static final int XERR_FILE_NOT_EXIST = 0x1001;
	public static final int XERR_FILE_EXIST = 0x1002;
	public static final int XERR_FILE_EOF = 0x1003;
	public static final int XERR_FILE_FULL = 0x1004;
	public static final int XERR_FILE_SEEK = 0x1005;
	public static final int XERR_FILE_READ = 0x1006;
	public static final int XERR_FILE_WRITE = 0x1007;
	public static final int XERR_FILE_OPEN = 0x1008;
	public static final int XERR_FILE_DELETE = 0x1009;
	public static final int XERR_FILE_RENAME = 0x100A;
	
	
	public static final int XERR_DB_BASE	= 0x2000;	
	public static final int XERR_DB_GENERAL = 0x2000;
	public static final int XERR_DB_NOT_EXIST = 0x2001;
	public static final int XERR_DB_EXIST = 0x2002;
	public static final int XERR_DB_EOF = 0x2003;
	public static final int XERR_DB_FULL = 0x2004;
	public static final int XERR_DB_SELECT = 0x2005;
	public static final int XERR_DB_READ = 0x2006;
	public static final int XERR_DB_WRITE = 0x2007;
	public static final int XERR_DB_OPEN = 0x2008;
	public static final int XERR_DB_DELETE = 0x2009;
	public static final int XERR_DB_RENAME = 0x200A;
	
			
	
	public static final int XERR_NET_BASE = 0x3000;
	public static final int XERR_NET_GENERAL = 0x3000;
	public static final int XERR_SOCKET_OPEN = 0x3001;
	public static final int XERR_SOCKET_READ = 0x3002;
	public static final int XERR_SOCKET_WRITE = 0x3003;
	public static final int XERR_SOCKET_CONNECT = 0x3004;
	public static final int XERR_SOCKET_MSGSIZE = 0x3005;
	public static final int XERR_SOCKET_TIMEDOUT = 0x3006;
	public static final int XERR_SOCKET_HOST_NOT_FOUND = 0x300A;	
	public static final int XERR_HTTP_GENERAL = 0x3100;
	public static final int XERR_HTTP_DATA_NOT_READY = 0x3101;
	public static final int XERR_HTTP_EOF = 0x3102;
	public static final int	XERR_HTTP_TIMEOUT = 0x3103;
	public static final int	XERR_HTTP_REQUEST_FAIL = 0x3103;
	public static final int	XERR_HTTP_NOBUFFERS	 = 0x3104;
	public static final int	XERR_HTTP_RESPONSE_300 = 0x3105;
	

	public static final int	XERR_THREAD_BASE = 0x4000;
	public static final int	XERR_THREAD_GENERAL = 0x4000;
	public static final int	XERR_THREAD_CREATE = 0x4001;
	public static final int	XERR_THREAD_DESTROY = 0x4002;
	public static final int	XERR_THREAD_SLEEP = 0x4003;

		

}

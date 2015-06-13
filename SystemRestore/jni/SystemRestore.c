/*----------------------------------------------------------------------------------------------
*
* This file is JuShang's property. It contains  JuShang's trade secret, proprietary and
* confidential information.
*
* The information and code contained in this file is only for authorized  JuShang employees
* to design, create, modify, or review.
*
* DO NOT DISTRIBUTE, DO NOT DUPLICATE OR TRANSMIT IN ANY FORM WITHOUT PROPER AUTHORIZATION.
*
* If you are not an intended recipient of this file, you must not copy, distribute, modify,
* or take any action in reliance on it.
*
* If you have received this file in error, please immediately notify  JuShang and
* permanently delete the original and any copy of any file and any printout thereof.
*
*---------------------------------------------------------------------------------------------*/
/*
 * SystemRestore.c:
 *
 * Purpose:
 *
 *
 * Code History:
 *      [2015-6-2] Huangjincai Initial version.
 *
 *
 * Code Review:
 *
 *
 *
 */
#include <stdio.h>
#include <fcntl.h>
#include <android/log.h>
#include "SystemRestore.h"

#define BACK_PATH						"/system/smallzhiBackUP/"
#define RUNNING_PATH					"/system/app/"
#define MAIN_APP						"com_voice_assistant.apk"
#define SERVR_APP						"com_iii_wifiserver.apk"
#define UPGRADE_APP						"com_voice_upgrade.apk"
#define TTSAPK_APP 						"com_voice_offline.apk"

#define MAIN_BACK_PATH					BACK_PATH##MAIN_APP
#define SRVER_BACK_PATH					BACK_PATH##SERVR_APP
#define UPGRADE_BACK_PATH				BACK_PATH##UPGRADE_APP
#define TTSAPK_BACK_PATH				BACK_PATH##TTSAPK_APP


#define MAIN_RUNNING_PATH				RUNNING_PATH##MAIN_APP
#define SRVER_RUNNING_PATH				RUNNING_PATH##SERVR_APP
#define UPGRADE_RUNNING_PATH			RUNNING_PATH##UPGRADE_APP
#define TTSAPK_RUNNING_PATH				RUNNING_PATH##TTSAPK_APP
#define SYS_LOG(const char* info)  __android_log_print(ANDROID_LOG_INFO, (const char*)"SystemRestore", (const char*)info);

#define BUFFER_SIZE		1024

int	FileRestore()
{
	int result = SYSTEM_ERROR;

	result = FileCopy(MAIN_BACK_PATH,MAIN_RUNNING_PATH);
	if(SYSTEM_ERROR == result)
	{
		return result;
	}
	SYS_LOG("Restore com_voice_assistant.apk sucess");


	result = FileCopy(SRVER_BACK_PATH,SRVER_RUNNING_PATH);
	if(SYSTEM_ERROR == result)
	{
		return result;
	}
	SYS_LOG("Restore com_iii_wifiserver.apk sucess");


	result = FileCopy(UPGRADE_BACK_PATH,UPGRADE_RUNNING_PATH);
	if(SYSTEM_ERROR == result)
	{
		return result;
	}
	SYS_LOG("Restore com_voice_upgrade.apk sucess");

	result = FileCopy(TTSAPK_BACK_PATH,TTSAPK_RUNNING_PATH);
	if(SYSTEM_ERROR == result)
	{
		return result;
	}
	SYS_LOG("Restore com_voice_offline.apk sucess");

	return result;

}


int ApplicationStop()
{
//TODO research


}


/**
 * 功能：卸载系统安装APP
 * 返回值：
 *      SYSTEM_SUCESS: 卸载成功
 *      SYSTEM_ERROR： 卸载失败
 */

int ClearFile()
{
		// important ,thie file maybe be removed
		//  for example , while is resetting, the system is closed and interrupt the app recovery
		int result = SYSTEM_ERROR；

		if(SYSTEM_SUCESS == access(MAIN_PATH,F_OK))
		{
			result = remove(MAIN_PATH);
			if(SYSTEM_ERROR == result)
			{
				return result;
			}
			SYS_LOG("remove com_voice_assistant.apk sucess");
		}

		if(SYSTEM_SUCESS == access(SRVER_PATH,F_OK))
		{
			result = remove(SRVER_PATH);
			if(SYSTEM_ERROR == result)
			{
				return result;
			}
			SYS_LOG("remove com_iii_wifiserver.apk sucess");
		}

		if(SYSTEM_SUCESS == access(UPGRADE_PATH, F_OK))
		{
			result = remove(UPGRADE_PATH);
			if(SYSTEM_ERROR == result)
			{
				return result;
			}
			SYS_LOG("remove com_voice_upgrade.apk sucess");
		}

		if(SYSTEM_SUCESS == access(TTSAPK_PATH, F_OK))
		{
			result = remove(TTSAPK_PATH);
			if(SYSTEM_ERROR == result)
			{
				return result;
			}
			SYS_LOG("remove com_voice_offline.apk sucess");
		}

		SYS_LOG("remove system sucess");
		return result;
}


/**
 * 功能：拷贝文件函数
 * 参数：
 *      sourcePath：源文件名（带路径）
 *      targetPath：目标文件名（带路径）
 * 返回值：
 *      SYSTEM_SUCESS: 拷贝成功
 *      SYSTEM_ERROR： 拷贝失败
 */
int FileCopy(const char *sourcePath,
        const char *targetPath)
{
    FILE *fpR, *fpW;
    char buffer[BUFFER_SIZE];
    int lenR, lenW;
    if ((fpR = fopen(sourcePath, "r")) == NULL)
    {
    	SYS_LOG("The sourcePath file  can not be opened! \n");
        return SYSTEM_ERROR;
    }
    if ((fpW = fopen(targetPath, "w")) == NULL)
    {
    	SYS_LOG("The targetPath file  can not be opened! \n");
        fclose(fpR);
        return SYSTEM_ERROR;
    }

    memset(buffer, 0, BUFFER_SIZE);
    while ((lenR = fread(buffer, 1, BUFFER_SIZE, fpR)) > 0)
    {
        if ((lenW = fwrite(buffer, 1, lenR, fpW)) != lenR)
        {
        	SYS_LOG("Write to file  failed!\n");
            fclose(fpR);
            fclose(fpW);
            return SYSTEM_ERROR;
        }
        memset(buffer, 0, BUFFER_SIZE);
    }

    fclose(fpR);
    fclose(fpW);
    return SYSTEM_SUCESS;
}


int SystemRestore()
{
	int result = SYSTEM_ERROR；
	result = ApplicationStop();

	if(SYSTEM_ERROR == result)
	{
		return result;
	}

	SYS_LOG("Stop Application sucess");

	result = ClearFile();
	if(SYSTEM_ERROR == result)
	{
		return result;
	}



}

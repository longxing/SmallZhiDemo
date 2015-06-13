package com.voice.upgrade.service.aidl;

interface UpgradeManager {

/**
 * 更新服务程序的版本号
 */
String serverVersion();

/**
 * 应用程序套件的版本号
 */
String version();

/**
 * 立刻更新
 */
void check();

/**
 * 批量安装apk包
 */
void install(in String[] apkfiles);

/**
 * 批量卸载apk包
 */
void uninstall(in String[] libfiles);
}
mount -o remount rw /system
mount -o remount rw /mnt/sdcard/
sleep 10

/mnt/sdcard/apkInstall.sh /mnt/sdcard/install_packages/com_voice_upgrade.apk /mnt/sdcard/install_packages/com_voice_upgrade/lib/armeabi/

if [ -f /mnt/sdcard/upgrade.cache ];then
	echo "save upgrade.properties"
	cp -f /mnt/sdcard/upgrade.cache /mnt/sdcard/upgrade.properties
fi

echo "3 normal">/mnt/sdcard/shut_config

reboot
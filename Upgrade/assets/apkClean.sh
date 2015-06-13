mount -o remount rw /system
mount -o remount rw /mnt/sdcard/
sleep 10

install_packages="/mnt/sdcard/install_packages"

#assign apk
if [ -d $install_packages ];then
	rm -rf "$install_packages"
else
	exit 0
fi

sync

echo "ok"


mount -o remount rw /system
sleep 2
chmod 777 /system/usr
mkdir system/usr/properties
cp -fr /sdcard/sn /system/usr/properties/sn
chmod -R 777 /system/usr/properties
sync
echo "ok"


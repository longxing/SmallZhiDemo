mount -o remount mnt/sdcard
sleep 3
chmod 777 mnt/sdcard
mount -o remount rw /system
sleep 2
chmod 777 /system/usr
mkdir system/usr/properties
chmod -R 777 /system/usr/properties
sync
echo "ok"
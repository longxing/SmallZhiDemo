mount -o remount rw /system
sleep 5
result=`ls -l /system/app/com_voice_assistant.apk|busybox awk '{print $1}'`
s1="-rw-r--r--";
#echo yiii $s1
#echo errrr $result

if [ "$result" != "$s1" ]; then
        chmod 0644 /system/app/com_voice_assistant.apk
        reboot
else
        exit 0
fi

sync

echo "ok"


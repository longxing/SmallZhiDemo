mount -o remount rw /system
sleep 5

sysapk="/system/app/"
syslib="/system/lib/"

currliblist="/mnt/sdcard/currliblist"
currapklist="/mnt/sdcard/currapklist"

if [ -z "$1" -o -z "$2" ]; then
	echo error
	exit 0
else
	apk=($1)
	if [ ! -f $apk ]; then
		exit 0
	fi
	echo "apk="$apk

	lib=($2)
	if [ ! -d $lib ];then
		exit 0
	fi
	echo "lib="$lib
fi

function cpapk(){
	apklist=$1
	#echo $apklist
	apkname=${apklist##*/}
	#echo $apkname
	echo "copy *.apk ......"
	if [ -f $sysapk$apkname ];then
		rm -rf $sysapk$apkname
	#	echo "rm $sysapk$apkname"
	fi
	cp -f $1 $sysapk
	#echo $1 >> $currapklist
	sync
	#set chmod
	chmod 0644 $sysapk$apkname
}

function cplib(){
	liblist=`ls $1`
	#echo "liblist:"$liblist
	echo "copy *.so ......"
	for v in $liblist
	do
		if [[ $v == *.so ]];then
			#echo $v to current score
			#echo "$v" >> ${currliblist}
			if [ -f $syslib$v ];then
				rm -rf $syslib$v
			#echo "rm $syslib$v"
			fi			
			cp -f "$1/$v" $syslib
			#set chmod
			chmod 0644 $syslib$v
			#echo ">>cp -f $v $syslib"
		fi
	done
}

cpapk $apk
cplib $lib

rm -f "/data/dalvik-cache/system@app@$apkname@classes.dex"
echo "clean cache /data/dalvik-cache/system@app@$apkname@classes.dex"

sync

sleep 5

sync

echo "ok"

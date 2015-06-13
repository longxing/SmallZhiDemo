#HeZi
ttsVoice  本地播报所需要的tts资源文件，格式为 16K， 单声道，wav 格式
for i in `find .  -type d ` ; do (cd $i && for x in *.mp3; do avconv -i "$x"  -ar 16000  -ac 1  -af volume=volume=4  "`basename "$x"mp3`@@..wav"; done) ; done 


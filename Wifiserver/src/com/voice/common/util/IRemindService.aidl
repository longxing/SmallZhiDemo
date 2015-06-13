package  com.voice.common.util;
import com.voice.common.util.Remind;

interface IRemindService{
	List<Remind> getRemindList();
	void deleteRemind(int id);
	void addRemind(in Remind r);
}